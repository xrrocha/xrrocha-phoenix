package com.sun.phoenix.web;

import static com.sun.phoenix.web.PhoenixWeb.PHOENIX_PROCESSOR_MANAGER;
import static com.sun.phoenix.web.PhoenixWeb.PHOENIX_REPOSITORY_LOCATION;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.sun.phoenix.ProcessorManager;
import com.sun.phoenix.components.store.CachingStore;
import com.sun.phoenix.components.store.NormalizingStore;
import com.sun.phoenix.components.store.Store;
import com.sun.phoenix.components.store.file.SerializedFileStore;
import com.sun.phoenix.components.store.file.SimpleFilenameNormalizer;
import com.sun.phoenix.factory.yaml.YamlProcessorFactoryRegistry;

public class PhoenixListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		ServletContext servletContext = servletContextEvent.getServletContext();

		final ProcessorManager processorManager = new ProcessorManager();

        final Store serializedFileStore = new SerializedFileStore(new File(PHOENIX_REPOSITORY_LOCATION));
        final Store normalizingStore = new NormalizingStore(serializedFileStore, new SimpleFilenameNormalizer());
        final Store cachingStore = new CachingStore(normalizingStore, 4096);
        processorManager.setStore(cachingStore);
        
        final YamlProcessorFactoryRegistry processorFactoryRegistry = new YamlProcessorFactoryRegistry();
        processorManager.setProcessorFactoryRegistry(processorFactoryRegistry);
        
		servletContext.setAttribute(PHOENIX_PROCESSOR_MANAGER, processorManager);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}
}
