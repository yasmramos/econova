package com.univsoftdev.econova.security;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SecureEncryptionUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int KEY_LENGTH = 256;
    private static final int IV_LENGTH = 16;
    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 100000;
    private static final String ENCRYPTED_FILE_HEADER = "ENCRYPTED_CONFIG_V2:";
    private static final String ENCRYPTED_PREFIX = "enc:";
    private final SecretKey secretKey;
    private final SecureRandom random;
    private final byte[] salt;

    public SecureEncryptionUtils(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.random = new SecureRandom();
        this.salt = generateSalt();
        this.secretKey = deriveKey(password, this.salt);
    }

    public SecureEncryptionUtils(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.random = new SecureRandom();
        this.salt = salt.clone();
        this.secretKey = deriveKey(password, this.salt);
    }

    private SecretKey deriveKey(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM);
    }

    private byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Encrypts a configuration file
     */
    public void encryptFile(Path inputFile, Path outputFile) throws Exception {
        byte[] fileContent = Files.readAllBytes(inputFile);
        byte[] encryptedContent = encryptFileContent(fileContent);
        Files.write(outputFile, encryptedContent);
    }

    /**
     * Decrypts a configuration file
     */
    public void decryptFile(Path encryptedFile, Path outputFile) throws Exception {
        byte[] encryptedContent = Files.readAllBytes(encryptedFile);
        byte[] decryptedContent = decryptFileContent(encryptedContent);
        Files.write(outputFile, decryptedContent);
    }

    /**
     * Encrypts file content with embedded salt and IV
     */
    public byte[] encryptFileContent(byte[] content) throws Exception {
        // Generate salt and IV
        byte[] salt = generateSalt();
        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(iv);

        // Derive key
        SecretKey key = deriveKeyFromSalt(salt);
        
        // Encrypt content
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(content);

        // Create final format: HEADER + SALT + IV + ENCRYPTED_DATA
        byte[] header = ENCRYPTED_FILE_HEADER.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[header.length + salt.length + iv.length + encrypted.length];

        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(salt, 0, result, header.length, salt.length);
        System.arraycopy(iv, 0, result, header.length + salt.length, iv.length);
        System.arraycopy(encrypted, 0, result, header.length + salt.length + iv.length, encrypted.length);

        return result;
    }

    private SecretKey deriveKeyFromSalt(byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // This method is for file encryption using the instance's password
        KeySpec spec = new PBEKeySpec(null, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), ALGORITHM);
    }

    /**
     * Decrypts file content
     */
    public byte[] decryptFileContent(byte[] encryptedData) throws Exception {
        // Verify header
        byte[] header = ENCRYPTED_FILE_HEADER.getBytes(StandardCharsets.UTF_8);
        if (encryptedData.length < header.length + SALT_LENGTH + IV_LENGTH) {
            throw new IllegalArgumentException("Invalid encrypted file format");
        }

        // Check header
        for (int i = 0; i < header.length; i++) {
            if (encryptedData[i] != header[i]) {
                throw new IllegalArgumentException("Invalid encrypted file header");
            }
        }

        // Extract salt, IV, and encrypted data
        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];

        System.arraycopy(encryptedData, header.length, salt, 0, SALT_LENGTH);
        System.arraycopy(encryptedData, header.length + SALT_LENGTH, iv, 0, IV_LENGTH);

        int encryptedStart = header.length + SALT_LENGTH + IV_LENGTH;
        byte[] encrypted = new byte[encryptedData.length - encryptedStart];
        System.arraycopy(encryptedData, encryptedStart, encrypted, 0, encrypted.length);

        // Derive key and decrypt
        SecretKey key = deriveKeyFromSalt(salt);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        return cipher.doFinal(encrypted);
    }

    /**
     * Checks if a file is encrypted
     */
    public static boolean isEncryptedFile(Path file) throws IOException {
        if (!Files.exists(file) || Files.size(file) == 0) {
            return false;
        }

        byte[] header = ENCRYPTED_FILE_HEADER.getBytes(StandardCharsets.UTF_8);
        byte[] fileStart = new byte[header.length];

        try (InputStream is = Files.newInputStream(file)) {
            int bytesRead = is.read(fileStart);
            if (bytesRead < header.length) {
                return false;
            }
        }

        return Arrays.equals(header, fileStart);
    }

    /**
     * Encrypts individual values
     */
    public String encrypt(String plaintext) throws Exception {
        if (plaintext == null || plaintext.isEmpty()) {
            return plaintext;
        }

        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // Combine IV and encrypted data
        byte[] combined = new byte[IV_LENGTH + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
        System.arraycopy(encrypted, 0, combined, IV_LENGTH, encrypted.length);

        return ENCRYPTED_PREFIX + Base64.getEncoder().encodeToString(combined);
    }

    /**
     * Decrypts individual values
     */
    public String decrypt(String encryptedValue) throws Exception {
        if (encryptedValue == null || !encryptedValue.startsWith(ENCRYPTED_PREFIX)) {
            return encryptedValue;
        }

        String base64Data = encryptedValue.substring(ENCRYPTED_PREFIX.length());
        byte[] combined = Base64.getDecoder().decode(base64Data);

        if (combined.length <= IV_LENGTH) {
            throw new IllegalArgumentException("Invalid encrypted value format");
        }

        byte[] iv = new byte[IV_LENGTH];
        byte[] encrypted = new byte[combined.length - IV_LENGTH];

        System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
        System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}