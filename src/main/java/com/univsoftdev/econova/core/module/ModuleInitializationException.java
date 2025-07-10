package com.univsoftdev.econova.core.module;

public class ModuleInitializationException extends Exception {

    public ModuleInitializationException() {
    }

    public ModuleInitializationException(String message) {
        super(message);
    }

    public ModuleInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleInitializationException(Throwable cause) {
        super(cause);
    }

    public ModuleInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
