package com.univsoftdev.econova.config.service;

import com.univsoftdev.econova.config.model.Exercise;
import com.univsoftdev.econova.config.model.Period;
import com.univsoftdev.econova.config.repository.ExerciseRepository;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import com.univsoftdev.econova.core.exception.BusinessLogicException;
import com.univsoftdev.econova.core.service.BaseService;
import io.ebean.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio avanzado para gestión de ejercicios contables. Incluye operaciones
 * para manejo de ejercicios, períodos asociados y validaciones complejas.
 */
@Slf4j
@Singleton
public class EjercicioService extends BaseService<Exercise, ExerciseRepository> {

    private final PeriodoService periodoService;

    @Inject
    public EjercicioService(ExerciseRepository repository, PeriodoService periodoService) {
        super(repository);
        this.periodoService = periodoService;
    }

    /**
     * Busca un ejercicio por nombre
     */
    @Transactional
    public Optional<Exercise> findByNombre(String nombre) {
        return repository.findByName(nombre);
    }

    /**
     * Busca un ejercicio por año
     *
     * @param year
     * @return
     */
    @Transactional
    public Optional<Exercise> findByYear(int year) {
        if (year < 1900 || year > 2100) {
            return Optional.empty();
        }
        return repository.findByYear(year);
    }

    /**
     * Crea un nuevo ejercicio contable con validaciones
     */
    @Transactional
    public Exercise crearEjercicio(String nombre, int year, LocalDate fechaInicio, LocalDate fechaFin) {
        // Validaciones
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BusinessLogicException("El nombre del ejercicio no puede ser nulo o vacío");
        }

        validarFechasEjercicio(fechaInicio, fechaFin);
        validarYearConsistente(year, fechaInicio, fechaFin);
        validarSolapamientoEjercicios(fechaInicio, fechaFin);

        Exercise nuevoEjercicio = new Exercise(nombre, year, fechaInicio, fechaFin, List.of());
        save(nuevoEjercicio); // Usar método de la clase base

