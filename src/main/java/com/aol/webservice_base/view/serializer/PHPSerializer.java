/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.view.serializer;

import java.io.DataOutputStream;
import java.io.IOException;

import com.aol.webservice_base.view.util.ObjectUtils;

public class PHPSerializer extends BaseSerializer {

	protected static final byte[] NULL_NODE = "N".getBytes();
	protected static final byte[] ARRAY_NODE = "a:".getBytes();
	protected static final byte[] BOOLEAN_NODE = "b:".getBytes();
	protected static final byte[] DOUBLE_NODE =  "d:".getBytes();
	protected static final byte[] INTEGER_NODE = "i:".getBytes();
	protected static final byte[] OBJECT_NODE = "O:".getBytes();
	protected static final byte[] STRING_NODE = "s:".getBytes();

	protected static final byte[] BOOLEAN_TRUE = "1".getBytes();
	protected static final byte[] BOOLEAN_FALSE = "0".getBytes();
	
	protected static final byte[] SEPARATOR_QUOTE = ":\"".getBytes();
	protected static final byte[] SEPARATOR_BRACE = ":{".getBytes();
	protected static final byte[] CLOSE_BRACE = "}".getBytes();
	protected static final byte[] TERMINATOR = ";".getBytes();
	protected static final byte[] CLOSE_STRING_SEPARATOR = "\":".getBytes();
	protected static final byte[] CLOSE_STRING_TERMINATOR = "\";".getBytes();
	
	@Override
	protected boolean wantsNulls() {
		return true;
	}
	
	protected static boolean isBoolean(Class clazz) {
		return (clazz.equals(boolean.class) || clazz.equals(Boolean.class));
	}

	protected static boolean isInteger(Class clazz) {
		return (clazz.equals(int.class) || clazz.equals(Integer.class) || clazz.equals(long.class) || clazz.equals(Long.class));
	}

	protected static boolean isDouble(Class clazz) {
		return (clazz.equals(double.class) || clazz.equals(Double.class) || clazz.equals(float.class) || clazz.equals(Float.class));
	}

	protected static void appendName(DataOutputStream os, String name) throws IOException {
		if (name != null) {
			int bytes = name.getBytes().length;
			os.write(STRING_NODE);
			os.write(Integer.toString(bytes).getBytes());
			os.write(SEPARATOR_QUOTE);
			os.write(name.getBytes());
			os.write(CLOSE_STRING_TERMINATOR);
		}
	}

	@Override
	protected void serialize(DataOutputStream os, Object objMarshall) throws SerializerException, IOException {
		if (objMarshall == null) {
			os.write(NULL_NODE);
		} else {
			super.serialize(os, objMarshall);
		}
	}
	@Override
	protected void openStartNode(DataOutputStream os, String name, boolean isSimple, Object object) throws IOException {		
		if (isSimple) {
			throw new RuntimeException("Should never render object as simple");
		}
		
		// use the class name here....
		name = object.getClass().getSimpleName();
		int bytes = name.getBytes().length;
		os.write(OBJECT_NODE);
		os.write(Integer.toString(bytes).getBytes());
		os.write(SEPARATOR_QUOTE);
		os.write(name.getBytes());
		os.write(CLOSE_STRING_SEPARATOR);
		os.write(Integer.toString(ObjectUtils.getAll(object).size()).getBytes());
		os.write(SEPARATOR_BRACE);
	}

	protected byte[] getSimpleTypeIdentifier(Object object) {
		if (object == null) {
			return NULL_NODE;
		}

		Class clazz = object.getClass();
		if (isInteger(clazz)) {
			return INTEGER_NODE;
		} else if (isBoolean(clazz)) {
			return BOOLEAN_NODE;
		} else if (isDouble(clazz)) {
			return DOUBLE_NODE;
		} else if (clazz.equals(String.class)) {
			return STRING_NODE;
		}
		
		return null;		
	}

	@Override
	protected void startNode(DataOutputStream os, String name, boolean isSimple, Object object) throws IOException {
		byte[] typeIdentifier = getSimpleTypeIdentifier(object);
		if (typeIdentifier != null) {
			appendName(os, name);
			if (typeIdentifier != NULL_NODE) {
				os.write(typeIdentifier);
				
				// special treatment for strings
				if (typeIdentifier == STRING_NODE) {
					int bytes = ((String)object).getBytes().length;
					os.write(Integer.toString(bytes).getBytes());
					os.write(SEPARATOR_QUOTE);
				}
			}
			
			return;
		} else {
			appendName(os, name);

			// we don't process arrays
			if (object.getClass().getSimpleName().endsWith("[]"))
				return;
			
			openStartNode(os, name, isSimple, object);
		}

	}

	@Override
	protected void endNode(DataOutputStream os, String name, boolean isSimple, Object object) throws IOException {
		if (object == null) {
			os.write(TERMINATOR);
			return;
		}
		
		if (object.getClass().getSimpleName().endsWith("[]"))
			return;
		
		if (object.getClass().equals(String.class))
			os.write(CLOSE_STRING_TERMINATOR);
		else if (!isSimple) {
			os.write(CLOSE_BRACE); // no trailing ;
		} else {
			os.write(TERMINATOR);
		}
	}

	@Override
	protected void startArray(DataOutputStream os, int size) throws IOException {
		os.write(ARRAY_NODE);
		os.write(Integer.toString(size).getBytes());
		os.write(SEPARATOR_BRACE);
	}

	@Override
	protected void endArray(DataOutputStream os) throws IOException {
		os.write(CLOSE_BRACE);
	}

	@Override
	protected void startArrayNode(DataOutputStream os, int index, String name, boolean isSimple, Object object) throws IOException {
		os.write(INTEGER_NODE);
		os.write(Integer.toString(index).getBytes());
		os.write(TERMINATOR);
		
		if (object == null) {
			// null object - we don't know type...  it's just N object
			return;
		} else if (isSimple) {
			byte[] typeIdentifier = getSimpleTypeIdentifier(object);
			os.write(typeIdentifier);
		} else { 
			openStartNode(os, name, isSimple, object);
		}
	}

	@Override
	protected void endArrayNode(DataOutputStream os, String name, boolean isSimple, Object object) throws IOException {
		if ((object == null) || isSimple) {
			os.write(TERMINATOR);
		} else {
			os.write(CLOSE_BRACE);
		}
	}

	@Override
	protected void writeValue(DataOutputStream os, Object value) throws IOException {
		if (value == null) { 
			os.write(NULL_NODE);
			return;
		}

		Class clazz = value.getClass();

		if (clazz.equals(Boolean.class) || clazz.equals(boolean.class))
			os.write(((Boolean)value) ? BOOLEAN_TRUE : BOOLEAN_FALSE);
		else 
			os.write(value.toString().getBytes());
	}

}
