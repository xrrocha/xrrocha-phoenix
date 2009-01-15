package com.sun.phoenix.deployer;

import java.util.jar.JarInputStream;

import com.sun.phoenix.factory.ProcessorFactory;

public interface ProcessorDeployer {
	public ProcessorFactory deploy(String name, JarInputStream inputStream) throws Exception;
	public void undeploy(String name) throws Exception;
	public ProcessorFactory[] getProcessorFactories();
}
