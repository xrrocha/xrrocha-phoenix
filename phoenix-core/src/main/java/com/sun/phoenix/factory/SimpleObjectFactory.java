package com.sun.phoenix.factory;


import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;


public class SimpleObjectFactory<T> implements ObjectFactory<T> {
    private Map<String, Object> properties;
    private Map<String, Object> transientProperties;
    
    private Class<T> clazz;

    @Override
    public T newInstance() throws Exception {
        T instance = clazz.newInstance();
        populateProperties(instance, properties);
        populateProperties(instance, transientProperties);
        return instance;
    }
    
    @Override
    public void restoreTransientProperties(T object) throws Exception {
        populateProperties(object, transientProperties);
    }
    
    private void populateProperties(Object object, Map<String, Object> props) throws Exception {
        if (props != null) {
            BeanUtils.populate(object, props);
        }
    }

    public String toString() {
        return "class:" + clazz.getName() + "\n" +
               "properties: " + properties + "\n" +
               "transientProperties: " + transientProperties;
        		
    }
    
    public Class<T> getClazz() {
    	return clazz;
    }
    public void setClazz(Class<T> clazz) {
    	this.clazz = clazz;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public Map<String, Object> getTransientProperties() {
        return transientProperties;
    }

    public void setTransientProperties(Map<String, Object> transientProperties) {
        this.transientProperties = transientProperties;
    }
}
