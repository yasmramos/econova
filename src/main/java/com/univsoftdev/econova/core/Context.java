package com.univsoftdev.econova.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author UnivSoftDev
 */
public class Context implements Serializable {

    private static final long serialVersionUID = 1L;
    private final static Logger logger = LoggerFactory.getLogger(Context.class);
    private String appName;
    private Version version;
    private String language;
    private String theme;
    private boolean running;
    private final List<String> eventLog;
    private final List<Module> modules;
    private final Map<String, Object> resources;

    public Context(String appName, Version version) {
        this.eventLog = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.appName = appName;
        this.version = version;
        this.language = "es";
        this.theme = "light";
        this.running = false;
        this.resources = new HashMap<>();
    }

    public void clearResources() {
        resources.clear();
    }

    public void removeResource(String key) {
        resources.remove(key);
    }

    public Object getResource(String key) {
        return resources.get(key);
    }

    public void addResource(String key, Object resource) {
        resources.put(key, resource);
    }

    public void addModule(Module module) {
        modules.add(module);
        module.initilaize();
    }

    public void logEvent(String event) {
        eventLog.add(event);
        logger.info("Evento registrado: " + event);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public void loadPreferences() {

    }

    public void loadResources() {

    }

    public void saveState(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this);
            logger.info("Estado duardado en: " + filePath);
        } catch (Exception ex) {
            logger.error("Error al guardar el estado: " + ex.getMessage());
        }
    }

    public static Context loadState(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            Context context = (Context) ois.readObject();
            logger.info("Estado cargado desde: " + filePath);
            return context;
        } catch (Exception ex) {
            logger.error("Error al cargar el estado: " + ex.getMessage());
            return null;
        }
    }

    public boolean validateConfiguration(String filePath) {
        return true;
    }

}
