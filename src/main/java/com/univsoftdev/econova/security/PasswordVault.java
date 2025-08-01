package com.univsoftdev.econova.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.KeyStore;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class PasswordVault {

    private long lastAccessTime;
    private static final long TIMEOUT = 5 * 60 * 1000; // 5 minutos
    private KeyStore keyStore;
    private char[] masterPassword;
    private final String keyStoreType = "JCEKS"; // Tipo específico para claves secretas

    public PasswordVault(char[] masterPassword) {
        this.masterPassword = masterPassword;
        try {
            this.keyStore = KeyStore.getInstance(keyStoreType);
            this.keyStore.load(null, masterPassword); // Crear nuevo KeyStore vacío
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException("Error al inicializar el PasswordVault", e);
        }
    }

    /**
     * Guarda una contraseña en el KeyStore
     *
     * @param alias Identificador único para la contraseña
     * @param password Contraseña a guardar
     */
    public void storePassword(String alias, char[] password) {
        try {
            PBEKeySpec keySpec = new PBEKeySpec(password);
            try {
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_256");
                SecretKey secretKey = keyFactory.generateSecret(keySpec);

                SecretKeyEntry secretKeyEntry = new SecretKeyEntry(secretKey);
                ProtectionParameter protectionParam = new KeyStore.PasswordProtection(masterPassword);

                keyStore.setEntry(alias, secretKeyEntry, protectionParam);
            } finally {
                keySpec.clearPassword(); // Limpiar datos sensibles
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al guardar contraseña", e);
        }
    }

    /**
     * Recupera una contraseña del KeyStore
     *
     * @param alias Identificador de la contraseña
     * @return La contraseña recuperada
     */
    public char[] retrievePassword(String alias) {
        checkAndLock();
        try {
            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(masterPassword);
            SecretKeyEntry entry = (SecretKeyEntry) keyStore.getEntry(alias, protectionParam);

            if (entry == null) {
                return null;
            }

            SecretKey secretKey = entry.getSecretKey();
            PBEKeySpec keySpec = (PBEKeySpec) SecretKeyFactory.getInstance("PBE")
                    .getKeySpec(secretKey, PBEKeySpec.class);

            updateAccessTime();
            return keySpec.getPassword();
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException | InvalidKeySpecException e) {
            throw new RuntimeException("Error al recuperar contraseña", e);
        }
    }

    /**
     * Elimina una contraseña del KeyStore
     *
     * @param alias Identificador de la contraseña a eliminar
     */
    public void deletePassword(String alias) {
        try {
            keyStore.deleteEntry(alias);
        } catch (KeyStoreException e) {
            throw new RuntimeException("Error al eliminar contraseña", e);
        }
    }

    /**
     * Lista todos los alias (identificadores) almacenados
     *
     * @return Array de aliases
     */
    public String[] listStoredPasswords() {
        checkAndLock();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            List<String> aliasList = new ArrayList<>();

            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                validateAlias(alias); // Validar cada alias
                aliasList.add(alias);
            }

            updateAccessTime();
            return aliasList.toArray(String[]::new);
        } catch (KeyStoreException e) {
            throw new RuntimeException("Error al listar contraseñas", e);
        }
    }

    /**
     * Guarda el KeyStore en un archivo
     *
     * @param filePath Ruta del archivo
     */
    public void saveToFile(String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            keyStore.store(fos, masterPassword);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el KeyStore", e);
        }
    }

    /**
     * Carga el KeyStore desde un archivo
     *
     * @param filePath Ruta del archivo
     */
    public void loadFromFile(String filePath) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            keyStore.load(fis, masterPassword);
            updateAccessTime();
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException("Error al cargar el KeyStore", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // Logear pero no lanzar
                    System.err.println("Advertencia: error al cerrar stream: " + e.getMessage());
                }
            }
        }
    }

    public boolean isPasswordStrong(char[] password) {
        if (password.length < 8) {
            return false;
        }
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;

        for (char c : password) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            }
            if (Character.isLowerCase(c)) {
                hasLower = true;
            }
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
            if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    private void updateAccessTime() {
        this.lastAccessTime = System.currentTimeMillis();
    }

    private void checkAndLock() {
        if (System.currentTimeMillis() - lastAccessTime > TIMEOUT) {
            Arrays.fill(masterPassword, '\0');
            throw new SecurityException("Vault bloqueado por inactividad");
        }
    }

    public void validateAlias(String alias) {
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("Alias no puede ser nulo o vacío");
        }
        if (alias.length() > 100) {
            throw new IllegalArgumentException("Alias demasiado largo");
        }
        // Validar caracteres permitidos
        if (!alias.matches("^[a-zA-Z0-9_-]+$")) {
            throw new IllegalArgumentException("Alias contiene caracteres inválidos");
        }
    }
}
