package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.contabilidad.TipoCuenta;
import com.univsoftdev.econova.contabilidad.NaturalezaCuenta;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CuentaTest {

    private Cuenta cuenta;

    public CuentaTest() {
    }

    @BeforeEach
    public void setUp() {
        cuenta = new Cuenta("110", "Efectivo en Caja", NaturalezaCuenta.DEUDORA, TipoCuenta.INGRESO, new Moneda("CUP", "Moneda Nacional"));
    }

    /**
     * Test of tieneSaldoNegativo method, of class Cuenta.
     */
    @Test
    public void testTieneSaldoNegativo() {
        System.out.println("tieneSaldoNegativo");
        boolean expResult = false;
        boolean result = cuenta.tieneSaldoNegativo();
        assertEquals(expResult, result);
    }

    /**
     * Test of obtenerSaldoNegativoTotal method, of class Cuenta.
     */
    @Test
    public void testObtenerSaldoNegativoTotal() {
        System.out.println("obtenerSaldoNegativoTotal");
        Cuenta instance = new Cuenta();
        BigDecimal expResult = BigDecimal.ZERO;
        BigDecimal result = instance.obtenerSaldoNegativoTotal();
        assertEquals(expResult, result);

    }

    /**
     * Test of addSubCuenta method, of class Cuenta.
     */
    @Test
    public void testAddSubCuenta() {
        System.out.println("addSubCuenta");
        Cuenta subCuenta = new Cuenta("0010", "Efectivo por Depositar en Banco");
        Cuenta instance = new Cuenta();
        instance.addSubCuenta(subCuenta);

    }

    /**
     * Test of getSaldo method, of class Cuenta.
     */
    @Test
    public void testGetSaldo() {
        System.out.println("getSaldo");
        Cuenta instance = new Cuenta();
        BigDecimal expResult = BigDecimal.ZERO;
        BigDecimal result = instance.getSaldo();
        assertEquals(expResult, result);

    }

    /**
     * Test of setSaldo method, of class Cuenta.
     */
    @Test
    public void testSetSaldo() {
        System.out.println("setSaldo");
        BigDecimal saldo = BigDecimal.ZERO;
        Cuenta instance = new Cuenta();
        instance.setSaldo(saldo);
    }

    /**
     * Test of getCodigo method, of class Cuenta.
     */
    @Test
    public void testGetCodigo() {
        System.out.println("getCodigo");
        String expResult = "110";
        String result = cuenta.getCodigo();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCodigo method, of class Cuenta.
     */
    @Test
    public void testSetCodigo() {
        System.out.println("setCodigo");
        String codigo = "110";
        Cuenta instance = new Cuenta();
        instance.setCodigo(codigo);
    }

    /**
     * Test of toString method, of class Cuenta.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        String expResult = "110 - Efectivo en Caja";
        String result = cuenta.toString();
        assertEquals(expResult, result);
    }

}
