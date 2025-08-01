package com.univsoftdev.econova;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.avaje.inject.BeanEntry;
import io.avaje.inject.BeanScope;

public class Injector {

    private static final BeanScope injector = BeanScope.builder()
            .shutdownHook(true)
            .build();

    private Injector() {
        // prevent instantiation
    }
    
    public static <T> T get(Class<T> type) {
        return injector.get(type);
    }

    public static <T> T get(Class<T> type, String ns) {
        return injector.get(type, ns);
    }

    public static <T> T get(Type type, String ns) {
        return injector.get(type, ns);
    }

    public static <T> Optional<T> getOptional(Class<T> type) {
        return injector.getOptional(type);
    }

    public static <T> Optional<T> getOptional(Type type, String ns) {
        return injector.getOptional(type, ns);
    }

    public static List<Object> listByAnnotation(Class<? extends Annotation> type) {
        return injector.listByAnnotation(type);
    }

    public static <T> List<T> list(Class<T> type) {
        return injector.list(type);
    }

    public static <T> List<T> list(Type type) {
        return injector.list(type);
    }

    public static <T> List<T> listByPriority(Class<T> type) {
        return injector.listByPriority(type);
    }

    public static <T> List<T> listByPriority(Class<T> type, Class<? extends Annotation> type1) {
        return injector.listByPriority(type, type1);
    }

    public static <T> Map<String, T> map(Type type) {
        return injector.map(type);
    }

    public static List<BeanEntry> all() {
        return injector.all();
    }

    public static boolean contains(Type type) {
        return injector.contains(type);
    }

    public static boolean contains(String string) {
        return injector.contains(string);
    }

    public static void close() {
        injector.close();
    }
}
