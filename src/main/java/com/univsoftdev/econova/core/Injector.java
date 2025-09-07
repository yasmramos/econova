package com.univsoftdev.econova.core;

import com.univsoftdev.econova.MainFormApp;
import com.univsoftdev.econova.cache.CacheManager;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.avaje.inject.BeanEntry;
import io.avaje.inject.BeanScope;
import io.avaje.inject.BeanScopeBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class Injector {

    private static BeanScope beanScope;

    private Injector() {

    }

    public static BeanScope init() {
        log.info("Iniciando contenedor de inyección de dependencias.");
        try {
            Injector.beanScope = BeanScope.builder()
                    .profiles("production", "test")
                    .shutdownHook(true)
                    .build();
        } catch (Exception e) {
            log.error("No se pudo iniciar el contenedor de inyección de dependencias.", e);
        }
        return beanScope;
    }
    
    public static BeanScopeBuilder.ForTesting forTesting(){
        return BeanScope.builder().forTesting();
    }

    public static <T> T get(Class<T> type) {
        return beanScope.get(type);
    }

    public static <T> T get(Class<T> type, String ns) {
        return beanScope.get(type, ns);
    }

    public static <T> T get(Type type, String ns) {
        return beanScope.get(type, ns);
    }

    public static <T> Optional<T> getOptional(Class<T> type) {
        return beanScope.getOptional(type);
    }

    public static <T> Optional<T> getOptional(Type type, String ns) {
        return beanScope.getOptional(type, ns);
    }

    public static List<Object> listByAnnotation(Class<? extends Annotation> type) {
        return beanScope.listByAnnotation(type);
    }

    public static <T> List<T> list(Class<T> type) {
        return beanScope.list(type);
    }

    public static <T> List<T> list(Type type) {
        return beanScope.list(type);
    }

    public static <T> List<T> listByPriority(Class<T> type) {
        return beanScope.listByPriority(type);
    }

    public static <T> List<T> listByPriority(Class<T> type, Class<? extends Annotation> type1) {
        return beanScope.listByPriority(type, type1);
    }

    public static <T> Map<String, T> map(Type type) {
        return beanScope.map(type);
    }

    public static List<BeanEntry> all() {
        return beanScope.all();
    }

    public static boolean contains(Type type) {
        return beanScope.contains(type);
    }

    public static boolean contains(String string) {
        return beanScope.contains(string);
    }

    public static void close() {
        beanScope.close();
    }

}
