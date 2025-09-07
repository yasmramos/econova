package com.univsoftdev.econova.ebean.config;

import com.univsoftdev.econova.core.UserContext;
import io.ebean.config.CurrentTenantProvider;

public class MyCurrentTenantProvider implements CurrentTenantProvider {

    @Override
    public Object currentId() {
        return UserContext.get().getTenantId();
    }
}
