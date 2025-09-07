package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.config.model.Period;
import com.univsoftdev.econova.contabilidad.EstadoAsiento;
import com.univsoftdev.econova.contabilidad.model.AccountingEntry;
import io.ebean.Finder;
import java.time.LocalDate;
import java.util.List;

public class AsientoFinder extends Finder<Long, AccountingEntry> {

    public AsientoFinder() {
        super(AccountingEntry.class);
    }

    public List<AccountingEntry> findByPeriodo(Period periodo) {
        return db().find(AccountingEntry.class)
                .where()
                .eq("periodo.id", periodo.getId())
                .findList();
    }

    public List<AccountingEntry> findConfirmadosByFecha(LocalDate desde, LocalDate hasta) {
        return db().find(AccountingEntry.class)
                .where()
                .eq("estadoAsiento", EstadoAsiento.CONFIRMADO)
                .ge("fecha", desde)
                .le("fecha", hasta)
                .findList();
    }
}
