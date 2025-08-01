package com.univsoftdev.econova.modules;

import io.avaje.inject.InjectModule;
import io.avaje.inject.spi.AvajeModule;
import io.avaje.inject.spi.Builder;

@InjectModule(
        requires = {
            AppModule.class,
            EbeanConfig.class,
            SecurityModule.class
        },
        name = "econova"
)
public class EconovaModule implements AvajeModule {

    @Override
    public Class<?>[] classes() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void build(Builder bldr) {

    }
}
