package com.sun.phoenix.components.store.file;

import java.io.File;

import com.sun.phoenix.components.store.IdentifierNormalizer;

public class SimpleFilenameNormalizer implements IdentifierNormalizer {
    private final String separatorReplacement = "_";

    private final String pathSeparatorReplacement = "__";

    @Override
    public String normalizeFilename(String filename) {
        final StringBuilder builder = new StringBuilder();
        for (final char c : filename.toCharArray()) {
            if (c == '/' || c == File.separatorChar) {
                builder.append(separatorReplacement);
            } else if (pathSeparatorReplacement != null && c == File.pathSeparatorChar) {
                builder.append(pathSeparatorReplacement);
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public String getSeparatorReplacement() {
        return separatorReplacement;
    }

    public String getPathSeparatorReplacement() {
        return pathSeparatorReplacement;
    }
}
