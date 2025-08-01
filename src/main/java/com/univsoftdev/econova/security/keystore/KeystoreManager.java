package com.univsoftdev.econova.security.keystore;

import com.univsoftdev.econova.security.PasswordGenerator;
import com.univsoftdev.econova.security.keystore.model.KeystoreBackup;
import com.univsoftdev.econova.core.config.AppConfig;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.security.*;
import java.security.KeyStore.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Gestor de keystore para almacenamiento seguro de contraseñas sensibles.
 * 
 * <p>Esta clase proporciona un sistema robusto para almacenar y recuperar contraseñas
 * de forma segura utilizando un keystore JCEKS. Implementa múltiples mecanismos
 * de persistencia y recuperación para garantizar la disponibilidad de los datos:</p>
 * 
 * <ul>
 *   <li>Almacenamiento local en archivo JCEKS</li>
 *   <li>Backup local automático</li>
 *   <li>Backup en base de datos PostgreSQL</li>
 *   <li>Recuperación automática desde diferentes fuentes</li>
 * </ul>
 * 
 * <p><strong>Características de seguridad:</strong></p>
 * <ul>
 *   <li>Uso de JCEKS para almacenamiento seguro de SecretKeys</li>
 *   <li>Encriptación PBE con SHA256 y AES_256 para contraseñas almacenadas</li>
 *   <li>Permisos restrictivos en sistemas Unix (solo propietario)</li>
 *   <li>Limpieza automática de contraseñas en memoria</li>
 *   <li>Backup programado cada hora</li>
 * </ul>
 * 
 * @author UnivSoftDev Team
 * @version 1.0
 * @since 1.0
 * 
 * @see KeyStore
 * @see PBEKeySpec
 * @see KeystoreBackup
 */
public class KeystoreManager {

    /**
     * Tipo de keystore utilizado para almacenamiento seguro.
     * JCEKS permite almacenar SecretKeys de forma segura.
     */
    private static final String KEYSTORE_TYPE = "JCEKS";
    
    /**
     * Nombre del archivo principal del keystore.
     */
    private static final String KEYSTORE_FILE = "contable_keystore.jceks";
    
    /**
     * Nombre del archivo de backup local del keystore.
     */
    private static final String BACKUP_FILE = "contable_keystore.backup";
    
    /**
     * Intervalo de backup automático en milisegundos (1 hora).
     */
    private static final long BACKUP_INTERVAL = TimeUnit.HOURS.toMillis(1);

    /**
     * Ruta al archivo principal del keystore.
     */
    private final Path keystorePath;
    
    /**
     * Ruta al archivo de backup del keystore.
     */
    private final Path backupPath;
    
    /**
     * Contraseña maestra para proteger el keystore.
     * Se mantiene como copia para seguridad.
     */
    private final char[] masterPassword;
    
    /**
     * Instancia del keystore JCEKS gestionado.
     */
    private KeyStore keyStore;
    
    /**
     * Scheduler para tareas programadas de backup.
     */
    private final ScheduledExecutorService scheduler;

    /**
     * Constructor que inicializa el gestor de keystore con una contraseña maestra.
     * 
     * <p>Este constructor:
     * <ol>
     *   <li>Crea una copia segura de la contraseña maestra</li>
     *   <li>Determina las rutas de almacenamiento según el sistema operativo</li>
     *   <li>Inicializa el keystore (carga existente o crea nuevo)</li>
     *   <li>Inicia el scheduler de backups</li>
     * </ol>
     * </p>
     * 
     * @param masterPassword Contraseña maestra para proteger el keystore (no null)
     * @throws IllegalArgumentException si masterPassword es null
     * @throws RuntimeException si ocurre un error durante la inicialización
     * 
     * @see #initialize()
     * @see #getAppConfigDir()
     */
    public KeystoreManager(char[] masterPassword) {
        if (masterPassword == null) {
            throw new IllegalArgumentException("Master password cannot be null");
        }
        
        this.masterPassword = Arrays.copyOf(masterPassword, masterPassword.length);
        this.keystorePath = getAppConfigDir().resolve(KEYSTORE_FILE);
        this.backupPath = getAppConfigDir().resolve(BACKUP_FILE);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        initialize();
    }

