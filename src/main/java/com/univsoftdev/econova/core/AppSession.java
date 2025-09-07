package com.univsoftdev.econova;

import com.univsoftdev.econova.cache.CacheManager;
import com.univsoftdev.econova.config.model.Ejercicio;
import com.univsoftdev.econova.config.model.Empresa;
import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.config.model.Unidad;
import com.univsoftdev.econova.config.model.User;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Sesión de usuario para la aplicación Econova. Gestiona el usuario, contexto
 * contable y preferencias de sesión. Seguro, serializable y multiplataforma.
 */
@Slf4j
@Singleton
public class AppSession implements AutoCloseable {

    private final CacheManager cacheManager;
    private Ejercicio ejercicio;
    private Empresa empresa;
    private Unidad unidad;
    private Periodo periodo;
    private String license;
    private final Map<String, Object> sessionCache = new HashMap<>();

    @Inject
    public AppSession(@NotNull CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        User currentUser = getUser();
        String userId = currentUser != null ? currentUser.getId().toString() : null;

        log.info("Sesión inicializada para usuario: {}", userId != null ? userId : "default");
    }

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

    public final User getUser() {
        return UserContext.get().getUser();
    }

    public void setUser(@NotNull User user) {
        UserContext.get().setUser(user);
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

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

}
