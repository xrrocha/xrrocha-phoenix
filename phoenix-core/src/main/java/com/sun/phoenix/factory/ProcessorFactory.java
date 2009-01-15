package com.sun.phoenix.factory;

import com.sun.phoenix.Processor;

public interface ProcessorFactory {
    public Processor<?> newProcessor() throws Exception;
    public Object buildRequestObject(Object requestSource) throws Exception;
    public void restoreTransientProperties(Processor<?> processor) throws Exception;
    public ClassLoader getClassLoader();
    public Class<?> getProcessorArgumentClass();
}
