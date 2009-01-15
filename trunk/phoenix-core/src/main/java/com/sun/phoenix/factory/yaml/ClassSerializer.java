package com.sun.phoenix.factory.yaml;

import net.sourceforge.yamlbeans.YamlException;
import net.sourceforge.yamlbeans.scalar.ScalarSerializer;

public class ClassSerializer implements ScalarSerializer<Class<?>> {
	private ClassLoader classLoader;
	
	public ClassSerializer() {}
	
	public ClassSerializer(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public String write (Class<?> aClass) throws YamlException {
		return aClass.getName();
	}

	public Class<?> read (String className) throws YamlException {
		try {
			if (classLoader != null) {
				return classLoader.loadClass(className);
			}
			return Class.forName(className);
		} catch (Throwable t) {
			throw new YamlException("Error loading class '" + className + "': " + t, t);
		}
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
}
