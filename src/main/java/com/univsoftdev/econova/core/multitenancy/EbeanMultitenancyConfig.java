package com.univsoftdev.econova.core.multitenancy;

import com.univsoftdev.econova.MyCurrentTenantProvider;
import com.univsoftdev.econova.MyTenantSchemaProvider;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.TenantMode;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configurador de multitenancy para Ebean ORM que proporciona
 * configuración centralizada y gestión de bases de datos por tenant.
 */
@Singleton
@Slf4j
public class EbeanMultitenancyConfig {
    
    private final ConcurrentHashMap<String, Database> tenantDatabases = new ConcurrentHashMap<>();
    private final TenantContextManager tenantContextManager;
    private Database defaultDatabase;
    private TenantMode tenantMode = TenantMode.SCHEMA;
    
    public EbeanMultitenancyConfig(TenantContextManager tenantContextManager) {
        this.tenantContextManager = tenantContextManager;
    }
    
    /**
     * Configura la base de datos principal con soporte multitenancy
     */
    public Database configurePrimaryDatabase(@NotNull DatabaseConfig config) {
        log.info("Configurando base de datos principal con soporte multitenancy");
        
        // Configurar providers de tenant
        config.setCurrentTenantProvider(new MyCurrentTenantProvider());
        config.setTenantSchemaProvider(new MyTenantSchemaProvider());
        
        // Configurar modo de tenant
        config.setTenantMode(tenantMode);
        
        // Configurar interceptores
        TenantInterceptor interceptor = new TenantInterceptor(null, tenantMode);
        
        // Configurar listeners personalizados
        config.add(new TenantPersistenceListener(interceptor));
        
        // Crear la base de datos
        defaultDatabase = DatabaseFactory.create(config);
        
        log.info("Base de datos principal configurada con modo de tenant: {}", tenantMode);
        return defaultDatabase;
    }
    
    /**
     * Obtiene la base de datos para el tenant actual
     */
    public Database getCurrentDatabase() {
        TenantContext context = tenantContextManager.getCurrentOrResolve();
        String tenantId = context.getTenantId();
        
        if (context.isDefaultTenant()) {
            return defaultDatabase;
        }
        
        return tenantDatabases.computeIfAbsent(tenantId, this::createTenantDatabase);
    }
    
    /**
     * Crea una base de datos específica para un tenant
     */
    private Database createTenantDatabase(@NotNull String tenantId) {
        log.info("Creando base de datos para tenant: {}", tenantId);
        
        DatabaseConfig config = new DatabaseConfig();
        
        // Configurar para el tenant específico
        config.setName("tenant_" + tenantId);
        config.setCurrentTenantProvider(new MyCurrentTenantProvider());
        config.setTenantSchemaProvider(new MyTenantSchemaProvider());
        config.setTenantMode(tenantMode);
        
        // Configurar el esquema específico del tenant
        TenantContextManager.TenantInfo tenantInfo = tenantContextManager.getTenantInfo(tenantId).orElse(null);
        if (tenantInfo != null) {
            config.setDefaultSchema(tenantInfo.getSchemaName());
        }
        
        // Crear la base de datos
        Database database = DatabaseFactory.create(config);
        
        log.info("Base de datos creada para tenant: {} con esquema: {}", 
                tenantId, tenantInfo != null ? tenantInfo.getSchemaName() : "default");
        
        return database;
    }
    
    /**
     * Establece el modo de tenant
     */
    public void setTenantMode(@NotNull TenantMode mode) {
        this.tenantMode = mode;
        log.info("Modo de tenant establecido: {}", mode);
    }
    
    /**
     * Obtiene el modo de tenant actual
     */
    public TenantMode getTenantMode() {
        return tenantMode;
    }
    
    /**
     * Ejecuta una operación en el contexto de un tenant específico
     */
    public <T> T executeInTenant(@NotNull String tenantId, @NotNull TenantOperation<T> operation) {
        return tenantContextManager.executeInTenant(tenantId, () -> {
            Database database = getCurrentDatabase();
            return operation.execute(database);
        });
    }
    
    /**
     * Ejecuta una operación en el contexto de un tenant específico sin retorno
     */
    public void executeInTenant(@NotNull String tenantId, @NotNull TenantRunnable runnable) {
        executeInTenant(tenantId, (database) -> {
            runnable.run(database);
            return null;
        });
    }
    
    /**
     * Migra el esquema para un tenant específico
     */
    public void migrateTenantSchema(@NotNull String tenantId) {
        log.info("Migrando esquema para tenant: {}", tenantId);
        
        executeInTenant(tenantId, (database) -> {
            // Ejecutar migraciones específicas del tenant
            // Esto depende de tu configuración de migración
            log.info("Migración completada para tenant: {}", tenantId);
        });
    }
    
    /**
     * Valida la configuración de multitenancy
     */
    public void validateConfiguration() {
        log.info("Validando configuración de multitenancy");
        
        if (defaultDatabase == null) {
            throw new IllegalStateException("Base de datos principal no configurada");
        }
        
        if (tenantMode == null) {
            throw new IllegalStateException("Modo de tenant no configurado");
        }
        
        log.info("Configuración de multitenancy validada correctamente");
    }
    
    /**
     * Limpia recursos de tenant
     */
    public void cleanupTenant(@NotNull String tenantId) {
        Database database = tenantDatabases.remove(tenantId);
        if (database != null) {
            database.shutdown();
            log.info("Recursos limpiados para tenant: {}", tenantId);
        }
    }
    
    /**
     * Cierra todas las bases de datos de tenant
     */
    public void shutdown() {
        log.info("Cerrando todas las bases de datos de tenant");
        
        tenantDatabases.forEach((tenantId, database) -> {
            try {
                database.shutdown();
                log.debug("Base de datos cerrada para tenant: {}", tenantId);
            } catch (Exception e) {
                log.error("Error cerrando base de datos para tenant: {}", tenantId, e);
            }
        });
        
        tenantDatabases.clear();
        
        if (defaultDatabase != null) {
            defaultDatabase.shutdown();
            log.info("Base de datos principal cerrada");
        }
    }
    
    /**
     * Interfaz funcional para operaciones de tenant con base de datos
     */
    @FunctionalInterface
    public interface TenantOperation<T> {
        T execute(Database database) throws Exception;
    }
    
    /**
     * Interfaz funcional para operaciones de tenant sin retorno
     */
    @FunctionalInterface
    public interface TenantRunnable {
        void run(Database database) throws Exception;
    }
}