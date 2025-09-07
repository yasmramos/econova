package com.univsoftdev.econova.db.postgres;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Utilidad para realizar operaciones de backup y restore de bases de datos PostgreSQL.
 * 
 * <p>Esta clase proporciona funcionalidades para:
 * <ul>
 *   <li>Crear backups de bases de datos PostgreSQL usando {@code pg_dump}</li>
 *   <li>Restaurar backups de bases de datos usando {@code pg_restore}</li>
 *   <li>Manejo automático de rutas de ejecutables de PostgreSQL</li>
 *   <li>Gestión de credenciales y variables de entorno</li>
 * </ul>
 * </p>
 * 
 * <p>Los backups se realizan en formato personalizado de PostgreSQL (-F c) 
 * con blobs incluidos (-b) para una restauración completa.</p>
 * 
 * @author UnivSoftDev Team
 * @version 1.0
 * @since 1.0
 * 
 * @see ProcessBuilder
 * @see PostgreSQLConnection
 */
@Slf4j
public class PostgreSQLBackup {

    /** Nombre de usuario para la conexión PostgreSQL. */
    private final String userName;
    
    /** Contraseña para la conexión PostgreSQL. */
    private final String password;
    
    /** Nombre de la base de datos por defecto a respaldar/restaurar. */
    private final String defaultDatabase;
    
    /** Ruta completa al archivo de backup. */
    private final String backupFilePath;
    
    /** 
     * Ruta en caché al directorio bin de PostgreSQL.
     * Se inicializa una vez para mejorar el rendimiento.
     */
    private String pgBinPath;

    /**
     * Constructor para crear una nueva instancia de PostgreSQLBackup.
     * 
     * @param userName Nombre de usuario de PostgreSQL (no null ni vacío)
     * @param password Contraseña de PostgreSQL (puede ser null)
     * @param defaultDatabase Nombre de la base de datos a respaldar/restaurar
     * @param backupFilePath Ruta completa donde se guardará el backup
     * 
     * @throws IllegalArgumentException si userName o defaultDatabase son null/vacíos
     */
    public PostgreSQLBackup(String userName, String password, String defaultDatabase, String backupFilePath) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (defaultDatabase == null || defaultDatabase.trim().isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty");
        }
        if (backupFilePath == null || backupFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Backup file path cannot be null or empty");
        }
        
