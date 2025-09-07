package com.univsoftdev.econova.core;

import com.univsoftdev.econova.cache.CacheManager;
import com.univsoftdev.econova.config.model.Exercise;
import com.univsoftdev.econova.config.model.Company;
import com.univsoftdev.econova.config.model.Period;
import com.univsoftdev.econova.config.model.Unit;
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
    private Exercise ejercicio;
    private Company empresa;
    private Unit unidad;
    private Period periodo;
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

    public Unit getUnidad() {
        return unidad;
    }

    public void setUnidad(@NotNull Unit unidad) {
        this.unidad = unidad;
    }

    public Period getPeriodo() {
        return periodo;
    }

    public void setPeriodo(@NotNull Period periodo) {
        this.periodo = periodo;
    }

    public void setEjercicio(@NotNull Exercise ejercicio) {
        this.ejercicio = ejercicio;
    }

    public Exercise getEjercicio() {
        return ejercicio;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(@NotNull String license) {
        this.license = license;
    }

    public Company getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Company empresa) {
        this.empresa = empresa;
    }

}
