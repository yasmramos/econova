package com.univsoftdev.econova.modules;

import jakarta.inject.Singleton;
import com.univsoftdev.econova.component.wizard.BsonDb;
import io.avaje.inject.Factory;
import jakarta.inject.Provider;

@Factory
public class BsonDbModule {

    @Singleton
    public static class BsonDbProvider implements Provider<BsonDb> {

        @Override
        public BsonDb get() {
            BsonDb db = new BsonDb();
            return db;
        }

    }

}
