package io.dbmaster.testng;


import java.util.Map;

import com.google.inject.Injector;

public abstract class BaseTestNGCase {
    private static Injector injector;
    private static Map<String,String> properties;
    
    protected static final Injector getInjector(){
        return injector;
    }
    
    protected static final <T> T getInstance(Class<T> clazz){
        return injector.getInstance(clazz);
    }
    
    protected synchronized static final String getTestProperty(String key){
        return properties.get(key);
    }
    
    protected synchronized static final String getTestProperty(String key, String defValue){
        String value = properties.get(key);
        return value == null ? defValue : value;
    }
}
