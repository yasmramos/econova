package com.univsoftdev.econova.contabilidad.views.comprobante;

import com.univsoftdev.econova.contabilidad.model.AccountingEntry;
import com.univsoftdev.econova.contabilidad.service.AccountingService;
import com.univsoftdev.econova.contabilidad.views.comprobante.factory.TransaccionFactory;
import com.univsoftdev.econova.contabilidad.views.comprobante.validation.AsientoRowValidator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class AsientoProcessorFactory {

    private final AccountingService contabilidadService;
    private final AsientoRowValidator validator;
    private final TransaccionFactory transaccionFactory;
    private final CuentaCodeBuilder cuentaCodeBuilder;

    @Inject
    public AsientoProcessorFactory(AccountingService contabilidadService,
            AsientoRowValidator validator,
            TransaccionFactory transaccionFactory,
            CuentaCodeBuilder cuentaCodeBuilder) {
        this.contabilidadService = contabilidadService;
        this.validator = validator;
        this.transaccionFactory = transaccionFactory;
        this.cuentaCodeBuilder = cuentaCodeBuilder;
    }

    public AsientoProcessor create(AccountingEntry asiento) {
        return new AsientoProcessor(contabilidadService, validator,
                transaccionFactory, cuentaCodeBuilder, asiento);
    }
}
