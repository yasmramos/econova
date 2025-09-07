package com.univsoftdev.econova.component.wizard;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WizardDataOutput {

    private static final String WIZARD_DATA_FILE = "wizard.dat";
    private static final Path WIZARD_DATA_PATH = Paths.get(WIZARD_DATA_FILE);

    /**
     * Guarda un objeto serializable en un archivo.
     *
     * @param object el objeto a guardar (debe implementar Serializable)
     * @return true si se guardó correctamente, false en caso contrario
     */
    public static boolean saveObject(Object object) {
        if (object == null) {
            log.warn("Intento de guardar objeto null");
            return false;
        }

        // Verificar que el objeto sea serializable
        if (!(object instanceof Serializable)) {
            log.error("El objeto no implementa Serializable: {}", object.getClass().getName());
            return false;
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(WIZARD_DATA_FILE))) {
            out.writeObject(object);
            out.flush();
            log.debug("Objeto guardado exitosamente en: {}", WIZARD_DATA_FILE);
            return true;

        } catch (FileNotFoundException ex) {
            log.error("No se pudo crear el archivo para guardar datos: {}", WIZARD_DATA_FILE, ex);
            return false;
        } catch (IOException ex) {
            log.error("Error de E/S al guardar objeto en: {}", WIZARD_DATA_FILE, ex);
            return false;
        } catch (Exception ex) {
            log.error("Error inesperado al guardar objeto", ex);
            return false;
        }
    }

    /**
     * Lee un objeto serializable desde un archivo.
     *
     * @param <T> el tipo del objeto esperado
     * @param expectedClass la clase esperada del objeto
     * @return el objeto leído, o null si hubo error o no existe el archivo
     */
    @SuppressWarnings("unchecked")
    public static <T> T readObject(Class<T> expectedClass) {
        // Verificar que el archivo exista
        if (!Files.exists(WIZARD_DATA_PATH)) {
            log.debug("Archivo de datos no encontrado: {}", WIZARD_DATA_FILE);
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(WIZARD_DATA_FILE))) {
            Object object = in.readObject();

            // Verificar que el objeto sea del tipo esperado
            if (object != null && expectedClass.isInstance(object)) {
                log.debug("Objeto leído exitosamente de: {} - Tipo: {}",
                        WIZARD_DATA_FILE, object.getClass().getSimpleName());
                return (T) object;
            } else if (object != null) {
                log.warn("Objeto leído no es del tipo esperado. Esperado: {}, Obtenido: {}",
                        expectedClass.getSimpleName(),
                        object.getClass().getSimpleName());
                return null;
            } else {
                log.warn("Objeto leído es null");
                return null;
            }

        } catch (FileNotFoundException ex) {
            log.error("Archivo no encontrado al leer datos: {}", WIZARD_DATA_FILE, ex);
            return null;
        } catch (ClassNotFoundException ex) {
            log.error("Clase no encontrada al leer objeto de: {}", WIZARD_DATA_FILE, ex);
            return null;
        } catch (IOException ex) {
            log.error("Error de E/S al leer objeto de: {}", WIZARD_DATA_FILE, ex);
            return null;
        } catch (Exception ex) {
            log.error("Error inesperado al leer objeto", ex);
            return null;
        }
    }

    /**
     * Lee un objeto sin verificar el tipo (uso con precaución).
     *
     * @return el objeto leído, o null si hubo error
     */
    public static Object readObject() {
        return readObject(Object.class);
    }

    /**
     * Elimina el archivo de datos del wizard.
     *
     * @return true si se eliminó correctamente o no existía, false si hubo
     * error
     */
    public static boolean deleteDataFile() {
        try {
            if (Files.exists(WIZARD_DATA_PATH)) {
                Files.delete(WIZARD_DATA_PATH);
                log.debug("Archivo de datos eliminado: {}", WIZARD_DATA_FILE);
                return true;
            } else {
                log.debug("Archivo de datos no existe, no se requiere eliminación: {}", WIZARD_DATA_FILE);
                return true;
            }
        } catch (IOException ex) {
            log.error("Error al eliminar archivo de datos: {}", WIZARD_DATA_FILE, ex);
            return false;
        } catch (Exception ex) {
            log.error("Error inesperado al eliminar archivo de datos", ex);
            return false;
        }
    }

    /**
     * Verifica si existe el archivo de datos del wizard.
     *
     * @return true si existe, false si no
     */
    public static boolean dataFileExists() {
        return Files.exists(WIZARD_DATA_PATH);
    }

    /**
     * Obtiene el tamaño del archivo de datos.
     *
     * @return tamaño en bytes, o -1 si no existe o hay error
     */
    public static long getDataFileSize() {
        try {
            if (Files.exists(WIZARD_DATA_PATH)) {
                return Files.size(WIZARD_DATA_PATH);
            }
        } catch (IOException ex) {
            log.error("Error al obtener tamaño del archivo de datos", ex);
        }
        return -1;
    }
}
