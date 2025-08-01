package com.univsoftdev.econova.modules;

import com.univsoftdev.econova.AppContext;
import com.univsoftdev.econova.contabilidad.views.comprobante.factory.TransaccionFactory;
import com.univsoftdev.econova.core.config.AppConfig;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Singleton;

@Factory
public class ContabilidadModule {

    @Bean
    @Singleton
    public TransaccionFactory transaccionFactory(AppConfig appConfig, AppContext appContext) {
        return new TransaccionFactory(appConfig, appContext);
    }
}
