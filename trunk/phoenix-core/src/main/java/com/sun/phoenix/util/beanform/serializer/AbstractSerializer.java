package com.sun.phoenix.util.beanform.serializer;

public abstract class AbstractSerializer<T> implements Serializer<T> {
	public String format(T value) {
		if (value == null) {
			return null;
		}
		return value.toString();
	}
}
