package com.univsoftdev.econova.core.module;

import com.univsoftdev.econova.core.AppContext;
import com.univsoftdev.econova.core.Version;
import java.util.Set;

public interface Module {

    /**
     * Identificador único del módulo
     */
    String getModuleId();

    /**
     * Nombre descriptivo del módulo
     */
    String getModuleName();

    /**
     * Versión del módulo
     */
    Version getVersion();

    /**
     * Lista de IDs de módulos requeridos antes de inicializar este módulo
     */
    Set<String> getDependencies();

    /**
     * Lista de IDs de módulos que son opcionales pero mejoran la funcionalidad
     */
    Set<String> getOptionalDependencies();

    /**
     * Prioridad de inicialización (menor número = mayor prioridad)
     */
    int getInitializationPriority();

    /**
     * Inicializa el módulo con acceso al contexto
     */
    void initialize(AppContext context) throws ModuleInitializationException;

    /**
     * Cierra el módulo de forma limpia
     */
    void shutdown() throws ModuleShutdownException;

    /**
     * Verifica si el módulo está correctamente inicializado
     */
    boolean isInitialized();

    /**
     * Obtiene el estado actual del módulo
     */
    ModuleState getState();
}
