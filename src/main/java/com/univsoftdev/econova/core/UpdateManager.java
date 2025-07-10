package com.univsoftdev.econova.core;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateManager {

    private final Logger logger = LoggerFactory.getLogger(UpdateManager.class);
    private String updateUrl; //ej. http://tu-servidor.com/version.txt
    private Version currentVersion;
    private final ExecutorService executorService; //Executor para manejar tareas en segundo plano
    private final ScheduledExecutorService scheduled; //Scheduled para reintentos
    private boolean checking;

    public UpdateManager(String updateUrl, Version currentVersion) {
        this.updateUrl = updateUrl;
        this.currentVersion = currentVersion;
        this.executorService = Executors.newSingleThreadExecutor(); //Un hilo para tareas en segundo plano
        this.scheduled = Executors.newScheduledThreadPool(1); // Un hilo para el scheduler
    }

    public void checkForUpdates() {
        executorService.submit(() -> {
            boolean shouldShutdown;
            try {
                URI uri = new URI(updateUrl);
                HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == 200) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String latestVersionString = in.readLine(); //Suponiendo que la primera linea es la version
                        Version latestVersion = parseVersion(latestVersionString);
                        if (latestVersion.compareTo(currentVersion) > 0) {
                            logger.info("Nueva versión disponible");
                            downloadUpdate(latestVersion);
                        } else {
                            logger.info("La aplicacion esta actualizada");
                        }
                    } //Suponiendo que la primera linea es la version
                } else {
                    logger.error("Error al conectar al servidor de actualizaciones. Código de respuesta: " + connection.getResponseCode());
                    startReconnectScheduler(); //Inicia el scheduler para reintentos
                }
            } catch (IOException | URISyntaxException e) {
                logger.error("Fallo en la conección al servidor de actualizaciones: " + e.getMessage());
                startReconnectScheduler(); //Inicia el scheduler para reintentos
            } finally {
                shouldShutdown = true; //Marcar que se debe cerrar al final
            }

            if (shouldShutdown) {
                executorService.shutdown();
                scheduled.shutdown();
                logger.info("Servicios de actualización detenidos");
            }
        });
    }

    private void startReconnectScheduler() {
        if (!isChecking()) {
            checking = true;
            scheduled.scheduleAtFixedRate(() -> {
                logger.info("Intentando reconectar al servidor de actualizaciones...");
                checkForUpdates(); //Volver a comprobar actualizaciones

            }, 5, 5, TimeUnit.SECONDS); //Reintentar cada 5 segundos
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }

    public boolean isChecking() {
        return checking;
    }

    public void setChecking(boolean checking) {
        this.checking = checking;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public Version getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Version currentVersion) {
        this.currentVersion = currentVersion;
    }

    private Version parseVersion(String versionString) {
        String[] parts = versionString.split("\\.");
        return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }

    private void downloadUpdate(Version version) {
        //Suponiendo que el archivo de actualizacion se llama update.jar
        try {
            URI downloadUri = new URI("http://tu-servidor.com/updates/update-" + version + ".jar");
            HttpURLConnection connection = (HttpURLConnection) downloadUri.toURL().openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200) {
                try (InputStream inputStream = connection.getInputStream()) {
                    FileOutputStream outputStream = new FileOutputStream("update-" + version + ".jar");
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    logger.info("Actualización descargada: update-" + version + ".jar");
                }
            } else {
                logger.error("Error al descargar la actualización. Código de respuesta: " + connection.getResponseCode());
            }
        } catch (IOException | URISyntaxException e) {
            logger.error("ERROR", e);
        }
    }

}
