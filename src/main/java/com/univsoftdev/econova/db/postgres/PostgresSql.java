package com.univsoftdev.econova.db.postgres;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import java.io.File;

/**
 * Utilidad para localizar la instalación de PostgreSQL en el sistema.
 *
 * <p>
 * Esta clase busca automáticamente PostgreSQL en las ubicaciones de instalación
 * más comunes y también verifica si está disponible en el PATH del sistema.</p>
 *
 * <p>
 * Soporta múltiples versiones de PostgreSQL (10-17) en sistemas Windows y
 * Unix.</p>
 *
 * @author UnivSoftDev Team
 * @version 1.0
 * @since 1.0
 */
@Slf4j
public class PostgresSql {

    /**
     * Ruta en caché al directorio bin de PostgreSQL. Se inicializa una vez y se
     * reutiliza para mejorar el rendimiento.
     */
    private static volatile String pgBinPath;
    /**
     * Nombre de la base de datos actualmente en uso.
     */
    private String currentDatabase;

    /**
     * Servidor PostgreSQL (hostname o IP).
     */
    private final String server;

    /**
     * Puerto de conexión PostgreSQL.
     */
    private final int port;

    /**
     * Nombre de usuario para la conexión.
     */
    private final String userName;

    /**
     * Contraseña para la conexión.
     */
    private final String password;

    /**
     * Objeto de bloqueo para sincronización thread-safe.
     */
    private static final Object LOCK = new Object();

    public PostgresSql(String server, int port, String userName, String password, String defaultDatabase) {
        validateConnectionParams(server, port, userName, defaultDatabase);
        this.server = server;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.currentDatabase = defaultDatabase;
    }

    private void validateConnectionParams(String server, int port,
            String username, String database) {
        if (server == null || server.trim().isEmpty()) {
            throw new IllegalArgumentException("Server cannot be null or empty");
        }
        if (port == 0 || port < 1000) {
            throw new IllegalArgumentException("Port must be a valid number");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (database == null || database.trim().isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty");
        }
    }

    /**
     * Obtiene la ruta al directorio bin de PostgreSQL.
     *
     * <p>
     * Este método primero verifica si la ruta ya ha sido encontrada y
     * almacenada en caché. Si no, busca en las ubicaciones comunes de
     * instalación y en el PATH del sistema.</p>
     *
     * @return La ruta al directorio bin de PostgreSQL terminada en separador de
     * directorio, o string vacío si PostgreSQL está en el PATH del sistema
     * @throws RuntimeException si PostgreSQL no se encuentra en ninguna
     * ubicación
     *
     * @see #findPostgreSqlBinPath()
     */
    public static String getPostgreSqlBinPath() {
        String result = pgBinPath;
        if (result != null) {
            return result;
        }

        synchronized (LOCK) {
            result = pgBinPath;
            if (result != null) {
                return result;
            }

            result = findPostgreSqlBinPath();
            pgBinPath = result;
            return result;
        }
    }

    /**
     * Busca la ruta al directorio bin de PostgreSQL en ubicaciones comunes.
     *
     * <p>
     * Busca en el siguiente orden:
     * <ol>
     * <li>Directorios de instalación estándar de PostgreSQL (versiones
     * 10-17)</li>
     * <li>Rutas comunes en sistemas Unix/Linux</li>
     * <li>Verificación en el PATH del sistema</li>
     * </ol>
     * </p>
     *
     * @return La ruta al directorio bin de PostgreSQL
     * @throws RuntimeException si no se encuentra PostgreSQL en ninguna
     * ubicación
     */
    private static String findPostgreSqlBinPath() {
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
                "/usr/local/bin/",
                "/usr/bin/",
                "/opt/local/lib/postgresql17/bin/",
                "/opt/local/lib/postgresql16/bin/",
                "/opt/local/lib/postgresql15/bin/",
                "/opt/local/lib/postgresql14/bin/",
                "/opt/local/lib/postgresql13/bin/",
                "/opt/local/lib/postgresql12/bin/",
                "/opt/local/lib/postgresql11/bin/",
                "/opt/local/lib/postgresql10/bin/"
        );

        // First, try to find by executable files
        for (String path : possiblePaths) {
            if (pathExists(path)) {
                log.info("PostgreSQL found at: {}", path);
                return path;
            }
        }

        // Check if commands are in system PATH
        if (isCommandAvailable("psql") || isCommandAvailable("postgres")) {
            log.info("PostgreSQL found in system PATH");
            return ""; // Return empty string to use system PATH
        }

        throw new RuntimeException("PostgreSQL installation not found. Tried paths:\n"
                + String.join("\n", possiblePaths)
                + "\n\nPlease ensure PostgreSQL is installed or add the 'bin' folder to system PATH.");
    }

