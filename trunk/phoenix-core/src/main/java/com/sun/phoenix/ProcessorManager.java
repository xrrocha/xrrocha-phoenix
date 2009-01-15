package com.sun.phoenix;

import static com.sun.phoenix.ProcessStatus.SCHEDULED;
import static com.sun.phoenix.ProcessStatus.SUSPENDED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.javaflow.Continuation;

import com.sun.phoenix.components.store.Store;
import com.sun.phoenix.factory.ProcessorFactory;
import com.sun.phoenix.factory.ProcessorFactoryRegistry;

// FIXME: Why should processorManager be serializable at all?
public class ProcessorManager implements Serializable {
    private static final long serialVersionUID = 1L;

	transient private Store store;
	transient private ProcessorFactoryRegistry processorFactoryRegistry;

    private static Logger logger = Logger.getLogger(ProcessorManager.class.getName());

    public void process(String uri, Object requestSource) throws Exception {
        final ContinuationToken continuationToken = getContinuationToken(uri);
        final ProcessorFactory processorFactory = processorFactoryRegistry.locateProcessorFactory(uri);
        if (continuationToken == null) {
            if (processorFactory != null) {
                newInstance(uri, processorFactory, requestSource);
            } else {
                logger.warning("No suitable handler for '" + uri + "'");
            }
        } else {
            if (continuationToken.hasJoinDependents()) {
                Object requestObject = null;
                if (requestSource != null && processorFactory != null) {
                    requestObject = processorFactory.buildRequestObject(requestSource);
                }
                expectedInstance(uri, continuationToken, requestObject);
            } else {
                logger.warning("Requested process instance has no outstanding dependencies: " + uri);
            }
        }
    }

    public ProcessInfo[] getProcessInfo() throws Exception {
        final List<ProcessInfo> processInfos = new ArrayList<ProcessInfo>();
        for (final String identifier : store.identifiers()) {
            final ProcessInfo processInfo = new ProcessInfo();
            final ContinuationToken continuationToken = getContinuationToken(identifier);

            processInfo.setUri(continuationToken.uri);
            if (continuationToken.continuation != null) {
                processInfo.setStatus(SUSPENDED);
            } else {
                processInfo.setStatus(SCHEDULED);
            }

            processInfos.add(processInfo);
        }
        return processInfos.toArray(new ProcessInfo[processInfos.size()]);
    }
    
    public Object query(String uri) throws Exception {
        final ContinuationToken continuationToken = getContinuationToken(uri);
        if (continuationToken != null && continuationToken.processor != null) {
            return continuationToken.processor.query();
        } else {
            logger.warning("No such process: " + uri);
            return null;
        }
    }

    private void newInstance(String uri, ProcessorFactory processorFactory, Object requestSource) throws Exception {
        logger.info("Initiating process: " + uri);
        final Processor<?> processor = processorFactory.newProcessor();
        processor.setUri(uri);
        processor.setProcessManager(this);

        ContinuationToken continuationToken = new ContinuationToken(processor, uri, null);
        putWrapper(uri, continuationToken);

        logger.info("Initiating execution: " + uri);
        Object argument = processorFactory.buildRequestObject(requestSource);
        final Continuation continuation = Continuation.startWith(new ProcessorRunner(processor, argument));

        if (continuation != null) {
            logger.info("Process execution suspended: " + uri);
            continuationToken = new ContinuationToken(continuationToken, continuation);
            putWrapper(uri, continuationToken);
        } else {
            logger.info("Process execution complete: " + uri);
            removeWrapper(uri);
        }
    }

    private void expectedInstance(String uri, ContinuationToken continuationToken, Object requestObject) throws Exception {
        logger.info("Pending receive: " + uri);
        Continuation continuation = null;

        if (continuationToken.processor != null) {
            logger.info("Executing pending receive: " + uri);
            continuationToken.processor.setUri(uri);
            continuationToken.processor.setProcessManager(this);
            continuation = Continuation.startWith(new ProcessorRunner(continuationToken.processor, requestObject));
            if (continuation != null) {
                logger.info("Receive suspended: " + uri);
                putWrapper(uri, new ContinuationToken(continuationToken, continuation));
            } else {
                logger.info("Receive completed: " + uri);
                removeWrapper(uri);
            }
        } else {
            logger.info("Removing empty join: " + uri);
            removeWrapper(uri);
        }

        if (continuation == null) {
            logger.info("Removing dependents from receive: " + uri);
            final Iterator<String> i = continuationToken.getJoinDependents().iterator();
            while (i.hasNext()) {
                final String dependentUri = i.next();
                i.remove();
                final ContinuationToken dependentWrapper = getContinuationToken(dependentUri);
                dependentWrapper.getJoinDependencies().remove(continuationToken.uri);
                if (dependentWrapper.getJoinDependencies().size() == 0) {
                    logger.info("Last dependency satisfied, resuming: " + dependentUri);
                    dependentWrapper.processor.setProcessManager(this);
					ProcessorFactory processorFactory = processorFactoryRegistry.locateProcessorFactory(dependentUri);
					if (processorFactory != null) {
						// Needed to restore transient processor configuration after deserialization
						logger.info("Restoring transient properties");
					    processorFactory.restoreTransientProperties(dependentWrapper.processor);
					}
                    final Continuation c = Continuation.continueWith(dependentWrapper.continuation);
                    if (c != null) {
                        logger.info("Dependent process suspended: " + dependentUri);
                        putWrapper(dependentWrapper.uri, new ContinuationToken(dependentWrapper, c));
                    } else {
                        logger.info("Dependent process finished: " + dependentUri);
                        removeWrapper(dependentWrapper.uri);
                    }
                }
            }
        }
    }

