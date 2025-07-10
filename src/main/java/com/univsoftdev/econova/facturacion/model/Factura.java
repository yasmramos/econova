package com.univsoftdev.econova.facturacion.model;

import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "fact_facturas")
public class Factura extends BaseModel {

    private String codigo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

}
