package com.univsoftdev.econova.security.shiro;

import com.univsoftdev.econova.security.CustomRealm;
import io.ebean.Database;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;

@Slf4j
@Singleton
public class ShiroConfig {

    private final CustomRealm customRealm;

    @Inject
    public ShiroConfig(Database database) {
        this.customRealm = new CustomRealm(database);
        initializeShiro();
    }

    private void initializeShiro() {
        DefaultSecurityManager securityManager = new DefaultSecurityManager(customRealm);
        SecurityUtils.setSecurityManager(securityManager);
        log.trace("Shiro initialized.");
    }
}
