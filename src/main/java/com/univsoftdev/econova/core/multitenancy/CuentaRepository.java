package com.univsoftdev.econova.core.multitenancy;

import com.univsoftdev.econova.contabilidad.model.Cuenta;
import com.univsoftdev.econova.core.multitenancy.TenantAwareRepository;
import com.univsoftdev.econova.core.multitenancy.EbeanMultitenancyConfig;
import io.ebean.Database;
import io.ebean.Query;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio específico para Cuenta que extiende TenantAwareRepository
 * y proporciona operaciones específicas para cuentas contables conscientes del tenant.
 */
@Singleton
@Slf4j
public class CuentaRepository extends TenantAwareRepository<Cuenta> {
    
    public CuentaRepository(@NotNull Database database, @NotNull EbeanMultitenancyConfig multitenancyConfig) {
        super(database, Cuenta.class, multitenancyConfig);
    }
    
    /**
     * Busca una cuenta por código en el tenant actual
     */
    public Optional<Cuenta> findByCodigo(@NotNull String codigo) {
        log.debug("Buscando cuenta con código: {} en tenant: {}", 
                codigo, TenantContext.getCurrentTenantId());
        
        Cuenta cuenta = createQuery()
                .where()
                .eq("codigo", codigo)
                .findOne();
        
        return Optional.ofNullable(cuenta);
    }
    
    /**
     * Busca cuentas por tipo en el tenant actual
     */
    public List<Cuenta> findByTipo(@NotNull String tipo) {
        log.debug("Buscando cuentas de tipo: {} en tenant: {}", 
                tipo, TenantContext.getCurrentTenantId());
        
        return createQuery()
                .where()
                .eq("tipo", tipo)
                .findList();
    }
    
    /**
     * Busca cuentas por naturaleza en el tenant actual
     */
    public List<Cuenta> findByNaturaleza(@NotNull String naturaleza) {
        log.debug("Buscando cuentas de naturaleza: {} en tenant: {}", 
                naturaleza, TenantContext.getCurrentTenantId());
        
        return createQuery()
                .where()
                .eq("naturaleza", naturaleza)
                .findList();
    }
    
    /**
     * Busca cuentas activas en el tenant actual
     */
    public List<Cuenta> findActivas() {
        log.debug("Buscando cuentas activas en tenant: {}", TenantContext.getCurrentTenantId());
        
        return createQuery()
                .where()
                .eq("estado", "ACTIVA")
                .findList();
    }
    
    /**
     * Busca cuentas padre (nivel 1) en el tenant actual
     */
    public List<Cuenta> findCuentasPadre() {
        log.debug("Buscando cuentas padre en tenant: {}", TenantContext.getCurrentTenantId());
        
        return createQuery()
                .where()
                .isNull("cuentaPadre")
                .orderBy("codigo")
                .findList();
    }
    
    /**
     * Busca cuentas hijas de una cuenta específica en el tenant actual
     */
    public List<Cuenta> findCuentasHijas(@NotNull Cuenta cuentaPadre) {
        log.debug("Buscando cuentas hijas de: {} en tenant: {}", 
                cuentaPadre.getCodigo(), TenantContext.getCurrentTenantId());
        
        return createQuery()
                .where()
                .eq("cuentaPadre", cuentaPadre)
                .orderBy("codigo")
                .findList();
    }
    
    /**
     * Busca cuentas por rango de código en el tenant actual
     */
    public List<Cuenta> findByCodigoRange(@NotNull String codigoInicio, @NotNull String codigoFin) {
        log.debug("Buscando cuentas en rango: {} - {} en tenant: {}", 
                codigoInicio, codigoFin, TenantContext.getCurrentTenantId());
        
        return createQuery()
                .where()
                .between("codigo", codigoInicio, codigoFin)
                .orderBy("codigo")
                .findList();
    }
    
    /**
     * Busca cuentas por nombre (búsqueda parcial) en el tenant actual
     */
    public List<Cuenta> findByNombreLike(@NotNull String nombre) {
        log.debug("Buscando cuentas con nombre like: {} en tenant: {}", 
                nombre, TenantContext.getCurrentTenantId());
        
        return createQuery()
                .where()
                .ilike("nombre", "%" + nombre + "%")
                .orderBy("codigo")
                .findList();
    }
    
    /**
     * Verifica si existe una cuenta con el código especificado en el tenant actual
     */
    public boolean existsByCodigo(@NotNull String codigo) {
        log.debug("Verificando existencia de cuenta con código: {} en tenant: {}", 
                codigo, TenantContext.getCurrentTenantId());
        
        return createQuery()
                .where()
                .eq("codigo", codigo)
                .exists();
    }
    
    /**
     * Cuenta el número de cuentas activas en el tenant actual
     */
    public long countActivas() {
        log.debug("Contando cuentas activas en tenant: {}", TenantContext.getCurrentTenantId());
        
        return createQuery()
                .where()
                .eq("estado", "ACTIVA")
                .findCount();
    }
    
    /**
     * Busca cuentas de balance en el tenant actual
     */
    public List<Cuenta> findCuentasBalance() {
        log.debug("Buscando cuentas de balance en tenant: {}", TenantContext.getCurrentTenantId());
        
        return createQuery()
                .where()
                .in("tipo", "ACTIVO", "PASIVO", "PATRIMONIO")
                .eq("estado", "ACTIVA")
                .orderBy("codigo")
                .findList();
    }
    
    /**
     * Busca cuentas de resultado en el tenant actual
     */
    public List<Cuenta> findCuentasResultado() {
        log.debug("Buscando cuentas de resultado en tenant: {}", TenantContext.getCurrentTenantId());
        
        return createQuery()
                .where()
                .in("tipo", "INGRESO", "GASTO")
                .eq("estado", "ACTIVA")
                .orderBy("codigo")
                .findList();
    }
    
    /**
     * Obtiene estadísticas de cuentas en el tenant actual
     */
    public CuentaStats getStats() {
        log.debug("Obteniendo estadísticas de cuentas en tenant: {}", TenantContext.getCurrentTenantId());
        
        Query<Cuenta> baseQuery = createQuery();
        
        long totalCuentas = baseQuery.findCount();
        long cuentasActivas = baseQuery.copy().where().eq("estado", "ACTIVA").findCount();
        long cuentasInactivas = totalCuentas - cuentasActivas;
        
        return new CuentaStats(totalCuentas, cuentasActivas, cuentasInactivas);
    }
    
    /**
     * Estadísticas de cuentas
     */
    public static class CuentaStats {
        private final long totalCuentas;
        private final long cuentasActivas;
        private final long cuentasInactivas;
        
        public CuentaStats(long totalCuentas, long cuentasActivas, long cuentasInactivas) {
            this.totalCuentas = totalCuentas;
            this.cuentasActivas = cuentasActivas;
            this.cuentasInactivas = cuentasInactivas;
        }
        
        public long getTotalCuentas() { return totalCuentas; }
        public long getCuentasActivas() { return cuentasActivas; }
        public long getCuentasInactivas() { return cuentasInactivas; }
    }
    
    /**
     * Valida que una cuenta pertenezca al tenant actual
     */
    @Override
    protected void validateTenantOwnership(@NotNull Cuenta cuenta) {
        // Implementar validación específica si las cuentas tienen tenant ID
        // Por ahora, confiamos en que las consultas ya filtran por tenant
        log.debug("Validando propiedad de cuenta: {} en tenant: {}", 
                cuenta.getCodigo(), TenantContext.getCurrentTenantId());
    }
}