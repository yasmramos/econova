package com.univsoftdev.econova.contabilidad.views.comprobante;

public class AsientoNotFoundException extends Exception {

    public AsientoNotFoundException() {
    }

    public AsientoNotFoundException(String message) {
        super(message);
    }

    public AsientoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AsientoNotFoundException(Throwable cause) {
        super(cause);
    }

    public AsientoNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