    protected void expect(String receiveUri) throws Exception {
        this.expect(receiveUri, (Processor<?>) null);
    }

    protected void expect(final String receiveUri, final Processor<?> processor) throws Exception {
        logger.info("Adding pending receive: " + receiveUri);
        putWrapper(receiveUri, new ContinuationToken(processor, receiveUri, null));
    }

    protected void removeExpect(final String receiveUri) throws Exception {
        logger.info("Removing pending receive: " + receiveUri);
        removeWrapper(receiveUri);
    }

    protected void expect(String callerUri, String... calleeUris) throws Exception {
        logger.info("Join: " + callerUri);
        final ContinuationToken callerWrapper = getContinuationToken(callerUri);
        if (callerWrapper == null) {
            logger.warning("No such running process: " + callerUri);
            return;
        }
        for (final String calleeUri : calleeUris) {
            final ContinuationToken calleeWrapper = getContinuationToken(calleeUri);
            if (calleeWrapper == null) {
                logger.warning("...No such pending receive: " + calleeUri);
            } else {
                logger.info("...Added callee: " + calleeWrapper.uri);
                calleeWrapper.getJoinDependents().add(callerWrapper.uri);
                callerWrapper.getJoinDependencies().add(calleeWrapper.uri);
            }
        }
        if (callerWrapper.hasJoinDependencies()) {
            logger.info("Suspending caller: " + callerUri);
            Continuation.suspend();
            logger.info("Resuming: " + callerUri);
        }
    }

    private ContinuationToken getContinuationToken(String uri) throws Exception {
        return (ContinuationToken) store.get(uri);
    }

    private void putWrapper(String uri, ContinuationToken continuationToken) {
        store.put(uri, continuationToken);
    }

    private void removeWrapper(String uri) {
        store.remove(uri);
    }

    public static class ContinuationToken implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String uri;
        private final Continuation continuation;
        private final Processor<?> processor;
        private Set<String> joinDependencies;
        private Set<String> joinDependents;

        private ContinuationToken(Processor<?> processor, String uri, Continuation continuation) {
            this.uri = uri;
            this.processor = processor;
            this.continuation = continuation;
        }

        private ContinuationToken(ContinuationToken continuationToken, Continuation continuation) {
            this.continuation = continuation;

            uri = continuationToken.uri;
            processor = continuationToken.processor;
            joinDependencies = continuationToken.joinDependencies;
            joinDependents = continuationToken.joinDependents;
        }

        private Set<String> getJoinDependencies() {
            if (joinDependencies == null) {
                joinDependencies = new HashSet<String>();
            }
            return joinDependencies;
        }

        private Set<String> getJoinDependents() {
            if (joinDependents == null) {
                joinDependents = new HashSet<String>();
            }
            return joinDependents;
        }

        private boolean hasJoinDependents() {
            return joinDependents != null && joinDependents.size() > 0;
        }

        private boolean hasJoinDependencies() {
            return joinDependencies != null && joinDependencies.size() > 0;
        }
    }

    public static class ProcessorRunner implements Runnable, Serializable {
        private static final long serialVersionUID = 1L;

        private final Processor<?> processor;
        private final Object argument;

        public ProcessorRunner(Processor<?> processor, Object argument) {
            this.processor = processor;
            this.argument = argument;
        }

        @SuppressWarnings("unchecked")
		public void run() {
            try {
                ((Processor<Object>) processor).process(argument);
            } catch (final Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error executing receive operation: " + e, e);
            }
        }
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public ProcessorFactoryRegistry getProcessorFactoryRegistry() {
        return processorFactoryRegistry;
    }

    public void setProcessorFactoryRegistry(ProcessorFactoryRegistry processorFactoryLocator) {
        this.processorFactoryRegistry = processorFactoryLocator;
    }
}
