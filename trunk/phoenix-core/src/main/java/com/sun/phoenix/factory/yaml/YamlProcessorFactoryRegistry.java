package com.sun.phoenix.factory.yaml;

import java.io.Reader;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.sourceforge.yamlbeans.YamlConfig;
import net.sourceforge.yamlbeans.YamlReader;
import net.sourceforge.yamlbeans.scalar.ScalarSerializer;

import com.sun.phoenix.factory.ProcessorFactory;
import com.sun.phoenix.factory.ProcessorFactoryImpl;
import com.sun.phoenix.factory.ProcessorFactoryRegistry;

public class YamlProcessorFactoryRegistry implements ProcessorFactoryRegistry {
    private static final ScalarSerializer<Class<?>> classSerializer = new ClassSerializer();
    private static final ScalarSerializer<Pattern> patternSerializer = new PatternScalarSerializer();
    
    private Set<ProcessorFactoryImpl> processorFactories = new LinkedHashSet<ProcessorFactoryImpl>();

    public void addProcessor(Reader reader, ClassLoader classLoader) throws Exception {
        YamlConfig yamlConfig = new YamlConfig();
    	yamlConfig.readConfig.setClassLoader(classLoader);
        yamlConfig.setScalarSerializer(Class.class, classSerializer);
        yamlConfig.setScalarSerializer(Pattern.class, patternSerializer);

        YamlReader yamlReader = new YamlReader(reader, yamlConfig);
        
        ProcessorFactoryImpl processorFactory = yamlReader.read(ProcessorFactoryImpl.class);
        processorFactories.add(processorFactory);
    }

    @Override
    public ProcessorFactory locateProcessorFactory(String uri) throws Exception {
        for (ProcessorFactoryImpl processorFactory: processorFactories) {
            if (processorFactory.matches(uri)) {
                return processorFactory;
            }
        }
        throw new IllegalArgumentException("No handler for : '" + uri + "'");
    }
}
