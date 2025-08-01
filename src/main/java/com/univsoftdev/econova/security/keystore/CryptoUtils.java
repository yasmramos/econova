package com.univsoftdev.econova.security.keystore;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Utilidades criptográficas para operaciones de cifrado/descifrado y generación de identificadores.
 * 
 * <p>Esta clase proporciona funcionalidades criptográficas seguras basadas en:
 * <ul>
 *   <li>AES-256 en modo GCM (Galois/Counter Mode) para cifrado autenticado</li>
 *   <li>PBKDF2 con SHA-256 para derivación de claves a partir de contraseñas</li>
 *   <li>Generación de identificadores únicos basados en hardware</li>
 * </ul>
 * </p>
 * 
 * <p><strong>Características de seguridad:</strong></p>
 * <ul>
 *   <li>Cifrado autenticado (confidencialidad e integridad)</li>
 *   <li>Protección contra ataques de diccionario con PBKDF2</li>
 *   <li>Uso de IV aleatorio para cada operación</li>
 *   <li>Limpieza segura de contraseñas en memoria</li>
 * </ul>
 * 
 * @author UnivSoftDev Team
 * @version 1.0
 * @since 1.0
 * 
 * @see Cipher
 * @see SecretKey
 * @see MessageDigest
 */
public class CryptoUtils {

    /**
     * Algoritmo de clave secreta utilizado (AES).
     */
    private static final String APP_SECRET_KEY = "AES";
    
    /**
     * Algoritmo de cifrado utilizado (AES/GCM/NoPadding).
     * GCM proporciona autenticación integrada.
     */
    private static final String APP_CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    
    /**
     * Longitud del Vector de Inicialización para GCM en bytes (96 bits).
     * Este tamaño es recomendado por NIST para GCM.
     */
    private static final int GCM_IV_LENGTH = 12;
    
    /**
     * Longitud de la etiqueta de autenticación GCM en bits.
     * 128 bits proporciona fuerte autenticación.
     */
    private static final int GCM_TAG_LENGTH = 128;
    
    /**
     * Número de iteraciones para PBKDF2.
     * Valor alto para resistir ataques de fuerza bruta.
     */
    private static final int ITERATIONS = 65536;
    
    /**
     * Longitud de la clave derivada en bits (256 bits = 32 bytes).
     * AES-256 es el estándar actual para cifrado fuerte.
     */
    private static final int KEY_LENGTH = 256;

