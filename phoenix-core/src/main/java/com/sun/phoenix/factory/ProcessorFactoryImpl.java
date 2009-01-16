/**
 * 
 */
package com.sun.phoenix.factory;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;

import com.sun.phoenix.Processor;
import com.sun.phoenix.components.builder.ObjectBuilder;

public class ProcessorFactoryImpl implements ProcessorFactory {
	private Pattern uriPattern;
	private SimpleObjectFactory<Processor<?>> processorFactory;
	private ObjectBuilder requestBuilder;
	private String uriExpression;
	
	private Map<String, Method> readerMap;

	public ProcessorFactoryImpl() {
	}

	public boolean matches(String uri) {
		return uriPattern.matcher(uri).find(0);
	}

	@Override
	public Processor<?> newProcessor() throws Exception {
		return processorFactory.newInstance();
	}

	@Override
	public Object buildRequestObject(Object requestSource) throws Exception {
		if (requestSource == null) {
			return null;
		}
		if (requestBuilder == null) {
			return requestSource;
		}
		return requestBuilder.buildObject(requestSource);
	}

	@Override
	public void restoreProcessor(Processor<?> processor) throws Exception {
		processorFactory.restoreTransientProperties(processor);
	}

	@Override
	public ClassLoader getClassLoader() {
		return processorFactory.getClass().getClassLoader();
	}

	@Override
	public Class<?> getProcessorArgumentClass() {
		return getGenericArgumentClass(processorFactory.getClazz());
	}
	
	private static Class<?> getGenericArgumentClass(Class<?> clazz) {
		ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
		return (Class<?>) parameterizedType.getActualTypeArguments()[0];
	}
	
	@Override
	@SuppressWarnings("unchecked")
	// TODO Decouple from Jexl by defining an expression language evaluator interface
	// NOTE: This assumes the argument object is a bean, never a primitive/scalar type
	public String getInstanceUri(Object argument) throws Exception {
		if (argument == null) {
			throw new NullPointerException("Argument cannot be null");
		}
		
		if (uriExpression == null) {
			throw new IllegalStateException("Uri expression cannot be null");
		}
		
		if (readerMap == null) {
			throw new IllegalStateException("Reader map cannot be null");
		}
		
		// TODO: argument.getClass() must be assignable from getProcessorArgumentClass()
		
        JexlContext jc = JexlHelper.createContext();
        for (String propertyName: readerMap.keySet()) {
            Method readMethod = readerMap.get(propertyName);
            Object propertyValue = readMethod.invoke(argument, new Object[] {});
            jc.getVars().put(propertyName, propertyValue);
        }

        Expression e = ExpressionFactory.createExpression(uriExpression);
        Object object = e.evaluate(jc);
        
        if (object == null) {
			throw new IllegalArgumentException("Instance uri cannot be null");
        }

		return object.toString();
	}

	private static Map<String, Method> getReaderMap(Class<?> clazz) throws Exception {
        Map<String, Method> readerMap = new HashMap<String, Method>();
        for (PropertyDescriptor propertyDescriptor: Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null && propertyDescriptor.getWriteMethod() != null) {
                readerMap.put(propertyDescriptor.getName(), readMethod);
            }
        }
        return readerMap;
    }

	public Pattern getUriPattern() {
		return uriPattern;
	}

	public void setUriPattern(Pattern uriPattern) {
		this.uriPattern = uriPattern;
	}

	public SimpleObjectFactory<Processor<?>> getProcessorFactory() {
		return processorFactory;
	}

	public void setProcessorFactory(SimpleObjectFactory<Processor<?>> processorFactory) throws Exception {
		this.processorFactory = processorFactory;
		readerMap = getReaderMap(getGenericArgumentClass(processorFactory.getClazz()));
	}

	public ObjectBuilder getRequestBuilder() {
		return requestBuilder;
	}

	public void setRequestBuilder(ObjectBuilder requestBuilder) {
		this.requestBuilder = requestBuilder;
	}

	public String getUriExpression() {
		return uriExpression;
	}

	public void setUriExpression(String uriExpression) {
		this.uriExpression = uriExpression;
	}
}