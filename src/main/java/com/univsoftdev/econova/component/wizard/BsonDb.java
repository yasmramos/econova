package com.univsoftdev.econova.component.wizard;

import java.io.IOException;
import org.bson.Document;
import org.bson.BsonBinaryWriter;
import org.bson.BsonBinaryReader;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.DecoderContext;
import org.bson.io.BasicOutputBuffer;
import org.bson.io.ByteBufferBsonInput;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bson.ByteBufNIO;

@Slf4j
public class BsonDb implements AutoCloseable {

    private List<Document> database = new ArrayList<>();
    private Map<String, Set<String>> indices = new ConcurrentHashMap<>(); // Índices: {"name": ["id1", "id2"]}
    private Map<String, Map<Object, Set<String>>> compoundIndices = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock(); // Para transacciones
    private String dbFile = "bsondb.dat";   // Archivo de persistencia
    private boolean isClosed = false;

    public BsonDb() {
        loadFromDisk();
    }

    public BsonDb(String dbFile) {
        this.dbFile = dbFile;
        loadFromDisk();
    }

    /**
     * Inserta un documento con transacción atómica.
     *
     * @param data
     * @return ID generado o empty si falla.
     */
    public Optional<String> insert(Document data) {
        lock.lock();
        try {
            String id = UUID.randomUUID().toString();
            data.append("_id", id);
            database.add(data);
            updateAllIndices(id, data); // Actualiza índices simples y compuestos
            saveToDisk();
            return Optional.of(id);
        } finally {
            lock.unlock();
        }
    }

    private void updateAllIndices(String id, Document doc) {
        // Actualiza índices simples (como en tu implementación original)
        updateIndices(id, doc);

        // Actualiza índices compuestos
        compoundIndices.forEach((indexName, indexMap) -> {
            Document fields = new Document();
            // Corregido: Usar Arrays.stream() para iterar sobre el array
            Arrays.stream(indexName.split("_")).forEach(field -> fields.append(field, 1));
            Object key = generateCompoundKey(doc, fields);
            indexMap.computeIfAbsent(key, k -> new HashSet<>()).add(id);
        });
    }

    /**
     * Busca un documento por ID.
     *
     * @param id Identificador del documento.
     * @return Documento si existe, o `Optional.empty()`.
     */
    public Optional<Document> findById(String id) {
        return database.stream()
                .filter(doc -> doc.getString("_id").equals(id))
                .findFirst();
    }

