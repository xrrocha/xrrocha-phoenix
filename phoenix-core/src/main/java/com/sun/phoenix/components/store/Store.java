package com.sun.phoenix.components.store;

public interface Store {
    public void put(String identifier, Object object);

    public Object get(String identifier);

    public void remove(String identifier);

    public Iterable<String> identifiers();

    public boolean hasIdentifier(String identifier);
}
