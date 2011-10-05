/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.view.serializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.aol.webservice_base.state.RequestState;
import com.aol.webservice_base.util.types.TypesHelper;
import com.aol.webservice_base.view.util.ObjectUtils;

public abstract class BaseSerializer {

	static Logger logger = Logger.getLogger(BaseSerializer.class);
	static final String RESPONSE_ELEMENT = "response";

	protected abstract boolean wantsNulls();

	protected void writeHeader(DataOutputStream os) throws IOException {
	}

	protected void writeFooter(DataOutputStream os) throws IOException {
	}

	/**
	 * Open start node - defaults to startNode functionality
	 * Can be overwritten (in XML for example) to add attribute functionality
	 *
	 * @param sb the sb
	 * @param name the name
	 * @param isSimple the is simple
	 */
	protected void openStartNode(DataOutputStream os, String name, boolean isSimple, Object object) throws IOException {
		startNode(os, name, isSimple, object);
	}
	protected void closeStartNode(DataOutputStream os, String name, boolean isSimple) throws IOException {}
	protected void addAttribute(DataOutputStream os, String attrName, String attrValue) throws IOException {}

	protected abstract void startNode(DataOutputStream os, String name, boolean isSimple, Object object) throws IOException;
	protected abstract void endNode(DataOutputStream os, String name, boolean isSimple, Object object) throws IOException;

	protected abstract void startArrayNode(DataOutputStream os, int index, String name, boolean isSimple, Object object) throws IOException;
	protected abstract void endArrayNode(DataOutputStream os, String name, boolean isSimple, Object object) throws IOException;

	protected void separateNodes(DataOutputStream os) throws IOException {
	}

	protected void startArray(DataOutputStream os, int size) throws IOException {
	}

	protected void endArray(DataOutputStream os) throws IOException {
	}

	protected abstract void writeValue(DataOutputStream os, Object value) throws IOException;

	public byte[] process(Object objMarshall) throws SerializerException, IOException {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(byteOutput);
		
		writeHeader(dataOutput);
		openStartNode(dataOutput, RESPONSE_ELEMENT, false, objMarshall);
		if (objMarshall instanceof RequestState) {
			RequestState requestState = (RequestState)objMarshall;
			if (requestState.getXmlNamespace() != null)
				addAttribute(dataOutput, "xmlns", requestState.getXmlNamespace());
		}
		closeStartNode(dataOutput, RESPONSE_ELEMENT, false);
		serialize(dataOutput, objMarshall);
		endNode(dataOutput, RESPONSE_ELEMENT, false, objMarshall);
		writeFooter(dataOutput);

		return byteOutput.toByteArray();
	}

	protected String singularize(String name) throws IOException {
		if (name.endsWith("s")) {
			if (name.endsWith("ies")) {
				name = name.substring(0, name.length() - 3) + "y";
			} else if (name.endsWith("ses") || name.endsWith("shes") || name.endsWith("ches") || name.endsWith("xes")) {
				name = name.substring(0, name.length() - 2);
			} else { // ends in "es" or "s"
				name = name.substring(0, name.length() - 1);
			}
		}
		return name;
	}

	@SuppressWarnings("unchecked")
	protected void serialize(DataOutputStream os, Object objMarshall) throws SerializerException, IOException {
		if ((objMarshall == null) && !wantsNulls())
			return;
		
		Map<String, Object> items = null;

		// Build map of names/values
		// If DataAccessor, then let it tell us the fields to emit
		if (objMarshall instanceof DataAccessor) {
			items = ((DataAccessor) objMarshall).getSerializerData();
		} else {
			items = new TreeMap<String, Object>();
			for (Field f : ObjectUtils.getAll(objMarshall)) {
				try {
					items.put(f.getName(), f.get(objMarshall));
				} catch (Exception e) {
					logger.error("Serializer: Error: " + e);
					throw new SerializerException(e.getMessage());
				}
			}
		}

		// Emit the info
		boolean hasBeforeSibling = false;
		try {
			for (String name : items.keySet()) {
				Object obj = items.get(name);

				if (obj == null) {
					if (wantsNulls()) {
						startNode(os, name, true, obj);
						writeValue(os, obj);
						endNode(os, name, true, obj);
					}
					continue;
				}

				// don't allow self-referential fields
				if (obj == objMarshall) {
					continue;
				}

				// If we are between items, put in a separator
				if (hasBeforeSibling) {
					separateNodes(os);
				}
				hasBeforeSibling = true;

				Class type = obj.getClass();

				// Convert lists to arrays
				if (obj instanceof List) {
					obj = ((List) obj).toArray();
					type = obj.getClass();
				}


				/* This will work if the field is a primitive
				 * However, if it is one of the Models defined by us,
				 * it needs to use the getId() method defined on the model to get the name for the node.
				 */
				if (TypesHelper.isSimpleType(type)) {
					// write out name and value (dates written out as long)
					startNode(os, name, true, obj);
					if (type == Date.class) {
						writeValue(os, ((Date) obj).getTime());
					} else {
						writeValue(os, obj);
					}
					endNode(os, name, true, obj);
				} else if (type.isArray()) {
					startNode(os, name, true, obj);
					startArray(os, Array.getLength(obj));
					Class componentType = type.getComponentType();

					for (int i = 0; i < Array.getLength(obj); i++) {
						if (i > 0) {
							separateNodes(os);
						}
						String id = singularize(name);
						try {
							if (componentType.getMethod("getId") != null) {
								id = (String) componentType.getMethod("getId").invoke(null);
							}
						} catch (NoSuchMethodException e) {
						}

						Object arrayItem = Array.get(obj, i);
						Class itemClass = (arrayItem != null) ? arrayItem.getClass() : componentType;
						startArrayNode(os, i, id, TypesHelper.isSimpleType(itemClass), arrayItem);

						if (TypesHelper.isSimpleType(itemClass)) {
							if (componentType == Date.class) {
								writeValue(os, ((Date)arrayItem).getTime());
							} else {
								writeValue(os, arrayItem);
							}
						} else {
							serialize(os, arrayItem);
						}

						endArrayNode(os, id, TypesHelper.isSimpleType(itemClass), arrayItem);
					}

					endArray(os);
					endNode(os, name, true, obj);
				} else {
					startNode(os, name, false, obj);
					serialize(os, obj);
					endNode(os, name, false, obj);
				}
			}
		} catch (Exception e) {
			logger.error("Serializer: Error: " + e);
			throw new SerializerException(e.getMessage());
		}
	}
}
