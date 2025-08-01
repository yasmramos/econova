package com.univsoftdev.econova.ebean.config;

import io.ebean.config.EncryptKey;
import io.ebean.config.EncryptKeyManager;

public class MyEncryptKeyManager implements EncryptKeyManager {

    @Override
    public EncryptKey getEncryptKey(String tableName, String columnName) {
        // Devuelve la clave de encriptación para esta tabla/columna
        String key = "my-encryption-key-123"; // En producción usa un sistema seguro de gestión de claves
        return new BasicEncryptKey(key);
    }

    @Override
    public void initialise() {
        // Inicialización si es necesaria
    }
}
