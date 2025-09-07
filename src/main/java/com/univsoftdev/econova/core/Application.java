package com.univsoftdev.econova.core;

import com.formdev.flatlaf.FlatLightLaf;
import com.univsoftdev.econova.MainFormApp;
import com.univsoftdev.econova.Splash;
import com.univsoftdev.econova.ebean.config.EbeanMigrator;
import jakarta.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Application {

    @Inject
    AppContext context;

    private UpdateManager updateManager;

    @Inject
    EbeanMigrator ebeanMigrator;

    public Application() {
        initializeUpdateManager();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void initializeUpdateManager() {
        String updateServerUri = System.getProperty("econova.update.server.uri", "http://tu-servidor.com/version.txt");
        try {
            URI updateUri = new URI(updateServerUri);
            updateManager = new UpdateManager(updateUri.toString(), context.getVersion());
            updateManager.checkForUpdates();
        } catch (URISyntaxException e) {
            log.error("Invalid URI: " + updateServerUri, e);
            throw new RuntimeException("Failed to initialize UpdateManager", e);
        }
    }

    public void run(String[] args) {

        context.setRunning(true);
        context = getContext();

        try {
            FlatLightLaf.setup();

            ebeanMigrator.migrate("accounting");

            new Splash(null, true).setVisible(true);

            MainFormApp.main(args);

        } catch (RuntimeException e) {
            log.error("Error initializing ebean orm: {}", e.getMessage(), e);
            context.setRunning(false);
        }
    }

    public void shutdown() {
        if (updateManager != null) {
            updateManager.shutdown();
        }
        context.setRunning(false);
        log.info("Application has been shut down.");
    }

    public AppContext getContext() {
        return context;
    }

    public void setContext(AppContext context) {
        this.context = context;
    }

    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    public void setUpdateManager(UpdateManager updateManager) {
        this.updateManager = updateManager;
    }

}
