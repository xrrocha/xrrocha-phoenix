package com.sun.phoenix.components.store;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CachingStore implements Store {
    private static final float hashTableLoadFactor = 0.75f;

    private Store store;

    private Map<String, Object> cache;

    public CachingStore() {
    }

    public CachingStore(final Store persistor, final int cacheSize) {
        store = persistor;

        final int hashTableCapacity = (int) Math.ceil(cacheSize / hashTableLoadFactor) + 1;
        cache = new LinkedHashMap<String, Object>(hashTableCapacity, hashTableLoadFactor, true) {
            private static final long serialVersionUID = 1;

            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
                return size() > cacheSize;
            };
        };
        cache = Collections.synchronizedMap(cache);
    }

    @Override
    public Object get(String identifier) {
        if (!cache.containsKey(identifier)) {
            final Object object = store.get(identifier);
            if (object != null) {
                cache.put(identifier, store.get(identifier));
            }
        }
        return cache.get(identifier);
    }

    @Override
    public Iterable<String> identifiers() {
        return store.identifiers();
    }

    @Override
    public void put(String identifier, Object object) {
        store.put(identifier, object);
        cache.put(identifier, object);
    }

    @Override
    public void remove(String identifier) {
        cache.remove(identifier);
        store.remove(identifier);
    }

    public boolean hasIdentifier(String identifier) {
        return store.hasIdentifier(identifier);
    }
}
