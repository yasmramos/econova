package com.univsoftdev.econova.modules;

import com.univsoftdev.econova.seguridad.ShiroConfig;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;

@Factory
public class ShiroModule {

    @Bean
    public ShiroConfig shiroConfig(){
        return new ShiroConfig();
    }
}
