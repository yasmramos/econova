package com.univsoftdev.econova.config.finder;

import com.univsoftdev.econova.contabilidad.model.Currency;
import io.ebean.Finder;

public class MonedaFinder extends Finder<Long, Currency> {

    public MonedaFinder() {
        super(Currency.class);
    }
}
