package com.univsoftdev.econova.core.module;

public class ModuleShutdownException extends Exception {

    public ModuleShutdownException() {
    }

    public ModuleShutdownException(String message) {
        super(message);
    }

    public ModuleShutdownException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleShutdownException(Throwable cause) {
        super(cause);
    }

    public ModuleShutdownException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
