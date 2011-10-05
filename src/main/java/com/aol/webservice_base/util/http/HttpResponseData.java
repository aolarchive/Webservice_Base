/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

 */

package com.aol.webservice_base.util.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;

public class HttpResponseData implements java.io.Serializable {
	private static final long serialVersionUID = -1813071481399606710L;
	private static final String ISO_8859_1 = "ISO-8859-1";
	private static final String WINDOWS_1252 = "WINDOWS-1252";
	private byte[] content;
	private String contentAsString = null;
	private Header[] headers;
	

	public HttpResponseData(Header[] headers, byte[] content) {
		this.headers = headers;
		this.content = content;
	}

	protected String getContentCharSet() {
		String charset = WINDOWS_1252; // default
		for (Header header: headers) {
			if (header.getName().equals("Content-Type")) {
				HeaderElement values[] = header.getElements();
				if (values.length == 1) {
					NameValuePair param = values[0].getParameterByName("charset");
					if (param != null) {
						charset = param.getValue().toUpperCase();
						// convert ISO-8859-1 to WINDOWS-1252 (superset)
						if (ISO_8859_1.equals(charset)) {
							charset = WINDOWS_1252;
						}
					}
				}
				break;
			}
		}
		return charset;
	}

	// we duplicate HttpMethod functionality, however, we default to WINDOWS-1252
	// instead of ISO-8859-1 (superset, many docs are improperly classified on web)
	public String getContentString() throws UnsupportedEncodingException {
		if (contentAsString != null)
			return contentAsString;

		String encoding = getContentCharSet();		
		
		contentAsString = getContentString(encoding);
		return contentAsString;
	}	

	public String getContentString(String encoding) throws UnsupportedEncodingException {
		return new String(content, encoding);
	}	

	public byte[] getContentBytes() {
		return content;
	}

	public Header[] getHeaders() {
		return headers;
	}
}
