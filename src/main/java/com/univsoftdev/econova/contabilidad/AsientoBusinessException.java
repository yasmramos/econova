package com.univsoftdev.econova.contabilidad;

public class AsientoBusinessException extends RuntimeException {

    public AsientoBusinessException(String message) {
        super(message);
    }

    public AsientoBusinessException() {
    }

    public AsientoBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public AsientoBusinessException(Throwable cause) {
        super(cause);
    }

    public AsientoBusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
