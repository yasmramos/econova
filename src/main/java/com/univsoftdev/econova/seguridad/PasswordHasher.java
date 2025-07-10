package com.univsoftdev.econova.seguridad;

public interface PasswordHasher {

    String hash(String password);

    boolean verify(String hash, String rawPassword);
}
