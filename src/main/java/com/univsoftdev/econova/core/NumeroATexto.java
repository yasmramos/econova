package com.univsoftdev.econova.core;

import jakarta.validation.constraints.NotNull;

public class NumeroATexto {

    // Arrays para las unidades, decenas y centenas en palabras
    private static final String[] UNIDADES = {
        "", "UNO", "DOS", "TRES", "CUATRO", "CINCO", "SEIS", "SIETE", "OCHO", "NUEVE"
    };
    private static final String[] DECENAS = {
        "", "DIEZ", "VEINTE", "TREINTA", "CUARENTA", "CINCUENTA", "SESENTA", "SETENTA", "OCHENTA", "NOVENTA"
    };
    private static final String[] ESPECIALES = {
        "DIEZ", "ONCE", "DOCE", "TRECE", "CATORCE", "QUINCE", "DIECISEIS", "DIECISIETE", "DIECIOCHO", "DIECINUEVE"
    };
    private static final String[] CENTENAS = {
        "", "CIENTO", "DOSCIENTOS", "TRESCIENTOS", "CUATROCIENTOS", "QUINIENTOS", "SEISCIENTOS", "SETECIENTOS", "OCHOCIENTOS", "NOVECIENTOS"
    };

    /**
     * Método para convertir números a palabras
     *
     * @param numero
     * @return
     */
    @NotNull
    public static String convertirNumeroAPalabras(final double numero) {
        // Separar la parte entera y la parte decimal
        final long parteEntera = (long) numero;
        final int parteDecimal = (int) Math.round((numero - parteEntera) * 100);

        // Convertir la parte entera a palabras
        final String parteEnteraTexto = convertirParteEntera(parteEntera);

        // Formatear la parte decimal como fracción
        final String parteDecimalTexto = " CON " + String.format("%02d", parteDecimal) + "/100";

        // Combinar ambas partes
        return parteEnteraTexto + parteDecimalTexto;
    }

    /**
     * Método auxiliar para convertir la parte entera a palabras
     *
     * @param numero
     * @return
     */
    private static String convertirParteEntera(final long numero) {
        if (numero == 0) {
            return "CERO";
        } else if (numero < 10) {
            return UNIDADES[(int) numero];
        } else if (numero < 20) {
            return ESPECIALES[(int) numero - 10];
        } else if (numero < 100) {
            final int decena = (int) (numero / 10);
            final int unidad = (int) (numero % 10);
            return DECENAS[decena] + (unidad > 0 ? " Y " + UNIDADES[unidad] : "");
        } else if (numero < 1000) {
            final int centena = (int) (numero / 100);
            final int resto = (int) (numero % 100);
            if (resto == 0 && centena == 1) {
                return "CIEN"; // Caso especial para "CIEN"
            }
            return CENTENAS[centena] + (resto > 0 ? " " + convertirParteEntera(resto) : "");
        } else if (numero < 1_000_000) {
            final long millar = numero / 1000;
            final long resto = numero % 1000;
            final String millarTexto = millar == 1 ? "MIL" : convertirParteEntera(millar) + " MIL";
            return millarTexto + (resto > 0 ? " " + convertirParteEntera(resto) : "");
        } else if (numero < 1_000_000_000) {
            final long millon = numero / 1_000_000;
            final long resto = numero % 1_000_000;
            final String millonTexto = millon == 1 ? "UN MILLON" : convertirParteEntera(millon) + " MILLONES";
            return millonTexto + (resto > 0 ? " " + convertirParteEntera(resto) : "");
        } else if (numero < 1_000_000_000_000L) {
            final long billon = numero / 1_000_000_000;
            final long resto = numero % 1_000_000_000;
            final String billonTexto = billon == 1 ? "UN BILLON" : convertirParteEntera(billon) + " BILLONES";
            return billonTexto + (resto > 0 ? " " + convertirParteEntera(resto) : "");
        } else if (numero < 1_000_000_000_000_000L) {
            final long trillon = numero / 1_000_000_000_000L;
            final long resto = numero % 1_000_000_000_000L;
            final String trillonTexto = trillon == 1 ? "UN TRILLON" : convertirParteEntera(trillon) + " TRILLONES";
            return trillonTexto + (resto > 0 ? " " + convertirParteEntera(resto) : "");
        } else {
            return "NÚMERO DEMASIADO GRANDE.";
        }
    }
}
