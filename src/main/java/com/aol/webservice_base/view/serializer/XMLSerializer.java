/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.view.serializer;

import java.io.DataOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class XMLSerializer extends BaseSerializer {
	private static String xmlDeclaration = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";
	protected static final byte[] QUOTES = "\"".getBytes();
	protected static final byte[] SPACE = " ".getBytes();
	protected static final byte[] EQUALS = "=\"".getBytes();
	protected static final byte[] NEW_LINE = "\n".getBytes();
	protected static final byte[] START_OPEN_TAG = "<".getBytes();
	protected static final byte[] START_CLOSE_TAG = "</".getBytes();
	protected static final byte[] CLOSE_TAG = ">".getBytes();
	
	@Override
	protected boolean wantsNulls() {
		return false;
	}	

	@Override 
	protected void writeHeader(DataOutputStream os) throws IOException {
		os.write(xmlDeclaration.getBytes());
		os.write(NEW_LINE);
	}

	@Override
	protected void openStartNode(DataOutputStream os, String name, boolean isSimple, Object object) throws IOException {
		os.write(START_OPEN_TAG);
		os.write(name.getBytes());
	}
	@Override
	protected void closeStartNode(DataOutputStream os, String name, boolean isSimple) throws IOException {
		os.write(CLOSE_TAG);
	}

	@Override
	protected void addAttribute(DataOutputStream os, String attrName, String attrValue) throws IOException {
		if (attrValue != null)
			os.write(SPACE);
		   os.write(attrName.getBytes());
		   os.write(EQUALS);
		   os.write(attrValue.getBytes());
		   os.write(QUOTES);
	}

	@Override
	protected void startNode(DataOutputStream os, String name, boolean isSimple, Object object) throws IOException {
		os.write(START_OPEN_TAG);
		os.write(name.getBytes());
		os.write(CLOSE_TAG);
	}

	@Override
	protected void endNode(DataOutputStream os, String name, boolean isSimple, Object object) throws IOException {
		os.write(START_CLOSE_TAG);
		os.write(name.getBytes());
		os.write(CLOSE_TAG);
	}

	@Override
	protected void startArrayNode(DataOutputStream os, int index, String name, boolean isSimple, Object object) throws IOException {
		startNode(os, name, isSimple, null);
	}

	@Override
	protected void endArrayNode(DataOutputStream os, String name, boolean isSimple, Object object) throws IOException {
		endNode(os, name, isSimple, null);
	}

	protected static String encodeXmlValue(String originalUnprotectedString) {
		if (originalUnprotectedString == null) {
			return null;
		}

		boolean anyCharactersProtected = false;

		StringBuilder encoded = new StringBuilder(originalUnprotectedString.length());
		for (int i = 0; i < originalUnprotectedString.length(); i++) {
			char ch = originalUnprotectedString.charAt(i);

			boolean controlCharacter = ch < 32;
			boolean unicodeButNotAscii = ch > 126;
			boolean characterWithSpecialMeaningInXML = ch == '<' || ch == '&' || ch == '>';

			if (characterWithSpecialMeaningInXML || unicodeButNotAscii || controlCharacter) {
				encoded.append("&#").append((int) ch).append(';');
				anyCharactersProtected = true;
			} else {
				encoded.append(ch);
			}
		}
		if (anyCharactersProtected == false) {
			return originalUnprotectedString;
		}

		return encoded.toString();
	}

	@Override
	protected void writeValue(DataOutputStream os, Object value) throws IOException {
		// Ensure written values are safe
		os.write(encodeXmlValue(value.toString()).getBytes());
	}
}
