package com.univsoftdev.econova.contabilidad.finder;

import com.univsoftdev.econova.contabilidad.AccountStatus;
import com.univsoftdev.econova.contabilidad.AccountType;
import com.univsoftdev.econova.contabilidad.model.Account;
import io.ebean.Finder;
import java.util.List;

public class AccountFinder extends Finder<Long, Account> {

    public AccountFinder() {
        super(Account.class);
    }

    public List<Account> findByTipo(AccountType tipo) {
        return db().find(Account.class).where().eq("tipoCuenta", tipo).findList();
    }

    public List<Account> findActivas() {
        return db().find(Account.class).where().eq("estadoCuenta", AccountStatus.ACTIVE).findList();
    }
}
