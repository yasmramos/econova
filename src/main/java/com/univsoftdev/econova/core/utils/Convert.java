package com.univsoftdev.econova.core.utils;

import lombok.SneakyThrows;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Clase utilitaria para conversión segura entre tipos de datos primitivos y
 * objetos. Proporciona métodos robustos para convertir entre String, números y
 * otros tipos básicos con manejo de errores y capacidades de formateo.
 *
 * @author UnivSoftDev
 * @version 2.0
 * @since 1.0
 */
public class Convert {

    private static final int DEFAULT_SCALE = 4;
    private static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;
    private static Locale defaultLocale = Locale.getDefault();
    private static final Map<String, Boolean> TRUE_VALUES = new HashMap<>();

    static {
        TRUE_VALUES.put("true", true);
        TRUE_VALUES.put("yes", true);
        TRUE_VALUES.put("1", true);
        TRUE_VALUES.put("y", true);
        TRUE_VALUES.put("sí", true);
        TRUE_VALUES.put("si", true);
        TRUE_VALUES.put("on", true);
        TRUE_VALUES.put("enable", true);
    }

    /**
     * Convierte un String a int de forma segura.
     *
     * @param number String a convertir
     * @return valor int convertido
     * @throws NumberFormatException si el String no contiene un entero válido
     */
    @SneakyThrows
    public static int toInt(String number) {
        if (number == null) {
            throw new NumberFormatException("El valor no puede ser nulo");
        }
        return Integer.parseInt(number.trim());
    }

