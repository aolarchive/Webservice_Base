/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.util.types;

/**
 * @author human
 *
 */
public class TypesHelper {
	public static final String TYPE_OBJECT = "Object";
	public static final String TYPE_STRING = "String";
	public static final String TYPE_BOOLEAN = "Boolean";
	public static final String TYPE_INTEGER = "Integer";
	public static final String TYPE_LONG = "Long";
	public static final String TYPE_DOUBLE = "Double";
	public static final String TYPE_FLOAT = "Float";

	protected static final String OBJECT_CLASS_NAME = Object.class.getName();
	protected static final String STRING_CLASS_NAME = String.class.getName();
	protected static final String BOOLEAN_CLASS_NAME = Boolean.class.getName();
	protected static final String INTEGER_CLASS_NAME = Integer.class.getName();
	protected static final String LONG_CLASS_NAME = Long.class.getName();
	protected static final String DOUBLE_CLASS_NAME = Double.class.getName();
	protected static final String FLOAT_CLASS_NAME = Float.class.getName();
	
	
	public static boolean isSimpleType(Class<?> type) {
		// primitive types and strings are not treated as composite (and not object)
		return ((type.isPrimitive() || type.getName().equals("java.util.Date") || 
					(type.getName().startsWith("java.lang.") && !type.getName().equals(OBJECT_CLASS_NAME))));
	}

	public static String determineSwitcher(String expectedType) {		
		String switcher = TypesHelper.TYPE_OBJECT;
		if (expectedType.equals(String.class.getName()))
			switcher = TypesHelper.TYPE_STRING;
		else if (expectedType.equals("boolean") || expectedType.equals(BOOLEAN_CLASS_NAME))
			switcher = TypesHelper.TYPE_BOOLEAN;
		else if (expectedType.equals("int") || expectedType.equals(INTEGER_CLASS_NAME))
			switcher = TypesHelper.TYPE_INTEGER;
		else if (expectedType.equals("long") || expectedType.equals(LONG_CLASS_NAME))
			switcher = TypesHelper.TYPE_LONG;
		else if (expectedType.equals("double") || expectedType.equals(DOUBLE_CLASS_NAME))
			switcher = TypesHelper.TYPE_DOUBLE;
		else if (expectedType.equals("float") || expectedType.equals(FLOAT_CLASS_NAME))
			switcher = TypesHelper.TYPE_FLOAT;
		return switcher;
	}	
	
	public static Object performSwitch(String value, String switcher) {
		if (value == null)
			return value;
		
		Object switched = value;

		// trim value before attempting conversion
		value = value.trim();
		if (switcher.equals(TypesHelper.TYPE_BOOLEAN))
			switched = Boolean.parseBoolean((String)value);
		else if (switcher.equals(TypesHelper.TYPE_INTEGER))
			switched = Integer.parseInt((String)value);
		else if (switcher.equals(TypesHelper.TYPE_LONG))
			switched = Long.parseLong((String)value);
		else if (switcher.equals(TypesHelper.TYPE_DOUBLE))
			switched = Double.parseDouble((String)value);
		else if (switcher.equals(TypesHelper.TYPE_FLOAT))
			switched = Float.parseFloat((String)value);		

		return switched;
	}
	
	
}
