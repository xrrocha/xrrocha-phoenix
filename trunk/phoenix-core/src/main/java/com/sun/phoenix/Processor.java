package com.sun.phoenix;

import java.io.ObjectStreamField;
import java.io.Serializable;

public abstract class Processor<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uri;

    // FIXME: transient is not working properly; standard serialization attempts to serialize this property
    transient private ProcessorManager processorManager;
    // FIXME This declaration doesn't work (and is redundant with respect to 'processorManager' being transient)
    private static final ObjectStreamField[] serialPersistentFields = {
    	new ObjectStreamField("uri", String.class)
    };

    protected abstract void process(T argument) throws Exception;
    protected Object query() throws Exception { return null; }

    protected void expect(String expectUri) throws Exception {
        processorManager.expect(buildExtUri(expectUri));
    }

    protected void expect(String expectUri, Processor<?> processor) throws Exception {
    	processor.uri = buildExtUri(expectUri);
    	processor.processorManager = processorManager;
        processorManager.expect(processor.uri, processor);
    }

    protected void waitFor(String... uris) throws Exception {
    	String[] extUris = new String[uris.length];
    	for (int i = 0; i < uris.length; i++) {
    		extUris[i] = buildExtUri(uris[i]);
    	}
        processorManager.expect(uri, extUris);
    }

    protected void removeExpect(String expectUri) throws Exception {
        processorManager.removeExpect(expectUri);
    }
    
    private String buildExtUri(String suffix) {
    	return uri + "/" + suffix;
    }

    public String getUri() {
        return uri;
    }
    
    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setProcessManager(ProcessorManager processorManager) {
        this.processorManager = processorManager;
    }
}
