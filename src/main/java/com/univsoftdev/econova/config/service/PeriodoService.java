package com.univsoftdev.econova.config.service;

import jakarta.inject.Inject;
import com.univsoftdev.econova.config.model.Ejercicio;
import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import com.univsoftdev.econova.core.Service;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import io.ebean.Database;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio avanzado para gestión de períodos contables con operaciones
 * específicas. Incluye validaciones de fechas, manejo de transacciones y
 * sincronización con ejercicios.
 */
@Slf4j
@Singleton
public class PeriodoService extends Service<Periodo> {

    @Inject
    public PeriodoService(Database database) {
        super(database, Periodo.class);
    }

    /**
     * Crea un nuevo período con validación de solapamiento de fechas
     */
    public Periodo crearPeriodo(String nombre, LocalDate fechaInicio, LocalDate fechaFin, Ejercicio ejercicio) {
        validarFechasPeriodo(fechaInicio, fechaFin);
        validarSolapamientoPeriodos(fechaInicio, fechaFin, ejercicio);

        Periodo nuevoPeriodo = new Periodo(nombre, fechaInicio, fechaFin, ejercicio);
        database.save(nuevoPeriodo);

        log.info("Nuevo período creado: {}", nuevoPeriodo.getNombreConFechas());
        return nuevoPeriodo;
    }

    /**
     * Actualiza las fechas de un período con validaciones
     */
    public Periodo actualizarFechasPeriodo(Long periodoId, LocalDate nuevaFechaInicio, LocalDate nuevaFechaFin) {
        Periodo periodo = database.find(Periodo.class, periodoId);

        validarFechasPeriodo(nuevaFechaInicio, nuevaFechaFin);
        validarSolapamientoPeriodos(nuevaFechaInicio, nuevaFechaFin, periodo.getEjercicio(), periodoId);

        periodo.setFechaInicio(nuevaFechaInicio);
        periodo.setFechaFin(nuevaFechaFin);
        database.update(periodo);

        log.info("Fechas actualizadas para período ID {}: {} - {}", periodoId, nuevaFechaInicio, nuevaFechaFin);
        return periodo;
    }

    /**
     * Obtiene el período actual (marcado como current)
     */
    public Optional<Periodo> obtenerPeriodoActual() {
        return database.createQuery(Periodo.class)
                .where()
                .eq("current", true)
                .findOneOrEmpty();
    }

    /**
     * Establece un período como el actual
     */
    public Periodo establecerPeriodoActual(Long periodoId) {
        // Primero desmarcar cualquier período actual
        database.createQuery(Periodo.class)
                .where()
                .eq("current", true)
                .asUpdate()
                .set("current", false)
                .update();

        // Marcar el nuevo período como actual
        Periodo nuevoActual = database.find(Periodo.class, periodoId);

        nuevoActual.setCurrent(true);
        database.update(nuevoActual);

        log.info("Período {} establecido como actual", nuevoActual.getNombre());
        return nuevoActual;
    }

    /**
     * Obtiene todos los períodos activos (que incluyen la fecha actual)
     */
    public List<Periodo> obtenerPeriodosActivos() {
        LocalDate hoy = LocalDate.now();
        return database.createQuery(Periodo.class)
                .where()
                .le("fechaInicio", hoy)
                .ge("fechaFin", hoy)
                .findList();
    }

    /**
     * Agrega una transacción al período con validación de fecha
     */
    public void agregarTransaccion(Long periodoId, Transaccion transaccion) {
        Periodo periodo = database.find(Periodo.class, periodoId);

        periodo.addTransaccion(transaccion);
        database.save(periodo);
    }

    /**
     * Obtiene todas las transacciones de un período ordenadas por fecha
     */
    public List<Transaccion> obtenerTransaccionesPeriodo(Long periodoId) {
        return database.createQuery(Transaccion.class)
                .where()
                .eq("periodo.id", periodoId)
                .orderBy("fecha asc")
                .findList();
    }

    /**
     * Valida que las fechas no se solapen con otros períodos del mismo
     * ejercicio
     */
    private void validarSolapamientoPeriodos(LocalDate inicio, LocalDate fin, Ejercicio ejercicio) {
        validarSolapamientoPeriodos(inicio, fin, ejercicio, null);
    }

    private void validarSolapamientoPeriodos(LocalDate inicio, LocalDate fin, Ejercicio ejercicio, Long periodoIdExcluir) {
        List<Periodo> periodosSolapados = database.createQuery(Periodo.class)
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
    public Optional<Periodo> obtenerPeriodoPorFecha(LocalDate fecha) {
        return database.createQuery(Periodo.class)
                .where()
                .le("fechaInicio", fecha)
                .ge("fechaFin", fecha)
                .findOneOrEmpty();
    }

    /**
     * Cierra un período (impide nuevas transacciones)
     */
    public Periodo cerrarPeriodo(Long periodoId) {
        Periodo periodo = database.find(Periodo.class, periodoId);

        // Aquí podrías agregar validaciones adicionales (ej. que todas las transacciones estén conciliadas)
        periodo.setCurrent(false);
        database.update(periodo);

        log.info("Período {} cerrado", periodo.getNombre());
        return periodo;
    }
}
