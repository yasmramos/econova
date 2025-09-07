package com.univsoftdev.econova.modules;

import com.univsoftdev.econova.security.argon2.Argon2CredentialsMatcher;
import com.univsoftdev.econova.security.shiro.CustomRealm;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.ebean.Database;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;

@Factory
public class ShiroModule {

    @Bean
    public CustomRealm customRealm(Database database, Argon2CredentialsMatcher argon2CredentialsMatcher) {
        return new CustomRealm(database, argon2CredentialsMatcher);
    }
    
    @Bean
    public PasswordService passwordService(){
        return new DefaultPasswordService();
    }
}