        this.userName = userName;
        this.password = password;
        this.defaultDatabase = defaultDatabase;
        this.backupFilePath = backupFilePath;
    }

    /**
     * Realiza un backup de la base de datos configurada.
     * 
     * <p>Utiliza {@code pg_dump} con las siguientes opciones:
     * <ul>
     *   <li>-F c: Formato personalizado de PostgreSQL</li>
     *   <li>-b: Incluir blobs grandes</li>
     *   <li>-v: Modo verbose</li>
     *   <li>-f: Archivo de salida</li>
     * </ul>
     * </p>
     * 
     * @throws IOException si ocurre un error de E/S durante la ejecución
     * @throws InterruptedException si el proceso es interrumpido
     * @throws RuntimeException si el backup falla
     * 
     * @see #executeCommand(String, String)
     * @see #ensureBackupDirectoryExists()
     */
    public void backup() throws IOException, InterruptedException {
        ensureBackupDirectoryExists();

        String pgDumpPath = getPostgreSqlBinPath() + (isWindows() ? "pg_dump.exe" : "pg_dump");
        log.info("Using pg_dump path: {}", pgDumpPath);

        String command;
        if (getPostgreSqlBinPath().isEmpty()) {
            // PostgreSQL está en el PATH del sistema
            command = String.format("pg_dump -U %s -d %s -F c -b -v -f \"%s\"",
                    userName, defaultDatabase, backupFilePath);
        } else {
            // PostgreSQL en ruta específica
            command = String.format("\"%s\" -U %s -d %s -F c -b -v -f \"%s\"",
                    pgDumpPath, userName, defaultDatabase, backupFilePath);
        }
        
        executeCommand(command, "Backup");
        log.info("Backup completed successfully to: {}", backupFilePath);
    }

    /**
     * Restaura una base de datos desde un archivo de backup.
     * 
     * <p>Utiliza {@code pg_restore} con las siguientes opciones:
     * <ul>
     *   <li>-U: Usuario de conexión</li>
     *   <li>-d: Base de datos destino</li>
     *   <li>-v: Modo verbose</li>
     * </ul>
     * </p>
     * 
     * <p><strong>Nota:</strong> La base de datos destino debe existir 
     * antes de realizar la restauración.</p>
     * 
     * @throws IOException si el archivo de backup no existe o hay error de E/S
     * @throws InterruptedException si el proceso es interrumpido
     * @throws RuntimeException si la restauración falla
     * 
     * @see #executeCommand(String, String)
     */
    public void restore() throws IOException, InterruptedException {
        File file = new File(backupFilePath);
        if (!file.exists()) {
            throw new IOException("Backup file not found: " + backupFilePath);
        }

        String pgRestorePath = getPostgreSqlBinPath() + (isWindows() ? "pg_restore.exe" : "pg_restore");
        log.info("Using pg_restore path: {}", pgRestorePath);

        String command;
        if (getPostgreSqlBinPath().isEmpty()) {
            // PostgreSQL está en el PATH del sistema
            command = String.format("pg_restore -U %s -d %s -v \"%s\"",
                    userName, defaultDatabase, backupFilePath);
        } else {
            // PostgreSQL en ruta específica
            command = String.format("\"%s\" -U %s -d %s -v \"%s\"",
                    pgRestorePath, userName, defaultDatabase, backupFilePath);
        }
        
        executeCommand(command, "Restore");
        log.info("Restore completed successfully from: {}", backupFilePath);
    }

    /**
     * Asegura que el directorio para el archivo de backup exista.
     * 
     * <p>Verifica si el directorio padre del archivo de backup existe,
     * y si no, intenta crearlo recursivamente.</p>
     * 
     * @throws IOException si no se puede crear el directorio
     */
    private void ensureBackupDirectoryExists() throws IOException {
        File backupFile = new File(backupFilePath);
        File parentDir = backupFile.getParentFile();

        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("Failed to create backup directory: " + parentDir.getAbsolutePath());
        }
    }

    /**
     * Ejecuta un comando del sistema operativo de forma segura.
     * 
     * <p>Este método:
     * <ul>
     *   <li>Establece la variable de entorno PGPASSWORD para autenticación</li>
     *   <li>Redirige la salida de error a la salida estándar</li>
     *   <li>Registra toda la salida del comando</li>
     *   <li>Verifica el código de salida para determinar éxito/fracaso</li>
     * </ul>
     * </p>
     * 
     * @param command El comando a ejecutar
     * @param operation Nombre de la operación para mensajes de error (ej: "Backup", "Restore")
     * @throws IOException si ocurre un error de E/S
     * @throws InterruptedException si el proceso es interrumpido
     * @throws RuntimeException si el comando falla (código de salida != 0)
     */
    private void executeCommand(String command, String operation) throws IOException, InterruptedException {
        // Dividir el comando correctamente para ProcessBuilder
        ProcessBuilder processBuilder;
        if (isWindows()) {
            // En Windows, usamos cmd.exe con el comando entre comillas
            processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        } else {
            // En Unix/Linux, usamos sh -c con el comando entre comillas
            processBuilder = new ProcessBuilder("sh", "-c", command);
        }

        // Establecer la contraseña de PostgreSQL como variable de entorno
        if (password != null && !password.isEmpty()) {
            processBuilder.environment().put("PGPASSWORD", password);
        }
        processBuilder.redirectErrorStream(true);

        // Loggear el comando que se ejecutará (útil para debugging)
        log.info("Executing {} command: {}", operation, command);

        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("{} output: {}", operation, line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException(operation + " failed with exit code " + exitCode);
        }
    }

    /**
     * Obtiene la ruta al directorio bin de PostgreSQL.
     * 
     * <p>Este método primero verifica si los comandos de PostgreSQL están
     * disponibles en el PATH del sistema. Si no, busca en las ubicaciones
     * de instalación comunes.</p>
     * 
     * @return La ruta al directorio bin de PostgreSQL, o string vacío si está en PATH
     * @throws RuntimeException si PostgreSQL no se encuentra
     * 
     * @see PostgresSql#getPostgreSqlBinPath()
     */
    private synchronized String getPostgreSqlBinPath() {
        if (pgBinPath != null) {
            return pgBinPath;
        }

        // Check if commands are in system PATH
        if (isCommandAvailable("pg_dump")) {
            pgBinPath = "";
            return pgBinPath;
        }

        // List of common PostgreSQL installation paths
        List<String> possiblePaths = Arrays.asList(
                pathWithVersionWindows(17),
                pathWithVersionWindows(16),
                pathWithVersionWindows(15),
                pathWithVersionWindows(14),
                pathWithVersionWindows(13),
                pathWithVersionWindows(12),
                pathWithVersionWindows(11),
                pathWithVersionWindows(10),
                "/usr/lib/postgresql/17/bin/",
                "/usr/lib/postgresql/16/bin/",
                "/usr/lib/postgresql/15/bin/",
                "/usr/lib/postgresql/14/bin/",
                "/usr/lib/postgresql/13/bin/",
                "/usr/lib/postgresql/12/bin/",
                "/usr/lib/postgresql/11/bin/",
                "/usr/lib/postgresql/10/bin/",
                "/usr/local/pgsql/bin/",
                "/opt/local/lib/postgresql17/bin/",
                "/opt/local/lib/postgresql16/bin/",
                "/opt/local/lib/postgresql15/bin/",
                "/opt/local/lib/postgresql14/bin/",
                "/opt/local/lib/postgresql13/bin/",
                "/opt/local/lib/postgresql12/bin/",
                "/opt/local/lib/postgresql11/bin/",
                "/opt/local/lib/postgresql10/bin/"
        );

        for (String path : possiblePaths) {
            String pgDumpPath = path + (isWindows() ? "pg_dump.exe" : "pg_dump");
            if (new File(pgDumpPath).exists()) {
                pgBinPath = path;
                return pgBinPath;
            }
        }

        throw new RuntimeException("PostgreSQL installation not found. Tried paths:\n"
                + String.join("\n", possiblePaths)
                + "\n\nPlease ensure PostgreSQL is installed or add the 'bin' folder to system PATH.");
    }

    /**
     * Construye la ruta de instalación estándar de PostgreSQL en Windows.
     * 
     * <p>La ruta sigue el patrón: {@code C:\Program Files\PostgreSQL\{version}\bin\}</p>
     * 
     * @param version El número de versión de PostgreSQL (ej: 15, 16, etc.)
     * @return La ruta al directorio bin de la versión especificada
     */
    public String pathWithVersionWindows(int version) {
        return "C:" + File.separator + "Program Files" + File.separator + 
               "PostgreSQL" + File.separator + version + File.separator + "bin" + File.separator;
    }

    /**
     * Determina si el sistema operativo actual es Windows.
     * 
     * @return {@code true} si el sistema es Windows, {@code false} en caso contrario
     */
    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * Verifica si un comando está disponible en el PATH del sistema.
     * 
     * <p>Utiliza {@code where} en Windows y {@code which} en sistemas Unix para
     * determinar si el comando especificado está disponible.</p>
     * 
     * @param command El nombre del comando a verificar (sin extensión .exe en Windows)
     * @return {@code true} si el comando está disponible, {@code false} en caso contrario
     */
    private boolean isCommandAvailable(String command) {
        try {
            ProcessBuilder pb;
            if (isWindows()) {
                pb = new ProcessBuilder("where", command);
            } else {
                pb = new ProcessBuilder("which", command);
            }
            
            Process process = pb.start();
            int result = process.waitFor();
            return result == 0;
        } catch (IOException | InterruptedException e) {
            log.debug("Command '{}' is not available: {}", command, e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupt status
            return false;
        }
    }

    /**
     * Obtiene el nombre de usuario configurado.
     * 
     * @return El nombre de usuario de PostgreSQL
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Obtiene la contraseña configurada.
     * 
     * @return La contraseña de PostgreSQL (puede ser null)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Obtiene el nombre de la base de datos por defecto.
     * 
     * @return El nombre de la base de datos configurada
     */
    public String getDefaultDatabase() {
        return defaultDatabase;
    }

    /**
     * Obtiene la ruta del archivo de backup.
     * 
     * @return La ruta completa al archivo de backup
     */
    public String getBackupFilePath() {
        return backupFilePath;
    }
}