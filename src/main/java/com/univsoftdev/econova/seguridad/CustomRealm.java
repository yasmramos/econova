package com.univsoftdev.econova.seguridad;

import jakarta.inject.Singleton;
import com.univsoftdev.econova.config.model.Rol;
import com.univsoftdev.econova.config.model.User;
import io.ebean.Database;
import jakarta.inject.Inject;
import java.util.Set;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

@Singleton
public class CustomRealm extends AuthorizingRealm {

    private final Database ebeanServer;

    @Inject
    public CustomRealm(Database ebeanServer) {
        this.ebeanServer = ebeanServer;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        User user = ebeanServer.find(User.class).where().eq("userName", username).findOne();
        if (user != null) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            Set<Rol> roles = user.getRoles();
            for (Rol role : roles) {
                info.addRole(role.getNombre());
            }
            return info;
        }
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        User user = ebeanServer.find(User.class).where().eq("userName", username).findOne();
        if (user != null) {
            return new SimpleAuthenticationInfo(user.getUserName(), user.getPassword(), getName());
        }
        throw new UnknownAccountException("No user with username of " + username);
    }
 
}
