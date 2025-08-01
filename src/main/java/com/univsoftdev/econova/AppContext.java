package com.univsoftdev.econova;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.univsoftdev.econova.cache.CacheManager;
import com.univsoftdev.econova.core.Version;
import com.univsoftdev.econova.core.config.AppConfig;
import com.univsoftdev.econova.core.module.ModuleDependencyManager;
import com.univsoftdev.econova.core.module.ModuleInitializationException;
import com.univsoftdev.econova.core.utils.EncryptionUtil;
import com.univsoftdev.econova.core.module.Module;
import com.univsoftdev.econova.core.module.ModuleShutdownException;
import io.avaje.config.Config;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * Contexto global unificado de la aplicación Econova. Singleton thread-safe,
 * multiplataforma y serializable. Gestiona dependencias, sesión, caché, módulos
 * y configuración global.
 */
@Slf4j
@Singleton
public class AppContext implements Serializable {

    private static final long serialVersionUID = 1L;

    private final transient AppSession session;
    private final transient CacheManager cacheManager;
    private final Version version;
    private final String appName;
    private String databaseName;
    private String language;
    private String theme;
    private boolean running;
    private final List<String> eventLog;
    private final List<Module> modules;
    private final Map<String, Object> resources;
    private ModuleDependencyManager moduleDependencyManager;

    @Inject
    public AppContext(AppSession session, CacheManager cacheManager) {
        this.session = session;
        this.cacheManager = cacheManager;
        this.version = new Version(0, 1, 0, 20250304);
        this.appName = AppConfig.getAppName();
        this.eventLog = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.language = AppConfig.getDefaultLanguage();
        this.theme = AppConfig.getDefaultTheme();
        this.running = false;
        this.resources = new HashMap<>();
        this.moduleDependencyManager = new ModuleDependencyManager(this);
        log.info("AppContext inicializado");
    }

    /**
     * Añade un módulo con validación y manejo de errores mejorado
     *
     * @param module
     */
    public void addModule(Module module) {
        if (module == null) {
            log.warn("Intento de añadir módulo nulo");
            return;
        }

        if (modules.contains(module)) {
            log.warn("El módulo {} ya está registrado", module.getClass().getSimpleName());
            return;
        }

        try {
            modules.add(module);
            module.initialize(this); // Corregido el error tipográfico  
            logEvent("Módulo añadido: " + module.getClass().getSimpleName());
            log.info("Módulo {} inicializado correctamente", module.getClass().getSimpleName());
        } catch (ModuleInitializationException e) {
            modules.remove(module); // Rollback en caso de error  
            log.error("Error al inicializar módulo {}: {}",
                    module.getClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Fallo al inicializar módulo", e);
        }
    }

    public void registerModule(Module module) {
        moduleDependencyManager.registerModule(module);
    }

    public void initializeAllModules() throws ModuleInitializationException {
        moduleDependencyManager.initializeAllModules();
    }

    /**
     * Remueve un módulo de forma segura
     *
     * @param module
     * @return
     */
    public boolean removeModule(Module module) {
        if (module == null) {
            return false;
        }

        try {
            if (modules.remove(module)) {
                module.shutdown();
                logEvent("Módulo removido: " + module.getClass().getSimpleName());
                return true;
            }
        } catch (ModuleShutdownException e) {
            log.error("Error al remover módulo: {}", e.getMessage(), e);
        }
        return false;
    }

    public void addResource(String key, Object resource) {
        if (key == null || key.trim().isEmpty()) {
            log.warn("Intento de añadir recurso con clave nula o vacía");
            return;
        }
        resources.put(key, resource);
        logEvent("Recurso añadido: " + key);
    }

    public Object getResource(String key) {
        return resources.get(key);
    }

    public void removeResource(String key) {
        if (resources.remove(key) != null) {
            logEvent("Recurso removido: " + key);
        }
    }

    public void clearResources() {
        int count = resources.size();
        resources.clear();
        logEvent("Recursos limpiados: " + count + " elementos");
    }

    public void logEvent(String event) {
        if (event != null && !event.trim().isEmpty()) {
            eventLog.add(event);
            log.info("Evento registrado: {}", event);
        }
    }

    public List<String> getEventLog() {
        return new ArrayList<>(eventLog); // Copia defensiva  
    }

    /**
     * Encripta datos usando el sistema de encriptación seguro
     *
     * @param data
     * @return
     */
    public String encryptData(String data) {
        String encryptionKey = Config.get("econova.encryption.key",
                System.getenv("ECONOVA_ENCRYPTION_KEY"));
        if (encryptionKey == null) {
            log.error("Clave de encriptación no configurada");
            throw new IllegalStateException("Clave de encriptación no disponible");
        }
        return EncryptionUtil.encryptWithCustomKey(data, encryptionKey);
    }

    // === MÉTODOS DE CONFIGURACIÓN ===  
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        cacheManager.put("econova.database.name", databaseName);
        logEvent("Base de datos configurada: " + databaseName);
    }

    public void reset() {
        session.clear();
        eventLog.clear();
        // No limpiar módulos ni recursos críticos durante reset  
        logEvent("Contexto reiniciado");
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
        logEvent("Estado de ejecución: " + (running ? "iniciado" : "detenido"));
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
        logEvent("Idioma cambiado a: " + language);
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
        logEvent("Tema cambiado a: " + theme);
    }

    public Version getVersion() {
        return version;
    }

    public String getAppName() {
        return appName;
    }

    public AppSession getSession() {
        return session;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public List<Module> getModules() {
        return modules;
    }

    public Map<String, Object> getResources() {
        return resources;
    }

    public ModuleDependencyManager getModuleDependencyManager() {
        return moduleDependencyManager;
    }

    public void setModuleDependencyManager(ModuleDependencyManager moduleDependencyManager) {
        this.moduleDependencyManager = moduleDependencyManager;
    }

}
