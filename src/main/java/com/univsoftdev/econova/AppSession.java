package com.univsoftdev.econova;

import com.univsoftdev.econova.cache.CacheManager;
import com.univsoftdev.econova.config.model.Ejercicio;
import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.config.model.Unidad;
import com.univsoftdev.econova.config.model.User;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Sesión de usuario para la aplicación Econova. Gestiona el usuario, contexto
 * contable y preferencias de sesión. Seguro, serializable y multiplataforma.
 */
public class ApplicationSession implements AutoCloseable {

    private Ejercicio ejercicio;
    private Unidad unidad;
    private Periodo periodo;
    private String license;
    private final CacheManager cacheManager;

    /**
     * Crea una sesión con un nuevo cache manager único.
     */
    public ApplicationSession() {
        this(new CacheManager(UUID.randomUUID().toString() + ".cache"));
    }

    /**
     * Crea una sesión con un cache manager específico.
     *
     * @param cacheManager
     */
    public ApplicationSession(@NotNull CacheManager cacheManager) {
        this.cacheManager = cacheManager;
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

    /**
     * Limpia la sesión y recursos asociados.
     */
    public void clear() {
        try {
            close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void close() {
        this.unidad = null;
        this.periodo = null;
        this.license = null;
        cacheManager.clear();
    }
}
