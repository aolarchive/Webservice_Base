/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.view.serializer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

public class AMF3Serializer extends BaseSerializer {
   private static final byte DATA_TYPE_BOOLEAN_FALSE 	= 2;
   private static final byte DATA_TYPE_BOOLEAN_TRUE 	= 3;
   private static final byte DATA_TYPE_INTEGER 			= 4;
   private static final byte DATA_TYPE_NUMBER 			= 5;
   private static final byte DATA_TYPE_STRING 			= 6;
   private static final byte DATA_TYPE_DATE 				= 8;
   private static final byte DATA_TYPE_ARRAY 			= 9;
   private static final byte DATA_TYPE_OBJECT 			= 10;

	@Override
	protected void writeHeader(DataOutputStream os) throws IOException {
		os.writeByte(DATA_TYPE_OBJECT);
		os.writeShort(0x0b01);
	}

	@Override
	protected void writeFooter(DataOutputStream os) throws IOException {
		os.writeByte(0x01);
	}	
	
	@Override
	protected boolean wantsNulls() {
		return false;
	}
	
	@Override
	protected void startNode(DataOutputStream os, String name, boolean isSimple,
			Object object) throws IOException {
		addStringValue(os, name);
		if (!isSimple) {
			os.writeByte(DATA_TYPE_OBJECT);
			os.writeByte(0x01);
		}
	}

	@Override
	protected void endNode(DataOutputStream os, String name, boolean isSimple,
			Object object) throws IOException {
		if (!isSimple)
			os.writeByte(0x01);
	}

	@Override
	protected void startArray(DataOutputStream os, int size) throws IOException {
		os.writeByte(DATA_TYPE_ARRAY);
		writeAMF3IntBytes(os, size << 1 | 0x01, 2);
		os.writeByte(0x01);		
	}
	
	@Override
	protected void startArrayNode(DataOutputStream os, int index, String name,
			boolean isSimple, Object object) throws IOException {
		if (!isSimple) {
			os.writeByte(DATA_TYPE_OBJECT);
			os.writeByte(0x01);
		}
	}

	@Override
	protected void endArrayNode(DataOutputStream os, String name, boolean isSimple,
			Object object) throws IOException {
		if (!isSimple) {
			os.writeByte(0x01);
		}
	}

	@Override
	protected void writeValue(DataOutputStream os, Object value) throws IOException {
		Class<?> type = value.getClass();
		if (type == String.class) {
			os.write(DATA_TYPE_STRING);
			addStringValue(os, (String)value);
		} else if (type == Integer.class) {
			Integer intValue = (Integer)value;
			if ((intValue & 0x70000000) != 0) {
				os.writeByte(DATA_TYPE_NUMBER);
				os.writeDouble(((Number) intValue).doubleValue());
			} else {
				os.writeByte(DATA_TYPE_INTEGER);
				writeAMF3Int(os, intValue);
			}
		} else if (type == Long.class) {
			Long longValue = (Long)value;
			os.writeByte(DATA_TYPE_NUMBER);
			os.writeDouble(((Number) longValue).doubleValue());
		} else if (type == Boolean.class) {
			Boolean booleanValue = (Boolean)value;
			if (booleanValue)
				os.writeByte(DATA_TYPE_BOOLEAN_TRUE);
			else
				os.writeByte(DATA_TYPE_BOOLEAN_FALSE);
		} else if (type == Timestamp.class) {
			Long longValue = ((Timestamp)value).getTime();
			os.writeByte(DATA_TYPE_NUMBER);
			os.writeDouble(((Number) longValue).doubleValue());
		} else if (type == Date.class) {
			Long longValue = ((Date)value).getTime();
			os.writeByte(DATA_TYPE_NUMBER);
			os.writeDouble(((Number) longValue).doubleValue());
		} else {
			os.writeByte(DATA_TYPE_OBJECT);
			os.writeShort(0x0b01);			
		}			
	}

	
	protected static void addStringValue(DataOutputStream os, String strValue) throws IOException {
		int len = strValue.length();
		writeAMF3Int(os, (len << 1) | 0x1);
		os.writeBytes(strValue);
	}
	
	/**
	 * write integer in amf3 format
	 * @param os
	 * @param i
	 * @throws IOException 
	 */
	protected static void writeAMF3Int(DataOutputStream os, int i) throws IOException {

		if (i < 0 || i >= 0x200000) {
			os.write(((i >> 22) & 0x7F) | 0x80);
			os.write(((i >> 15) & 0x7F) | 0x80);
			os.write(((i >> 8) & 0x7F) | 0x80);
			os.write(i & 0xFF);
		} else {
			if (i >= 0x4000)
				os.write(((i >> 14) & 0x7F) | 0x80);
			if (i >= 0x80)
				os.write(((i >> 7) & 0x7F) | 0x80);
			os.write(i & 0x7F);
		}
	}	

	/**
	 * Write integer in amf3 format using the specified number of bytes
	 * @param os
	 * @param i
	 * @param i bytes to use
	 * @throws IOException 
	 */
	protected static void writeAMF3IntBytes(DataOutputStream os, int i, int size) throws IOException {
		switch (size) {
			case 4:
				os.write(((i >> 22) & 0x7F) | 0x80);
				os.write(((i >> 15) & 0x7F) | 0x80);
				os.write(((i >> 8) & 0x7F) | 0x80);
				os.write(i & 0xFF);
				break;
			case 3:
				os.write(((i >> 14) & 0x7F) | 0x80);
				os.write(((i >> 7) & 0x7F) | 0x80);
				os.write(i & 0x7F);
				break;
			case 2:
				os.write(((i >> 7) & 0x7F) | 0x80);
				os.write(i & 0x7F);
				break;
			default:
				os.write(i & 0x7F);
			break;
		}
	}
}
