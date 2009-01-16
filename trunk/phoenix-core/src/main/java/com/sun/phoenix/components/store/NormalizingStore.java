package com.sun.phoenix.components.store;

import java.util.Iterator;

public class NormalizingStore implements Store {
    private Store store;

    private IdentifierNormalizer identifierNormalizer;

    public NormalizingStore(Store store, IdentifierNormalizer identifierNormalizer) {
        this.store = store;
        this.identifierNormalizer = identifierNormalizer;
    }

    @Override
    public Object get(String identifier) {
        final String normalizedIdentifier = identifierNormalizer.normalizeFilename(identifier);
        return store.get(normalizedIdentifier);
    }

    @Override
    public Iterable<String> identifiers() {
        final Iterator<String> iterator = store.identifiers().iterator();
        return new Iterable<String>() {
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public String next() {
                        final String normalizedIdentifier = iterator.next();
                        return identifierNormalizer.denormalizeFilename(normalizedIdentifier);
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Unsupported operation: remove");
                    }
                };
            }
        };
    }

    @Override
    public void put(String identifier, Object object) {
        final String normalizedIdentifier = identifierNormalizer.normalizeFilename(identifier);
        store.put(normalizedIdentifier, object);
    }

    @Override
    public void remove(String identifier) {
        final String normalizedIdentifier = identifierNormalizer.normalizeFilename(identifier);
        store.remove(normalizedIdentifier);
    }

    public boolean hasIdentifier(String identifier) {
        final String normalizedIdentifier = identifierNormalizer.normalizeFilename(identifier);
        return store.hasIdentifier(normalizedIdentifier);
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public IdentifierNormalizer getIdentifierNormalizer() {
        return identifierNormalizer;
    }

    public void setIdentifierNormalizer(IdentifierNormalizer identifierNormalizer) {
        this.identifierNormalizer = identifierNormalizer;
    }

}
