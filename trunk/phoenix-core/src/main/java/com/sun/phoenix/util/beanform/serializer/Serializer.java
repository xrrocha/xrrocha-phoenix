package com.sun.phoenix.util.beanform.serializer;

import java.text.ParseException;

public interface Serializer<T> {
	public String format(T value);
	public T parse(String s) throws ParseException;
}
