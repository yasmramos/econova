package com.univsoftdev.econova.config.model;

import com.univsoftdev.econova.config.finder.UnidadFinder;
import com.univsoftdev.econova.contabilidad.model.Asiento;
import com.univsoftdev.econova.contabilidad.model.Transaccion;
import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sys_unidades")
public class Unidad extends BaseModel {

    public static UnidadFinder finder = new UnidadFinder();
    private static final long serialVersionUID = 1L;

    @NotNull(message = "El código no puede ser nulo.")
    @Column(unique = true)
    private String codigo;

    @NotNull(message = "El nombre no puede ser nulo.")
    private String nombre;

    private String direccion;

    @Email(message = "El correo debe tener un formato válido.")
    private String correo;

    private String nae;

    private String dpa;

    private String reup;

    @OneToMany(mappedBy = "unidad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaccion> transacciones = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Asiento> asientos = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    public Unidad() {
    }

    public Unidad(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public Unidad(String codigo, String nombre, String direccion, String correo, String nae, String dpa, String reup) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.direccion = direccion;
        this.correo = correo;
        this.nae = nae;
        this.dpa = dpa;
        this.reup = reup;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(@NotNull Empresa empresa) {
        this.empresa = empresa;
    }

    public List<Transaccion> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(@NotNull List<Transaccion> transacciones) {
        this.transacciones = transacciones;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
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

    public String getReup() {
        return reup;
    }

    public void setReup(String reup) {
        this.reup = reup;
    }

    public void addTransaccion(@NotNull Transaccion transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula.");
        }
        transaccion.setUnidad(this); // Ensure bidirectional relationship
        this.transacciones.add(transaccion);
    }

    public void removeTransaccion(@NotNull Transaccion transaccion) {
        if (transaccion == null) {
            throw new IllegalArgumentException("La transacción no puede ser nula.");
        }
        transaccion.setUnidad(null); // Break bidirectional relationship
        this.transacciones.remove(transaccion);
    }

    public void addAsiento(@NotNull Asiento asiento) {
        asiento.setUnidad(this);
        this.asientos.add(asiento);
    }

    public void removeAsiento(@NotNull Asiento asiento) {
        asiento.setUnidad(null);
        this.asientos.remove(asiento);
    }

    public boolean tieneAsientosConfirmados() {
        return !this.asientos.isEmpty();
    }

    public int getCantidadDeAsientos() {
        return this.asientos.size();
    }

    public BigDecimal getSaldoTotal() {
        return this.transacciones.stream()
                .map(t -> t.esDebito() ? t.getMonto() : t.getMonto().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
