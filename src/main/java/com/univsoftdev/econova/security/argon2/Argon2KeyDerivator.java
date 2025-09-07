package com.univsoftdev.econova.security.argon2;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Implementación mejorada de Argon2 para derivación de claves criptográficas.
 *
 * <p>
 * Extiende la funcionalidad del Argon2PasswordHasher para permitir:
 * <ul>
 * <li>Generación controlada de salt</li>
 * <li>Derivación de claves en formato binario</li>
 * <li>Configuración ajustable para seguridad mejorada</li>
 * </ul>
 * </p>
 * 
 * // Crear derivador Argon2KeyDerivator derivator = new Argon2KeyDerivator();
 *
 * // Derivar nueva clave char[] password = "miContraseñaSecreta".toCharArray();
 * try { Argon2KeyDerivator.KeyDerivationResult result =
 * derivator.deriveKey(password);
 *
 * // Obtener resultados byte[] claveDerivada = result.getKey(); // 32 bytes
 * (256 bits) byte[] salt = result.getSalt();
 *
 * // Usar la clave para cifrado, etc. // ...
 *
 * // Verificar después boolean valida = derivator.verifyKey(password, salt,
 * claveDerivada); } finally { // Limpiar el password de memoria
 * Arrays.fill(password, '\0'); }
 */
public class Argon2KeyDerivator {

    private static final int DEFAULT_ITERATIONS = 10;
    private static final int DEFAULT_MEMORY = 65536; // 64MB
    private static final int DEFAULT_PARALLELISM = 4;
    private static final int DEFAULT_SALT_LENGTH = 16;
    private static final int DEFAULT_HASH_LENGTH = 32; // 256 bits

    private final Argon2 argon2;
    private final SecureRandom secureRandom;

    public Argon2KeyDerivator() {
        this.argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        this.secureRandom = new SecureRandom();
    }

    public KeyDerivationResult deriveKey(char[] password) {
        byte[] salt = generateSalt(DEFAULT_SALT_LENGTH);
        return deriveKey(password, salt);
    }

    public KeyDerivationResult deriveKey(char[] password, byte[] salt) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (salt == null || salt.length == 0) {
            throw new IllegalArgumentException("Salt cannot be null or empty");
        }

        try {
            // Convertimos el salt a String Base64 para incluirlo en la contraseña
            String saltBase64 = java.util.Base64.getEncoder().encodeToString(salt);

            // Concatenamos password y salt
            char[] passwordWithSalt = concatenate(password, saltBase64.toCharArray());

            try {
                // Generamos el hash
                String encodedHash = argon2.hash(
                        DEFAULT_ITERATIONS,
                        DEFAULT_MEMORY,
                        DEFAULT_PARALLELISM,
                        passwordWithSalt
                );

                // Extraemos el hash raw
                byte[] rawHash = extractRawHash(encodedHash);

                // Aseguramos la longitud correcta
                if (rawHash.length > DEFAULT_HASH_LENGTH) {
                    rawHash = Arrays.copyOf(rawHash, DEFAULT_HASH_LENGTH);
                }

                return new KeyDerivationResult(rawHash, salt);
            } finally {
                Arrays.fill(passwordWithSalt, '\0');
            }
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private char[] concatenate(char[] a, char[] b) {
        char[] result = new char[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private byte[] extractRawHash(String encodedHash) {
        String[] parts = encodedHash.split("\\$");
        if (parts.length < 6) {
            throw new IllegalStateException("Invalid Argon2 hash format");
        }

        String base64Hash = parts[5];
        return java.util.Base64.getDecoder().decode(base64Hash);
    }

    public byte[] generateSalt(int length) {
        byte[] salt = new byte[length];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public boolean verifyKey(char[] password, byte[] salt, byte[] expectedKey) {
        KeyDerivationResult result = deriveKey(password, salt);
        try {
            return Arrays.equals(result.getKey(), expectedKey);
        } finally {
            Arrays.fill(result.getKey(), (byte) 0);
        }
    }

    public static class KeyDerivationResult {

        private final byte[] key;
        private final byte[] salt;

        public KeyDerivationResult(byte[] key, byte[] salt) {
            this.key = key;
            this.salt = salt;
        }

        public byte[] getKey() {
            return key;
        }

        public byte[] getSalt() {
            return salt;
        }
    }
}
