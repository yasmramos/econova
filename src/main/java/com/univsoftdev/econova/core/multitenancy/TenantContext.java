package com.univsoftdev.econova.core.multitenancy;

import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.config.model.Empresa;
import com.univsoftdev.econova.config.model.Unidad;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Contexto de tenant mejorado que gestiona información completa del tenant actual.
 * Proporciona acceso a usuario, empresa, unidad y esquema de base de datos.
 */
@Data
@Slf4j
public class TenantContext {
    
    private static final ThreadLocal<TenantContext> currentContext = new ThreadLocal<>();
    private static final String DEFAULT_TENANT_ID = "accounting";
    private static final String DEFAULT_SCHEMA = "accounting";
    
    private String tenantId;
    private String schemaName;
    private User currentUser;
    private Empresa currentEmpresa;
    private Unidad currentUnidad;
    private boolean isolated = true;
    
    /**
     * Constructor privado para uso interno
     */
    private TenantContext(String tenantId, String schemaName) {
        this.tenantId = tenantId;
        this.schemaName = schemaName;
    }
    
    /**
     * Crea un contexto de tenant con información básica
     */
    public static TenantContext create(@NotNull String tenantId, @NotNull String schemaName) {
        return new TenantContext(tenantId, schemaName);
    }
    
    /**
     * Crea un contexto de tenant basado en usuario
     */
    public static TenantContext createFromUser(@NotNull User user) {
        String tenantId = user.getTenantSchema() != null ? user.getTenantSchema() : DEFAULT_TENANT_ID;
        String schemaName = user.getTenantSchema() != null ? user.getTenantSchema() : DEFAULT_SCHEMA;
        
        TenantContext context = new TenantContext(tenantId, schemaName);
        context.setCurrentUser(user);
        
        log.debug("Creado contexto de tenant para usuario: {} con tenant: {}", user.getUserName(), tenantId);
        return context;
    }
    
    /**
     * Crea un contexto de tenant basado en empresa
     */
    public static TenantContext createFromEmpresa(@NotNull Empresa empresa, @Nullable User user) {
        String tenantId = "empresa_" + empresa.getId();
        String schemaName = "empresa_" + empresa.getId().toString().replaceAll("-", "_");
        
        TenantContext context = new TenantContext(tenantId, schemaName);
        context.setCurrentEmpresa(empresa);
        context.setCurrentUser(user);
        
        log.debug("Creado contexto de tenant para empresa: {} con tenant: {}", empresa.getName(), tenantId);
        return context;
    }
    
    /**
     * Crea un contexto de tenant basado en unidad
     */
    public static TenantContext createFromUnidad(@NotNull Unidad unidad, @Nullable User user) {
        String tenantId = "unidad_" + unidad.getId();
        String schemaName = "unidad_" + unidad.getId().toString().replaceAll("-", "_");
        
        TenantContext context = new TenantContext(tenantId, schemaName);
        context.setCurrentUnidad(unidad);
        context.setCurrentEmpresa(unidad.getEmpresa());
        context.setCurrentUser(user);
        
        log.debug("Creado contexto de tenant para unidad: {} con tenant: {}", unidad.getNombre(), tenantId);
        return context;
    }
    
    /**
     * Establece el contexto actual para el hilo actual
     */
    public static void setCurrent(@NotNull TenantContext context) {
        currentContext.set(context);
        log.debug("Establecido contexto de tenant: {}", context.getTenantId());
    }
    
    /**
     * Obtiene el contexto actual
     */
    public static Optional<TenantContext> getCurrent() {
        return Optional.ofNullable(currentContext.get());
    }
    
    /**
     * Obtiene el contexto actual o crea uno por defecto
     */
    public static TenantContext getCurrentOrDefault() {
        return getCurrent().orElseGet(() -> {
            log.debug("No hay contexto de tenant, creando contexto por defecto");
            return create(DEFAULT_TENANT_ID, DEFAULT_SCHEMA);
        });
    }
    
    /**
     * Obtiene el tenant ID actual
     */
    public static String getCurrentTenantId() {
        return getCurrent().map(TenantContext::getTenantId).orElse(DEFAULT_TENANT_ID);
    }
    
    /**
     * Obtiene el esquema actual
     */
    public static String getCurrentSchema() {
        return getCurrent().map(TenantContext::getSchemaName).orElse(DEFAULT_SCHEMA);
    }
    
    /**
     * Limpia el contexto actual
     */
    public static void clear() {
        TenantContext context = currentContext.get();
        if (context != null) {
            log.debug("Limpiando contexto de tenant: {}", context.getTenantId());
            currentContext.remove();
        }
    }
    
    /**
     * Ejecuta una operación en el contexto del tenant especificado
     */
    public static <T> T executeInTenant(@NotNull TenantContext context, @NotNull TenantOperation<T> operation) {
        TenantContext previousContext = currentContext.get();
        try {
            setCurrent(context);
            return operation.execute();
        } finally {
            if (previousContext != null) {
                setCurrent(previousContext);
            } else {
                clear();
            }
        }
    }
    
    /**
     * Ejecuta una operación en el contexto del tenant especificado sin retorno
     */
    public static void executeInTenant(@NotNull TenantContext context, @NotNull TenantRunnable runnable) {
        executeInTenant(context, () -> {
            runnable.run();
            return null;
        });
    }
    
    /**
     * Verifica si el contexto actual es válido
     */
    public boolean isValid() {
        return tenantId != null && schemaName != null;
    }
    
    /**
     * Verifica si el contexto actual es el tenant por defecto
     */
    public boolean isDefaultTenant() {
        return DEFAULT_TENANT_ID.equals(tenantId);
    }
    
    /**
     * Verifica si el contexto tiene un usuario asociado
     */
    public boolean hasUser() {
        return currentUser != null;
    }
    
    /**
     * Verifica si el contexto tiene una empresa asociada
     */
    public boolean hasEmpresa() {
        return currentEmpresa != null;
    }
    
    /**
     * Verifica si el contexto tiene una unidad asociada
     */
    public boolean hasUnidad() {
        return currentUnidad != null;
    }
    
    /**
     * Interfaz funcional para operaciones en contexto de tenant
     */
    @FunctionalInterface
    public interface TenantOperation<T> {
        T execute() throws Exception;
    }
    
    /**
     * Interfaz funcional para operaciones en contexto de tenant sin retorno
     */
    @FunctionalInterface
    public interface TenantRunnable {
        void run() throws Exception;
    }
    
    @Override
    public String toString() {
        return String.format("TenantContext{tenantId='%s', schema='%s', user='%s', empresa='%s', unidad='%s'}", 
                tenantId, schemaName, 
                currentUser != null ? currentUser.getUserName() : "null",
                currentEmpresa != null ? currentEmpresa.getName() : "null",
                currentUnidad != null ? currentUnidad.getNombre() : "null");
    }
}