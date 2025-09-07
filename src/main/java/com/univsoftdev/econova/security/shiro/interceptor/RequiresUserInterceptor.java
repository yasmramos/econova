package com.univsoftdev.econova.security.shiro.interceptor;

import com.univsoftdev.econova.security.shiro.annotations.RequiresUser;
import io.avaje.inject.aop.AspectProvider;
import io.avaje.inject.aop.Invocation;
import io.avaje.inject.aop.MethodInterceptor;
import jakarta.inject.Singleton;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.subject.Subject;

@Slf4j
@Singleton
public class RequiresUserInterceptor implements AspectProvider<RequiresUser>, MethodInterceptor {

    @Override
    public MethodInterceptor interceptor(Method method, RequiresUser requiresUser) {
        return this;
    }

    @Override
    public void invoke(Invocation invocation) throws Throwable {
        log.info("RequiresUserInterceptor funciona");
        Method method = invocation.method();
        RequiresUser annotation = method.getAnnotation(RequiresUser.class);

        if (annotation != null) {
            checkUser();
        }

        // Si las verificaciones pasan, ejecutar el método original  
        invocation.invoke();
    }

    private void checkUser() {
        Subject subject = SecurityUtils.getSubject();

        // RequiresUser verifica que el usuario esté autenticado O recordado  
        if (subject.getPrincipal() == null) {
            throw new UnauthenticatedException("Attempting to perform a user-only operation. "
                    + "The current Subject is not a user (they haven't been authenticated or remembered from a previous login). "
                    + "Access denied.");
        }
    }
}
