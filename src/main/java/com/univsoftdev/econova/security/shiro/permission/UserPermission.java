package com.univsoftdev.econova.security.shiro.permission;

import org.apache.shiro.authz.Permission;

public class UserPermission implements Permission {

    public UserPermission() {
    }

    @Override
    public boolean implies(Permission prmsn) {
        return true;
    }

}
