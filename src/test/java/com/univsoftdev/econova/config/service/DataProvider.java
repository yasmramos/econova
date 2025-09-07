package com.univsoftdev.econova.config.service;

import com.univsoftdev.econova.config.model.Exercise;
import com.univsoftdev.econova.config.model.Period;
import java.time.LocalDate;
import java.util.*;

public class DataProvider {

    public static Optional<Exercise> ejercicioOptionalMock() {
        // Usar constructor con parámetros para evitar problemas de validación
        LocalDate inicio = LocalDate.now();
        LocalDate fin = LocalDate.now().plusYears(1);

        Exercise ejercicio = new Exercise(
                "2025",
                2025,
                inicio,
                fin,
                new ArrayList<>()
        );

        ejercicio.setCurrent(true);
        // No necesitamos llamar setYear() porque ya se establece en el constructor

        return Optional.of(ejercicio);
    }

    public static Exercise ejercicioMock() {
        return ejercicioOptionalMock().get();
    }

    public static Exercise ejercicioIniciado() {
        Exercise ejercicio = ejercicioMock();
        ejercicio.setInitiated(true);
        return ejercicio;
    }

    public static Exercise ejercicioNoActual() {
        Exercise ejercicio = ejercicioMock();
        ejercicio.setCurrent(false);
        return ejercicio;
    }

    public static List<Exercise> ejerciciosVarios() {
        return Arrays.asList(
                ejercicioMock(),
                ejercicioNoActual(),
                ejercicioIniciado()
        );
    }

    public static Exercise ejercicioActual() {
        Exercise ejercicio = ejercicioMock();
        ejercicio.setCurrent(true);
        return ejercicio;
    }

    public static Exercise ejercicioConPeriodos() {
        Exercise ejercicio = ejercicioMock();
        Period periodo = new Period();
        periodo.setCurrent(false);
        ejercicio.addPeriodo(periodo); // Usar addPeriodo en lugar de setPeriodos
        return ejercicio;
    }

    public static Exercise ejercicioConPeriodosActivos() {
        Exercise ejercicio = ejercicioMock();
        Period periodo = new Period();
        periodo.setCurrent(true);
        ejercicio.addPeriodo(periodo); // Usar addPeriodo en lugar de setPeriodos
        return ejercicio;
    }
}
