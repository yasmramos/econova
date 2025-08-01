package com.univsoftdev.econova.modules;

import com.univsoftdev.econova.AppContext;
import com.univsoftdev.econova.AppSession;
import com.univsoftdev.econova.cache.CacheManager;
import com.univsoftdev.econova.cache.KryoSerializer;
import com.univsoftdev.econova.core.config.AppConfig;
import com.univsoftdev.econova.ebean.config.FlywayMigrator;
import com.univsoftdev.econova.security.keystore.KeystoreManager;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.ebean.Database;
import jakarta.inject.Singleton;

@Factory
public class AppModule {

    @Bean
    public AppSession appSession(CacheManager cacheManager) {
        return new AppSession(cacheManager);
    }

    @Bean
    @Singleton
    public AppContext appContext(AppSession session, CacheManager cacheManager) {
        return new AppContext(session, cacheManager);
    }

    @Bean
    @Singleton
    public AppConfig appConfig() {
        return new AppConfig();
    }

    @Bean
    @Singleton
    public CacheManager cacheManager() {
        return new CacheManager("cache.db");
    }

    @Bean
    @Singleton
    public KryoSerializer kryoSerializer() {
        return new KryoSerializer();
    }
    
    @Bean
    @Singleton
    public Database database(){
        return new EbeanConfig().configure();
    }
    
    @Bean
    @Singleton
    public KeystoreManager keystoreManager() {
        return new KeystoreManager("wajefgawegaljal435s4dg35sa435g4a3s4dg43sg435as4g35".toCharArray());
    }
    
    @Singleton
    public FlywayMigrator flywayMigrator() {
        return new FlywayMigrator();
    }
}
