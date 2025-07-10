package com.univsoftdev.econova.config.service;

import jakarta.inject.Inject;
import com.univsoftdev.econova.config.model.Ejercicio;
import com.univsoftdev.econova.config.model.Periodo;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import com.univsoftdev.econova.core.Service;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import io.ebean.Database;
import io.ebean.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio avanzado para gestión de ejercicios contables. Incluye operaciones
 * para manejo de ejercicios, períodos asociados y validaciones complejas.
 */
@Slf4j
@Singleton
public class EjercicioService extends Service<Ejercicio> {

    private final PeriodoService periodoService;

    @Inject
    public EjercicioService(Database database, PeriodoService periodoService) {
        super(database, Ejercicio.class);
        this.periodoService = periodoService;
    }

    @Transactional
    public Optional<Ejercicio> findByNombre(String nombre) {
        return findBy("nombre", nombre);
    }

    @Transactional
    public Optional<Ejercicio> findByYear(int year) {
        return findBy("year", year);
    }

    /**
     * Crea un nuevo ejercicio contable con validaciones
     */
    public Ejercicio crearEjercicio(String nombre, int year, LocalDate fechaInicio, LocalDate fechaFin) {
        validarFechasEjercicio(fechaInicio, fechaFin);
        validarYearConsistente(year, fechaInicio, fechaFin);
        validarSolapamientoEjercicios(fechaInicio, fechaFin);

        Ejercicio nuevoEjercicio = new Ejercicio(nombre, year, fechaInicio, fechaFin, List.of());
        database.save(nuevoEjercicio);

        log.info("Nuevo ejercicio creado: {} ({})", nombre, year);
        return nuevoEjercicio;
    }

    /**
     * Inicia un ejercicio (marca como iniciado)
     */
    public Ejercicio iniciarEjercicio(Long ejercicioId) {
        Ejercicio ejercicio = database.find(Ejercicio.class, ejercicioId);

        if (ejercicio.isIniciado()) {
            throw new BusinessLogicException("El ejercicio ya está iniciado");
        }

        ejercicio.setIniciado(true);
        database.update(ejercicio);

        log.info("Ejercicio {} iniciado", ejercicio.getNombre());
        return ejercicio;
    }

    /**
     * Cierra un ejercicio (marca como no corriente y cierra todos sus períodos)
     */
    public Ejercicio cerrarEjercicio(Long ejercicioId) {
        Ejercicio ejercicio = database.find(Ejercicio.class, ejercicioId);

        // Validar que no haya períodos abiertos
        if (ejercicio.getPeriodos().stream().anyMatch(Periodo::isCurrent)) {
            throw new BusinessLogicException("No se puede cerrar un ejercicio con períodos activos");
        }

        ejercicio.setCurrent(false);
        database.update(ejercicio);

        log.info("Ejercicio {} cerrado", ejercicio.getNombre());
        return ejercicio;
    }

    /**
     * Establece un ejercicio como el actual
     */
    public Ejercicio establecerEjercicioActual(Long ejercicioId) {
        // Desmarcar cualquier ejercicio actual
        database.createQuery(Ejercicio.class)
                .where()
                .eq("current", true)
                .asUpdate()
                .set("current", false)
                .update();

        // Marcar el nuevo ejercicio como actual
        Ejercicio nuevoActual = database.find(Ejercicio.class, ejercicioId);

        nuevoActual.setCurrent(true);
        database.update(nuevoActual);

        log.info("Ejercicio {} establecido como actual", nuevoActual.getNombre());
        return nuevoActual;
    }

    /**
     * Obtiene el ejercicio actual (marcado como current)
     */
    public Optional<Ejercicio> obtenerEjercicioActual() {
        return database.createQuery(Ejercicio.class)
                .where()
                .eq("current", true)
                .findOneOrEmpty();
    }

    /**
     * Obtiene el ejercicio correspondiente a una fecha específica
     */
    public Optional<Ejercicio> obtenerEjercicioPorFecha(LocalDate fecha) {
        return database.createQuery(Ejercicio.class)
                .where()
                .le("fechaInicio", fecha)
                .ge("fechaFin", fecha)
                .findOneOrEmpty();
    }

    /**
     * Obtiene el ejercicio por año
     */
    public Optional<Ejercicio> obtenerEjercicioPorYear(int year) {
        return database.createQuery(Ejercicio.class)
                .where()
                .eq("year", year)
                .findOneOrEmpty();
    }