    /**
     * Cifra datos utilizando AES-256-GCM con una contraseña.
     * 
     * <p>El proceso incluye:
     * <ol>
     *   <li>Generación de IV aleatorio de 12 bytes</li>
     *   <li>Derivación de clave usando PBKDF2 con el IV como salt</li>
     *   <li>Cifrado de los datos con AES-GCM</li>
     *   <li>Concatenación del IV y datos cifrados</li>
     * </ol>
     * </p>
     * 
     * <p><strong>Formato del resultado:</strong> IV (12 bytes) + Datos cifrados + Tag de autenticación</p>
     * 
     * @param data Datos a cifrar (no null)
     * @param password Contraseña para derivar la clave (no null)
     * @return Array de bytes con los datos cifrados e IV concatenados
     * @throws IllegalArgumentException si data o password son null
     * @throws RuntimeException si ocurre un error criptográfico
     * 
     * @see #decrypt(byte[], char[])
     * @see #deriveKey(char[], byte[])
     */
    public static byte[] encrypt(byte[] data, char[] password) throws Exception {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        // Generar IV aleatorio
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // Derivar clave a partir de la contraseña
        SecretKey secretKey = deriveKey(password, iv);
        
        // Configurar y realizar el cifrado
        Cipher cipher = Cipher.getInstance(APP_CIPHER_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        byte[] encryptedData = cipher.doFinal(data);
        
        // Concatenar IV y datos cifrados
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
        byteBuffer.put(iv);
        byteBuffer.put(encryptedData);
        return byteBuffer.array();
    }

    /**
     * Descifra datos previamente cifrados con {@link #encrypt(byte[], char[])}.
     * 
     * <p>El proceso incluye:
     * <ol>
     *   <li>Extracción del IV del inicio de los datos cifrados</li>
     *   <li>Derivación de la misma clave usando PBKDF2</li>
     *   <li>Descifrado de los datos con AES-GCM</li>
     *   <li>Verificación automática de la etiqueta de autenticación</li>
     * </ol>
     * </p>
     * 
     * @param encryptedData Datos cifrados con IV concatenado (no null)
     * @param password Contraseña utilizada originalmente para cifrar (no null)
     * @return Array de bytes con los datos descifrados
     * @throws IllegalArgumentException si encryptedData o password son null
     * @throws RuntimeException si la autenticación falla o hay error criptográfico
     * 
     * @see #encrypt(byte[], char[])
     * @see #deriveKey(char[], byte[])
     */
    public static byte[] decrypt(byte[] encryptedData, char[] password) throws Exception {
        if (encryptedData == null) {
            throw new IllegalArgumentException("Encrypted data cannot be null");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (encryptedData.length < GCM_IV_LENGTH) {
            throw new IllegalArgumentException("Invalid encrypted data format");
        }

        // Extraer IV y datos cifrados
        ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);
        byte[] iv = new byte[GCM_IV_LENGTH];
        byteBuffer.get(iv);
        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);

        // Derivar la misma clave
        SecretKey secretKey = deriveKey(password, iv);
        
        // Configurar y realizar el descifrado
        Cipher cipher = Cipher.getInstance(APP_CIPHER_ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        return cipher.doFinal(cipherText);
    }

    /**
     * Deriva una clave criptográfica a partir de una contraseña usando PBKDF2.
     * 
     * <p>Este método utiliza:
     * <ul>
     *   <li>PBKDF2 con HMAC-SHA256 como función PRF</li>
     *   <li>Salt proporcionado (normalmente el IV)</li>
     *   <li>65,536 iteraciones para resistencia contra fuerza bruta</li>
     *   <li>Clave de 256 bits para AES-256</li>
     * </ul>
     * </p>
     * 
     * @param password Contraseña base para la derivación (no null)
     * @param salt Salt para la derivación (no null)
     * @return Clave secreta derivada lista para usar en AES
     * @throws NoSuchAlgorithmException si PBKDF2WithHmacSHA256 no está disponible
     * @throws InvalidKeySpecException si la especificación de clave es inválida
     * 
     * @see SecretKeyFactory
     * @see PBEKeySpec
     */
    private static SecretKey deriveKey(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), APP_SECRET_KEY);
    }

    /**
     * Genera un identificador único basado en características del hardware del sistema.
     * 
     * <p>Combina información del sistema para crear un ID que sea consistente
     * en la misma máquina pero diferente en otras:</p>
     * <ul>
     *   <li>Nombre del sistema operativo</li>
     *   <li>Arquitectura del sistema</li>
     *   <li>Nombre del usuario del sistema</li>
     *   <li>Número de procesadores disponibles</li>
     * </ul>
     * 
     * <p>El resultado se hashea con SHA-256 y se codifica en Base64.</p>
     * 
     * @return String con el identificador de hardware en Base64
     * @throws RuntimeException si ocurre un error durante la generación
     * 
     * @see MessageDigest
     * @see Base64
     */
    public static String generateHardwareId() {
        try {
            // Recopilar información del sistema
            String hardwareInfo = System.getProperty("os.name")
                    + System.getProperty("os.arch")
                    + System.getProperty("user.name")
                    + Runtime.getRuntime().availableProcessors();
            
            // Hash la información para crear un ID fijo pero no reversible
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(hardwareInfo.getBytes());
            
            // Codificar en Base64 para fácil almacenamiento y transmisión
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating hardware ID", e);
        }
    }
    
    /**
     * Cifra una cadena de texto y la devuelve como Base64.
     * 
     * <p>Método de conveniencia que maneja la conversión de String a byte[] y viceversa.</p>
     * 
     * @param plainText Texto a cifrar (no null)
     * @param password Contraseña para el cifrado (no null)
     * @return String con los datos cifrados en formato Base64
     * @throws RuntimeException si ocurre un error durante el cifrado
     */
    public static String encryptString(String plainText, char[] password) {
        if (plainText == null) {
            throw new IllegalArgumentException("Plain text cannot be null");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        
        try {
            byte[] data = plainText.getBytes("UTF-8");
            byte[] encrypted = encrypt(data, password);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting string", e);
        }
    }
    
    /**
     * Descifra una cadena Base64 y la devuelve como texto plano.
     * 
     * <p>Método de conveniencia que maneja la conversión de Base64 a byte[] y viceversa.</p>
     * 
     * @param encryptedText Texto cifrado en Base64 (no null)
     * @param password Contraseña para el descifrado (no null)
     * @return String con el texto descifrado
     * @throws RuntimeException si ocurre un error durante el descifrado
     */
    public static String decryptString(String encryptedText, char[] password) {
        if (encryptedText == null) {
            throw new IllegalArgumentException("Encrypted text cannot be null");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        
        try {
            byte[] encrypted = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted = decrypt(encrypted, password);
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting string", e);
        }
    }
    
    /**
     * Obtiene la longitud del IV utilizado en GCM.
     * 
     * @return Longitud del IV en bytes
     */
    public static int getGcmIvLength() {
        return GCM_IV_LENGTH;
    }
    
    /**
     * Obtiene la longitud de la etiqueta de autenticación GCM.
     * 
     * @return Longitud de la etiqueta en bits
     */
    public static int getGcmTagLength() {
        return GCM_TAG_LENGTH;
    }
    
    /**
     * Obtiene el número de iteraciones de PBKDF2.
     * 
     * @return Número de iteraciones
     */
    public static int getIterations() {
        return ITERATIONS;
    }
}