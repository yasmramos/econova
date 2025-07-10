package com.univsoftdev.econova.core.utils;

public enum ModalBorder {
    
    DEFAULT_OPTION(-1),
    YES_NO_OPTION(0),
    YES_NO_CANCEL_OPTION(1),
    OK_CANCEL_OPTION(2),
    YES_OPTION(3),
    NO_OPTION(4),
    CANCEL_OPTION(5),
    OK_OPTION(6),
    CLOSE_OPTION(-2),
    OPENED(20);

    private final int value;

    private ModalBorder(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
