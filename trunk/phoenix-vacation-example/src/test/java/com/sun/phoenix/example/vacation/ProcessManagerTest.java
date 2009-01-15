package com.sun.phoenix.example.vacation;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.logging.Logger;

import com.sun.phoenix.ProcessInfo;
import com.sun.phoenix.ProcessorManager;
import com.sun.phoenix.components.store.CachingStore;
import com.sun.phoenix.components.store.NormalizingStore;
import com.sun.phoenix.components.store.Store;
import com.sun.phoenix.components.store.file.SerializedFileStore;
import com.sun.phoenix.components.store.file.SimpleFilenameNormalizer;
import com.sun.phoenix.factory.yaml.YamlProcessorFactoryRegistry;

public class ProcessManagerTest {
    private static final Logger logger = Logger.getLogger(ProcessManagerTest.class.getName());
    
    public static void main(String[] args) throws Exception {
        final ProcessorManager processorManager = new ProcessorManager();

        final String repositoryLocation = "repository";
        final Store serializedFileStore = new SerializedFileStore(new File(repositoryLocation));
        final Store normalizingStore = new NormalizingStore(serializedFileStore, new SimpleFilenameNormalizer());
        final Store cachingStore = new CachingStore(normalizingStore, 4096);
        processorManager.setStore(cachingStore);
        
        final YamlProcessorFactoryRegistry processorFactoryRegistry = new YamlProcessorFactoryRegistry();
        processorFactoryRegistry.addProcessor(new FileReader("vacation.yaml"));
        processorManager.setProcessorFactoryRegistry(processorFactoryRegistry);

        deleteDirectory("repository");

        logger.info("Sending request /vacationRequest/emp01");
        processorManager.process("/vacationRequest/emp01", new StringReader(
                "<vacationRequest>" +
                    "<employeeId>emp01</employeeId>" +
                    "<startDate>1/1/2009</startDate>" +
                    "<endDate>2/1/2009 </endDate>" +
                    "<comments>Need to unplug</comments>" +
                "</vacationRequest>"));
        printStatus(processorManager);

        logger.info("Sending request /vacationRequest/emp01/hrAcknowledgement");
        processorManager.process("/vacationRequest/emp01/hrAcknowledgement", null);
        printStatus(processorManager);

        logger.info("Sending request /vacationRequest/emp01/managerReply");
        processorManager.process("/vacationRequest/emp01/managerReply", new StringReader(
                "<managerReply>" +
                    "<approved>true</approved>" +
                    "<comments>Have fun</comments>" +
                "</managerReply>"));
        printStatus(processorManager);

        logger.info("Sending request /vacationRequest/emp01/employeeAcknowledgement");
        processorManager.process("/vacationRequest/emp01/employeeAcknowledgement", null);
        printStatus(processorManager);
    }

    private static void printStatus(ProcessorManager processorManager) throws Exception {
        ProcessInfo[] infos = processorManager.getProcessInfo();
        for (final ProcessInfo info : infos) {
            logger.info("*** Status: " + info.getUri() + " -> " + info.getStatus());
            Object status = processorManager.query(info.getUri());
            if (status != null) {
                logger.info("....." + status);
            }
        }
    }
    
    private static void deleteDirectory(String directoryName) {
        deleteFile(new File(directoryName));
    }
    private static void deleteFile(File file) {
        if (file.isDirectory()) {
            for (File child: file.listFiles()) {
                deleteFile(child);
            }
            file.delete();
        } else {
            file.delete();
        }
    }
}
