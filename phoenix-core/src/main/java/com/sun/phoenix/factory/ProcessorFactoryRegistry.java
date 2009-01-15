package com.sun.phoenix.factory;


public interface ProcessorFactoryRegistry {
    public ProcessorFactory locateProcessorFactory(String uri) throws Exception;
}
