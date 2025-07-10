package com.univsoftdev.econova.seguridad;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author CNA
 */
@Slf4j
public class PostgresSql {

    private static String pgBinPath;

    public static synchronized String getPostgreSqlBinPath() {
        if (pgBinPath != null) {
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
            String pgPath = path + (isWindows() ? "postgres.exe" : "postgres");
            if (new File(pgPath).exists()) {
                pgBinPath = path;
                return pgBinPath;
            }
        }
        // Check if commands are in system PATH
        if (isCommandAvailable("postgres")) {
            pgBinPath = "PostgresSQL on Path.";
            return pgBinPath;
        }
        
        throw new RuntimeException("PostgreSQL installation not found. Tried paths:\n"
                + String.join("\n", possiblePaths)
                + "\n\nPlease ensure PostgreSQL is installed or add the 'bin' folder to system PATH.");
    }

    private static String pathWithVersionWindows(int version) {
        return "C:" + File.separator + "Program Files" + File.separator + "PostgreSQL" + File.separator + version + File.separator + "bin" + File.separator;
    }

    private static boolean isCommandAvailable(String command) {
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

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
