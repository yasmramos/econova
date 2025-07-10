package com.univsoftdev.econova.config.finder;

import com.univsoftdev.econova.config.model.Ejercicio;
import io.ebean.Finder;

public class EjercicioFinder extends Finder<Long, Ejercicio> {

    public EjercicioFinder() {
        super(Ejercicio.class);
    }
}
