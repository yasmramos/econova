package com.univsoftdev.econova;

import io.avaje.inject.BeanScope;

public class BeanScopeFactory {
    
    public static BeanScope create(){
        return BeanScope.builder().build();
    }
}
