package com.univsoftdev.econova;

import com.univsoftdev.econova.cache.CacheManager;
import com.univsoftdev.econova.config.model.Ejercicio;
import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.config.model.Unidad;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.core.config.AppConfig;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Sesión de usuario para la aplicación Econova. Gestiona el usuario, contexto
 * contable y preferencias de sesión. Seguro, serializable y multiplataforma.
 */
@Slf4j
public class AppSession implements AutoCloseable {

    // Datos de sesión  
    private Ejercicio ejercicio;  
    private Unidad unidad;  
    private Periodo periodo;  
    private String license;  
    private final CacheManager cacheManager;  
      
    // Delegación de configuración  
    private final AppConfig appConfig;  
    private final AppConfig.UserConfig userConfig;  
    private final Map<String, Object> sessionCache = new HashMap<>();  
      
    public AppSession(@NotNull CacheManager cacheManager, @NotNull AppConfig appConfig) {  
        this.cacheManager = cacheManager;  
        this.appConfig = appConfig;  
          
        User currentUser = getCurrentUser();  
        String userId = currentUser != null ? currentUser.getId().toString() : null;  
        this.userConfig = appConfig.getUserConfig(userId);  
          
        log.info("Sesión inicializada para usuario: {}", userId != null ? userId : "default");  
    }  
      
    // === DELEGACIÓN A APPCONFIG ===  
      
    public AppConfig getAppConfig() {  
        return appConfig;  
    }  
      
    public AppConfig.UserConfig getUserConfig() {  
        return userConfig;  
    }  
      
    // === MÉTODOS DE CONVENIENCIA ===  
      
    public String getPreferredLanguage() {  
        return userConfig.getPreferredLanguage();  
    }  
      
    public void setPreferredLanguage(String language) {  
        userConfig.setPreferredLanguage(language);  
    }  
      
    public String getPreferredTheme() {  
        return userConfig.getPreferredTheme();  
    }  
      
    public void setPreferredTheme(String theme) {  
        userConfig.setPreferredTheme(theme);  
    }  
      
    // === GESTIÓN DE CACHÉ DE SESIÓN ===  
      
    public void cacheSessionData(String key, Object data) {  
        sessionCache.put(key, data);  
        cacheManager.put("session_" + key, data);  
    }  
      
    public <T> T getCachedSessionData(String key, Class<T> type) {  
        Object cached = sessionCache.get(key);  
        if (cached != null && type.isInstance(cached)) {  
            return type.cast(cached);  
        }  
          
        Object fromCache = cacheManager.get("session_" + key);  
        if (fromCache != null && type.isInstance(fromCache)) {  
            sessionCache.put(key, fromCache);  
            return type.cast(fromCache);  
        }  
          
        return null;  
    }  
      
    // === LIMPIEZA ===  
      
    public void clear() {  
        try {  
            this.unidad = null;  
            this.periodo = null;  
            this.ejercicio = null;  
            this.license = null;  
              
            sessionCache.clear();  
            cacheManager.clear();  
              
            log.info("Sesión limpiada exitosamente");  
        } catch (Exception e) {  
            log.error("Error al limpiar sesión: {}", e.getMessage(), e);  
        }  
    }  
      
    @Override  
    public void close() {  
        clear();  
    }  

    public User getCurrentUser() {
        return MyCurrentUserProvider.getUser();
    }

    public void setCurrentUser(@NotNull User currentUser) {
        MyCurrentUserProvider.setUser(currentUser);
    }

    public Unidad getUnidad() {
        return unidad;
    }

    public void setUnidad(@NotNull Unidad unidad) {
        this.unidad = unidad;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(@NotNull Periodo periodo) {
        this.periodo = periodo;
    }

    public void setEjercicio(@NotNull Ejercicio ejercicio) {
        this.ejercicio = ejercicio;
    }

    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(@NotNull String license) {
        this.license = license;
    }
}
