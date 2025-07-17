package com.univsoftdev.econova.core.multitenancy;

import com.univsoftdev.econova.core.multitenancy.TenantContext;
import com.univsoftdev.econova.core.multitenancy.TenantContextManager;
import com.univsoftdev.econova.core.multitenancy.EbeanMultitenancyConfig;
import com.univsoftdev.econova.core.multitenancy.TenantRegistry;
import com.univsoftdev.econova.core.multitenancy.MultitenancyService;
import com.univsoftdev.econova.core.multitenancy.MultitenancyUtils;
import com.univsoftdev.econova.AppContext;
import io.ebean.config.TenantMode;
import io.ebean.Database;
import io.ebean.config.DatabaseConfig;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * Configurador principal del sistema de multitenancy que inicializa
 * y configura todos los componentes necesarios.
 */
@Singleton
@Slf4j
public class MultitenancyInitializer {
    
    private final AppContext appContext;
    private MultitenancyService multitenancyService;
    private boolean initialized = false;
    
    public MultitenancyInitializer(AppContext appContext) {
        this.appContext = appContext;
    }
    
    /**
     * Inicializa el sistema completo de multitenancy
     */
    public void initializeMultitenancy() {
        if (initialized) {
            log.warn("Sistema de multitenancy ya inicializado");
            return;
        }
        
        log.info("Inicializando sistema de multitenancy mejorado");
        
        try {
            // 1. Crear componentes básicos
            TenantContextManager contextManager = new TenantContextManager();
            TenantRegistry tenantRegistry = new TenantRegistry(contextManager);
            
            // 2. Configurar Ebean para multitenancy
            EbeanMultitenancyConfig multitenancyConfig = new EbeanMultitenancyConfig(contextManager);
            Database database = configureEbeanMultitenancy(multitenancyConfig);
            
            // 3. Crear servicio principal
            multitenancyService = new MultitenancyService(contextManager, tenantRegistry, multitenancyConfig);
            
            // 4. Crear utilidades
            MultitenancyUtils utils = new MultitenancyUtils(multitenancyService);
            
            // 5. Registrar componentes en el contexto de la aplicación
            appContext.addResource("multitenancyService", multitenancyService);
            appContext.addResource("tenantContextManager", contextManager);
            appContext.addResource("tenantRegistry", tenantRegistry);
            appContext.addResource("multitenancyConfig", multitenancyConfig);
            appContext.addResource("multitenancyUtils", utils);
            
            // 6. Inicializar servicios
            multitenancyService.initialize();
            
            // 7. Establecer contexto inicial
            multitenancyService.setTenantFromSession();
            
            initialized = true;
            log.info("Sistema de multitenancy inicializado correctamente");
            
        } catch (Exception e) {
            log.error("Error inicializando sistema de multitenancy", e);
            throw new RuntimeException("Fallo al inicializar multitenancy", e);
        }
    }
    
    /**
     * Configura Ebean para multitenancy
     */
    private Database configureEbeanMultitenancy(@NotNull EbeanMultitenancyConfig multitenancyConfig) {
        log.info("Configurando Ebean para multitenancy");
        
        // Obtener configuración de base de datos existente o crear nueva
        DatabaseConfig config = new DatabaseConfig();
        config.setName("econova");
        
        // Configurar modo de tenant (por defecto SCHEMA)
        multitenancyConfig.setTenantMode(TenantMode.SCHEMA);
        
        // Configurar la base de datos principal
        Database database = multitenancyConfig.configurePrimaryDatabase(config);
        
        // Validar configuración
        multitenancyConfig.validateConfiguration();
        
        log.info("Ebean configurado para multitenancy con modo: {}", multitenancyConfig.getTenantMode());
        return database;
    }
    
    /**
     * Obtiene el servicio de multitenancy
     */
    public MultitenancyService getMultitenancyService() {
        if (!initialized) {
            throw new IllegalStateException("Sistema de multitenancy no inicializado");
        }
        return multitenancyService;
    }
    
    /**
     * Verifica si el sistema está inicializado
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    /**
     * Cierra el sistema de multitenancy
     */
    public void shutdown() {
        if (initialized && multitenancyService != null) {
            log.info("Cerrando sistema de multitenancy");
            multitenancyService.shutdown();
            initialized = false;
        }
    }
    
    /**
     * Reinicia el sistema de multitenancy
     */
    public void restart() {
        log.info("Reiniciando sistema de multitenancy");
        shutdown();
        initializeMultitenancy();
    }
    
    /**
     * Configura el sistema para modo de desarrollo
     */
    public void configureForDevelopment() {
        log.info("Configurando multitenancy para modo desarrollo");
        
        if (!initialized) {
            initializeMultitenancy();
        }
        
        // Registrar tenants adicionales para desarrollo
        multitenancyService.registerTenant("dev_tenant", "dev_tenant", "Tenant de desarrollo", true);
        multitenancyService.registerTenant("test_tenant", "test_tenant", "Tenant de pruebas", true);
        
        log.info("Multitenancy configurado para desarrollo");
    }
    
    /**
     * Configura el sistema para modo producción
     */
    public void configureForProduction() {
        log.info("Configurando multitenancy para modo producción");
        
        if (!initialized) {
            initializeMultitenancy();
        }
        
        // Configuraciones adicionales para producción
        // Por ejemplo, configurar limpieza automática de tenants inactivos
        
        log.info("Multitenancy configurado para producción");
    }
    
    /**
     * Valida la configuración del sistema
     */
    public void validateConfiguration() {
        if (!initialized) {
            throw new IllegalStateException("Sistema de multitenancy no inicializado");
        }
        
        log.info("Validando configuración de multitenancy");
        
        // Validar que el contexto actual es válido
        TenantContext currentContext = multitenancyService.getCurrentTenantContext();
        if (!currentContext.isValid()) {
            throw new IllegalStateException("Contexto de tenant actual inválido");
        }
        
        // Validar que el tenant actual está activo
        if (!multitenancyService.isTenantActive(currentContext.getTenantId())) {
            throw new IllegalStateException("Tenant actual no está activo");
        }
        
        log.info("Configuración de multitenancy validada correctamente");
    }
    
    /**
     * Obtiene estadísticas del sistema
     */
    public MultitenancyService.MultitenancyStats getStats() {
        if (!initialized) {
            throw new IllegalStateException("Sistema de multitenancy no inicializado");
        }
        return multitenancyService.getStats();
    }
}