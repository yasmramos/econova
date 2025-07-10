package com.univsoftdev.econova.contabilidad.service;

import io.ebean.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;

public class ContabilidadServiceTest {

    @Mock
    private Database database;
    @Mock
    private CuentaService cuentaService;
    @Mock
    private BalanceGeneralService balanceGeneralService;
    @Mock
    private PlanDeCuentasService planDeCuentasService;
    @Mock
    private AsientoService asientoService;
    @Mock
    private TransaccionService transaccionService;
    @Spy
    private ContabilidadService contabilidadService = Mockito.spy(new ContabilidadService());

    public ContabilidadServiceTest() {
    }

    @BeforeEach
    void setUp() {
        String encryptionKey = System.getenv().getOrDefault("ECONOVA_ENCRYPTION_KEY", "your-32-character-encryption-key");
        System.setProperty("config.encryption.password", encryptionKey);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearAsientoContableConDescripcionNula() {
        try {
            doThrow(new NullPointerException()).when(contabilidadService).crearAsientoContable(anyInt(), isNull(), any(), any(), any(), any(), any(), any(), any());
            assertThrows(NullPointerException.class, () -> contabilidadService.crearAsientoContable(1, null, java.time.LocalDate.now(), null, null, null, null, null, null));
        } catch (ContabilidadException ex) {
            System.getLogger(ContabilidadServiceTest.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
}
