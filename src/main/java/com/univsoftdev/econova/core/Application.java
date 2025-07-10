package com.univsoftdev.econova.core;

import com.formdev.flatlaf.FlatLightLaf;
import com.univsoftdev.econova.MainFormApp;
import com.univsoftdev.econova.Splash;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private Context context;
    private UpdateManager updateManager;
    private final static Logger logger = LoggerFactory.getLogger(Application.class);

    public Application() {
        Version currentVersion = new Version(1, 0, 0, 0);
        context = new Context("Application", currentVersion);
        initializeUpdateManager();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public Application(Context context) {
        this.context = context;
        initializeUpdateManager();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void initializeUpdateManager() {
        String updateServerUri = System.getProperty("update.server.uri", "http://tu-servidor.com/version.txt");
        try {
            URI updateUri = new URI(updateServerUri);
            updateManager = new UpdateManager(updateUri.toString(), context.getVersion());
            updateManager.checkForUpdates();
        } catch (URISyntaxException e) {
            logger.error("Invalid URI: " + updateServerUri, e);
            throw new RuntimeException("Failed to initialize UpdateManager", e);
        }
    }

    public void run(String[] args) {
        context.setRunning(true);
        context = getContext();
        try {
            FlatLightLaf.setup();
            new Splash(null, true).setVisible(true);
            MainFormApp.main(args);
        } catch (RuntimeException e) {
            logger.error("Error initializing ebean orm: {}", e.getMessage(), e);
        }
    }

    public void shutdown() {
        if (updateManager != null) {
            updateManager.shutdown();
        }
        context.setRunning(false);
        logger.info("Application has been shut down.");
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    public void setUpdateManager(UpdateManager updateManager) {
        this.updateManager = updateManager;
    }

}
