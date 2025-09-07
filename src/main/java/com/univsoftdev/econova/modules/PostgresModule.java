package com.univsoftdev.econova.modules;

import com.univsoftdev.econova.core.FileUtils;
import com.univsoftdev.econova.core.config.AppConfig;
import com.univsoftdev.econova.db.postgres.PostgreSQLBackup;
import com.univsoftdev.econova.db.postgres.PostgreSQLDatabaseLister;
import com.univsoftdev.econova.db.postgres.PostgresSql;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Singleton;

@Factory
public class PostgresModule {

    @Bean
    @Singleton
    public PostgresSql postgresSql() {
        return new PostgresSql(
                AppConfig.getDatabaseHost(),
                AppConfig.getDatabasePort(),
                AppConfig.getDatabaseUser(),
                AppConfig.getDatabasePassword(),
                AppConfig.getDefaultDatabase()
        );
    }

    @Bean
    @Singleton
    public PostgreSQLDatabaseLister postgreSQLDatabaseLister() {
        return new PostgreSQLDatabaseLister(
                AppConfig.getDatabaseHost(),
                AppConfig.getDatabasePort(),
                AppConfig.getDatabaseUser(),
                AppConfig.getDatabasePassword()
        );
    }

    @Bean
    @Singleton
    public PostgreSQLBackup postgreSQLBackup() {
        return new PostgreSQLBackup(
                AppConfig.getDatabaseUser(),
                AppConfig.getDatabasePassword(), "postgres", FileUtils.BACKUP_PATH);
    }
}
