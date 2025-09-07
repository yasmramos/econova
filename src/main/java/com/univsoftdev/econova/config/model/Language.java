package com.univsoftdev.econova.config.model;

import com.univsoftdev.econova.config.finder.IdiomaFinder;
import com.univsoftdev.econova.core.model.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "sys_idiomas")
public class Idioma extends BaseModel {

    public static IdiomaFinder finder = new IdiomaFinder();
    private String symbol;
    private String nombre;
    private String pais;

    public Idioma() {
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

}
