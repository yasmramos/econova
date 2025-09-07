package com.univsoftdev.econova;

import com.univsoftdev.econova.core.exception.BusinessLogicException;
import jakarta.validation.constraints.NotNull;
import java.util.regex.Pattern;

public class Validations {

    // Expresiones regulares precompiladas para mejor performance
    private static final Pattern EMAIL_PATTERN
            = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
    private static final Pattern PASSWORD_PATTERN
            = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
    private static final Pattern PHONE_PATTERN
            = Pattern.compile("^[+]?[(]?[0-9]{1,4}[)]?[-\\s./0-9]*$");
    private static final Pattern URL_PATTERN
            = Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");
    private static final Pattern DATE_PATTERN
            = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$");

    public static boolean isValidEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Formato de email inválido.");
        }
        return true;
    }
    
    public static void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new BusinessLogicException("La contraseña debe tener al menos 8 caracteres");
        }
        
    }

    public static boolean isValidPassword(@NotNull String password) {
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, "
                    + "incluir mayúsculas, minúsculas, números y caracteres especiales.");
        }
        return true;
    }

    public static boolean isValidPhoneNumber(@NotNull String phoneNumber) {
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("Número de teléfono inválido.");
        }
        return true;
    }

    public static boolean isValidUrl(@NotNull String url) {
        if (!URL_PATTERN.matcher(url).matches()) {
            throw new IllegalArgumentException("URL inválida.");
        }
        return true;
    }

    public static boolean isValidDate(@NotNull String date) {
        if (!DATE_PATTERN.matcher(date).matches()) {
            throw new IllegalArgumentException("Fecha inválida. Formato esperado: YYYY-MM-DD");
        }
        return true;
    }

    public static boolean isNotEmpty(@NotNull String value, String fieldName) {
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " no puede estar vacío.");
        }
        return true;
    }

    public static boolean isLengthValid(@NotNull String value, int min, int max) {
        if (value.length() < min || value.length() > max) {
            throw new IllegalArgumentException("Longitud inválida. Debe estar entre "
                    + min + " y " + max + " caracteres.");
        }
        return true;
    }

    public static boolean isNumberInRange(@NotNull Number value, double min, double max) {
        if (value.doubleValue() < min || value.doubleValue() > max) {
            throw new IllegalArgumentException("Valor fuera de rango. Debe estar entre "
                    + min + " y " + max + ".");
        }
        return true;
    }
}
