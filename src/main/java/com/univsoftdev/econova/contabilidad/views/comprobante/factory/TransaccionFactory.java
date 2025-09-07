package com.univsoftdev.econova.contabilidad.views.comprobante.factory;

import com.univsoftdev.econova.core.AppContext;
import com.univsoftdev.econova.contabilidad.model.Transaction;
import com.univsoftdev.econova.contabilidad.model.Account;
import com.univsoftdev.econova.contabilidad.model.Currency;
import com.univsoftdev.econova.contabilidad.model.AccountingEntry;
import com.univsoftdev.econova.contabilidad.views.comprobante.dto.TransaccionData;
import com.univsoftdev.econova.core.config.AppConfig;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class TransaccionFactory {

    private final AppContext appContext;

    @Inject
    public TransaccionFactory(AppContext appContext) {
        this.appContext = appContext;
    }

    public Transaction createTransaccion(TransaccionData data, Account cuenta, AccountingEntry asiento) {
        Currency moneda = getDefaultMoneda();

        Transaction transaccion = new Transaction(
                data.tipo(),
                data.monto(),
                asiento.getFecha(),
                moneda,
                appContext.getSession().getUser(),
                cuenta
        );

        transaccion.setAsiento(asiento);
        transaccion.setLedger(cuenta.getLedger());

        return transaccion;
    }

    private Currency getDefaultMoneda() {
        String codigoMoneda = AppConfig.getDefaultCurrency();
        String nombreMoneda = AppConfig.getDefaultCurrencyName();
        return new Currency(codigoMoneda, nombreMoneda);
    }
}
