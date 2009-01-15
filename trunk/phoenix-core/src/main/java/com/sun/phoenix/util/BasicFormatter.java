package com.sun.phoenix.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class BasicFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        final StringBuilder builder = new StringBuilder();
        String className = record.getSourceClassName();
        className = className.substring(className.lastIndexOf('.') + 1);
        builder.append(className);
        builder.append('.');
        builder.append(record.getSourceMethodName());
        builder.append(": ");
        builder.append(record.getMessage());
        builder.append('\n');
        return builder.toString();
    }
}
