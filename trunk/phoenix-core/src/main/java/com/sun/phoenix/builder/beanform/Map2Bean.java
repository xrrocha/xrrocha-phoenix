package com.sun.phoenix.builder.beanform;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.sun.phoenix.builder.ObjectBuilder;
import com.sun.phoenix.util.beanform.serializer.Serializers;
import com.sun.phoenix.util.beanform.serializer.Serializer;

public class Map2Bean implements ObjectBuilder {
	private Class<?> beanClass;
	private Map<Class<?>, Serializer<?>> serializers;
	
	public Map2Bean() {
		serializers = new HashMap<Class<?>, Serializer<?>>(Serializers.getGlobalSerializers());
	}
	
	public Map2Bean(Class<?> beanClass) {
		this.beanClass = beanClass;
		serializers = new HashMap<Class<?>, Serializer<?>>(Serializers.getGlobalSerializers());
	}
	
	public Map2Bean(Class<?> beanClass, Map<Class<?>, Serializer<?>> parsers) {
		this.beanClass = beanClass;
		this.serializers = parsers;
	}
	
	public void addParser(Class<?> beanClass, Serializer<?> parser) {
		serializers.put(beanClass, parser);
	}
	
	@Override
	public Object buildObject(Object source) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) source;
		Object instance = beanClass.newInstance();
		for (PropertyDescriptor propertyDescriptor: Introspector.getBeanInfo(beanClass).getPropertyDescriptors()) {
			Method writeMethod = propertyDescriptor.getWriteMethod();
			if (writeMethod != null) {
				String value = map.get(propertyDescriptor.getName());
				if (value != null) {
					Serializer<?> parser = serializers.get(propertyDescriptor.getPropertyType());
					if (parser != null) {
						Object propertyValue = parser.parse(value);
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
