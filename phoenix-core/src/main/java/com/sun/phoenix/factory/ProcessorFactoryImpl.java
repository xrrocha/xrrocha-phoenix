/**
 * 
 */
package com.sun.phoenix.factory;

import java.lang.reflect.ParameterizedType;
import java.util.regex.Pattern;

import com.sun.phoenix.Processor;
import com.sun.phoenix.components.builder.ObjectBuilder;

public class ProcessorFactoryImpl implements ProcessorFactory {
	private Pattern uriPattern;
	private SimpleObjectFactory<Processor<?>> processorFactory;
	private ObjectBuilder requestBuilder;

	public ProcessorFactoryImpl() {
	}

	public ProcessorFactoryImpl(Pattern pattern, SimpleObjectFactory<Processor<?>> objectFactory, ObjectBuilder objectBuilder) {
		this.uriPattern = pattern;
		this.processorFactory = objectFactory;
		this.requestBuilder = objectBuilder;
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
	public void restoreTransientProperties(Processor<?> processor) throws Exception {
		processorFactory.restoreTransientProperties(processor);
	}

	@Override
	public ClassLoader getClassLoader() {
		return processorFactory.getClazz().getClassLoader();
	}

	@Override
	public Class<?> getProcessorArgumentClass() {
		ParameterizedType parameterizedType = (ParameterizedType) processorFactory.getClazz().getGenericSuperclass();
		Class<?> clazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
		return clazz;
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

	public void setProcessorFactory(
			SimpleObjectFactory<Processor<?>> processorFactory) {
		this.processorFactory = processorFactory;
	}

	public ObjectBuilder getRequestBuilder() {
		return requestBuilder;
	}

	public void setRequestBuilder(ObjectBuilder requestBuilder) {
		this.requestBuilder = requestBuilder;
	}
}