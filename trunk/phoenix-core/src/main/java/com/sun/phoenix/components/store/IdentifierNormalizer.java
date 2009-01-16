package com.sun.phoenix.components.store;

public interface IdentifierNormalizer {
    public String normalizeFilename(String filename);
    public String denormalizeFilename(String normalizedFilename);
}
