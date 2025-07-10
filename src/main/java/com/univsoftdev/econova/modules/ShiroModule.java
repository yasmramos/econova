package com.univsoftdev.econova.modules;

import com.univsoftdev.econova.seguridad.ShiroConfig;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Singleton;

@Factory
public class ShiroModule {

    @Bean
    @Singleton
    public ShiroConfig shiroConfig(){
        return new ShiroConfig();
    }
}
