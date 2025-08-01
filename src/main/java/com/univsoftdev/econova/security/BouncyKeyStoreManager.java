package com.univsoftdev.econova.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * Gestor de keystores basado en Bouncy Castle para operaciones criptográficas.
 * 
 * <p>Esta clase proporciona funcionalidades completas para:
 * <ul>
 *   <li>Gestión de keystores (creación, carga, guardado)</li>
 *   <li>Generación de pares de claves RSA</li>
 *   <li>Creación y manejo de certificados X.509 autofirmados</li>
 *   <li>Almacenamiento seguro de claves privadas y certificados</li>
 *   <li>Operaciones de consulta y administración de entradas</li>
 * </ul>
 * </p>
 * 
 * <p>Utiliza el proveedor criptográfico Bouncy Castle para mayor compatibilidad
 * y soporte de algoritmos modernos.</p>
 * 
 * @author UnivSoftDev Team
 * @version 1.0
 * @since 1.0
 * 
 * @see KeyStore
 * @see BouncyCastleProvider
 * @see X509Certificate
 */
@Slf4j
public class BouncyKeyStoreManager {

    /** Algoritmo de firma por defecto para certificados. */
    private static final String DEFAULT_SIGNATURE_ALGORITHM = "SHA256WithRSA";
    
    /** Tamaño de clave por defecto en bits. */
    private static final int DEFAULT_KEY_SIZE = 2048;
    
    /** Días de validez por defecto para certificados. */
    private static final int DEFAULT_VALIDITY_DAYS = 365;

    /** Keystore gestionado por esta instancia. */
    private KeyStore keyStore;
    
    /** Tipo de keystore (JKS, PKCS12, etc.). */
    private final String keyStoreType;
    
    /** Contraseña maestra del keystore. */
    private final char[] password;
    
    /** Algoritmo de firma para certificados generados. */
    private final String signatureAlgorithm;

    /**
     * Bloque estático para registrar el proveedor Bouncy Castle.
     * 
     * <p>Se ejecuta una única vez cuando la clase es cargada por el ClassLoader.</p>
     */
    static {
        // Registrar el proveedor Bouncy Castle
        Security.addProvider(new BouncyCastleProvider());
        log.debug("Bouncy Castle provider registered successfully");
    }

