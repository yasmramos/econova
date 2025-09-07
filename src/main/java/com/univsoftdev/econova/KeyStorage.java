package com.univsoftdev.econova;

import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.JsonKeysetWriter;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.hybrid.HybridConfig;
import com.google.crypto.tink.hybrid.HybridKeyTemplates;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyStorage {

    public static void saveKeysetToFile(KeysetHandle keysetHandle, String filename)
            throws IOException, GeneralSecurityException {
        File file = new File(filename);
        // Crear directorios padres si no existen
        file.getParentFile().mkdirs();
        CleartextKeysetHandle.write(keysetHandle,
                JsonKeysetWriter.withOutputStream(new FileOutputStream(file)));
    }

    public static KeysetHandle loadKeysetFromFile(String filename)
            throws IOException, GeneralSecurityException {
        File keysetFile = new File(filename);
        if (!keysetFile.exists()) {
            throw new IOException("Archivo de claves no encontrado: " + filename);
        }
        return CleartextKeysetHandle.read(JsonKeysetReader.withFile(keysetFile));
    }

    public static void initializeTink() {
        try {
            HybridConfig.register();
            log.info("Tink hybrid config registrado exitosamente");
        } catch (GeneralSecurityException e) {
            log.error("Error al registrar Tink config: ", e);
            throw new RuntimeException("No se pudo inicializar Tink", e);
        }
    }

    public static KeysetHandle generateHybridKeys() throws GeneralSecurityException {
        return KeysetHandle.generateNew(
                HybridKeyTemplates.ECIES_P256_HKDF_HMAC_SHA256_AES128_GCM);
    }
}
