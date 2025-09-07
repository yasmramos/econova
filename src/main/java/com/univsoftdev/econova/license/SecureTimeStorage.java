package com.univsoftdev.econova.license;

import java.util.Date;
import java.util.prefs.Preferences;

public class SecureTimeStorage {

    public static void storeSecureTimestamp(String key, Date timestamp) {
        try {
            String encrypted = encryptTimestamp(timestamp.getTime());
            // Almacenar en preferencias seguras o archivo oculto
            Preferences.userRoot().put(key, encrypted);
        } catch (Exception e) {
            // Manejar error
        }
    }

    public static Date loadSecureTimestamp(String key) {
        try {
            String encrypted = Preferences.userRoot().get(key, null);
            if (encrypted != null) {
                long time = decryptTimestamp(encrypted);
                return new Date(time);
            }
        } catch (Exception e) {
            // Manejar error
        }
        return null;
    }

    private static String encryptTimestamp(long timestamp) {
        // Implementar cifrado simple pero efectivo
        return String.valueOf(timestamp ^ 0xCAFEBABE);
    }

    private static long decryptTimestamp(String encrypted) {
        return Long.parseLong(encrypted) ^ 0xCAFEBABE;
    }
}
