package com.univsoftdev.econova.contabilidad.views.comprobante.factory;  
  
import com.univsoftdev.econova.AppContext;
import com.univsoftdev.econova.contabilidad.model.Transaccion;  
import com.univsoftdev.econova.contabilidad.model.Cuenta;  
import com.univsoftdev.econova.contabilidad.model.Moneda;  
import com.univsoftdev.econova.contabilidad.model.Asiento;  
import com.univsoftdev.econova.contabilidad.views.comprobante.dto.TransaccionData;
import com.univsoftdev.econova.core.config.AppConfig;  
import jakarta.inject.Inject;  
import jakarta.inject.Singleton;  
  
@Singleton    
public class TransaccionFactory {    
        
    private final AppConfig appConfig;  
    private final AppContext appContext; // Add this field  
        
    @Inject    
    public TransaccionFactory(AppConfig appConfig, AppContext appContext) {    
        this.appConfig = appConfig;  
        this.appContext = appContext; // Inject AppContext  
    }    
        
    public Transaccion createTransaccion(TransaccionData data, Cuenta cuenta, Asiento asiento) {    
        Moneda moneda = getDefaultMoneda();    
            
        Transaccion transaccion = new Transaccion(    
            data.tipo(),    
            data.monto(),    
            asiento.getFecha(),    
            moneda,    
            appContext.getSession().getCurrentUser(), // Now this will work  
            cuenta    
        );    
            
        transaccion.setAsiento(asiento);    
        transaccion.setLibroMayor(cuenta.getLibroMayor());    
            
        return transaccion;    
    }    
        
    private Moneda getDefaultMoneda() {    
        String codigoMoneda = appConfig.getDefaultCurrency();    
        String nombreMoneda = appConfig.getDefaultCurrencyName();    
        return new Moneda(codigoMoneda, nombreMoneda);    
    }    
}