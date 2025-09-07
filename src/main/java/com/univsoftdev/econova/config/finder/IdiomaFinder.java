package com.univsoftdev.econova.config.finder;

import com.univsoftdev.econova.config.model.Language;
import io.ebean.Finder;

public class IdiomaFinder extends Finder<Long, Language> {

    public IdiomaFinder() {
        super(Language.class);
    }
}