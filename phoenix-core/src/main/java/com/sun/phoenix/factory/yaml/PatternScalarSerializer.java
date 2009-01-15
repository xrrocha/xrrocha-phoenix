/**
 * 
 */
package com.sun.phoenix.factory.yaml;

import java.util.regex.Pattern;

import net.sourceforge.yamlbeans.YamlException;
import net.sourceforge.yamlbeans.scalar.ScalarSerializer;

public class PatternScalarSerializer implements ScalarSerializer<Pattern> {
    @Override
    public Pattern read(String value) throws YamlException {
        return Pattern.compile(value);
    }
    @Override
    public String write(Pattern pattern) throws YamlException {
        return pattern.toString();
    }
}