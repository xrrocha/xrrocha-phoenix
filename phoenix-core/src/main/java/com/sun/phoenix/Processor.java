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

    protected void expect(String receiveUri) throws Exception {
        processorManager.expect(receiveUri);
    }

    protected void expect(String receiveUri, Processor<?> processor) throws Exception {
        processorManager.expect(receiveUri, processor);
    }

    protected void waitFor(String... uris) throws Exception {
        processorManager.expect(uri, uris);
    }

    protected void removeExpect(String receiveUri) throws Exception {
        processorManager.removeExpect(receiveUri);
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
