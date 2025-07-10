package com.univsoftdev.econova.core.utils;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.util.Base64;

public class Argon2Util {

    private static final int SALT_LENGTH = 16; // bytes
    private static final int HASH_LENGTH = 32; // bytes (256 bits)
    private static final int ITERATIONS = 3; 
    private static final int MEMORY_KB = 65536; // 64MB
    private static final int PARALLELISM = 4; // hilos

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    // 1. Generar salt aleatorio
    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    // 2. Crear parámetros de Argon2
    public static Argon2Parameters createParameters(byte[] salt) {
        return new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withIterations(ITERATIONS)
                .withMemoryAsKB(MEMORY_KB)
                .withParallelism(PARALLELISM)
                .build();
    }

    // 3. Generar hash a partir de contraseña y parámetros
    public static byte[] generateHash(String password, Argon2Parameters parameters) {
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(parameters);
        byte[] hash = new byte[HASH_LENGTH];
        generator.generateBytes(password.getBytes(), hash);
        return hash;
    }

    // 4. Combinar salt + hash para almacenamiento
    public static byte[] combineSaltAndHash(byte[] salt, byte[] hash) {
        byte[] combined = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(hash, 0, combined, salt.length, hash.length);
        return combined;
    }

    // 5. Extraer salt de un hash combinado
    public static byte[] extractSalt(byte[] combinedHash) {
        byte[] salt = new byte[SALT_LENGTH];
        System.arraycopy(combinedHash, 0, salt, 0, SALT_LENGTH);
        return salt;
    }

    // 6. Verificar contraseña
    public static boolean verifyPassword(String password, byte[] combinedHash) {
        byte[] salt = extractSalt(combinedHash);
        byte[] storedHash = new byte[combinedHash.length - SALT_LENGTH];
        System.arraycopy(combinedHash, SALT_LENGTH, storedHash, 0, storedHash.length);

        Argon2Parameters params = createParameters(salt);
        byte[] testHash = generateHash(password, params);
        
        return MessageDigest.isEqual(testHash, storedHash);
    }

    // Método auxiliar para codificar en Base64
    public static String encodeBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    // Método auxiliar para decodificar de Base64
    public static byte[] decodeBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }
}