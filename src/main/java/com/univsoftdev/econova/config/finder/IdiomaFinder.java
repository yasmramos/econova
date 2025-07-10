package com.univsoftdev.econova.config.finder;

import com.univsoftdev.econova.config.model.Idioma;
import io.ebean.Finder;

public class IdiomaFinder extends Finder<Long, Idioma> {

    public IdiomaFinder() {
        super(Idioma.class);
    }
}