    /**
     * Agrega un período al ejercicio con validaciones
     */
    public Ejercicio agregarPeriodo(Long ejercicioId, Periodo periodo) {
        Ejercicio ejercicio = database.find(Ejercicio.class, ejercicioId);

        validarPeriodoDentroEjercicio(periodo, ejercicio);

        ejercicio.addPeriodo(periodo);
        database.update(ejercicio);

        log.info("Período {} agregado al ejercicio {}", periodo.getNombre(), ejercicio.getNombre());
        return ejercicio;
    }

    /**
     * Obtiene todas las transacciones del ejercicio ordenadas por fecha
     */
    public List<Transaccion> obtenerTransaccionesEjercicio(Long ejercicioId) {
        return database.createQuery(Transaccion.class)
                .where()
                .eq("periodo.ejercicio.id", ejercicioId)
                .orderBy("fecha asc")
                .findList();
    }

    /**
     * Genera períodos mensuales automáticamente para el ejercicio
     */
    public Ejercicio generarPeriodosMensuales(Long ejercicioId) {
        Ejercicio ejercicio = database.find(Ejercicio.class, ejercicioId);

        if (!ejercicio.getPeriodos().isEmpty()) {
            throw new BusinessLogicException("El ejercicio ya tiene períodos definidos");
        }

        LocalDate inicio = ejercicio.getFechaInicio();
        int year = inicio.getYear();

        for (int mes = 1; mes <= 12; mes++) {
            LocalDate inicioMes = LocalDate.of(year, mes, 1);
            LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

            // Ajustar el primer y último mes según las fechas del ejercicio
            if (mes == 1) {
                inicioMes = ejercicio.getFechaInicio();
            }
            if (mes == 12) {
                finMes = ejercicio.getFechaFin();
            }

            String nombrePeriodo = String.format("%s %d", obtenerNombreMes(mes), year);
            Periodo periodo = new Periodo(nombrePeriodo, inicioMes, finMes, ejercicio);

            periodoService.crearPeriodo(nombrePeriodo, inicioMes, finMes, ejercicio);
        }

        log.info("12 períodos mensuales generados para el ejercicio {}", ejercicio.getNombre());
        return ejercicio;
    }

    /**
     * Valida que las fechas del ejercicio sean coherentes
     */
    private void validarFechasEjercicio(LocalDate inicio, LocalDate fin) {
        if (inicio.isAfter(fin)) {
            throw new BusinessLogicException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        if (fin.isAfter(inicio.plusYears(1))) {
            throw new BusinessLogicException("Un ejercicio no puede durar más de 1 año");
        }

        if (inicio.isBefore(LocalDate.now().minusYears(5))) {
            throw new BusinessLogicException("No se pueden crear ejercicios con más de 5 años de antigüedad");
        }
    }

    /**
     * Valida que el año coincida con las fechas del ejercicio
     */
    private void validarYearConsistente(int year, LocalDate inicio, LocalDate fin) {
        if (inicio.getYear() != year || fin.getYear() != year) {
            throw new BusinessLogicException("El año debe coincidir con las fechas del ejercicio");
        }
    }

    /**
     * Valida que no se solape con otros ejercicios
     */
    private void validarSolapamientoEjercicios(LocalDate inicio, LocalDate fin) {
        List<Ejercicio> ejerciciosSolapados = database.createQuery(Ejercicio.class)
                .where()
                .or()
                .and()
                .le("fechaInicio", fin)
                .ge("fechaFin", inicio)
                .endOr()
                .findList();

        if (!ejerciciosSolapados.isEmpty()) {
            throw new BusinessLogicException("El ejercicio se solapa con: "
                    + ejerciciosSolapados.get(0).getNombre());
        }
    }

    /**
     * Valida que un período esté dentro del rango del ejercicio
     */
    private void validarPeriodoDentroEjercicio(Periodo periodo, Ejercicio ejercicio) {
        if (periodo.getFechaInicio().isBefore(ejercicio.getFechaInicio())
                || periodo.getFechaFin().isAfter(ejercicio.getFechaFin())) {
            throw new BusinessLogicException("El período debe estar dentro del rango del ejercicio");
        }
    }

    /**
     * Obtiene el nombre del mes en español
     */
    private String obtenerNombreMes(int mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes - 1];
    }
}
