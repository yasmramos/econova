package com.univsoftdev.econova.contabilidad.service;

import jakarta.inject.Singleton;
import com.univsoftdev.econova.contabilidad.model.Auditoria;
import com.univsoftdev.econova.core.Service;
import io.ebean.Database;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class AuditoriaService extends Service<Auditoria>{

    @Inject
    public AuditoriaService(Database database) {
        super(database, Auditoria.class);
    }
    
    /**
     * Registra una acción genérica en la auditoría.
     * @param accion Acción realizada (ej: "CIERRE_PERIODO")
     * @param entidad Entidad afectada (ej: "Periodo", "Ejercicio")
     * @param detalles Detalles adicionales (ej: "Cierre del periodo 2025-01")
     * @param usuario Usuario que realiza la acción
     */
    public void registrarAccion(String accion, String entidad, String detalles, String usuario) {
        Auditoria registro = Auditoria.registrar(accion, entidad, detalles, usuario);
        save(registro);
        log.info("Auditoría registrada: {}", registro);
    }

    /**
     * Registra un cierre contable en la auditoría.
     * @param entidad Entidad cerrada (ej: "Periodo", "Ejercicio")
     * @param detalles Detalles del cierre
     * @param usuario Usuario que realiza el cierre
     */
    public void registrarCierre(String entidad, String detalles, String usuario) {
        registrarAccion("CIERRE", entidad, detalles, usuario);
    }

    /**
     * Ejemplo: registra un cierre genérico (sin detalles específicos).
     */
    public void registrarCierre() {
        registrarAccion("CIERRE", "GENERAL", "Cierre contable genérico", "sistema");
    }
}