    /**
     * Constructor para crear un nuevo gestor de keystore.
     * 
     * @param keyStoreType Tipo de keystore (ej: "JKS", "PKCS12") (no null)
     * @param password Contraseña maestra del keystore (no null)
     * @param signatureAlgorithm Algoritmo de firma para certificados (puede ser null, usa SHA256WithRSA por defecto)
     * 
     * @throws IllegalArgumentException si keyStoreType o password son null
     * @throws RuntimeException si ocurre un error al crear la instancia de KeyStore
     */
    public BouncyKeyStoreManager(String keyStoreType, char[] password, String signatureAlgorithm) {
        if (keyStoreType == null || keyStoreType.trim().isEmpty()) {
            throw new IllegalArgumentException("KeyStore type cannot be null or empty");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        
        this.keyStoreType = keyStoreType;
        this.password = password.clone(); // Crear copia para seguridad
        this.signatureAlgorithm = signatureAlgorithm != null ? signatureAlgorithm : DEFAULT_SIGNATURE_ALGORITHM;
        
        try {
            this.keyStore = KeyStore.getInstance(keyStoreType, "BC");
            log.debug("KeyStore instance created successfully. Type: {}, Provider: BC", keyStoreType);
        } catch (KeyStoreException | NoSuchProviderException e) {
            log.error("Error creating KeyStore instance. Type: {}, Provider: BC", keyStoreType, e);
            throw new RuntimeException("Error al crear el KeyStore", e);
        }
    }

    /**
     * Crea un nuevo keystore vacío.
     * 
     * <p>Inicializa el keystore sin entradas, listo para añadir claves y certificados.</p>
     * 
     * @throws RuntimeException si ocurre un error durante la inicialización
     * 
     * @see KeyStore#load(java.io.InputStream, char[])
     */
    public void createEmptyKeyStore() {
        try {
            keyStore.load(null, password);
            log.info("Empty KeyStore created successfully. Type: {}", keyStoreType);
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            log.error("Error creating empty KeyStore", e);
            throw new RuntimeException("Error al crear keystore vacío", e);
        }
    }

    /**
     * Carga un keystore existente desde un archivo.
     * 
     * <p>Lee el contenido del archivo y lo carga en la instancia de KeyStore,
     * verificando la contraseña proporcionada.</p>
     * 
     * @param filePath Ruta al archivo del keystore (no null ni vacío)
     * @throws IOException si ocurre un error de E/S
     * @throws RuntimeException si ocurre un error durante la carga
     * @throws IllegalArgumentException si filePath es null o vacío
     * 
     * @see FileInputStream
     * @see KeyStore#load(java.io.InputStream, char[])
     */
    public void loadKeyStore(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        
        log.debug("Loading KeyStore from file: {}", filePath);
        
        try (FileInputStream fis = new FileInputStream(filePath)) {
            keyStore.load(fis, password);
            log.info("KeyStore loaded successfully from file: {}", filePath);
        } catch (NoSuchAlgorithmException | CertificateException e) {
            log.error("Error loading KeyStore from file: {}", filePath, e);
            throw new RuntimeException("Error al cargar el keystore", e);
        }
    }

    /**
     * Guarda el keystore actual en un archivo.
     * 
     * <p>Serializa el contenido del keystore y lo guarda en el archivo especificado,
     * protegido con la contraseña maestra.</p>
     * 
     * @param filePath Ruta donde se guardará el keystore (no null ni vacío)
     * @throws IOException si ocurre un error de E/S
     * @throws RuntimeException si ocurre un error durante el guardado
     * @throws IllegalArgumentException si filePath es null o vacío
     * 
     * @see FileOutputStream
     * @see KeyStore#store(java.io.OutputStream, char[])
     */
    public void saveKeyStore(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        
        log.debug("Saving KeyStore to file: {}", filePath);
        
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            keyStore.store(fos, password);
            log.info("KeyStore saved successfully to file: {}", filePath);
        } catch (NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
            log.error("Error saving KeyStore to file: {}", filePath, e);
            throw new RuntimeException("Error al guardar el keystore", e);
        }
    }

