package com.univsoftdev.econova.core.license;

import io.ebean.DB;
import io.ebean.Query;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Level;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class LicenceManager {

    private static final Logger logger = LoggerFactory.getLogger(LicenceManager.class);

    /**
     * Adds a new license to the database.
     *
     * @param licenceKey The license key.
     * @param expiryDate The expiry date of the license.
     */
    public void addLicence(String licenceKey, LocalDate expiryDate) {
        String hardwareIdentifier = HardwareUtil.getHardwareIdentifier();
        Licence licence = new Licence(encrypt(licenceKey), expiryDate, hardwareIdentifier);
        DB.save(licence);
        logger.info("License added successfully.");
    }

    /**
     * Activates a license if it's valid, not expired, not already used, and matches the hardware identifier.
     *
     * @param licenceKey The license key to activate.
     * @return true if the license was successfully activated, false otherwise.
     */
    public boolean activateLicence(String licenceKey) {
        String hardwareIdentifier = HardwareUtil.getHardwareIdentifier();
        Optional<Licence> optionalLicence = findLicenceByKeyAndHardware(encrypt(licenceKey), hardwareIdentifier);

        if (optionalLicence.isPresent()) {
            Licence licence = optionalLicence.get();

            if (!licence.isUsed() && !licence.getExpiryDate().isBefore(LocalDate.now())) {
                licence.setActive(true);
                licence.setUsed(true); // Mark as used after activation
                DB.update(licence);
                logger.info("License activated successfully.");
                return true;
            } else {
                logger.warn("Failed to activate license: Already used or expired.");
            }
        } else {
            logger.warn("License not found or hardware mismatch.");
        }

        return false;
    }

    /**
     * Checks if a license is valid and active.
     *
     * @param licenceKey The license key to check.
     * @return true if the license is valid and active, false otherwise.
     */
    public boolean isLicenceValid(String licenceKey) {
        String hardwareIdentifier = HardwareUtil.getHardwareIdentifier();
        Optional<Licence> optionalLicence = findLicenceByKeyAndHardware(encrypt(licenceKey), hardwareIdentifier);

        if (optionalLicence.isPresent()) {
            Licence licence = optionalLicence.get();

            if (licence.isActive() && !licence.getExpiryDate().isBefore(LocalDate.now())) {
                logger.info("License is valid and active.");
                return true;
            } else {
                logger.warn("License is valid but not active or expired.");
            }
        } else {
            logger.warn("License not found or hardware mismatch.");
        }

        return false;
    }

    /**
     * Finds a license by its encrypted key and hardware identifier.
     *
     * @param encryptedLicenceKey The encrypted license key to search for.
     * @param hardwareIdentifier The hardware identifier to match.
     * @return An Optional containing the license if found, or empty otherwise.
     */
    private Optional<Licence> findLicenceByKeyAndHardware(String encryptedLicenceKey, String hardwareIdentifier) {
        Query<Licence> query = (Query<Licence>) DB.find(Licence.class)
                .where()
                .eq("licenceKey", encryptedLicenceKey)
                .eq("hardwareIdentifier", hardwareIdentifier);
        return Optional.ofNullable(query.findOne());
    }

    /**
     * Encrypts a string using AES encryption.
     *
     * @param data The data to encrypt.
     * @return The encrypted data as a Base64-encoded string.
     */
    private String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return java.util.Base64.getEncoder().encodeToString(encryptedData);
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            logger.error("Error encrypting data", e);
            return null;
        } catch (InvalidKeyException ex) {
            java.util.logging.Logger.getLogger(LicenceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Generates or retrieves the secret key for encryption/decryption.
     *
     * @return The secret key.
     */
    private SecretKey getSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128); // AES-128
        return keyGen.generateKey();
    }
}