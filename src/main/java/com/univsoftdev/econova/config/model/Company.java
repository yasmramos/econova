package com.univsoftdev.econova.config.model;

import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sys_companys")
public class Company extends BaseModel {

    private static final long serialVersionUID = 1L;

    private String code;

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres.")
    private String name;

    @NotBlank(message = "La razón social no puede estar vacía.")
    @Size(max = 150, message = "La razón social no puede tener más de 150 caracteres.")
    private String razonSocial;

    @Size(max = 20, message = "El NIF no puede tener más de 20 caracteres.")
    @Column(unique = true)
    private String nif; // Número de identificación fiscal

    @Size(max = 100, message = "La dirección no puede tener más de 100 caracteres.")
    private String address;

    @Size(max = 20, message = "El teléfono no puede tener más de 20 caracteres.")
    private String telefono;

    @Email(message = "El correo debe tener un formato válido.")
    private String email;

    @Size(max = 20, message = "El código postal no puede tener más de 20 caracteres.")
    private String codigoPostal;

    @Size(max = 50, message = "La ciudad no puede tener más de 50 caracteres.")
    private String ciudad;

    @Size(max = 50, message = "La provincia no puede tener más de 50 caracteres.")
    private String provincia;

    @Size(max = 50, message = "El país no puede tener más de 50 caracteres.")
    private String pais;

    @Size(max = 20, message = "El CIF no puede tener más de 20 caracteres.")
    @Column(name = "cif", unique = true)
    private String cif; 
    private String nae;

    private String dpa;

    private String reuup;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Unit> units = new ArrayList<>();

    public Company() {
    }

    public Company(String nombre, String razonSocial, String nif) {
        this.name = nombre;
        this.razonSocial = razonSocial;
        this.nif = nif;
    }

    @Override
    public String toString() {
        return String.format("Empresa [%s] - %s (%s)", getId(), name, nif);
    }

    public void addUnidad(@NotNull Unit unidad) {
        this.units.add(unidad);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getNae() {
        return nae;
    }

    public void setNae(String nae) {
        this.nae = nae;
    }

    public String getDpa() {
        return dpa;
    }

    public void setDpa(String dpa) {
        this.dpa = dpa;
    }

    public String getReuup() {
        return reuup;
    }

    public void setReuup(String reuup) {
        this.reuup = reuup;
    }

    public List<Unit> getUnits() {
        return units;
    }
    
    
}
