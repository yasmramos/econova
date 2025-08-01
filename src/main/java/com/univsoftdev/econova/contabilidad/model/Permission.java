package com.univsoftdev.econova.contabilidad.model;

import com.univsoftdev.econova.config.model.Rol;
import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "conf_permission")
public class Permission extends BaseModel {

    @NotBlank
    private String name;
    
    @NotBlank
    @Column(unique = true)
    private String code;
    
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Rol> roles = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }
}
