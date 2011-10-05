/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.view.serializer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.aol.webservice_base.view.util.ObjectUtils;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class XStreamConverter implements Converter {
	static Logger logger = Logger.getLogger(XStreamConverter.class);
	/**
	 * Writing my own so that the marshalling can skip private members of a class
	 */
	@SuppressWarnings("unchecked")
	public void marshal(Object value, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		for (Field f : ObjectUtils.getAll(value)) {
			try {
				if (f.get(value) != null) {
					String beanClassFullName = f.get(value).getClass().getName();
					String beanClassName = f.get(value).getClass().getSimpleName();
					Class type = f.getType();
					Object fieldObject = f.get(value);
					
					/* This will work if the field is a primitive
					 * However, if it is one of the Models defined by us, 
					 * it needs to use the getId() method defined on the model to get the name for the node.
					 */
					writer.startNode(f.getName());
					if (type.isPrimitive() || type.getName().startsWith("java.lang")) {
						writer.setValue(fieldObject.toString());
						
					} else if (type.isArray()) {
						Class componentType = type.getComponentType();
						
						for (int i=0; i<Array.getLength(fieldObject); i++) {
							String id = (String) componentType.getMethod("getId").invoke(null);
							if (id == null) {
								Method xmlIdMethod = componentType.getMethod("getXMLId");
								if (xmlIdMethod != null)
									id = (String)xmlIdMethod.invoke(null);
							}
							if (id != null) {
								writer.startNode(id);
							}
							if (componentType.isPrimitive() || componentType.getName().startsWith("java.lang")) {
								writer.setValue(Array.get(fieldObject, i).toString());
								
							} else {
								marshal(Array.get(fieldObject, i), writer, context);
							}
							if (id != null) {
								writer.endNode();
							}
						}

					} else {
						marshal(fieldObject, writer, context);
					}
					writer.endNode();
				}
			} catch (Exception e) {
				logger.error("XStreamConverter: Error: " + e);
			}
		}
	}

	public Object unmarshal(HierarchicalStreamReader arg0,
			UnmarshallingContext arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public boolean canConvert(Class arg0) {
		return true;
	}
}
