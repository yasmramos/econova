package com.univsoftdev.econova.core.utils;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EncryptionUtil {

    private static final int GCM_IV_LENGTH = 12; // Longitud del IV para GCM
    private static final int GCM_TAG_LENGTH = 128; // Longitud de la etiqueta de autenticación

    /**
     * Deriva una clave AES válida a partir de una clave base.
     *
     * @param secretKey Clave base.
     * @return Clave AES derivada.
     */
    public static SecretKey deriveAESKey(String secretKey) {
        try {
            // Configuración de PBKDF2
            byte[] salt = "fixedSalt12345678".getBytes(StandardCharsets.UTF_8); // Usa un salt fijo o aleatorio
            int iterations = 65536; // Número de iteraciones
            int keyLength = 256; // Longitud deseada en bits

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt, iterations, keyLength);
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("Error al derivar la clave AES: " + e.getMessage());
            throw new RuntimeException("Error al derivar la clave AES", e);
        }
    }

    /**
     * Genera un vector de inicialización (IV) aleatorio.
     *
     * @return IV generado.
     */
    private static byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv); // Genera bytes aleatorios seguros
        return iv;
    }

    /**
     * Cifra un texto usando AES-GCM con una clave personalizada.
     *
     * @param strToEncrypt Texto a cifrar.
     * @param secretKey Clave de cifrado personalizada.
     * @return Texto cifrado codificado en Base64.
     */
    public static String encryptWithCustomKey(String strToEncrypt, String secretKey) {
        try {
            SecretKey aesKey = deriveAESKey(secretKey);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Generar IV
            byte[] iv = generateIV();
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            // Inicializar el cifrado
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);
            byte[] encryptedBytes = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));

            // Combinar IV y texto cifrado
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            // Devolver como Base64
            return Base64.getEncoder().encodeToString(combined);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("Error al cifrar: " + e.getMessage());
            return null;
        }
    }

    /**
     * Descifra un texto cifrado usando AES-GCM con una clave personalizada.
     *
     * @param strToDecrypt Texto cifrado codificado en Base64.
     * @param secretKey Clave de cifrado personalizada.
     * @return Texto descifrado.
     */
    public static String decryptWithCustomKey(String strToDecrypt, String secretKey) {
        try {
            SecretKey aesKey = deriveAESKey(secretKey);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Decodificar Base64
            byte[] combined = Base64.getDecoder().decode(strToDecrypt);

            // Extraer IV y texto cifrado
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedBytes = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, encryptedBytes, 0, encryptedBytes.length);

            // Inicializar el descifrado
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            // Devolver como texto
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("Error al descifrar: " + e.getMessage());
            return null;
        }
    }
}
