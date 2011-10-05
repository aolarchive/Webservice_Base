/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

*/

package com.aol.webservice_base.util.dom;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author tjj
 *
 */
public class DomHelper {

	/**
	 * Xml to string.
	 * 
	 * @param node
	 *           the node
	 * 
	 * @return the string
	 */
	public static String toXML(Node node) {
		try {
			Source source = new DOMSource(node);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		}
		catch (Exception e) {}
		return null;
	}


	/**
	/* Parse an XML string into a DOM document
	*/
	public	static	Document	stringToDoc(String s, boolean nameSpaceAware)
		throws ParserConfigurationException, SAXException, IOException
	{

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

		domFactory.setNamespaceAware(nameSpaceAware);
		DocumentBuilder docBuilder = domFactory.newDocumentBuilder();

                InputSource is = new InputSource();

	        is.setCharacterStream(new StringReader(s));
		Document doc = docBuilder.parse(is);

                return doc;
	}
}
