package com.sun.phoenix.example.vacation;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtilsBean;

public class TemplateExpander {
    private String openingDelimiter = "${";

    private String closingDelimiter = "}";

    public String expand(String template, Map<String, Object> context) {
        if (template == null) {
            return null;
        }
        if (context == null) {
            return template;
        }
        int currentPosition = 0;
        final int stringLength = template.length();
        final StringBuilder builder = new StringBuilder();
        while (currentPosition < stringLength) {
            final int initialPosition = template.indexOf(openingDelimiter, currentPosition);
            if (initialPosition == -1) {
                builder.append(template.substring(currentPosition));
                break;
            }

            builder.append(template.substring(currentPosition, initialPosition));
            currentPosition = initialPosition + openingDelimiter.length();
            final int finalPosition = template.indexOf(closingDelimiter, currentPosition);
            if (finalPosition == -1) {
                throw new IllegalArgumentException("Unclosed variable reference: " + template.substring(currentPosition, currentPosition + 8) + "...");
            }

            final String name = template.substring(currentPosition, finalPosition);

            String beanName = null;
            String propertyName = null;
            final int pos = name.indexOf('.');
            if (pos == -1) {
                beanName = name;
            } else {
                beanName = name.substring(0, pos);
                propertyName = name.substring(pos + 1);
            }

            if (context.containsKey(beanName)) {
                final Object bean = context.get(beanName);
                Object propertyValue = bean;
                if (propertyName != null) {
                    try {
                        propertyValue = new PropertyUtilsBean().getProperty(bean, propertyName);
                    } catch (final Exception e) {
                    }
                }
                if (propertyValue != null) {
                    String value = propertyValue.toString();
                    if (value.indexOf(openingDelimiter) != -1) {
                        String newValue = null;
                        String previousValue = value;
                        while (!(newValue = expand(previousValue, context)).equals(previousValue)) {
                            previousValue = newValue;
                        }
                        value = newValue;
                    }
                    builder.append(value);
                }
            } else {
                builder.append(openingDelimiter);
                builder.append(beanName);
                builder.append(closingDelimiter);
            }
            currentPosition = finalPosition + closingDelimiter.length();
        }
        return builder.toString();
    }

    public Set<String> variableReferences(String template) {
        if (template == null) {
            return null;
        }

        int currentPosition = 0;
        final int stringLength = template.length();
        final Set<String> variableReferences = new LinkedHashSet<String>();

        while (currentPosition < stringLength) {
            final int initialPosition = template.indexOf(openingDelimiter, currentPosition);
            if (initialPosition == -1) {
                break;
            }
            currentPosition = initialPosition + openingDelimiter.length();
            final int finalPosition = template.indexOf(closingDelimiter, currentPosition);
            if (finalPosition == -1) {
                throw new IllegalArgumentException("Unclosed variable reference: " + template.substring(currentPosition, currentPosition + 8) + "...");
            }

            final String name = template.substring(currentPosition, finalPosition);

            variableReferences.add(name);
            currentPosition = finalPosition + closingDelimiter.length();
        }

        return variableReferences;
    }

    public String getOpeningDelimiter() {
        return openingDelimiter;
    }

    public void setOpeningDelimiter(String openingDelimiter) {
        this.openingDelimiter = openingDelimiter;
    }

    public String getClosingDelimiter() {
        return closingDelimiter;
    }

    public void setClosingDelimiter(String closingDelimiter) {
        this.closingDelimiter = closingDelimiter;
    }
}
