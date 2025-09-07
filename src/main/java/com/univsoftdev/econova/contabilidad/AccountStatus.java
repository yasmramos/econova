package com.univsoftdev.econova.contabilidad;

public enum AccountStatus {
    
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String description;

    private AccountStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
