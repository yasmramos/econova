package com.univsoftdev.econova.seguridad;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostgreSQLBackup {

    private final String userName;
    private final String password;
    private final String defaultDatabase;
    private final String backupFilePath;
    private String pgBinPath; // Cache for PostgreSQL binary path

    public PostgreSQLBackup(String userName, String password, String defaultDatabase, String backupFilePath) {
        this.userName = userName;
        this.password = password;
        this.defaultDatabase = defaultDatabase;
        this.backupFilePath = backupFilePath;
    }

    public void backup() throws IOException, InterruptedException {
        ensureBackupDirectoryExists();

        String pgDumpPath = getPostgreSqlBinPath() + (isWindows() ? "pg_dump.exe" : "pg_dump");
        log.info("Using pg_dump path: {}", pgDumpPath);

        String command;

        if ("pg_dump.exe".equals(pgDumpPath)) {
            command = String.format("%s -U %s -d %s -F c -b -v -f \"%s\"",
                    pgDumpPath, userName, defaultDatabase, backupFilePath);

        } else {
            // Comillas alrededor de la ruta y del archivo de salida
            command = String.format("\"%s\" -U %s -d %s -F c -b -v -f \"%s\"",
                    pgDumpPath, userName, defaultDatabase, backupFilePath);
        }
        executeCommand(command, "Backup");
        log.info("Backup completed successfully to: {}", backupFilePath);
    }

    public void restore() throws IOException, InterruptedException {
        File file = new File(backupFilePath);
        if (!file.exists()) {
            throw new IOException("Backup file not found: " + backupFilePath);
        }

        String pgRestorePath = getPostgreSqlBinPath() + (isWindows() ? "pg_restore.exe" : "pg_restore");
        log.info("Using pg_restore path: {}", pgRestorePath);

        String command;
        if ("pg_dump.exe".equals(pgRestorePath)) {
            command = String.format("%s -U %s -d %s -v \"%s\"",
                    pgRestorePath, userName, defaultDatabase, backupFilePath);
        } else {
            // Comillas alrededor de la ruta y del archivo de entrada
            command = String.format("\"%s\" -U %s -d %s -v \"%s\"",
                    pgRestorePath, userName, defaultDatabase, backupFilePath);
        }
        executeCommand(command, "Restore");
        log.info("Restore completed successfully from: {}", backupFilePath);
    }

    private void ensureBackupDirectoryExists() throws IOException {
        File backupFile = new File(backupFilePath);
        File parentDir = backupFile.getParentFile();

        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("Failed to create backup directory: " + parentDir.getAbsolutePath());
        }
    }

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

        processBuilder.environment().put("PGPASSWORD", password);
        processBuilder.redirectErrorStream(true);

        // Loggear el comando que se ejecutará (útil para debugging)
        log.info("Executing command: {}", command);

        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException(operation + " failed with exit code " + exitCode);
        }
    }

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

    public String pathWithVersionWindows(int version) {
        return "C:" + File.separator + "Program Files" + File.separator + "PostgreSQL" + File.separator + version + File.separator + "bin" + File.separator;
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private boolean isCommandAvailable(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(isWindows() ? "cmd.exe" : "sh",
                    isWindows() ? "/c" : "-c",
                    command + (isWindows() ? ".exe" : "") + " --version");
            Process process = pb.start();
            return process.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            log.debug("El comamdo no esta disponible: " + e.getMessage());
            return false;
        }
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getDefaultDatabase() {
        return defaultDatabase;
    }

    public String getBackupFilePath() {
        return backupFilePath;
    }
}
