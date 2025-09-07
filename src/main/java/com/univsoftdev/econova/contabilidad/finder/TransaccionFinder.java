package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.config.model.Period;
import com.univsoftdev.econova.contabilidad.model.Account;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import io.ebean.Finder;
import java.util.List;

public class TransaccionFinder extends Finder<Long, Transaction> {

    public TransaccionFinder() {
        super(Transaction.class);
    }

    public List<Transaction> findByCuenta(Account cuenta) {
        return db().find(Transaction.class)
                .where()
                .eq("cuenta.id", cuenta.getId())
                .findList();
    }

    public List<Transaction> findByPeriodo(Period periodo) {
        return db().find(Transaction.class)
                .where()
                .eq("periodo.id", periodo.getId())
                .findList();
    }
}
