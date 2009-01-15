package com.sun.phoenix.components.store;

import java.io.Serializable;
import java.util.Iterator;

public class NormalizingStore implements Store {
    private Store store;

    private IdentifierNormalizer identifierNormalizer;

    private static class NormalizedEntry implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private final String identifier;

        private final Object object;

        private NormalizedEntry(String identifier, Object value) {
            this.identifier = identifier;
            object = value;
        }
    }

    public NormalizingStore(Store store, IdentifierNormalizer identifierNormalizer) {
        this.store = store;
        this.identifierNormalizer = identifierNormalizer;
    }

    @Override
    public Object get(String identifier) {
        final String normalizedIdentifier = identifierNormalizer.normalizeFilename(identifier);
        final NormalizedEntry entry = (NormalizedEntry) store.get(normalizedIdentifier);
        if (entry == null) {
            return null;
        }
        return entry.object;
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
                        final NormalizedEntry entry = (NormalizedEntry) store.get(normalizedIdentifier);
                        return entry.identifier;
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
        store.put(normalizedIdentifier, new NormalizedEntry(identifier, object));
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
