package com.univsoftdev.econova.modules;

import com.univsoftdev.econova.cache.CacheManager;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Singleton;

@Factory
public class AppModule {

    @Bean
    @Singleton
    public CacheManager cacheManager() {
        return new CacheManager("cache.db");
    }
     
}
