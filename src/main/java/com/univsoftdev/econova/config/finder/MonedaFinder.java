package com.univsoftdev.econova.config.finder;

import com.univsoftdev.econova.contabilidad.model.Moneda;
import io.ebean.Finder;

public class MonedaFinder extends Finder<Long, Moneda> {

    public MonedaFinder() {
        super(Moneda.class);
    }
}
