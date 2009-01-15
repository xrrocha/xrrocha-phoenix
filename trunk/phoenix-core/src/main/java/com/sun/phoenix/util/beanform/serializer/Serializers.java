package com.sun.phoenix.util.beanform.serializer;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Serializers {
	public static final String defaultDatePattern = "MM/dd/yyyy";
	private static DateFormat dateFormat = new SimpleDateFormat(defaultDatePattern);

	private static final Map<Class<?>, Serializer<?>> globalSerializers = new HashMap<Class<?>, Serializer<?>>();

	public static Map<Class<?>, Serializer<?>> getGlobalSerializers() {
		return globalSerializers;
	}

	public static Object parse(String value, Class<?> valueClass) throws ParseException {
		return parse(value, valueClass, globalSerializers);
	}

	public static Object parse(String value, Class<?> valueClass, Map<Class<?>, Serializer<?>> parsers) throws ParseException {
		Serializer<?> parser = parsers.get(valueClass);
		if (parser == null) {
			throw new IllegalArgumentException("Not a scalar type: '" + valueClass.getName() + "'");
		}
		return parser.parse(value);
	}

	public static DateFormat setDateFormat(DateFormat newDateFormat) {
		if (newDateFormat == null) {
			throw new NullPointerException("Date format cannot be null");
		}
		DateFormat previous = dateFormat;
		dateFormat = newDateFormat;
		return previous;
	}
	
	static {
		globalSerializers.put(String.class, new AbstractSerializer<String>() {
			public String parse(String value) {
				return value;
			}
		});
		
		Serializer<Character> characterSerializer = new AbstractSerializer<Character>() {
			public Character parse(String value) {
				return Character.valueOf(value.charAt(0));
			}
		};
		globalSerializers.put(char.class, characterSerializer);
		globalSerializers.put(Character.class, characterSerializer);
		
		Serializer<Byte> byteSerializer = new AbstractSerializer<Byte>() {
			public Byte parse(String value) {
				return Byte.parseByte(value);
			}
		};
		globalSerializers.put(Byte.class, byteSerializer);
		globalSerializers.put(byte.class, byteSerializer);
		
		Serializer<Boolean> booleanSerializer = new AbstractSerializer<Boolean>() {
			public Boolean parse(String value) {
				return Boolean.valueOf(value);
			}
		};
		globalSerializers.put(Boolean.class, booleanSerializer);
		globalSerializers.put(boolean.class, booleanSerializer);
		
		Serializer<Integer> integerSerializer = new AbstractSerializer<Integer>() {
			public Integer parse(String value) {
				return Integer.parseInt(value);
			}
		};
		globalSerializers.put(Integer.class, integerSerializer);
		globalSerializers.put(int.class, integerSerializer);

		Serializer<Short> shortSerializer = new AbstractSerializer<Short>() {
			public Short parse(String value) {
				return Short.parseShort(value);
			}
		}; 
		globalSerializers.put(Short.class, shortSerializer);
		globalSerializers.put(short.class, shortSerializer);
		
		Serializer<Long> longSerializer = new AbstractSerializer<Long>() {
			public Long parse(String value) {
				return Long.parseLong(value);
			}
		};
		globalSerializers.put(Long.class, longSerializer);
		globalSerializers.put(long.class, longSerializer);

		Serializer<Float> floatSerializer = new AbstractSerializer<Float>() {
			public Float parse(String value) {
				return Float.parseFloat(value);
			}
		};
		globalSerializers.put(Float.class, floatSerializer);
		globalSerializers.put(float.class, floatSerializer);

		Serializer<Double> doubleSerializer = new AbstractSerializer<Double>() {
			public Double parse(String value) {
				return Double.parseDouble(value);
			}
		};
		globalSerializers.put(Double.class, doubleSerializer);
		globalSerializers.put(double.class, doubleSerializer);
		
		globalSerializers.put(BigDecimal.class, new AbstractSerializer<BigDecimal>() {
			public BigDecimal parse(String value) {
				return BigDecimal.valueOf(Double.parseDouble(value));
			}
		});

		globalSerializers.put(Date.class, new Serializer<Date>() {
			public Date parse(String value) throws ParseException {
				return dateFormat.parse(value);
			}
			public String format(Date date) {
				return dateFormat.format(date);
			}
		});
	}
}
