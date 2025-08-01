package com.univsoftdev.econova.bsondb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import de.undercouch.bson4jackson.BsonFactory;
import jakarta.persistence.Id;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BsonDb implements AutoCloseable {

    private final ObjectMapper objectMapper;
    private final Map<String, List<?>> collections;
    private final Map<Class<?>, Field> idFieldsCache = new HashMap<>();
    private final ThreadLocal<Transaction> currentTransaction = ThreadLocal.withInitial(Transaction::new);
    private final Object lock = new Object();
    private final Map<String, Map<String, Function<?, ?>>> indexExtractors = new HashMap<>();
    private final Map<String, Map<String, Map<Object, Object>>> indices = new HashMap<>();
    private volatile File currentDatabaseFile;
    private final ReadWriteLock fileChangeLock = new ReentrantReadWriteLock();

    public BsonDb() {
        this("data.db");
    }

    public BsonDb(String filename) {
        this.objectMapper = new ObjectMapper(new BsonFactory());
        this.currentDatabaseFile = new File(filename);
        this.collections = new HashMap<>();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        loadData();
    }

    /**
     * Cambia el archivo de base de datos actual
     *
     * @param newFilename Ruta del nuevo archivo
     * @param migrateData Si true, migra los datos al nuevo archivo
     */
    public void changeDatabaseFile(String newFilename, boolean migrateData) {
        File newFile = new File(newFilename);

        fileChangeLock.writeLock().lock();
        try {
            // 1. Validar el nuevo archivo
            if (newFile.equals(currentDatabaseFile)) {
                return; // Ya es el archivo actual
            }

            if (newFile.exists() && !newFile.canWrite()) {
                throw new BsonDbException("No se puede escribir en el archivo especificado: " + newFilename);
            }

            // 2. Guardar los datos actuales si es necesario
            if (migrateData) {
                saveDataToFile(newFile);
            } else {
                // Limpiar datos en memoria si no migramos
                collections.clear();
                indices.clear();
                indexExtractors.clear();
            }

            // 3. Actualizar la referencia al archivo
            File oldFile = currentDatabaseFile;
            currentDatabaseFile = newFile;

            // 4. Cargar datos del nuevo archivo
            if (newFile.exists()) {
                loadData();
            } else {
                // Crear archivo vacío
                saveData();
            }
        } finally {
            fileChangeLock.writeLock().unlock();
        }
    }

    private void saveDataToFile(File targetFile) {
        try {
            Map<String, Map<String, Object>> dataToSave = new HashMap<>();

            for (Map.Entry<String, List<?>> entry : collections.entrySet()) {
                String collectionName = entry.getKey();
                List<?> items = entry.getValue();

                Map<String, Object> collectionData = new HashMap<>();
                if (!items.isEmpty()) {
                    collectionData.put("_type", items.get(0).getClass().getName());
                    collectionData.put("items", items);
                }
                dataToSave.put(collectionName, collectionData);
            }

            objectMapper.writeValue(targetFile, dataToSave);
        } catch (IOException e) {
            throw new BsonDbException("Failed to save database to file: " + targetFile.getAbsolutePath(), e);
        }
    }

    private Field findIdField(Class<?> clazz) {
        return idFieldsCache.computeIfAbsent(clazz, k -> {
            // 1. Buscar anotación @Id
            List<Field> annotatedFields = Arrays.stream(clazz.getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(Id.class))
                    .collect(Collectors.toList());

            if (!annotatedFields.isEmpty()) {
                Field idField = annotatedFields.get(0);
                idField.setAccessible(true);
                return idField;
            }

            // 2. Buscar campos comunes de ID
            String[] commonIdNames = {"id", "_id", "uuid", "key", "code"};
            for (String fieldName : commonIdNames) {
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field;
                } catch (NoSuchFieldException ignored) {
                }
            }

            // 3. Para colecciones, no requiere ID
            if (Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz)) {
                return null;
            }

            // 4. Para otros objetos, intentar con el primer campo
            if (clazz.getDeclaredFields().length > 0) {
                Field firstField = clazz.getDeclaredFields()[0];
                firstField.setAccessible(true);
                return firstField;
            }

            return null;
        });
    }

    public boolean collectionExists(String collectionName) {
        return collections.containsKey(collectionName);
    }

    public List<Object> getAll(String collectionName) {
        return getCollection(collectionName, Object.class);
    }

    public boolean deleteById(String collectionName, Object id) {
        try (TransactionContext ctx = beginTransaction()) {
            List<Object> collection = getCollection(collectionName, Object.class);
            boolean removed = collection.removeIf(item -> getOrGenerateId(item).equals(id));
            if (removed) {
                ctx.commit();
            }
            return removed;
        }
    }

    private Object getOrGenerateId(Object object) {
        // Caso especial para colecciones
        if (object instanceof Collection) {
            return UUID.randomUUID().toString(); // ID único para la colección
        }

        // Para Mapas
        if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            if (map.containsKey("id")) {
                return map.get("id");
            }
            if (map.containsKey("_id")) {
                return map.get("_id");
            }
            return UUID.randomUUID().toString();
        }

        // Para otros objetos
        Field idField = findIdField(object.getClass());
        try {
            
            if (idField!=null) {
                Object idValue = idField.get(object);
                if (idValue != null) {
                    return idValue;
                }
                // Generar ID si es null
                Object generatedId = UUID.randomUUID().toString();
                idField.set(object, generatedId);
                return generatedId;
            }
            // Como último recurso, usar hash del objeto
            return Objects.hash(object);
        } catch (IllegalAccessException e) {
            throw new BsonDbException("Failed to access ID field", e);
        }
    }

    private Object getIdValue(Object object) {
        try {
            Field idField = getIdField(object.getClass());
            return idField.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access ID field", e);
        }
    }

    private Field getIdField(Class<? extends Object> aClass) {
        return findIdField(aClass);
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        fileChangeLock.readLock().lock();
        try {
            if (currentDatabaseFile.exists() && currentDatabaseFile.length() > 0) {
                try {
                    Map<String, Map<String, Object>> savedData = objectMapper.readValue(currentDatabaseFile,
                            objectMapper.getTypeFactory().constructMapType(HashMap.class, String.class, Map.class));

                    for (Map.Entry<String, Map<String, Object>> entry : savedData.entrySet()) {
                        String collectionName = entry.getKey();
                        Map<String, Object> collectionData = entry.getValue();

                        if (!collectionData.isEmpty()) {
                            Class<?> objectClass = Class.forName((String) collectionData.get("_type"));
                            CollectionType listType = objectMapper.getTypeFactory()
                                    .constructCollectionType(List.class, objectClass);

                            List<?> items = objectMapper.convertValue(collectionData.get("items"), listType);
                            collections.put(collectionName, items);
                        }
                    }
                } catch (IOException | ClassNotFoundException | IllegalArgumentException e) {
                    System.err.println("Error loading data: " + e.getMessage());
                }
            }
        } finally {
            fileChangeLock.readLock().unlock();
        }
    }

    private void saveData() {
        fileChangeLock.readLock().lock();
        try {
            try {
                Map<String, Map<String, Object>> dataToSave = new HashMap<>();

                for (Map.Entry<String, List<?>> entry : collections.entrySet()) {
                    String collectionName = entry.getKey();
                    List<?> items = entry.getValue();

                    Map<String, Object> collectionData = new HashMap<>();
                    if (!items.isEmpty()) {
                        collectionData.put("_type", items.get(0).getClass().getName());
                        collectionData.put("items", items);
                    }
                    dataToSave.put(collectionName, collectionData);
                }
                objectMapper.writeValue(currentDatabaseFile, dataToSave);
            } catch (IOException e) {
                throw new BsonDbException("Failed to save database", e);
            }
        } finally {
            fileChangeLock.readLock().unlock();
        }
    }

    public void backupDatabase(String backupPath) {
        File backupFile = new File(backupPath);
        changeDatabaseFile(backupPath, true);
        // Volver al archivo original
        changeDatabaseFile(currentDatabaseFile.getAbsolutePath(), false);
    }

    public <T> List<T> getCollection(String collectionName, Class<T> type) {
        return (List<T>) collections.computeIfAbsent(collectionName, k -> new ArrayList<>());
    }

    public void insert(String collectionName, Object object) {
        if (object == null) {
            throw new BsonDbException("Cannot insert null object");
        }

        try (TransactionContext ctx = beginTransaction()) {
            List<Object> collection = getCollection(collectionName, Object.class);
            Object id = getOrGenerateId(object);

            // Verificar ID único
            if (collection.stream().anyMatch(item -> getOrGenerateId(item).equals(id))) {
                throw new DuplicateIdException("ID " + id + " already exists in collection " + collectionName);
            }

            collection.add(object);
            updateAllIndices(collectionName, object);
            ctx.commit();
        } catch (Exception e) {
            throw new BsonDbException("Failed to insert object into collection '" + collectionName + "'", e);
        }
    }

    public void insertCollection(String collectionName, Collection<?> items) {
        try (TransactionContext ctx = beginTransaction()) {
            List<Object> collection = getCollection(collectionName, Object.class);

            // Generar IDs para items sin ID
            Map<Object, Object> itemsWithIds = items.stream()
                    .collect(Collectors.toMap(
                            this::getOrGenerateId,
                            item -> item,
                            (existing, replacement) -> {
                                throw new DuplicateIdException("Duplicate ID detected in collection items");
                            }
                    ));

            collection.addAll(itemsWithIds.values());
            ctx.commit();
        }
    }

    private <T> void updateAllIndices(String collectionName, T item) {
        synchronized (lock) {
            // Obtener los extractores de índices para esta colección
            Map<String, Function<?, ?>> extractors = indexExtractors.get(collectionName);
            if (extractors == null) {
                return;
            }

            // Obtener los índices para esta colección
            Map<String, Map<Object, Object>> collectionIndices = indices.computeIfAbsent(collectionName, k -> new HashMap<>());

            // Actualizar cada índice
            extractors.forEach((indexName, extractor) -> {
                // Convertir el extractor al tipo correcto
                @SuppressWarnings("unchecked")
                Function<T, Object> typedExtractor = (Function<T, Object>) extractor;

                // Calcular la clave del índice
                Object key = typedExtractor.apply(item);

                // Obtener o crear el índice específico
                Map<Object, Object> index = collectionIndices.computeIfAbsent(indexName, k -> new HashMap<>());

                // Actualizar la entrada del índice
                index.put(key, item);
            });
        }
    }

    public void executeInTransaction(Runnable operation) {
        Transaction tx = currentTransaction.get();
        if (tx.isActive()) {
            operation.run();
            return;
        }

        try {
            tx.begin();
            operation.run();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            if (!tx.isActive()) {
                currentTransaction.remove();
            }
        }
    }

    public <T> List<T> findAll(String collectionName, Class<T> type) {
        return getCollection(collectionName, type);
    }

    public Optional<Object> findById(String collectionName, Object id) {
        return getCollection(collectionName, Object.class).stream()
                .filter(item -> getOrGenerateId(item).equals(id))
                .findFirst();
    }

    public <T> List<T> findCollection(String collectionName, Predicate<T> condition, Class<T> type) {
        return getCollection(collectionName, type).stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    public <T> void update(String collectionName, T updatedObject) {
        try (TransactionContext ctx = beginTransaction()) {
            List<T> collection = getCollection(collectionName, (Class<T>) updatedObject.getClass());
            Object updatedId = getIdValue(updatedObject);

            // Buscar y actualizar el objeto
            boolean found = false;
            for (int i = 0; i < collection.size(); i++) {
                if (getIdValue(collection.get(i)).equals(updatedId)) {
                    collection.set(i, updatedObject);
                    found = true;

                    // Actualizar índices
                    updateAllIndices(collectionName, updatedObject);
                    break;
                }
            }

            if (!found) {
                throw new ObjectNotFoundException(updatedId);
            }

            ctx.commit();
        }
    }

    public <T> boolean delete(String collectionName, Object id, Class<T> type) {
        try (TransactionContext ctx = beginTransaction()) {
            List<T> collection = getCollection(collectionName, type);
            Optional<T> toRemove = collection.stream()
                    .filter(item -> getIdValue(item).equals(id))
                    .findFirst();

            if (toRemove.isPresent()) {
                // Eliminar de índices primero
                removeFromAllIndices(collectionName, toRemove.get());

                // Eliminar de la colección
                collection.removeIf(item -> getIdValue(item).equals(id));

                ctx.commit();
                return true;
            }

            ctx.rollback();
            return false;
        }
    }

    private <T> void removeFromAllIndices(String collectionName, T item) {
        synchronized (lock) {
            Map<String, Function<?, ?>> extractors = indexExtractors.get(collectionName);
            if (extractors == null) {
                return;
            }

            Map<String, Map<Object, Object>> collectionIndices = indices.get(collectionName);
            if (collectionIndices == null) {
                return;
            }

            extractors.forEach((indexName, extractor) -> {
                @SuppressWarnings("unchecked")
                Function<T, Object> typedExtractor = (Function<T, Object>) extractor;
                Object key = typedExtractor.apply(item);

                Map<Object, Object> index = collectionIndices.get(indexName);
                if (index != null) {
                    index.remove(key);
                }
            });
        }
    }

    public <T, K> void createIndex(String collectionName, String indexName,
            Function<T, K> keyExtractor, Class<T> type) {
        synchronized (lock) {
            // Almacenar la función extractora
            indexExtractors.computeIfAbsent(collectionName, k -> new HashMap<>())
                    .put(indexName, keyExtractor);

            // Crear el índice inicial
            Map<Object, Object> index = new HashMap<>();
            getCollection(collectionName, type).forEach(item -> {
                K key = keyExtractor.apply(item);
                index.put(key, item);
            });

            // Almacenar el índice
            indices.computeIfAbsent(collectionName, k -> new HashMap<>())
                    .put(indexName, index);
        }
    }

    public <T, K> Optional<T> findByIndex(String collectionName, String indexName, K key, Class<T> type) {
        synchronized (lock) {
            // Obtener los índices de la colección
            Map<String, Map<Object, Object>> collectionIndices = indices.get(collectionName);
            if (collectionIndices == null) {
                return Optional.empty();
            }

            // Obtener el índice específico
            Map<Object, Object> index = collectionIndices.get(indexName);
            if (index == null) {
                return Optional.empty();
            }

            // Buscar el valor y asegurar el tipo correcto
            @SuppressWarnings("unchecked")
            T result = (T) index.get(key);
            return Optional.ofNullable(result);
        }
    }

    public <T, K> void updateIndex(String collectionName, String indexName,
            Function<T, K> keyExtractor, T item) {
        synchronized (lock) {
            Map<Object, Object> index = indices.getOrDefault(collectionName, new HashMap<>())
                    .get(indexName);
            if (index != null) {
                K key = keyExtractor.apply(item);
                index.put(key, item);
            }
        }
    }

    public void dropIndex(String collectionName, String indexName) {
        synchronized (lock) {
            // Eliminar el extractor
            Map<String, Function<?, ?>> extractors = indexExtractors.get(collectionName);
            if (extractors != null) {
                extractors.remove(indexName);
            }

            // Eliminar el índice
            Map<String, Map<Object, Object>> collectionIndices = indices.get(collectionName);
            if (collectionIndices != null) {
                collectionIndices.remove(indexName);
            }
        }
    }

    public boolean hasIndex(String collectionName, String indexName) {
        synchronized (lock) {
            Map<String, Map<Object, Object>> collectionIndices = indices.get(collectionName);
            return collectionIndices != null && collectionIndices.containsKey(indexName);
        }
    }

    public <T> List<T> findWhere(String collectionName, Class<T> type, Predicate<T> condition) {
        return getCollection(collectionName, type).stream()
                .filter(condition)
                .collect(Collectors.toList());
    }

    public <T> void bulkInsert(String collectionName, List<T> objects, Class<T> type) {
        try (TransactionContext ctx = beginTransaction()) {
            Set<Object> ids = new HashSet<>();
            List<T> collection = getCollection(collectionName, type);

            for (T obj : objects) {
                Object id = getIdValue(obj);
                if (!ids.add(id)) {
                    throw new DuplicateIdException(id);
                }
                if (collection.stream().anyMatch(item -> getIdValue(item).equals(id))) {
                    throw new DuplicateIdException(id);
                }
            }

            collection.addAll(objects);
            ctx.commit();
        }
    }

    public Path getDbFile() throws IOException {
        fileChangeLock.readLock().lock();
        try {
            return currentDatabaseFile.exists() ? Path.of(currentDatabaseFile.getAbsolutePath()) : null;
        } finally {
            fileChangeLock.readLock().unlock();
        }
    }

    public <T> void insertAll(String collectionName, List<T> objects) {
        if (objects == null || objects.isEmpty()) {
            return;
        }

        try {
            Transaction tx = currentTransaction.get();
            boolean shouldCommit = !tx.isActive();

            if (shouldCommit) {
                tx.begin();
            }

            Class<?> type = objects.get(0).getClass();
            List<T> collection = getCollection(collectionName, (Class<T>) type);
            Set<Object> ids = new HashSet<>();

            for (T obj : objects) {
                Object id = getIdValue(obj);
                if (!ids.add(id)) {
                    throw new IllegalStateException("Duplicate ID found: " + id);
                }
                if (collection.stream().anyMatch(item -> getIdValue(item).equals(id))) {
                    throw new IllegalStateException("Object with ID " + id + " already exists");
                }
            }

            collection.addAll(objects);

            if (shouldCommit) {
                tx.commit();
            }
        } catch (IllegalStateException e) {
            currentTransaction.get().rollback();
            throw e;
        } finally {
            if (!currentTransaction.get().isActive()) {
                currentTransaction.remove();
            }
        }
    }

    public <T> long count(String collectionName, Class<T> type) {
        return getCollection(collectionName, type).size();
    }

    public <T> void clearCollection(String collectionName, Class<T> type) {
        try (TransactionContext ctx = beginTransaction()) {
            getCollection(collectionName, type).clear();
            ctx.commit();
        }
    }

    public TransactionContext beginTransaction() {
        return new TransactionContext(currentTransaction.get());
    }

    @Override
    public void close() {
        fileChangeLock.writeLock().lock();
        try {
            saveData();
            collections.clear();
            indices.clear();
            indexExtractors.clear();
        } finally {
            fileChangeLock.writeLock().unlock();
        }
    }

    public DatabaseInfo getDatabaseInfo() {
        fileChangeLock.readLock().lock();
        try {
            return new DatabaseInfo(
                    currentDatabaseFile.getAbsolutePath(),
                    currentDatabaseFile.length(),
                    collections.keySet().size(),
                    collections.values().stream().mapToInt(List::size).sum()
            );
        } finally {
            fileChangeLock.readLock().unlock();
        }
    }

    private class Transaction {

        private final Map<String, List<?>> snapshot = new HashMap<>();
        private boolean active = false;
        private int nestingLevel = 0;

        void begin() {
            if (nestingLevel == 0) {
                collections.forEach((k, v) -> snapshot.put(k, new ArrayList<>(v)));
            }
            nestingLevel++;
        }

        void commit() {
            if (nestingLevel <= 0) {
                throw new IllegalStateException("No active transaction");
            }
            nestingLevel--;
            if (nestingLevel == 0) {
                saveData();
                snapshot.clear();
            }
        }

        void rollback() {
            if (nestingLevel <= 0) {
                return;
            }
            if (nestingLevel == 1) {
                collections.clear();
                snapshot.forEach((k, v) -> collections.put(k, new ArrayList<>(v)));
                snapshot.clear();
            }
            nestingLevel = 0;
        }

        boolean isActive() {
            return nestingLevel > 0;
        }
    }

    public class TransactionContext implements AutoCloseable {

        private final Transaction tx;
        private boolean committed = false;

        TransactionContext(Transaction tx) {
            this.tx = tx;
            tx.begin();
        }

        public void commit() {
            tx.commit();
            committed = true;
        }

        @Override
        public void close() {
            if (!committed) {
                tx.rollback();
            }
        }

        public void rollback() {
            tx.rollback();
            committed = false;
        }
    }
}
