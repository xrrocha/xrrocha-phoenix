package com.sun.phoenix.components.factory.yaml;


import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.sun.phoenix.components.factory.ObjectFactory;

public class YamlBeansObjectFactory<T> implements ObjectFactory<T> {
    private Map<String, Object> properties;
    private Map<String, Object> transientProperties;
    
    private Class<T> processorClass;

    @Override
    public T newInstance() throws Exception {
        T instance = processorClass.newInstance();
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
        return "class:" + processorClass.getName() + "\n" +
               "properties: " + properties + "\n" +
               "transientProperties: " + transientProperties;
        		
    }
    
    public Class<T> getProcessorClass() {
    	return processorClass;
    }
    public void setProcessorClass(Class<T> clazz) {
    	this.processorClass = clazz;
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