    /**
     * Obtiene el directorio de configuración de la aplicación según el sistema operativo.
     * 
     * <p>Ubicaciones por sistema:
     * <ul>
     *   <li><strong>Windows:</strong> %APPDATA%\AppName</li>
     *   <li><strong>macOS:</strong> ~/Library/Application Support/AppName</li>
     *   <li><strong>Linux/Unix:</strong> ~/.appname</li>
     * </ul>
     * </p>
     * 
     * <p>Si el directorio no existe, se crea con permisos apropiados.</p>
     * 
     * @return Path al directorio de configuración de la aplicación
     * @throws RuntimeException si no se puede crear el directorio
     * 
     * @see #setDirectoryPermissions(Path)
     * @see AppConfig#getAppName()
     */
    private Path getAppConfigDir() {
        String os = System.getProperty("os.name").toLowerCase();
        Path path;
        
        if (os.contains("win")) {
            path = Paths.get(System.getenv("APPDATA"), AppConfig.getAppName());
        } else if (os.contains("mac")) {
            path = Paths.get(System.getProperty("user.home"), "Library", "Application Support", AppConfig.getAppName());
        } else {
            path = Paths.get(System.getProperty("user.home"), "." + AppConfig.getAppName().toLowerCase());
        }
        
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                setDirectoryPermissions(path);
            }
            return path;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear directorio de configuración", e);
        }
    }

    /**
     * Establece permisos restrictivos en directorios en sistemas Unix.
     * 
     * <p>Configura permisos 700 (rwx------) para el directorio,
     * permitiendo acceso solo al propietario.</p>
     * 
     * @param path Directorio al que aplicar permisos
     * @throws IOException si ocurre un error al establecer permisos
     * 
     * @see PosixFilePermission
     */
    private void setDirectoryPermissions(Path path) throws IOException {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            Files.setPosixFilePermissions(path, perms);
        }
    }

    /**
     * Inicializa el keystore intentando cargarlo desde múltiples fuentes.
     * 
     * <p>Orden de intentos de carga:
     * <ol>
     *   <li>Archivo principal local</li>
     *   <li>Archivo de backup local</li>
     *   <li>Backup en base de datos PostgreSQL</li>
     *   <li>Crear nuevo keystore si no existe ninguno</li>
     * </ol>
     * </p>
     * 
     * @throws RuntimeException si ocurre un error durante la inicialización
     * 
     * @see #loadFromFile(Path)
     * @see #loadFromBytes(byte[])
     * @see #saveKeyStore()
     * @see #startBackupScheduler()
     */
    private void initialize() {
        try {
            this.keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            
            // Intenta cargar desde archivo local principal
            if (Files.exists(keystorePath)) {
                loadFromFile(keystorePath);
                startBackupScheduler();
                return;
            }
            
            // Intenta cargar desde archivo de backup local
            if (Files.exists(backupPath)) {
                loadFromFile(backupPath);
                saveKeyStore(); // Restaura el archivo principal
                startBackupScheduler();
                return;
            }
            
            // Intenta cargar desde PostgreSQL
            Optional<KeystoreBackup> dbBackup = KeystoreBackup.findCurrent();
            if (dbBackup.isPresent()) {
                loadFromBytes(dbBackup.get().getKeystoreData());
                saveKeyStore(); // Crea archivo local
                startBackupScheduler();
                return;
            }
            
            // Crea un nuevo keystore
            keyStore.load(null, masterPassword);
            saveKeyStore();
            startBackupScheduler();
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar el keystore", e);
        }
    }

    /**
     * Carga el keystore desde un archivo.
     * 
     * @param path Ruta al archivo del keystore
     * @throws Exception si ocurre un error durante la carga
     * 
     * @see KeyStore#load(InputStream, char[])
     */
    private void loadFromFile(Path path) throws Exception {
        try (InputStream is = Files.newInputStream(path)) {
            keyStore.load(is, masterPassword);
        }
    }

    /**
     * Carga el keystore desde un array de bytes.
     * 
     * @param data Datos serializados del keystore
     * @throws Exception si ocurre un error durante la carga
     * 
     * @see KeyStore#load(InputStream, char[])
     */
    private void loadFromBytes(byte[] data) throws Exception {
        try (InputStream is = new ByteArrayInputStream(data)) {
            keyStore.load(is, masterPassword);
        }
    }

    /**
     * Inicia el scheduler para backups programados.
     * 
     * <p>Programa la tarea de backup para ejecutarse cada hora,
     * comenzando después de una hora de la inicialización.</p>
     * 
     * @see #backupKeystore()
     * @see ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)
     */
    private void startBackupScheduler() {
        scheduler.scheduleAtFixedRate(this::backupKeystore,
                BACKUP_INTERVAL, BACKUP_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * Guarda el keystore en todas las ubicaciones de persistencia.
     * 
     * <p>Este método guarda el keystore en:
     * <ol>
     *   <li>Archivo principal local</li>
     *   <li>Archivo de backup local</li>
     *   <li>Base de datos PostgreSQL como backup</li>
     * </ol>
     * </p>
     * 
     * @throws RuntimeException si ocurre un error durante el guardado
     * 
     * @see #saveToFile(Path)
     * @see KeystoreBackup#updateBackup(byte[])
         */
    public synchronized void saveKeyStore() {
        try {
            // Guardar en archivo principal
            saveToFile(keystorePath);
            
            // Crear backup local
            Files.copy(keystorePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            setFilePermissions(backupPath);
            
            // Backup en PostgreSQL
            byte[] keystoreData = Files.readAllBytes(keystorePath);
            KeystoreBackup.updateBackup(keystoreData);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el keystore", e);
        }
    }

    /**
     * Guarda el keystore en un archivo específico.
     * 
     * @param path Ruta donde guardar el keystore
     * @throws Exception si ocurre un error durante el guardado
     * 
     * @see KeyStore#store(OutputStream, char[])
     * @see #setFilePermissions(Path)
     */
    private void saveToFile(Path path) throws Exception {
        try (OutputStream os = Files.newOutputStream(path,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            keyStore.store(os, masterPassword);
            setFilePermissions(path);
        }
    }

    /**
     * Establece permisos restrictivos en archivos en sistemas Unix.
     * 
     * <p>Configura permisos 600 (rw-------) para el archivo,
     * permitiendo acceso solo al propietario.</p>
     * 
     * @param path Archivo al que aplicar permisos
     * @throws IOException si ocurre un error al establecer permisos
     * 
     * @see PosixFilePermission
     */
    private void setFilePermissions(Path path) throws IOException {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(path, perms);
        }
    }

    /**
     * Realiza un backup del keystore en la base de datos.
     * 
     * <p>Este método se ejecuta periódicamente por el scheduler
     * para mantener actualizado el backup en la base de datos.</p>
     * 
     * @see KeystoreBackup#updateBackup(byte[])
     */
    private void backupKeystore() {
        try {
            byte[] keystoreData = Files.readAllBytes(keystorePath);
            KeystoreBackup.updateBackup(keystoreData);
        } catch (IOException e) {
            System.err.println("Error en backup programado: " + e.getMessage());
        }
    }

    /**
     * Almacena una contraseña en el keystore.
     * 
     * <p>La contraseña se convierte en una SecretKey usando PBE
     * con SHA256 y AES_256 antes de ser almacenada en el keystore.</p>
     * 
     * @param alias Nombre único para identificar la contraseña
     * @param password Contraseña a almacenar (será limpiada automáticamente)
     * @throws RuntimeException si ocurre un error durante el almacenamiento
     * 
     * @see #saveKeyStore()
     * @see PasswordGenerator#clearPassword(char[])
     */
    public void storePassword(String alias, char[] password) {
        try {
            PBEKeySpec keySpec = new PBEKeySpec(password);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_256");
            SecretKey secretKey = keyFactory.generateSecret(keySpec);
            SecretKeyEntry secretKeyEntry = new SecretKeyEntry(secretKey);
            ProtectionParameter protectionParam = new PasswordProtection(masterPassword);
            keyStore.setEntry(alias, secretKeyEntry, protectionParam);
            saveKeyStore();
        } catch (KeyStoreException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al guardar contraseña", e);
        } finally {
            PasswordGenerator.clearPassword(password);
        }
    }

    /**
     * Recupera una contraseña previamente almacenada.
     * 
     * @param alias Nombre de la contraseña a recuperar
     * @return Array de caracteres con la contraseña, o null si no existe
     * @throws RuntimeException si ocurre un error durante la recuperación
     */
    public char[] retrievePassword(String alias) {
        try {
            ProtectionParameter protectionParam = new PasswordProtection(masterPassword);
            SecretKeyEntry entry = (SecretKeyEntry) keyStore.getEntry(alias, protectionParam);
            if (entry == null) {
                return null;
            }
            SecretKey secretKey = entry.getSecretKey();
            PBEKeySpec keySpec = (PBEKeySpec) SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_256")
                    .getKeySpec(secretKey, PBEKeySpec.class);
            return keySpec.getPassword();
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al recuperar contraseña", e);
        }
    }

    /**
     * Elimina una contraseña del keystore.
     * 
     * @param alias Nombre de la contraseña a eliminar
     * @throws RuntimeException si ocurre un error durante la eliminación
     * 
     * @see #saveKeyStore()
     */
    public void deletePassword(String alias) {
        try {
            keyStore.deleteEntry(alias);
            saveKeyStore();
        } catch (KeyStoreException e) {
            throw new RuntimeException("Error al eliminar contraseña", e);
        }
    }

    /**
     * Lista todos los aliases de contraseñas almacenadas.
     * 
     * @return Lista con los nombres de todas las contraseñas almacenadas
     * @throws RuntimeException si ocurre un error durante la enumeración
     */
    public List<String> listPasswordAliases() {
        try {
            Enumeration<String> aliases = keyStore.aliases();
            List<String> aliasList = new ArrayList<>();
            while (aliases.hasMoreElements()) {
                aliasList.add(aliases.nextElement());
            }
            return aliasList;
        } catch (KeyStoreException e) {
            throw new RuntimeException("Error al listar contraseñas", e);
        }
    }

    /**
     * Cierra el gestor de keystore liberando recursos.
     * 
     * <p>Este método:
     * <ol>
     *   <li>Detiene el scheduler de backups</li>
     *   <li>Espera la finalización ordenada de tareas</li>
     *   <li>Limpia la contraseña maestra de memoria</li>
     * </ol>
     * </p>
     * 
     * @see ScheduledExecutorService#shutdown()
     * @see ScheduledExecutorService#shutdownNow()
     * @see PasswordGenerator#clearPassword(char[])
     */
    public void close() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        PasswordGenerator.clearPassword(masterPassword);
    }

    /**
     * Migra datos desde una versión anterior del keystore.
     * 
     * <p>Este método permite migrar entradas desde un keystore
     * de tipo JKS a la implementación actual JCEKS.</p>
     * 
     * <p>Después de la migración, el keystore antiguo se renombra
     * como backup y se guarda el nuevo keystore actualizado.</p>
     * 
     * @param oldKeystorePath Ruta al keystore antiguo
     * @param oldPassword Contraseña del keystore antiguo (será limpiada)
     * @throws RuntimeException si ocurre un error durante la migración
     * 
     * @see #saveKeyStore()
     * @see PasswordGenerator#clearPassword(char[])
     */
    public void migrateFromOldVersion(Path oldKeystorePath, char[] oldPassword) {
        try {
            KeyStore oldKs = KeyStore.getInstance("JKS");
            try (InputStream is = Files.newInputStream(oldKeystorePath)) {
                oldKs.load(is, oldPassword);
            }
            
            Enumeration<String> aliases = oldKs.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (oldKs.isKeyEntry(alias)) {
                    KeyStore.Entry entry = oldKs.getEntry(alias,
                            new PasswordProtection(oldPassword));
                    keyStore.setEntry(alias, entry,
                            new PasswordProtection(masterPassword));
                }
            }
            
            saveKeyStore();
            Files.move(oldKeystorePath,
                    oldKeystorePath.resolveSibling("old_keystore.backup"),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException | CertificateException e) {
            throw new RuntimeException("Error en migración", e);
        } finally {
            PasswordGenerator.clearPassword(oldPassword);
        }
    }
    
    /**
     * Obtiene la ruta al archivo principal del keystore.
     * 
     * @return Path al archivo del keystore
     */
    public Path getKeystorePath() {
        return keystorePath;
    }
    
    /**
     * Obtiene la ruta al archivo de backup del keystore.
     * 
     * @return Path al archivo de backup
     */
    public Path getBackupPath() {
        return backupPath;
    }
    
    /**
     * Verifica si el keystore contiene una entrada con el alias especificado.
     * 
     * @param alias Alias a verificar
     * @return {@code true} si existe una entrada con ese alias, {@code false} en caso contrario
     * @throws RuntimeException si ocurre un error durante la verificación
     */
    public boolean containsAlias(String alias) {
        try {
            return keyStore.containsAlias(alias);
        } catch (KeyStoreException e) {
            throw new RuntimeException("Error al verificar alias", e);
        }
    }
}
