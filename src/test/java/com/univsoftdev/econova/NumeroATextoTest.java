package com.univsoftdev.econova;

import com.univsoftdev.econova.NumeroATexto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author 
 */
public class NumeroATextoTest {

    /**
     * Test of convertirNumeroAPalabras method, of class NumeroATexto.
     */
    @Test
    public void testConvertirNumeroAPalabras() {
        System.out.println("convertirNumeroAPalabras");
        double numero = 10520.93;
        String expResult = "DIEZ MIL QUINIENTOS VEINTE CON 93/100";
        String result = NumeroATexto.convertirNumeroAPalabras(numero);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testConvertirNumeroAPalabrasMenor10() {
        System.out.println("testConvertirNumeroAPalabrasMenor10");
        double numero = 9;
        String expResult = "NUEVE CON 00/100";
        String result = NumeroATexto.convertirNumeroAPalabras(numero);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testConvertirNumeroAPalabrasCasoEsp100() {
        System.out.println("testConvertirNumeroAPalabrasCasoEsp100");
        double numero = 100;
        String expResult = "CIEN CON 00/100";
        String result = NumeroATexto.convertirNumeroAPalabras(numero);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testConvertirNumeroAPalabrasSoloCero() {
        System.out.println("testConvertirNumeroAPalabrasSoloCero");
        double numero = 0;
        String expResult = "CERO CON 00/100";
        String result = NumeroATexto.convertirNumeroAPalabras(numero);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testConvertirNumeroAPalabrasNumeroMenor1_000_000_000() {
        System.out.println("testConvertirNumeroAPalabrasNumeroMenor1_000_000_000");
        double numero = 1_000_000_000;
        String expResult = "UN BILLON CON 00/100";
        String result = NumeroATexto.convertirNumeroAPalabras(numero);
        assertEquals(expResult, result);
    }
    
}
