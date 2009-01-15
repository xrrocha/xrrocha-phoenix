package com.sun.phoenix.factory;


public interface ProcessorFactoryLocator {
    public ProcessorFactory locateProcessorFactory(String uri) throws Exception;
}
