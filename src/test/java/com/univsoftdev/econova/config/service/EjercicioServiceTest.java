package com.univsoftdev.econova.config.service;

import com.univsoftdev.econova.config.model.Ejercicio;
import io.ebean.Database;
import io.ebean.Query;
import io.ebean.ExpressionList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EjercicioServiceTest {

    @Mock
    private Database database;

    @Mock
    private Query<Ejercicio> query;

    @Mock
    private ExpressionList<Ejercicio> expressionList;

    @InjectMocks
    private EjercicioService ejercicioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String encryptionKey = System.getenv().getOrDefault("ECONOVA_ENCRYPTION_KEY", "your-32-character-encryption-key");
        System.setProperty("config.encryption.password", encryptionKey);
    }

    @Test
    void testFindByNombre() {
        // Given
        String nombre = "test";
        Ejercicio mockEjercicio = new Ejercicio();

        when(database.find(Ejercicio.class)).thenReturn(query);
        when(query.where()).thenReturn(expressionList);
        when(expressionList.eq("nombre", nombre)).thenReturn(expressionList);
        when(expressionList.findOneOrEmpty()).thenReturn(Optional.of(mockEjercicio));

        // When
        Optional<Ejercicio> result = ejercicioService.findByNombre(nombre);

        // Then
        assertTrue(result.isPresent());
        assertEquals(mockEjercicio, result.get());

        verify(database).find(Ejercicio.class);
        verify(query).where();
        verify(expressionList).eq("nombre", nombre);
        verify(expressionList).findOneOrEmpty();
    }

    @Test
    void testFindByYear() {
        // Given
        int year = 2023;
        Ejercicio mockEjercicio = new Ejercicio();

        when(database.find(Ejercicio.class)).thenReturn(query);
        when(query.where()).thenReturn(expressionList);
        when(expressionList.eq("year", year)).thenReturn(expressionList);
        when(expressionList.findOneOrEmpty()).thenReturn(Optional.of(mockEjercicio));

        // When
        Optional<Ejercicio> result = ejercicioService.findByYear(year);

        // Then
        assertTrue(result.isPresent());
        assertEquals(mockEjercicio, result.get());

        verify(database).find(Ejercicio.class);
        verify(query).where();
        verify(expressionList).eq("year", year);
        verify(expressionList).findOneOrEmpty();
    }

}
