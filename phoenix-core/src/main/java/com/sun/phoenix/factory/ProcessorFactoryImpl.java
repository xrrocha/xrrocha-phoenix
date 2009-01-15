/**
 * 
 */
package com.sun.phoenix.factory;

import java.util.regex.Pattern;

import com.sun.phoenix.Processor;
import com.sun.phoenix.builder.ObjectBuilder;
import com.sun.phoenix.components.factory.ObjectFactory;

public class ProcessorFactoryImpl implements ProcessorFactory {
    private Pattern uriPattern;
    private ObjectFactory<Processor<?>> processorFactory;
    private ObjectBuilder requestBuilder;
    
    public ProcessorFactoryImpl() {}
    
    public ProcessorFactoryImpl(Pattern pattern, ObjectFactory<Processor<?>> objectFactory, ObjectBuilder objectBuilder) {
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

    public Pattern getUriPattern() {
        return uriPattern;
    }

    public void setUriPattern(Pattern uriPattern) {
        this.uriPattern = uriPattern;
    }

    public ObjectFactory<Processor<?>> getProcessorFactory() {
        return processorFactory;
    }

    public void setProcessorFactory(ObjectFactory<Processor<?>> processorFactory) {
        this.processorFactory = processorFactory;
    }

    public ObjectBuilder getRequestBuilder() {
        return requestBuilder;
    }

    public void setRequestBuilder(ObjectBuilder requestBuilder) {
        this.requestBuilder = requestBuilder;
    }
}