package com.univsoftdev.econova.security.shiro;

import io.avaje.inject.aop.AspectProvider;
import io.avaje.inject.aop.Invocation;
import io.avaje.inject.aop.MethodInterceptor;
import jakarta.inject.Singleton;
import java.lang.reflect.Method;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;

@Singleton
public class RequiresRolesInterceptor implements AspectProvider<RequiresRoles>, MethodInterceptor {

    @Override
    public MethodInterceptor interceptor(Method method, RequiresRoles requiresRoles) {
        return this;
    }

    @Override
    public void invoke(Invocation invocation) throws Throwable {
        Method method = invocation.method();
        RequiresRoles annotation = method.getAnnotation(RequiresRoles.class);

        if (annotation != null) {
            checkRoles(annotation);
        }

        // Si las verificaciones pasan, ejecutar el método original  
        invocation.invoke();
    }

    private void checkRoles(RequiresRoles requiresRoles) {
        Subject subject = SecurityUtils.getSubject();

        String[] roles = requiresRoles.value();
        if (roles.length == 0) {
            return;
        }

        if (roles.length == 1) {
            // Un solo rol  
            subject.checkRole(roles[0]);
        } else {
            // Múltiples roles - verificar según la lógica (AND por defecto)  
            if (requiresRoles.logical() == org.apache.shiro.authz.annotation.Logical.AND) {
                // Todos los roles deben estar presentes  
                subject.checkRoles(roles);
            } else {
                // Al menos uno de los roles debe estar presente  
                boolean hasRole = false;
                for (String role : roles) {
                    if (subject.hasRole(role)) {
                        hasRole = true;
                        break;
                    }
                }
                if (!hasRole) {
                    throw new AuthorizationException("Subject does not have any of the required roles: "
                            + String.join(", ", roles));
                }
            }
        }
    }
}
