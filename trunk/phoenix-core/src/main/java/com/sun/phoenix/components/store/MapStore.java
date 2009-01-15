package com.sun.phoenix.components.store;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapStore implements Store {
    private final Map<String, Object> objects = new LinkedHashMap<String, Object>();

    @Override
    public Object get(String identifier) {
        return objects.get(identifier);
    }

    @Override
    public Iterable<String> identifiers() {
        return objects.keySet();
    }

    @Override
    public void put(String identifier, Object object) {
        objects.put(identifier, object);
    }

    @Override
    public void remove(String identifier) {
        objects.remove(identifier);
    }

    public boolean hasIdentifier(String identifier) {
        return objects.containsKey(identifier);
    }
}
