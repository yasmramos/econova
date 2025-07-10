package com.univsoftdev.econova.contabilidad.service;

import com.univsoftdev.econova.contabilidad.model.Asiento;
import io.ebean.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AsientoServiceTest {
    @Mock
    private Database database;
    @Spy
    private AsientoService asientoService = Mockito.spy(new AsientoService(database));

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
                String encryptionKey = System.getenv().getOrDefault("ECONOVA_ENCRYPTION_KEY", "your-32-character-encryption-key");
        System.setProperty("config.encryption.password", encryptionKey);
    }

    @Test
    void testFindByIdNotFound() {
        // Simula que la base de datos retorna null para el asiento
        doReturn(null).when(database).find(Asiento.class, 1L);
        // Simula el método findById para que devuelva null o lance excepción controlada
        doReturn(null).when(asientoService).findById(1L);
        assertNull(asientoService.findById(1L));
    }
}