    /**
     * Convierte un String a int con valor por defecto en caso de error.
     *
     * @param number String a convertir
     * @param defaultValue valor a retornar si la conversión falla
     * @return valor int convertido o defaultValue si falla
     */
    public static int toInt(String number, int defaultValue) {
        try {
            return toInt(number);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Convierte un objeto Number a int.
     *
     * @param number Objeto Number a convertir
     * @return valor int
     */
    public static int toInt(Number number) {
        return number != null ? number.intValue() : 0;
    }

    /**
     * Convierte un String a double de forma segura.
     *
     * @param number String a convertir
     * @return valor double convertido
     * @throws NumberFormatException si el String no contiene un número válido
     */
    @SneakyThrows
    public static double toDouble(String number) {
        if (number == null) {
            throw new NumberFormatException("El valor no puede ser nulo");
        }
        return Double.parseDouble(number.trim());
    }

    /**
     * Convierte un String a double con valor por defecto en caso de error.
     *
     * @param number String a convertir
     * @param defaultValue valor a retornar si la conversión falla
     * @return valor double convertido o defaultValue si falla
     */
    public static double toDouble(String number, double defaultValue) {
        try {
            return toDouble(number);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Convierte un double a String.
     *
     * @param number valor double a convertir
     * @return String representando el número
     */
    public static String toString(double number) {
        return String.valueOf(number);
    }

    /**
     * Convierte un double a String con formato y precisión específicos.
     *
     * @param number valor double a convertir
     * @param scale número de decimales
     * @return String formateado
     */
    public static String toString(double number, int scale) {
        return BigDecimal.valueOf(number)
                .setScale(scale, DEFAULT_ROUNDING_MODE)
                .toString();
    }

    /**
     * Convierte un double a String con formato de moneda.
     *
     * @param number valor double a convertir
     * @param scale número de decimales
     * @param currencySymbol símbolo de moneda (ej. "$")
     * @return String formateado como moneda
     */
    public static String toCurrencyString(double number, int scale, String currencySymbol) {
        return currencySymbol + toString(number, scale);
    }

    /**
     * Convierte un String a boolean. Valores considerados true: "true", "yes",
     * "1", "y", "sí" (case insensitive)
     *
     * @param value String a convertir
     * @return valor boolean convertido
     */
    public static boolean toBoolean(String value) {
        if (value == null) {
            return false;
        }
        String normalized = value.trim().toLowerCase();
        return normalized.equals("true")
                || normalized.equals("yes")
                || normalized.equals("1")
                || normalized.equals("y")
                || normalized.equals("sí");
    }

    /**
     * Convierte un valor a boolean con reglas extendidas.
     *
     * @param value Valor a convertir (String, Number, Boolean)
     * @return true para valores considerados verdaderos
     */
    public static boolean toBoolean(Object value) {
        if (value == null) {
            return false;
        }

        if (value instanceof Boolean aBoolean) {
            return aBoolean;
        }

        if (value instanceof Number number) {
            return number.intValue() != 0;
        }

        String strVal = value.toString().trim().toLowerCase();
        return TRUE_VALUES.containsKey(strVal);
    }

    /**
     * Convierte un boolean a String.
     *
     * @param value boolean a convertir
     * @return "true" o "false"
     */
    public static String toString(boolean value) {
        return String.valueOf(value);
    }

    /**
     * Convierte un String a long.
     *
     * @param number String a convertir
     * @return valor long convertido
     * @throws NumberFormatException si el String no contiene un long válido
     */
    @SneakyThrows
    public static long toLong(String number) {
        if (number == null) {
            throw new NumberFormatException("El valor no puede ser nulo");
        }
        return Long.parseLong(number.trim());
    }

    /**
     * Convierte un String a long con valor por defecto en caso de error.
     *
     * @param number String a convertir
     * @param defaultValue valor a retornar si la conversión falla
     * @return valor long convertido o defaultValue si falla
     */
    public static long toLong(String number, long defaultValue) {
        try {
            return toLong(number);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Verifica si un String puede convertirse a int.
     *
     * @param value String a verificar
     * @return true si es convertible a int, false en caso contrario
     */
    public static boolean isInteger(String value) {
        try {
            toInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Verifica si un String puede convertirse a double.
     *
     * @param value String a verificar
     * @return true si es convertible a double, false en caso contrario
     */
    public static boolean isDouble(String value) {
        try {
            toDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Convierte un String localizado a double.
     *
     * @param value String con formato localizado
     * @param locale Locale a usar para la conversión
     * @return valor double
     * @throws ParseException si el formato no coincide con el locale
     */
    @SneakyThrows
    public static double toLocalizedDouble(String value, Locale locale) {
        NumberFormat format = NumberFormat.getInstance(locale);
        return format.parse(value.trim()).doubleValue();
    }

    /**
     * Formatea un número según el locale especificado.
     *
     * @param number Número a formatear
     * @param locale Locale a usar
     * @return String formateado
     */
    public static String toLocalizedString(Number number, Locale locale) {
        return NumberFormat.getInstance(locale).format(number);
    }

    /**
     * Convierte un array de Strings a array de ints.
     *
     * @param values Array de Strings
     * @return Array de ints
     */
    public static int[] toIntArray(String[] values) {
        if (values == null) {
            return new int[0];
        }

        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = toInt(values[i], 0);
        }
        return result;
    }

    /**
     * Formatea un número con el patrón especificado.
     *
     * @param number Número a formatear
     * @param pattern Patrón de formato (ej. "#,##0.00")
     * @return String formateado
     */
    public static String formatNumber(Number number, String pattern) {
        DecimalFormat formatter = new DecimalFormat(pattern);
        return formatter.format(number);
    }

    /**
     * Convierte un número a formato de porcentaje.
     *
     * @param number Número a convertir (ej. 0.85 para 85%)
     * @param decimalPlaces Número de decimales
     * @return String en formato de porcentaje
     */
    public static String toPercentage(Number number, int decimalPlaces) {
        DecimalFormat formatter = new DecimalFormat("%#" + (decimalPlaces > 0 ? "." + repeat('0', decimalPlaces) : ""));
        return formatter.format(number.doubleValue());
    }

    /**
     * Convierte un array de bytes a su representación hexadecimal.
     *
     * @param bytes Array de bytes
     * @return String hexadecimal
     */
    public static String toHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    /**
     * Establece el locale por defecto para conversiones.
     *
     * @param locale Nuevo locale por defecto
     */
    public static void setDefaultLocale(Locale locale) {
        defaultLocale = locale;
    }

    /**
     * Verifica si un String puede convertirse al tipo especificado.
     *
     * @param value String a verificar
     * @param type Clase del tipo objetivo (ej. Integer.class)
     * @return true si es convertible
     */
    public static boolean isConvertible(String value, Class<?> type) {
        try {
            if (type == Integer.class || type == int.class) {
                toInt(value);
                return true;
            }
            // Implementar para otros tipos
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static String repeat(char c, int count) {
        return new String(new char[count]).replace('\0', c);
    }
}
