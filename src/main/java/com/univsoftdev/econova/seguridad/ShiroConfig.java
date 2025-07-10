package com.univsoftdev.econova.seguridad;

import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;

@Singleton
public class ShiroConfig {

    @Inject
    public static void setupShiro(Database database){
        CustomRealm customRealm = new CustomRealm(database);
        DefaultSecurityManager securityManager = new DefaultSecurityManager(customRealm);
        SecurityUtils.setSecurityManager(securityManager);
    }
    
}
