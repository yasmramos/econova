package com.univsoftdev.econova.modules;

import com.univsoftdev.econova.config.service.RolService;
import com.univsoftdev.econova.config.service.UsuarioService;
import com.univsoftdev.econova.security.Argon2PasswordHasher;
import com.univsoftdev.econova.security.PasswordHasher;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.ebean.Database;
import jakarta.inject.Singleton;

@Factory
public class SecurityModule {

    @Bean
    @Singleton
    public PasswordHasher passwordHasher() {
        return new Argon2PasswordHasher();
    }
    
    @Bean
    @Singleton
    public UsuarioService usuarioService(Database database, RolService rolService, PasswordHasher passwordHasher) {
        return new UsuarioService(database, rolService, passwordHasher);
    }
}
