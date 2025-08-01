package com.univsoftdev.econova.security.shiro;

import io.avaje.inject.aop.AspectProvider;
import io.avaje.inject.aop.Invocation;
import io.avaje.inject.aop.MethodInterceptor;
import jakarta.inject.Singleton;
import java.lang.reflect.Method;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

@Singleton
public class RequiresPermissionsInterceptor implements AspectProvider<RequiresPermissions>, MethodInterceptor {

    @Override
    public MethodInterceptor interceptor(Method method, RequiresPermissions requiresPermissions) {
        return this;
    }

    @Override
    public void invoke(Invocation invocation) throws Throwable {
        Method method = invocation.method();
        RequiresPermissions annotation = method.getAnnotation(RequiresPermissions.class);

        if (annotation != null) {
            checkPermissions(annotation);
        }

        // Si las verificaciones pasan, ejecutar el método original  
        invocation.invoke();
    }

    private void checkPermissions(RequiresPermissions requiresPermissions) {
        Subject subject = SecurityUtils.getSubject();

        String[] permissions = requiresPermissions.value();
        if (permissions.length == 0) {
            return;
        }

        if (permissions.length == 1) {
            // Un solo permiso  
            subject.checkPermission(permissions[0]);
        } else {
            // Múltiples permisos - verificar según la lógica (AND por defecto)  
            if (requiresPermissions.logical() == org.apache.shiro.authz.annotation.Logical.AND) {
                // Todos los permisos deben estar presentes  
                subject.checkPermissions(permissions);
            } else {
                // Al menos uno de los permisos debe estar presente  
                boolean hasPermission = false;
                for (String permission : permissions) {
                    if (subject.isPermitted(permission)) {
                        hasPermission = true;
                        break;
                    }
                }
                if (!hasPermission) {
                    throw new AuthorizationException("Subject does not have any of the required permissions: "
                            + String.join(", ", permissions));
                }
            }
        }
    }
}