    /**
     * Verifica si existe una instalación válida de PostgreSQL en la ruta
     * especificada.
     *
     * <p>
     * Comprueba que el directorio exista y contenga los ejecutables esenciales
     * de PostgreSQL: {@code postgres} y {@code psql}.</p>
     *
     * @param path La ruta al directorio bin de PostgreSQL a verificar
     * @return {@code true} si la ruta contiene una instalación válida de
     * PostgreSQL, {@code false} en caso contrario
     */
    private static boolean pathExists(String path) {
        try {
            File binDir = new File(path);
            if (!binDir.exists() || !binDir.isDirectory()) {
                return false;
            }

            // Check for essential PostgreSQL executables
            String postgresExe = isWindows() ? "postgres.exe" : "postgres";
            String psqlExe = isWindows() ? "psql.exe" : "psql";

            File postgresFile = new File(binDir, postgresExe);
            File psqlFile = new File(binDir, psqlExe);

            return postgresFile.exists() || psqlFile.exists();
        } catch (Exception e) {
            log.debug("Error checking path {}: {}", path, e.getMessage());
            return false;
        }
    }

    /**
     * Construye la ruta de instalación estándar de PostgreSQL en Windows.
     *
     * <p>
     * La ruta sigue el patrón:
     * {@code C:\Program Files\PostgreSQL\{version}\bin\}</p>
     *
     * @param version El número de versión de PostgreSQL (ej: 15, 16, etc.)
     * @return La ruta al directorio bin de la versión especificada
     */
    private static String pathWithVersionWindows(int version) {
        return "C:" + File.separator + "Program Files" + File.separator
                + "PostgreSQL" + File.separator + version + File.separator + "bin" + File.separator;
    }

    /**
     * Verifica si un comando está disponible en el PATH del sistema.
     *
     * <p>
     * Utiliza {@code where} en Windows y {@code which} en sistemas Unix para
     * determinar si el comando especificado está disponible.</p>
     *
     * @param command El nombre del comando a verificar (sin extensión .exe en
     * Windows)
     * @return {@code true} si el comando está disponible, {@code false} en caso
     * contrario
     */
    private static boolean isCommandAvailable(String command) {
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
     * Determina si el sistema operativo actual es Windows.
     *
     * @return {@code true} si el sistema es Windows, {@code false} en caso
     * contrario
     */
    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * Obtiene la ruta completa a un ejecutable específico de PostgreSQL.
     *
     * <p>
     * Combina la ruta del directorio bin con el nombre del ejecutable,
     * añadiendo la extensión .exe en sistemas Windows si es necesario.</p>
     *
     * @param executable El nombre del ejecutable (ej: "psql", "pg_dump")
     * @return La ruta completa al ejecutable
     *
     * @see #getPostgreSqlBinPath()
     */
    public static String getPostgreSqlExecutable(String executable) {
        String binPath = getPostgreSqlBinPath();
        if (binPath.isEmpty()) {
            // Use system PATH
            return isWindows() ? executable + ".exe" : executable;
        }
        return binPath + (isWindows() ? executable + ".exe" : executable);
    }

    /**
     * Reinicia la caché de la ruta de PostgreSQL.
     *
     * <p>
     * Útil principalmente para pruebas unitarias donde se necesita forzar una
     * nueva búsqueda de la instalación de PostgreSQL.</p>
     */
    public static void reset() {
        synchronized (LOCK) {
            pgBinPath = null;
        }
    }
}
