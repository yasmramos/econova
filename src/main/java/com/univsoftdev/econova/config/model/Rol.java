package com.univsoftdev.econova.config.model;

import com.univsoftdev.econova.contabilidad.model.Permiso;
import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sys_roles")
public class Rol extends BaseModel {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @Column(unique = true)
    private String nombre;

    private String descripcion;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> usuarios = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rol_permiso",
            joinColumns = @JoinColumn(name = "rol_id"),
            inverseJoinColumns = @JoinColumn(name = "permiso_id")
    )
    private Set<Permiso> permisos = new HashSet<>();

    public Rol() {
    }

    public Rol(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede ser nulo o vacío.");
        }
        this.nombre = nombre.trim();
    }

    public boolean tienePermiso(String codigoPermiso) {
        return permisos.stream().anyMatch(p -> p.getCodigo().equals(codigoPermiso));
    }

    public void agregarPermiso(@NotNull Permiso permiso) {
        permisos.add(permiso);
        permiso.getRoles().add(this);
    }

    public void removerPermiso(@NotNull Permiso permiso) {
        permisos.remove(permiso);
        permiso.getRoles().remove(this);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Set<User> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(@NotNull Set<User> usuarios) {
        this.usuarios = usuarios;
    }

    public Set<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(@NotNull Set<Permiso> permisos) {
        this.permisos = permisos;
    }
}
