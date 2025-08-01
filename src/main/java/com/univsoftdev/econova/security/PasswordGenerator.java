package com.univsoftdev.econova.security;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Generador de contraseñas seguras con características criptográficamente fuertes.
 * 
 * <p>Esta clase genera contraseñas que cumplen con los estándares de seguridad:
 * <ul>
 *   <li>Longitud mínima configurable (recomendado 12+ caracteres)</li>
 *   <li>Inclusión garantizada de caracteres de todos los tipos</li>
 *   <li>Uso de {@link SecureRandom} para generación criptográficamente segura</li>
 *   <li>Evita caracteres confusos como 0, O, 1, l, I</li>
 *   <li>Shuffle seguro para distribución aleatoria</li>
 * </ul>
 * </p>
 * 
 * <p><strong>Caracteres utilizados:</strong></p>
 * <ul>
 *   <li>Mayúsculas: {@code ABCDEFGHJKLMNPQRSTUVWXYZ} (excluye I, O)</li>
 *   <li>Minúsculas: {@code abcdefghijkmnpqrstuvwxyz} (excluye l, o)</li>
 *   <li>Dígitos: {@code 23456789} (excluye 0, 1)</li>
 *   <li>Especiales: {@code !@#$%^&*-_=+?}</li>
 * </ul>
 * 
 * @author UnivSoftDev Team
 * @version 1.0
 * @since 1.0
 * 
 * @see SecureRandom
 * @see PasswordHasher
 */
public class PasswordGenerator {

    /**
     * Caracteres mayúsculos permitidos (excluye I y O para evitar confusión).
     */
    private static final String UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    
    /**
     * Caracteres minúsculos permitidos (excluye l y o para evitar confusión).
     */
    private static final String LOWER = "abcdefghijkmnpqrstuvwxyz";
    
    /**
     * Dígitos permitidos (excluye 0 y 1 para evitar confusión).
     */
    private static final String DIGITS = "23456789";
    
    /**
     * Caracteres especiales seguros para contraseñas.
     */
    private static final String SPECIAL = "!@#$%^&*-_=+?";
    
    /**
     * Generador de números aleatorios criptográficamente seguro.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Genera una contraseña segura con la longitud especificada.
     * 
     * <p>La contraseña generada garantiza al menos un carácter de cada tipo:
     * mayúscula, minúscula, dígito y carácter especial.</p>
     * 
     * <p><strong>Recomendaciones de longitud:</strong></p>
     * <ul>
     *   <li>Mínimo: 12 caracteres (para cuentas normales)</li>
     *   <li>Recomendado: 16+ caracteres (para cuentas administrativas)</li>
     *   <li>Alto seguridad: 20+ caracteres</li>
     * </ul>
     * 
     * @param length Longitud deseada de la contraseña (mínimo 12 caracteres)
     * @return Array de caracteres con la contraseña generada
     * @throws IllegalArgumentException si la longitud es menor a 12 caracteres
     * 
     * @see #clearPassword(char[])
     * @see SecureRandom
     */
    public static char[] generateStrongPassword(int length) {
        if (length < 12) {
            throw new IllegalArgumentException("La longitud mínima debe ser 12 caracteres");
        }

        char[] password = new char[length];

        // Asegurar al menos un carácter de cada tipo requerido
        password[0] = UPPER.charAt(RANDOM.nextInt(UPPER.length()));
        password[1] = LOWER.charAt(RANDOM.nextInt(LOWER.length()));
        password[2] = DIGITS.charAt(RANDOM.nextInt(DIGITS.length()));
        password[3] = SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length()));

        // Combinar todos los caracteres permitidos para el resto
        String allCharacters = UPPER + LOWER + DIGITS + SPECIAL;

        // Generar el resto de caracteres aleatorios
        for (int i = 4; i < length; i++) {
            password[i] = allCharacters.charAt(RANDOM.nextInt(allCharacters.length()));
        }

        // Mezclar los caracteres usando Fisher-Yates shuffle para distribución uniforme
        shuffleArray(password);

        return password;
    }

    /**
     * Limpia de forma segura un array de caracteres sobrescribiéndolo con ceros.
     * 
     * <p>Este método es crucial para la seguridad ya que previene que las
     * contraseñas permanezcan en memoria después de su uso.</p>
     * 
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * {@code
     * char[] password = PasswordGenerator.generateStrongPassword(16);
     * // ... usar la contraseña ...
     * PasswordGenerator.clearPassword(password);
     * }
     * </pre>
     * 
     * @param password Array de caracteres a limpiar (puede ser null)
     * 
     * @see Arrays#fill(char[], char)
     */
    public static void clearPassword(char[] password) {
        if (password != null) {
            Arrays.fill(password, '\0');
        }
    }
    
    /**
     * Implementación del algoritmo Fisher-Yates shuffle para mezclar arrays.
     * 
     * <p>Este algoritmo proporciona una distribución uniforme y es resistente
     * a ciertos tipos de ataques criptográficos.</p>
     * 
     * @param array Array a mezclar
     */
    private static void shuffleArray(char[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }
}
