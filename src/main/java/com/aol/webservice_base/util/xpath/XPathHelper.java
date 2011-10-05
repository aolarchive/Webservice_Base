/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.util.xpath;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author human
 *
 */
public class XPathHelper {
	public static Node getXpathExpressionNode(Object xprContext, String xpExpression) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();

		XPathExpression xpe = xpath.compile(xpExpression);
		Node node = (Node)xpe.evaluate(xprContext, XPathConstants.NODE);
		return node;
	}		

	public static NodeList getXpathExpressionNodeList(Object xprContext, String xpExpression) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();

		XPathExpression xpe = xpath.compile(xpExpression);
		NodeList nodes = (NodeList)xpe.evaluate(xprContext, XPathConstants.NODESET);
		return nodes;
	}	

	public static String getXpathExpressionValue(Object xprContext, String xpExpression) throws XPathExpressionException {
		XPath xpath = XPathFactory.newInstance().newXPath();

		XPathExpression xpe = xpath.compile(xpExpression);
		Node valueNode = (Node)xpe.evaluate(xprContext, XPathConstants.NODE);
		String value = null;
		if (valueNode != null)
			value = valueNode.getNodeValue();
		if (value != null) {
			// if the node is a text node - then we trim and (potentially) look for CDATA
			if (valueNode.getNodeType() == Node.TEXT_NODE) {
				value = value.trim();

				// look for CDATA if we got nothing (stupid whitespace)
				if (value.length() == 0) {
					Node siblingForCDATA = valueNode.getNextSibling();
					if (siblingForCDATA.getNodeType() == Node.CDATA_SECTION_NODE) {
						value = siblingForCDATA.getNodeValue();
					}
				}
			}
		}

		return value;
	}
	
	public static void replaceXPathNode(Node replaceThis, Node replaceWith) throws XPathExpressionException {
		Node parentNode = replaceThis.getParentNode();
		parentNode.insertBefore(replaceWith, replaceThis);
		parentNode.removeChild(replaceThis);
	}
}
