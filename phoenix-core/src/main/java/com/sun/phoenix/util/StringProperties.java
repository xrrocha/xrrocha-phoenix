package com.sun.phoenix.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StringProperties {
	private Map<String, String> properties;
	
	public StringProperties() {}
	
	public StringProperties(Map<String, String> properties) {
		this.properties = properties;
	}
	
	public Byte getByte(String propertyName) {
		String value = properties.get(propertyName);
		if (value == null || value.length() == 0) {
			return null;
		}
		return Byte.valueOf(value);
	}
	
	public Character getCharacter(String propertyName) {
		String value = properties.get(propertyName);
		if (value == null || value.length() == 0) {
			return null;
		}
		return Character.valueOf(value.charAt(0));
	}
	
	public Boolean getBoolean(String propertyName) {
		String value = properties.get(propertyName);
		return Boolean.valueOf(value);
	}
	
	public String getString(String propertyName) {
		return properties.get(propertyName);
	}
	
	public Integer getInteger(String propertyName) {
		String value = properties.get(propertyName);
		if (value == null) {
			return null;
		}
		return Integer.parseInt(value);
	}
	
	public Short getShort(String propertyName) {
		String value = properties.get(propertyName);
		if (value == null || value.length() == 0) {
			return null;
		}
		return Short.parseShort(value);
	}
	
	public Long getLong(String propertyName) {
		String value = properties.get(propertyName);
		if (value == null || value.length() == 0) {
			return null;
		}
		return Long.parseLong(value);
	}
	
	public Float getFloat(String propertyName) {
		String value = properties.get(propertyName);
		if (value == null || value.length() == 0) {
			return null;
		}
		return Float.parseFloat(value);
	}
	
	public Double getDouble(String propertyName) {
		String value = properties.get(propertyName);
		if (value == null || value.length() == 0) {
			return null;
		}
		return Double.parseDouble(value);
	}
	
	public BigDecimal getBigDecimal(String propertyName) {
		String value = properties.get(propertyName);
		if (value == null || value.length() == 0) {
			return null;
		}
		return BigDecimal.valueOf(Double.parseDouble(value));
	}
	
	private static final String defaultDatePattern = "MM/dd/yyyy";
	private static final DateFormat defaultDateFormat = new SimpleDateFormat(defaultDatePattern);
	private static final Map<String, DateFormat> dateFormats = new HashMap<String, DateFormat>();
	static {
		dateFormats.put(defaultDatePattern, defaultDateFormat);
	}
	public Date getDate(String propertyName) throws ParseException {
		return getDate(propertyName, defaultDatePattern);
	}

	public Date getDate(String propertyName, String formatPattern) throws ParseException {
		String value = properties.get(propertyName);
		if (value == null || value.length() == 0) {
			return null;
		}
		DateFormat dateFormat = dateFormats.get(formatPattern);
		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat(formatPattern);
			synchronized (dateFormats) {
				dateFormats.put(formatPattern, dateFormat);
			}
		}
		return dateFormat.parse(value);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
}
