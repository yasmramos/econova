package com.univsoftdev.econova.contabilidad.service;

public class ContabilidadException extends Exception {

    public ContabilidadException() {
    }

    public ContabilidadException(String message) {
        super(message);
    }

    public ContabilidadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContabilidadException(Throwable cause) {
        super(cause);
    }

    public ContabilidadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
