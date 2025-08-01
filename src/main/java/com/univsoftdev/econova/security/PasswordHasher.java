package com.univsoftdev.econova.security;

/**
 * Interfaz para operaciones de hash de contraseñas.
 * 
 * <p>Proporciona métodos estándar para:
 * <ul>
 *   <li>Generar hash de contraseñas planas</li>
 *   <li>Verificar contraseñas planas contra hashes almacenados</li>
 * </ul>
 * </p>
 * 
 * <p>Esta interfaz sigue el principio de inversión de dependencias y permite
 * diferentes implementaciones (BCrypt, PBKDF2, Argon2, etc.) sin cambiar
 * el código cliente.</p>
 * 
 * <p><strong>Ejemplo de uso:</strong></p>
 * <pre>
 * {@code
 * PasswordHasher hasher = new BCryptPasswordHasher();
 * String hash = hasher.hash("mypassword123");
 * boolean isValid = hasher.verify(hash, "mypassword123");
 * }
 * </pre>
 * 
 * @author UnivSoftDev Team
 * @version 1.0
 * @since 1.0
 * 
 * @see BCryptPasswordHasher
 * @see PBKDF2PasswordHasher
 */
public interface PasswordHasher {

    /**
     * Genera un hash criptográfico de la contraseña proporcionada.
     * 
     * <p>Este método debe ser determinista para verificación posterior
     * y generalmente incluye un salt aleatorio para prevenir ataques
     * de diccionario y rainbow tables.</p>
     * 
     * @param password La contraseña en texto plano a hashear (no null)
     * @return El hash criptográfico de la contraseña
     * @throws IllegalArgumentException si la contraseña es null
     * @throws RuntimeException si ocurre un error durante el proceso de hashing
     * 
     * @see #verify(String, String)
     */
    String hash(String password);

    /**
     * Verifica si una contraseña en texto plano corresponde a un hash dado.
     * 
     * <p>Este método debe ser resistente a ataques timing (timing attacks)
     * comparando hashes de forma segura.</p>
     * 
     * @param hash El hash almacenado a verificar contra (no null)
     * @param rawPassword La contraseña en texto plano a verificar (no null)
     * @return {@code true} si la contraseña es válida, {@code false} en caso contrario
     * @throws IllegalArgumentException si alguno de los parámetros es null
     * @throws RuntimeException si el formato del hash es inválido
     * 
     * @see #hash(String)
     */
    boolean verify(String hash, String rawPassword);
}