    /**
     * Genera un par de claves RSA con el tamaño especificado.
     * 
     * <p>Utiliza el proveedor Bouncy Castle para la generación criptográficamente segura
     * de claves RSA. Tamaños recomendados: 2048, 3072, o 4096 bits.</p>
     * 
     * @param keySize Tamaño de la clave en bits (mínimo 1024, recomendado 2048+)
     * @return Par de claves pública/privada generado
     * @throws IllegalArgumentException si keySize es menor a 1024
     * @throws RuntimeException si ocurre un error durante la generación
     * 
     * @see KeyPairGenerator
     * @see KeyPair
     */
    public KeyPair generateKeyPair(int keySize) {
        if (keySize < 1024) {
            throw new IllegalArgumentException("Key size must be at least 1024 bits");
        }
        
        log.debug("Generating RSA key pair with size: {} bits", keySize);
        
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
            keyGen.initialize(keySize);
            KeyPair keyPair = keyGen.generateKeyPair();
            log.info("RSA key pair generated successfully. Size: {} bits", keySize);
            return keyPair;
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            log.error("Error generating RSA key pair with size: {} bits", keySize, e);
            throw new RuntimeException("Error al generar par de claves", e);
        }
    }

    /**
     * Añade una entrada de clave privada con certificado al keystore.
     * 
     * <p>Almacena una clave privada protegida con su propia contraseña,
     * junto con su cadena de certificados asociada.</p>
     * 
     * @param alias Nombre único para identificar la entrada (no null)
     * @param privateKey Clave privada a almacenar (no null)
     * @param certificate Certificado asociado a la clave privada (no null)
     * @param keyPassword Contraseña para proteger la clave privada (no null)
     * @throws IllegalArgumentException si algún parámetro es null
     * @throws RuntimeException si ocurre un error durante el almacenamiento
     * 
     * @see KeyStore#setKeyEntry(String, java.security.Key, char[], java.security.cert.Certificate[])
     */
    public void addPrivateKeyEntry(String alias, PrivateKey privateKey, X509Certificate certificate, char[] keyPassword) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        if (privateKey == null) {
            throw new IllegalArgumentException("Private key cannot be null");
        }
        if (certificate == null) {
            throw new IllegalArgumentException("Certificate cannot be null");
        }
        if (keyPassword == null) {
            throw new IllegalArgumentException("Key password cannot be null");
        }
        
        log.debug("Adding private key entry with alias: {}", alias);
        
        try {
            Certificate[] chain = {certificate};
            keyStore.setKeyEntry(alias, privateKey, keyPassword, chain);
            log.info("Private key entry added successfully. Alias: {}", alias);
        } catch (KeyStoreException e) {
            log.error("Error adding private key entry. Alias: {}", alias, e);
            throw new RuntimeException("Error al añadir entrada de clave privada", e);
        }
    }

    /**
     * Añade un certificado al keystore como entrada de confianza.
     * 
     * <p>Almacena un certificado sin clave privada asociada, útil para
     * certificados de autoridades de confianza (CA) o certificados de otros.</p>
     * 
     * @param alias Nombre único para identificar la entrada (no null)
     * @param certificate Certificado a almacenar (no null)
     * @throws IllegalArgumentException si algún parámetro es null
     * @throws RuntimeException si ocurre un error durante el almacenamiento
     * 
     * @see KeyStore#setCertificateEntry(String, java.security.cert.Certificate)
     */
    public void addCertificateEntry(String alias, Certificate certificate) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        if (certificate == null) {
            throw new IllegalArgumentException("Certificate cannot be null");
        }
        
        log.debug("Adding certificate entry with alias: {}", alias);
        
        try {
            keyStore.setCertificateEntry(alias, certificate);
            log.info("Certificate entry added successfully. Alias: {}", alias);
        } catch (KeyStoreException e) {
            log.error("Error adding certificate entry. Alias: {}", alias, e);
            throw new RuntimeException("Error al añadir certificado", e);
        }
    }

    /**
     * Obtiene una clave privada del keystore.
     * 
     * @param alias Nombre de la entrada que contiene la clave privada (no null)
     * @param keyPassword Contraseña de protección de la clave privada (no null)
     * @return La clave privada solicitada
     * @throws IllegalArgumentException si algún parámetro es null
     * @throws RuntimeException si ocurre un error durante la recuperación
     * 
     * @see KeyStore#getKey(String, char[])
     */
    public PrivateKey getPrivateKey(String alias, char[] keyPassword) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        if (keyPassword == null) {
            throw new IllegalArgumentException("Key password cannot be null");
        }
        
        log.debug("Retrieving private key with alias: {}", alias);
        
        try {
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keyPassword);
            if (privateKey != null) {
                log.debug("Private key retrieved successfully. Alias: {}", alias);
            } else {
                log.warn("Private key not found. Alias: {}", alias);
            }
            return privateKey;
        } catch (Exception e) {
            log.error("Error retrieving private key. Alias: {}", alias, e);
            throw new RuntimeException("Error al obtener clave privada", e);
        }
    }

    /**
     * Obtiene un certificado del keystore.
     * 
     * @param alias Nombre de la entrada que contiene el certificado (no null)
     * @return El certificado solicitado o null si no existe
     * @throws IllegalArgumentException si alias es null
     * @throws RuntimeException si ocurre un error durante la recuperación
     * 
     * @see KeyStore#getCertificate(String)
     */
    public X509Certificate getCertificate(String alias) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        
        log.debug("Retrieving certificate with alias: {}", alias);
        
        try {
            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
            if (certificate != null) {
                log.debug("Certificate retrieved successfully. Alias: {}", alias);
            } else {
                log.warn("Certificate not found. Alias: {}", alias);
            }
            return certificate;
        } catch (Exception e) {
            log.error("Error retrieving certificate. Alias: {}", alias, e);
            throw new RuntimeException("Error al obtener certificado", e);
        }
    }

    /**
     * Obtiene la cadena de certificados asociada a una entrada.
     * 
     * @param alias Nombre de la entrada (no null)
     * @return Array de certificados o null si no existe la entrada
     * @throws IllegalArgumentException si alias es null
     * @throws RuntimeException si ocurre un error durante la recuperación
     * 
     * @see KeyStore#getCertificateChain(String)
     */
    public Certificate[] getCertificateChain(String alias) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        
        log.debug("Retrieving certificate chain with alias: {}", alias);
        
        try {
            Certificate[] chain = keyStore.getCertificateChain(alias);
            if (chain != null) {
                log.debug("Certificate chain retrieved successfully. Alias: {}, Chain length: {}", 
                         alias, chain.length);
            } else {
                log.warn("Certificate chain not found. Alias: {}", alias);
            }
            return chain;
        } catch (KeyStoreException e) {
            log.error("Error retrieving certificate chain. Alias: {}", alias, e);
            throw new RuntimeException("Error al obtener cadena de certificados", e);
        }
    }

    /**
     * Genera un certificado X.509 autofirmado.
     * 
     * <p>Crea un certificado digital autofirmado con los parámetros especificados,
     * útil para entornos de desarrollo o certificados internos.</p>
     * 
     * @param keyPair Par de claves asociado al certificado (no null)
     * @param subjectDn Nombre distintivo del sujeto (ej: "CN=localhost, OU=IT, O=MyOrg, C=US") (no null)
     * @param validityDays Días de validez del certificado
     * @return Certificado X.509 autofirmado generado
     * @throws IllegalArgumentException si algún parámetro es null o inválido
     * @throws RuntimeException si ocurre un error durante la generación
     * 
     * @see X509v3CertificateBuilder
     * @see JcaContentSignerBuilder
     */
    public X509Certificate generateSelfSignedCertificate(KeyPair keyPair, String subjectDn, int validityDays) {
        if (keyPair == null) {
            throw new IllegalArgumentException("Key pair cannot be null");
        }
        if (subjectDn == null || subjectDn.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject DN cannot be null or empty");
        }
        if (validityDays <= 0) {
            throw new IllegalArgumentException("Validity days must be positive");
        }
        
        log.debug("Generating self-signed certificate. Subject DN: {}, Validity: {} days", 
                 subjectDn, validityDays);
        
        try {
            X500Name issuerName = new X500Name(subjectDn);
            X500Name subjectName = issuerName; // Autofirmado

            BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
            Date startDate = new Date();
            Date endDate = new Date(startDate.getTime() + (long) validityDays * 86400000L);

            X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                    issuerName,
                    serial,
                    startDate,
                    endDate,
                    subjectName,
                    keyPair.getPublic()
            );

            ContentSigner signer = new JcaContentSignerBuilder(signatureAlgorithm)
                    .build(keyPair.getPrivate());

            X509CertificateHolder certHolder = builder.build(signer);

            X509Certificate certificate = new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(certHolder);
                    
            log.info("Self-signed certificate generated successfully. Subject DN: {}, Valid until: {}", 
                    subjectDn, endDate);
                    
            return certificate;
        } catch (CertificateException | OperatorCreationException e) {
            log.error("Error generating self-signed certificate. Subject DN: {}", subjectDn, e);
            throw new RuntimeException("Error al generar certificado autofirmado", e);
        }
    }

    /**
     * Elimina una entrada del keystore.
     * 
     * @param alias Nombre de la entrada a eliminar (no null)
     * @throws IllegalArgumentException si alias es null
     * @throws RuntimeException si ocurre un error durante la eliminación
     * 
     * @see KeyStore#deleteEntry(String)
     */
    public void deleteEntry(String alias) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        
        log.debug("Deleting entry with alias: {}", alias);
        
        try {
            keyStore.deleteEntry(alias);
            log.info("Entry deleted successfully. Alias: {}", alias);
        } catch (KeyStoreException e) {
            log.error("Error deleting entry. Alias: {}", alias, e);
            throw new RuntimeException("Error al eliminar entrada", e);
        }
    }

    /**
     * Lista todos los aliases presentes en el keystore.
     * 
     * @return Array con todos los nombres de alias
     * @throws RuntimeException si ocurre un error durante la enumeración
     * 
     * @see KeyStore#aliases()
     */
    public String[] listAliases() {
        try {
            List<String> aliasesList = new ArrayList<>();
            Enumeration<String> aliases = keyStore.aliases();
            
            for (Iterator<String> aliasIterator = aliases.asIterator(); aliasIterator.hasNext();) {
                String alias = aliasIterator.next();
                aliasesList.add(alias);
            }
            
            String[] result = aliasesList.toArray(String[]::new);
            log.debug("Aliases listed successfully. Count: {}", result.length);
            return result;
        } catch (KeyStoreException e) {
            log.error("Error listing aliases", e);
            throw new RuntimeException("Error al listar aliases", e);
        }
    }

    /**
     * Verifica si una entrada contiene una clave privada.
     * 
     * @param alias Nombre de la entrada a verificar (no null)
     * @return {@code true} si la entrada contiene una clave privada, {@code false} en caso contrario
     * @throws IllegalArgumentException si alias es null
     * @throws RuntimeException si ocurre un error durante la verificación
     * 
     * @see KeyStore#isKeyEntry(String)
     */
    public boolean isKeyEntry(String alias) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        
        try {
            boolean result = keyStore.isKeyEntry(alias);
            log.debug("Key entry check. Alias: {}, Is key entry: {}", alias, result);
            return result;
        } catch (KeyStoreException e) {
            log.error("Error checking key entry. Alias: {}", alias, e);
            throw new RuntimeException("Error al verificar entrada", e);
        }
    }

    /**
     * Verifica si una entrada contiene solo un certificado.
     * 
     * @param alias Nombre de la entrada a verificar (no null)
     * @return {@code true} si la entrada contiene solo un certificado, {@code false} en caso contrario
     * @throws IllegalArgumentException si alias es null
     * @throws RuntimeException si ocurre un error durante la verificación
     * 
     * @see KeyStore#isCertificateEntry(String)
     */
    public boolean isCertificateEntry(String alias) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias cannot be null or empty");
        }
        
        try {
            boolean result = keyStore.isCertificateEntry(alias);
            log.debug("Certificate entry check. Alias: {}, Is certificate entry: {}", alias, result);
            return result;
        } catch (KeyStoreException e) {
            log.error("Error checking certificate entry. Alias: {}", alias, e);
            throw new RuntimeException("Error al verificar entrada", e);
        }
    }

    /**
     * Obtiene el tipo de keystore configurado.
     * 
     * @return El tipo de keystore (ej: "JKS", "PKCS12")
     */
    public String getKeyStoreType() {
        return keyStoreType;
    }

    /**
     * Obtiene el algoritmo de firma configurado.
     * 
     * @return El algoritmo de firma (ej: "SHA256WithRSA")
     */
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    /**
     * Obtiene el tamaño de la contraseña maestra.
     * 
     * @return Longitud de la contraseña maestra
     */
    public int getPasswordLength() {
        return password != null ? password.length : 0;
    }
}