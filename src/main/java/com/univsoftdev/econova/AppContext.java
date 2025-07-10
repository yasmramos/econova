package com.univsoftdev.econova;

import com.univsoftdev.econova.cache.CacheManager;
import com.univsoftdev.econova.core.Version;
import com.univsoftdev.econova.core.module.Module;
import com.univsoftdev.econova.core.module.ModuleDependencyManager;
import com.univsoftdev.econova.core.module.ModuleInitializationException;
import com.univsoftdev.econova.core.utils.EncryptionUtil;
import io.avaje.inject.BeanScope;
import io.avaje.config.Config;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JMenuBar;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contexto global unificado de la aplicación Econova. Singleton thread-safe,
 * multiplataforma y serializable. Gestiona dependencias, sesión, caché, módulos
 * y configuración global.
 */
@Data
public class AppContext implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AppContext.class);
    private static volatile AppContext instance;

    // Propiedades originales de AppContext  
    private final Version version;
    private final String appName;
    private transient final AppSession session;
    private transient final CacheManager cacheManager;
    private String databaseName;
    private boolean loggedIn = false;
    private JMenuBar mainMenuBar;
    private BeanScope injector;

    // Propiedades migradas de Context  
    private String language;
    private String theme;
    private boolean running;
    private final List<String> eventLog;
    private final List<Module> modules;
    private final Map<String, Object> resources;
    private ModuleDependencyManager moduleDependencyManager;

    /**
     * Constructor privado mejorado. Inicializa todas las dependencias y
     * recursos.
     */
    private AppContext() {
        // Inicialización original de AppContext  
        this.version = new Version(0, 1, 0, 20250304);
        this.appName = "Econova";
        this.injector = BeanScope.builder().shutdownHook(true).build();
        this.cacheManager = new CacheManager();
        this.session = new AppSession(cacheManager);

        // Inicialización migrada de Context  
        this.eventLog = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.language = Config.get("app.language", "es");
        this.theme = Config.get("app.theme", "light");
        this.running = false;
        this.resources = new HashMap<>();
        this.moduleDependencyManager = new ModuleDependencyManager(this);
        logger.info("AppContext inicializado con configuración unificada");
    }

    /**
     * Obtiene la instancia singleton thread-safe
     */
    public static AppContext getInstance() {
        AppContext inst = AppContext.instance;
        if (inst == null) {
            synchronized (AppContext.class) {
                inst = AppContext.instance;
                if (inst == null) {
                    AppContext.instance = inst = new AppContext();
                }
            }
        }
        return inst;
    }

    // === GESTIÓN DE MÓDULOS MEJORADA ===  
    /**
     * Añade un módulo con validación y manejo de errores mejorado
     */
    public void addModule(Module module) {
        if (module == null) {
            logger.warn("Intento de añadir módulo nulo");
            return;
        }

        if (modules.contains(module)) {
            logger.warn("El módulo {} ya está registrado", module.getClass().getSimpleName());
            return;
        }

        try {
            modules.add(module);
            module.initialize(this); // Corregido el error tipográfico  
            logEvent("Módulo añadido: " + module.getClass().getSimpleName());
            logger.info("Módulo {} inicializado correctamente", module.getClass().getSimpleName());
        } catch (ModuleInitializationException e) {
            modules.remove(module); // Rollback en caso de error  
            logger.error("Error al inicializar módulo {}: {}",
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
        } catch (Exception e) {
            logger.error("Error al remover módulo: {}", e.getMessage(), e);
        }
        return false;
    }

    // === GESTIÓN DE RECURSOS MEJORADA ===  
    public void addResource(String key, Object resource) {
        if (key == null || key.trim().isEmpty()) {
            logger.warn("Intento de añadir recurso con clave nula o vacía");
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

    // === GESTIÓN DE EVENTOS MEJORADA ===  
    public void logEvent(String event) {
        if (event != null && !event.trim().isEmpty()) {
            eventLog.add(event);
            logger.info("Evento registrado: {}", event);
        }
    }

    public List<String> getEventLog() {
        return new ArrayList<>(eventLog); // Copia defensiva  
    }

    // === GESTIÓN DE CREDENCIALES SEGURA ===  
    /**
     * Encripta datos usando el sistema de encriptación seguro
     */
    public String encryptData(String data) {
        String encryptionKey = Config.get("econova.encryption.key",
                System.getenv("ECONOVA_ENCRYPTION_KEY"));
        if (encryptionKey == null) {
            logger.error("Clave de encriptación no configurada");
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

    // === MÉTODOS DE ESTADO ===  
    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
        logEvent("Estado de ejecución: " + (running ? "iniciado" : "detenido"));
    }

    // Getters y setters adicionales...  
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
}
