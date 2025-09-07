package com.univsoftdev.econova.contabilidad.service;

import com.univsoftdev.econova.contabilidad.model.Audit;
import com.univsoftdev.econova.contabilidad.repository.AuditoriaRepository;
import com.univsoftdev.econova.core.service.BaseService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class AuditoriaService extends BaseService<Audit, AuditoriaRepository>{

    @Inject
    public AuditoriaService(AuditoriaRepository database) {
        super(database);
    }
    
    /**
     * Registra una acción genérica en la auditoría.
     * @param accion Acción realizada (ej: "CIERRE_PERIODO")
     * @param entidad Entidad afectada (ej: "Periodo", "Ejercicio")
     * @param detalles Detalles adicionales (ej: "Cierre del periodo 2025-01")
     * @param usuario Usuario que realiza la acción
     */
    public void registrarAccion(String accion, String entidad, String detalles, LocalDateTime date) {
        Audit registro = new Audit(accion, entidad, detalles, date);
        save(registro);
        log.info("Auditoría registrada: {}", registro);
    }

    /**
     * Registra un cierre contable en la auditoría.
     * @param entidad Entidad cerrada (ej: "Periodo", "Ejercicio")
     * @param detalles Detalles del cierre
     * @param usuario Usuario que realiza el cierre
     */
    public void registrarCierre(String entidad, String detalles,LocalDateTime date) {
        registrarAccion("CIERRE", entidad, detalles, date);
    }

    /**
     * Ejemplo: registra un cierre genérico (sin detalles específicos).
     */
    public void registrarCierre() {
        registrarAccion("CIERRE", "GENERAL", "Cierre contable genérico", LocalDateTime.now());
    }
}
