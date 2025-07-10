package com.univsoftdev.econova.config.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para gestionar operaciones CRUD de la entidad {@link Config}.
 * Utiliza Ebean ORM para operaciones de base de datos y Google Guice para inyección de dependencias.
 */
@Slf4j
@Singleton
public class ConfigService{

    @Inject
    public ConfigService() {
    }
  
}