    /**
     * Actualiza un documento de forma atómica.
     *
     * @param id
     * @param newData
     * @return
     */
    public boolean update(String id, Document newData) {
        lock.lock();
        try {
            Optional<Document> docOpt = findById(id);
            if (docOpt.isPresent()) {
                Document doc = docOpt.get();
                newData.forEach(doc::put);
                updateIndices(id, doc); // Reindexar
                saveToDisk();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Búsqueda por campo indexado (ej: "name").
     *
     * @param field
     * @param value
     * @return
     */
    public List<Document> findByIndex(String field, Object value) {
        if (!indices.containsKey(field)) {
            return Collections.emptyList();
        }

        return indices.get(field).stream()
                .filter(id -> findById(id).isPresent())
                .map(id -> findById(id).get())
                .filter(doc -> doc.get(field).equals(value))
                .collect(Collectors.toList());
    }

    public synchronized void saveToDisk() {
        if (isClosed) {
            throw new IllegalStateException("Database is closed");
        }
        try {
            Files.write(Paths.get(dbFile), toBson());
        } catch (IOException ex) {
            System.getLogger(BsonDb.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    private void loadFromDisk() {
        lock.lock();
        try {
            if (Files.exists(Paths.get(dbFile))) {
                byte[] data = Files.readAllBytes(Paths.get(dbFile));
                fromBson(data);
                rebuildIndices();
            }
        } catch (IOException e) {
            System.err.println("Error loading database: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    private void updateIndices(String id, Document doc) {
        doc.keySet().forEach(key -> {
            indices.computeIfAbsent(key, k -> new HashSet<>()).add(id);
        });
    }

    private void rebuildIndices() {
        indices.clear();
        database.forEach(doc -> {
            String id = doc.getString("_id");
            doc.keySet().forEach(key -> {
                indices.computeIfAbsent(key, k -> new HashSet<>()).add(id);
            });
        });
    }

    /**
     * Elimina un documento por ID.
     *
     * @param id Identificador del documento.
     * @return `true` si se eliminó, `false` si no se encontró.
     */
    public boolean delete(String id) {
        return database.removeIf(doc -> doc.getString("_id").equals(id));
    }

    /**
     * Obtiene todos los documentos.
     *
     * @return Lista de documentos en formato BSON (Document).
     */
    public List<Document> findAll() {
        return new ArrayList<>(database);
    }

    /**
     * Serializa la base de datos a BSON (byte[]).
     *
     * @return
     */
    public byte[] toBson() {
        BasicOutputBuffer buffer = new BasicOutputBuffer();
        try (BsonBinaryWriter writer = new BsonBinaryWriter(buffer)) {
            new DocumentCodec().encode(
                    writer,
                    new Document("data", database),
                    EncoderContext.builder().build()
            );
        }
        return buffer.toByteArray();
    }

    /**
     * Deserializa desde BSON (byte[]).
     *
     * @param bson
     */
    public void fromBson(byte[] bson) {
        ByteBuffer nioBuffer = ByteBuffer.wrap(bson);
        ByteBufNIO bsonCompatibleBuffer = new ByteBufNIO(nioBuffer);  // Adaptador

        try (BsonBinaryReader reader = new BsonBinaryReader(new ByteBufferBsonInput(bsonCompatibleBuffer))) {
            Document dbDocument = new DocumentCodec().decode(reader, DecoderContext.builder().build());
            this.database = dbDocument.getList("data", Document.class);
        }
    }

    /**
     * Búsqueda con operadores avanzados.
     *
     * @param query Documento con operadores (ej: {"age": {"$gt": 25}}).
     * @return
     */
    public List<Document> find(Document query) {
        return database.stream()
                .filter(doc -> matchesQuery(doc, query))
                .collect(Collectors.toList());
    }

    private boolean matchesQuery(Document doc, Document query) {
        for (Map.Entry<String, Object> entry : query.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Document document) {
                // Operador avanzado (ej: {"$gt": 25})
                if (!matchesOperator(doc, key, document)) {
                    return false;
                }
            } else if (!doc.get(key).equals(value)) {
                // Comparación exacta (ej: {"name": "Alice"})
                return false;
            }
        }
        return true;
    }

    private boolean matchesOperator(Document doc, String field, Document operatorQuery) {
        for (Map.Entry<String, Object> opEntry : operatorQuery.entrySet()) {
            String operator = opEntry.getKey();
            Object value = opEntry.getValue();

            switch (operator) {
                case "$gt" -> {
                    if (doc.getInteger(field) <= ((Number) value).intValue()) {
                        return false;
                    }
                }
                case "$lt" -> {
                    if (doc.getInteger(field) >= ((Number) value).intValue()) {
                        return false;
                    }
                }
                case "$in" -> {
                    if (!((List<?>) value).contains(doc.get(field))) {
                        return false;
                    }
                }
                default ->
                    throw new UnsupportedOperationException("Operador no soportado: " + operator);
            }
        }
        return true;
    }

    /**
     * Crea un índice compuesto.
     *
     * @param fields Campos a indexar (ej: {"name": 1, "age": -1}).
     */
    public void createCompoundIndex(Document fields) {
        String indexName = fields.keySet().stream().collect(Collectors.joining("_"));
        compoundIndices.putIfAbsent(indexName, new ConcurrentHashMap<>());

        // Indexar documentos existentes
        database.forEach(doc -> {
            String id = doc.getString("_id");
            Object indexKey = generateCompoundKey(doc, fields);
            compoundIndices.get(indexName).computeIfAbsent(indexKey, k -> new HashSet<>()).add(id);
        });
    }

    private Object generateCompoundKey(Document doc, Document fields) {
        List<Object> keyParts = new ArrayList<>();
        // Iterar sobre las entradas del Document 'fields'
        fields.forEach((field, order) -> {
            keyParts.add(doc.get(field));
        });
        return keyParts; // Retorna lista como clave compuesta
    }

    /**
     * Búsqueda usando índice compuesto.
     *
     * @param indexName
     * @param key
     * @return
     */
    public List<Document> findByCompoundIndex(String indexName, Object key) {
        if (!compoundIndices.containsKey(indexName)) {
            return Collections.emptyList();
        }

        return compoundIndices.get(indexName).getOrDefault(key, Collections.emptySet()).stream()
                .map(this::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Cierra la base de datos de manera segura, garantizando la persistencia de
     * los datos y liberando recursos.
     *
     */
    @Override
    public synchronized void close() {
        if (isClosed) {
            return;
        }

        saveToDisk();
        // Limpieza de recursos
        if (database != null) {
            database.clear();
        }
        if (indices != null) {
            indices.clear();
        }
        if (compoundIndices != null) {
            compoundIndices.clear();
        }
        isClosed = true;
    }

    public String getDbFile() {
        return dbFile;
    }

    public void setDbFile(String dbFile) {
        this.dbFile = dbFile;
    }

}
