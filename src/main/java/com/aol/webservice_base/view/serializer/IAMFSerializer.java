/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.view.serializer;

import java.io.IOException;

/**
 * Interface for serializing data into Adobe's AMF0 and AMF3 message formats
 * 
 * http://download.macromedia.com/pub/labs/amf/amf3_spec_121207.pdf
 * http://download.macromedia.com/pub/labs/amf/amf0_spec_121207.pdf
 * @author lsrinil
 *
 */
public interface IAMFSerializer {

	public abstract void addString(String name, String strValue) throws IOException;

	public abstract void addInt(String name, int intValue) throws IOException;

	public abstract void addLong(String name, long longValue) throws IOException;

	public abstract void addBoolean(String name, boolean booleanValue) throws IOException;

	public abstract void beginArray(String name, int arrayLen) throws IOException;
	
	public abstract void beginArrayObject() throws IOException;
	
	public abstract void endArrayObject() throws IOException;

	public abstract void endArray(String name);

	public abstract void beginObject(String name) throws IOException;

	public abstract void endObject(String name) throws IOException;

	public abstract void serialize() throws IOException;

	public abstract String getSerializedStr();

	public abstract byte[] toByteArray() throws IOException;

}