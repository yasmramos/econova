package com.univsoftdev.econova.core.license;

import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "licences")
public class Licence extends BaseModel {

    @Column(nullable = false, unique = true)
    private String licenceKey; // Clave de licencia (puede estar cifrada)

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false)
    private boolean isUsed; //para indicar si la licencia ya fue usada
    
    @Column(nullable = false)
    private String hardwareIdentifier; // Identificador único del hardware

    public Licence() {
    }

    public Licence(String licenceKey, LocalDate expiryDate) {
        this.licenceKey = licenceKey;
        this.expiryDate = expiryDate;
        this.isActive = false; // Initially inactive
        this.isUsed = false;   // Initially not used
    }

    public Licence(String licenceKey, LocalDate expiryDate, String hardwareIdentifier) {
        this.licenceKey = licenceKey;
        this.expiryDate = expiryDate;
        this.hardwareIdentifier = hardwareIdentifier;
        this.isActive = false; // Initially inactive
        this.isUsed = false;   // Initially not used
    }

    public String getHardwareIdentifier() {
        return hardwareIdentifier;
    }

    public void setHardwareIdentifier(String hardwareIdentifier) {
        this.hardwareIdentifier = hardwareIdentifier;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public boolean isIsUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }

    public String getLicenceKey() {
        return licenceKey;
    }

    public void setLicenceKey(String licenceKey) {
        this.licenceKey = licenceKey;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }
}
