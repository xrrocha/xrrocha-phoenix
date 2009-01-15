/**
 * 
 */
package com.sun.phoenix.components.builder;

import java.io.Reader;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;

public class XStreamObjectBuilder implements ObjectBuilder {
    private XStream xstream;
    
    private String dateFormat;
    private Map<String, String> aliases;
    
    public XStreamObjectBuilder() {
        xstream = new XStream();
    }

    @Override
    public Object buildObject(Object source) throws Exception {
        return xstream.fromXML((Reader) source);
    }
    
    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        xstream.registerConverter(new DateConverter(dateFormat, new String[0]));
    }

    public Map<String, String> getAliases() {
        return aliases;
    }
    
    public void setAliases(Map<String, String> aliases) throws Exception {
        this.aliases = aliases;
        for (String alias: aliases.keySet()) {
            xstream.alias(alias, Class.forName(aliases.get(alias)));
        }
    }
}