package com.univsoftdev.econova.exceptions;

/**
 * Excepción para entidades no encontradas.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
