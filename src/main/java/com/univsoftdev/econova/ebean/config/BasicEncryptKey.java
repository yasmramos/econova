package com.univsoftdev.econova.ebean.config;

import io.ebean.config.EncryptKey;

public class BasicEncryptKey implements EncryptKey {

    private final String key;

    public BasicEncryptKey(String key) {
        this.key = key;
    }

    @Override
    public String getStringValue() {
        return key;
    }

}
