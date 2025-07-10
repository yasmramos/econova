package com.univsoftdev.econova.contabilidad.views.comprobante;

import com.univsoftdev.econova.contabilidad.model.Asiento;
import com.univsoftdev.econova.contabilidad.service.ContabilidadService;
import com.univsoftdev.econova.contabilidad.views.comprobante.factory.TransaccionFactory;
import com.univsoftdev.econova.contabilidad.views.comprobante.validation.AsientoRowValidator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton  
public class AsientoProcessorFactory {  
      
    private final ContabilidadService contabilidadService;  
    private final AsientoRowValidator validator;  
    private final TransaccionFactory transaccionFactory;  
    private final CuentaCodeBuilder cuentaCodeBuilder;  
      
    @Inject  
    public AsientoProcessorFactory(ContabilidadService contabilidadService,  
            AsientoRowValidator validator,  
            TransaccionFactory transaccionFactory,  
            CuentaCodeBuilder cuentaCodeBuilder) {  
        this.contabilidadService = contabilidadService;  
        this.validator = validator;  
        this.transaccionFactory = transaccionFactory;  
        this.cuentaCodeBuilder = cuentaCodeBuilder;  
    }  
      
    public AsientoProcessor create(Asiento asiento) {  
        return new AsientoProcessor(contabilidadService, validator,   
                                  transaccionFactory, cuentaCodeBuilder, asiento);  
    }  
}
