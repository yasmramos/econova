package com.univsoftdev.econova.modules;

import com.univsoftdev.econova.component.wizard.BsonDb;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Singleton;

@Factory
public class BsonDbModule {

    @Bean
    @Singleton
    public BsonDb bsonDb() {
        BsonDb db = new BsonDb();
        return db;
    }

}
