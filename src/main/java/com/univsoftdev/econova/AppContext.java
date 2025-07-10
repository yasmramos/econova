package com.univsoftdev.econova;

import com.univsoftdev.econova.cache.CacheManager;
import com.univsoftdev.econova.core.Version;
import io.avaje.inject.BeanScope;
import java.io.Serializable;
import javax.swing.JMenuBar;
import lombok.Data;

/**
 * Contexto global de la aplicación Econova. Singleton thread-safe,
 * multiplataforma y serializable. Gestiona dependencias, sesión, caché y
 * configuración global.
 */
@Data
public class AppContext implements Serializable {

    private static final long serialVersionUID = 1L;
    private static volatile AppContext instance;
    private final Version version;
    private final String appName;
    private transient final ApplicationSession session;
    private transient final CacheManager cacheManager;
    private String databaseName;
    private boolean loggedIn = false;
    private JMenuBar mainMenuBar;
    private BeanScope injector;

    /**
     * Constructor privado. Inicializa dependencias y recursos.
     */
    private AppContext() {
        this.version = new Version(0, 1, 0, 20250304);
        this.appName = "Econova";
        this.injector = BeanScope.builder().shutdownHook(true).build();
        this.cacheManager = new CacheManager();
        this.session = new ApplicationSession(cacheManager);
    }

    /**
     * Obtiene la instancia singleton de AppContext (thread-safe).
     * @return 
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

    public void setIsLoggedIn(boolean logged) {
        this.loggedIn = logged;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public Version getVersion() {
        return version;
    }

    public String getAppName() {
        return appName;
    }

    public BeanScope getInjector() {
        return injector;
    }

    public ApplicationSession getSession() {
        return session;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        cacheManager.put("econova.database.name", databaseName);
    }

    public void reset() {
        session.clear();
    }

    public JMenuBar getMainMenuBar() {
        return mainMenuBar;
    }

    public void setMainMenuBar(JMenuBar menuBar) {
        this.mainMenuBar = menuBar;
    }
}
