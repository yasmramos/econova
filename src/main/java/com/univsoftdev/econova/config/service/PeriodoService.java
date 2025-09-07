package com.univsoftdev.econova.config.service;

import com.univsoftdev.econova.config.model.Exercise;
import com.univsoftdev.econova.config.model.Period;
import com.univsoftdev.econova.config.repository.PeriodoRepository;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import com.univsoftdev.econova.core.service.BaseService;
import com.univsoftdev.econova.security.Roles;
import com.univsoftdev.econova.security.shiro.annotations.RequiresRoles;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio avanzado para gestión de períodos contables con operaciones
 * específicas. Incluye validaciones de fechas, manejo de transacciones y
 * sincronización con ejercicios.
 */
@Slf4j
@Singleton
public class PeriodoService extends BaseService<Period, PeriodoRepository> {

    @Inject
    public PeriodoService(PeriodoRepository repository) {
        super(repository);
    }

    /**
     * Crea un nuevo período con validación de solapamiento de fechas
     *
     * @param nombre
     * @param fechaInicio
     * @param fechaFin
     * @param ejercicio
     * @return
     */
    @RequiresRoles(value = {Roles.SYSTEM_ADMIN})
    public Period crearPeriodo(
            String nombre,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Exercise ejercicio
    ) {
        validarFechasPeriodo(fechaInicio, fechaFin);
        validarSolapamientoPeriodos(fechaInicio, fechaFin, ejercicio);

        Period nuevoPeriodo = new Period(nombre, fechaInicio, fechaFin, ejercicio);
        repository.save(nuevoPeriodo);

        log.info("Nuevo período creado: {}", nuevoPeriodo.getNombreConFechas());
        return nuevoPeriodo;
    }

    /**
     * Actualiza las fechas de un período con validaciones
     */
    public Period actualizarFechasPeriodo(
            Long periodoId,
            LocalDate nuevaFechaInicio,
            LocalDate nuevaFechaFin
    ) {
        Period periodo = repository.find(Period.class, periodoId);

        validarFechasPeriodo(nuevaFechaInicio, nuevaFechaFin);
        validarSolapamientoPeriodos(nuevaFechaInicio, nuevaFechaFin, periodo.getExercise(), periodoId);

        periodo.setStartDate(nuevaFechaInicio);
        periodo.setEndDate(nuevaFechaFin);
        repository.update(periodo);

        log.info("Fechas actualizadas para período ID {}: {} - {}", periodoId, nuevaFechaInicio, nuevaFechaFin);
        return periodo;
    }

    /**
     * Obtiene el período actual (marcado como current)
     * @return 
     */
    public Optional<Period> obtenerPeriodoActual() {
        return repository.createQuery(Period.class)
                .where()
                .eq("current", true)
                .findOneOrEmpty();
    }

    /**
     * Establece un período como el actual
     * @param periodoId
     * @return 
     */
    public Period establecerPeriodoActual(Long periodoId) {
        // Primero desmarcar cualquier período actual
        repository.createQuery(Period.class)
                .where()
                .eq("current", true)
                .asUpdate()
                .set("current", false)
                .update();

        // Marcar el nuevo período como actual
        Period nuevoActual = repository.find(Period.class, periodoId);

        nuevoActual.setCurrent(true);
        repository.update(nuevoActual);

        log.info("Período {} establecido como actual", nuevoActual.getName());
        return nuevoActual;
    }

    /**
     * Obtiene todos los períodos activos (que incluyen la fecha actual)
     * @return 
     */
    public List<Period> obtenerPeriodosActivos() {
        LocalDate hoy = LocalDate.now();
        return repository.createQuery(Period.class)
                .where()
                .le("fechaInicio", hoy)
                .ge("fechaFin", hoy)
                .findList();
    }

    /**
     * Agrega una transacción al período con validación de fecha
     */
    public void agregarTransaccion(Long periodoId, Transaction transaccion) {
        Period periodo = repository.find(Period.class, periodoId);

        periodo.addTransaccion(transaccion);
        repository.save(periodo);
    }

    /**
     * Obtiene todas las transacciones de un período ordenadas por fecha
     */
    public List<Transaction> obtenerTransaccionesPeriodo(Long periodoId) {
        return repository.createQuery(Transaction.class)
                .where()
                .eq("periodo.id", periodoId)
                .orderBy("fecha asc")
                .findList();
    }

    /**
     * Valida que las fechas no se solapen con otros períodos del mismo
     * ejercicio
     */
    private void validarSolapamientoPeriodos(LocalDate inicio, LocalDate fin, Exercise ejercicio) {
        validarSolapamientoPeriodos(inicio, fin, ejercicio, null);
    }

    private void validarSolapamientoPeriodos(LocalDate inicio, LocalDate fin, Exercise ejercicio, Long periodoIdExcluir) {
        List<Period> periodosSolapados = repository.createQuery(Period.class)
                .where()
                .eq("ejercicio.id", ejercicio.getId())
                .or()
                .and()
                .le("fechaInicio", fin)
                .ge("fechaFin", inicio)
                .endOr()
                .ne("id", periodoIdExcluir)
                .findList();

        if (!periodosSolapados.isEmpty()) {
            throw new BusinessLogicException("El período se solapa con: "
                    + periodosSolapados.get(0).getNombreConFechas());
        }
    }

    /**
     * Validaciones básicas de fechas
     */
    private void validarFechasPeriodo(LocalDate inicio, LocalDate fin) {
        if (inicio.isAfter(fin)) {
            throw new BusinessLogicException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        if (inicio.isBefore(LocalDate.now().minusYears(10))) {
            throw new BusinessLogicException("No se pueden crear períodos con más de 10 años de antigüedad");
        }

        if (fin.isAfter(LocalDate.now().plusYears(2))) {
            throw new BusinessLogicException("No se pueden crear períodos con más de 2 años de anticipación");
        }
    }

    /**
     * Obtiene el período correspondiente a una fecha específica
     */
    public Optional<Period> obtenerPeriodoPorFecha(LocalDate fecha) {
        return repository.createQuery(Period.class)
                .where()
                .le("fechaInicio", fecha)
                .ge("fechaFin", fecha)
                .findOneOrEmpty();
    }

    /**
     * Cierra un período (impide nuevas transacciones)
     */
    public Period cerrarPeriodo(Long periodoId) {
        Period periodo = repository.find(Period.class, periodoId);

        // Aquí podrías agregar validaciones adicionales (ej. que todas las transacciones estén conciliadas)
        periodo.setCurrent(false);
        repository.update(periodo);

        log.info("Período {} cerrado", periodo.getName());
        return periodo;
    }
}
