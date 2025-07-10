package com.univsoftdev.econova.inventarios.model;

import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "inv_productos")
public class Producto extends BaseModel{
    private String codigo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
}
