/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.validator;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;



public abstract class AbstractRequest
{
	public static final String AUTH_SRC_LOGIN_ID = "s";
	public static final String AUTH_LANGUAGE = "language";
	public static final String FMT_JSON = "json";	
	
	protected TreeMap<String, Object> queryParams;
	protected String contentType;

	protected AbstractRequest(TreeMap<String, Object> queryParams)
	{
		this.queryParams = queryParams;
		contentType = "text/json";
	}

	/**
	 * Returns HTTP ContentType
	 *
	 * @return HTTP content type
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * Returns the requested response format.
	 *
	 * @return response format
	 */
	public String getFormat()
	{
		return (String) queryParams.get(FMT_JSON);
	}

	/**
	 * Returns the source login Id provided in the request.
	 *
	 * @return source login id
	 */
	public String getSrcLoginId()
	{
		return (String) queryParams.get(AUTH_SRC_LOGIN_ID);
	}

	/**
	 * Returns the language locale specified in the request.
	 *
	 * @return language locale
	 */
	public String getLanguage()
	{
		return (String) queryParams.get(AUTH_LANGUAGE);
	}

	/**
	 * Create URL query string for this request.
	 *
	 * @return URL query string
	 * @throws AuthException
	 */
	public String createQueryString() {
		StringBuffer sb = new StringBuffer();

		Set<Map.Entry<String, Object>> set = queryParams.entrySet();
		Enumeration<Map.Entry<String, Object>> en = Collections.enumeration(set);
		try
		{
			while (en.hasMoreElements())
			{
				Map.Entry<String, Object> entry = en.nextElement();
				String key = entry.getKey();
				Object val = entry.getValue();
				if (val instanceof String)
				{
					String s = null;
					s = (String) val;
					s = URLEncoder.encode(s, "US-ASCII");
					sb.append("&").append(key).append("=").append(s);
				}
				else if (val instanceof String[])
					sb.append("&").append(getNameValueString(key, (String[]) val));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return sb.substring(1);
	}

	/**
	 * Returns the query string for the specific name-value pair where the value
	 * is an array of values
	 *
	 * @param name
	 * @param values -
	 *            an array of values
	 * @return the query string for the input
	 * @throws UnsupportedEncodingException
	 */
	private String getNameValueString(String name, String[] values) throws UnsupportedEncodingException
				{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < values.length; i++)
		{
			if (i > 0)
				sb.append("&");

			String s = null;
			try {
				s = URLEncoder.encode(values[i],"US-ASCII");
			} catch (UnsupportedEncodingException ex) {
				ex.getMessage();
			}
			sb.append(name).append("=").append(s);
		}

		return sb.toString();
				}

}
