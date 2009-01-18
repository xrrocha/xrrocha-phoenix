package com.sun.phoenix.components.builder;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.sun.phoenix.util.beanform.serializer.Serializers;
import com.sun.phoenix.util.beanform.serializer.Serializer;

public class Map2BeanObjectBuilder implements ObjectBuilder {
	private Class<?> beanClass;
	private Map<Class<?>, Serializer<?>> serializers;
	
	public Map2BeanObjectBuilder() {
		serializers = new HashMap<Class<?>, Serializer<?>>(Serializers.getGlobalSerializers());
	}
	
	public Map2BeanObjectBuilder(Class<?> beanClass) {
		this.beanClass = beanClass;
		serializers = new HashMap<Class<?>, Serializer<?>>(Serializers.getGlobalSerializers());
	}
	
	public Map2BeanObjectBuilder(Class<?> beanClass, Map<Class<?>, Serializer<?>> parsers) {
		this.beanClass = beanClass;
		this.serializers = parsers;
	}
	
	public void addParser(Class<?> beanClass, Serializer<?> parser) {
		serializers.put(beanClass, parser);
	}
	
	@Override
	public Object buildObject(Object prototype) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) prototype;
		Object instance = beanClass.newInstance();
		for (PropertyDescriptor propertyDescriptor: Introspector.getBeanInfo(beanClass).getPropertyDescriptors()) {
			Method writeMethod = propertyDescriptor.getWriteMethod();
			if (writeMethod != null) {
				String value = map.get(propertyDescriptor.getName());
				if (value != null) {
					Serializer<?> serializer = serializers.get(propertyDescriptor.getPropertyType());
					if (serializer != null) {
						Object propertyValue = serializer.parse(value);
						writeMethod.invoke(instance, new Object[] {propertyValue});
					}
				}
			}
		}
		return instance;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}
}