        log.info("Nuevo ejercicio creado: {} ({})", nombre, year);
        return nuevoEjercicio;
    }

    /**
     * Inicia un ejercicio (marca como iniciado)
     */
    @Transactional
    public Exercise iniciarEjercicio(Long ejercicioId) {
        if (ejercicioId == null) {
            throw new BusinessLogicException("El ID del ejercicio no puede ser nulo");
        }

        Optional<Exercise> optEjercicio = findById(ejercicioId);
        if (optEjercicio.isPresent()) {
            var ejercicio = optEjercicio.get();
            if (ejercicio.isInitiated()) {
                throw new BusinessLogicException("El ejercicio ya está iniciado");
            }

            ejercicio.setInitiated(true);
            update(ejercicio); // Usar método de la clase base

            log.info("Ejercicio {} iniciado", ejercicio.getName());
            return ejercicio;
        }

        return null;
    }

    /**
     * Cierra un ejercicio (marca como no corriente y cierra todos sus períodos)
     */
    @Transactional
    public Exercise cerrarEjercicio(Long ejercicioId) {
        if (ejercicioId == null) {
            throw new BusinessLogicException("El ID del ejercicio no puede ser nulo");
        }

        Optional<Exercise> optEjercicio = findById(ejercicioId);
        if (optEjercicio.isPresent()) {
            var ejercicio = optEjercicio.get();
            // Validar que no haya períodos abiertos
            if (ejercicio.getPeriodos().stream().anyMatch(Period::isCurrent)) {
                throw new BusinessLogicException("No se puede cerrar un ejercicio con períodos activos");
            }

            ejercicio.setCurrent(false);
            update(ejercicio);

            log.info("Ejercicio {} cerrado", ejercicio.getName());
            return ejercicio;
        }
        return null;
    }

    /**
     * Establece un ejercicio como el actual
     * @param ejercicioId
     * @return 
     */
    @Transactional
    public Exercise establecerEjercicioActual(Long ejercicioId) {
        if (ejercicioId == null) {
            throw new BusinessLogicException("El ID del ejercicio no puede ser nulo");
        }

        // Desmarcar cualquier ejercicio actual
        repository.createQuery(Exercise.class)
                .where()
                .eq("current", true)
                .eq("deleted", false)
                .asUpdate()
                .set("current", false)
                .update();

        // Marcar el nuevo ejercicio como actual
        Optional<Exercise> optEjercicio = findById(ejercicioId);
        if (optEjercicio.isPresent()) {
            var nuevoActual = optEjercicio.get();
            nuevoActual.setCurrent(true);
            update(nuevoActual);

            log.info("Ejercicio {} establecido como actual", nuevoActual.getName());
            return nuevoActual;
        }
        return null;
    }

    /**
     * Obtiene el ejercicio actual (marcado como current)
     * @return 
     */
    @Transactional
    public Optional<Exercise> obtenerEjercicioActual() {
        return repository.createQuery(Exercise.class)
                .where()
                .eq("current", true)
                .eq("deleted", false)
                .findOneOrEmpty();
    }

    /**
     * Obtiene el ejercicio correspondiente a una fecha específica
     */
    @Transactional
    public Optional<Exercise> obtenerEjercicioPorFecha(LocalDate fecha) {
        if (fecha == null) {
            return Optional.empty();
        }

        return repository.createQuery(Exercise.class)
                .where()
                .le("fechaInicio", fecha)
                .ge("fechaFin", fecha)
                .eq("deleted", false)
                .findOneOrEmpty();
    }

    /**
     * Agrega un período al ejercicio con validaciones
     */
    @Transactional
    public Exercise agregarPeriodo(Long ejercicioId, Period periodo) {
        if (ejercicioId == null) {
            throw new BusinessLogicException("El ID del ejercicio no puede ser nulo");
        }
        if (periodo == null) {
            throw new BusinessLogicException("El período no puede ser nulo");
        }

        Optional<Exercise> optEjercicio = findById(ejercicioId);
        if (optEjercicio.isPresent()) {
            var ejercicio = optEjercicio.get();
            validarPeriodoDentroEjercicio(periodo, ejercicio);

            ejercicio.addPeriodo(periodo);
            update(ejercicio);

            log.info("Período {} agregado al ejercicio {}", periodo.getName(), ejercicio.getName());
            return ejercicio;
        }
        return null;
    }

    /**
     * Obtiene todas las transacciones del ejercicio ordenadas por fecha
     */
    @Transactional
    public List<Transaction> obtenerTransaccionesEjercicio(Long ejercicioId) {
        if (ejercicioId == null) {
            return List.of();
        }

        return repository.createQuery(Transaction.class)
                .where()
                .eq("period.exercise.id", ejercicioId)
                .eq("deleted", false)
                .orderBy("fecha asc")
                .findList();
    }

    /**
     * Genera períodos mensuales automáticamente para el ejercicio
     */
    @Transactional
    public Exercise generarPeriodosMensuales(Long ejercicioId) {
        if (ejercicioId == null) {
            throw new BusinessLogicException("El ID del ejercicio no puede ser nulo");
        }

        Optional<Exercise> optEjercicio = findById(ejercicioId);
        if (optEjercicio.isPresent()) {
            var ejercicio = optEjercicio.get();
            if (!ejercicio.getPeriodos().isEmpty()) {
                throw new BusinessLogicException("El ejercicio ya tiene períodos definidos");
            }

            LocalDate fechaActual = ejercicio.getStartDate();
            LocalDate fechaFinEjercicio = ejercicio.getEndDate();

            while (!fechaActual.isAfter(fechaFinEjercicio)) {
                YearMonth yearMonth = YearMonth.from(fechaActual);
                LocalDate inicioMes = yearMonth.atDay(1);
                LocalDate finMes = yearMonth.atEndOfMonth();

                // Ajustar al rango del ejercicio
                if (inicioMes.isBefore(ejercicio.getStartDate())) {
                    inicioMes = ejercicio.getStartDate();
                }
                if (finMes.isAfter(ejercicio.getEndDate())) {
                    finMes = ejercicio.getEndDate();
                }

                // Solo crear período si tiene días válidos
                if (!inicioMes.isAfter(finMes)) {
                    String nombrePeriodo = String.format("%s %d",
                            obtenerNombreMes(inicioMes.getMonthValue()),
                            inicioMes.getYear());

                    // Crear el período directamente
                    Period nuevoPeriodo = new Period(nombrePeriodo, inicioMes, finMes);
                    ejercicio.addPeriodo(nuevoPeriodo);
                }

                // Avanzar al siguiente mes
                fechaActual = finMes.plusDays(1);
            }

            // Guardar el ejercicio actualizado
            update(ejercicio);
            return ejercicio;
        }
        return null;
    }

    /**
     * Valida que las fechas del ejercicio sean coherentes
     */
    private void validarFechasEjercicio(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) {
            throw new BusinessLogicException("Las fechas no pueden ser nulas");
        }

        if (inicio.isAfter(fin)) {
            throw new BusinessLogicException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        if (fin.isAfter(inicio.plusYears(2))) {
            throw new BusinessLogicException("Un ejercicio no puede durar más de 2 años");
        }

        if (inicio.isBefore(LocalDate.now().minusYears(10))) {
            throw new BusinessLogicException("No se pueden crear ejercicios con más de 10 años de antigüedad");
        }
    }

    /**
     * Valida que el año coincida con las fechas del ejercicio
     */
    private void validarYearConsistente(int year, LocalDate inicio, LocalDate fin) {
        if (inicio.getYear() > year || fin.getYear() < year) {
            throw new BusinessLogicException("El año debe coincidir con las fechas del ejercicio");
        }
    }

    /**
     * Valida que no se solape con otros ejercicios
     */
    private void validarSolapamientoEjercicios(LocalDate startDate, LocalDate endDate) {
        List<Exercise> ejerciciosSolapados = repository.createQuery(Exercise.class)
                .where()
                .eq("deleted", false)
                .le("startDate", startDate)
                .ge("endDate", endDate)
                .findList();

        if (!ejerciciosSolapados.isEmpty()) {
            throw new BusinessLogicException("El ejercicio se solapa con: "
                    + ejerciciosSolapados.get(0).getName());
        }
    }

    /**
     * Valida que un período esté dentro del rango del ejercicio
     */
    private void validarPeriodoDentroEjercicio(Period periodo, Exercise ejercicio) {
        if (periodo.getStartDate().isBefore(ejercicio.getStartDate())
                || periodo.getEndDate().isAfter(ejercicio.getEndDate())) {
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
