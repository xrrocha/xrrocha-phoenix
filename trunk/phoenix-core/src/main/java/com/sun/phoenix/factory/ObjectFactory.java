package com.sun.phoenix.factory;

public interface ObjectFactory<T> {
    public T newInstance() throws Exception;
    public void restoreTransientProperties(T element) throws Exception;
}