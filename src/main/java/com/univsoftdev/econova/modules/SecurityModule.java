package com.univsoftdev.econova.modules;

import com.univsoftdev.econova.security.argon2.Argon2PasswordHasher;
import com.univsoftdev.econova.security.argon2.PasswordHasher;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import lombok.extern.slf4j.Slf4j;

/**
 * Módulo de configuración de seguridad para la aplicación. Provee
 * implementaciones de servicios de seguridad como el hasher de contraseñas.
 */
@Factory
@Slf4j
public class SecurityModule {

    /**
     * Provee una implementación de PasswordHasher basada en Argon2. Argon2 es
     * un algoritmo de hashing de contraseñas moderno y seguro.
     *
     * @return Instancia singleton de Argon2PasswordHasher
     */
    @Bean
    public PasswordHasher passwordHasher() {
        log.info("Inicializando Argon2PasswordHasher");
        Argon2PasswordHasher hasher = new Argon2PasswordHasher();
        log.debug("Argon2PasswordHasher inicializado correctamente");
        return hasher;
    }
}
