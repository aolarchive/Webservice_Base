package com.aol.webservice_base.validator.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author human
 */
 
public class MockServletRequestFacade implements HttpServletRequest {
	protected HttpServletRequest req;
	protected Map<String, String[]> addedParameters;
	
	public MockServletRequestFacade(HttpServletRequest req) {
		this.req = req;
		addedParameters = new TreeMap<String, String[]>();
	}
	
	public void addParameterValue(String name, String value) {
		addedParameters.put(name, new String[]{value});
	}
	
	/*
	 * Everything below is a facade to "req"
	 * Although the parameter management is altered to support defaulted 
	 */
	
	public String getAuthType() {
		return req.getAuthType();
	}
	public String getContextPath() {
		return req.getContextPath();
	}
	public Cookie[] getCookies() {
		return req.getCookies();
	}
	public long getDateHeader(String arg0) {
		return req.getDateHeader(arg0);
	}
	public String getHeader(String arg0) {
		return req.getHeader(arg0);	
	}
	@SuppressWarnings("unchecked")
	public Enumeration getHeaderNames() {
		return req.getHeaderNames();
	}
	@SuppressWarnings("unchecked")
	public Enumeration getHeaders(String arg0) {
		return req.getHeaders(arg0);
	}
	public int getIntHeader(String arg0) {
		return req.getIntHeader(arg0);
	}
	public String getMethod() {
		return req.getMethod();
	}
	public String getPathInfo() {
		return req.getPathInfo();
	}
	public String getPathTranslated() {
		return req.getPathTranslated();
	}
	public String getQueryString() {
		return req.getQueryString();
	}
	public String getRemoteUser() {
		return req.getRemoteUser();
	}
	public String getRequestURI() {		
		return req.getRequestURI();
	}
	public StringBuffer getRequestURL() {
		return req.getRequestURL();
	}
	public String getRequestedSessionId() {
		return req.getRequestedSessionId();
	}
	public String getServletPath() {
		return req.getServletPath();
	}
	public HttpSession getSession() {		
		return req.getSession();
	}
	public HttpSession getSession(boolean arg0) {
		return req.getSession(arg0);
	}
	public Principal getUserPrincipal() {
		return req.getUserPrincipal();
	}
	
	public boolean isRequestedSessionIdFromCookie() {		
		return req.isRequestedSessionIdFromCookie();
	}
	public boolean isRequestedSessionIdFromURL() {		
		return req.isRequestedSessionIdFromURL();
	}
	@Deprecated
	public boolean isRequestedSessionIdFromUrl() {		
		return req.isRequestedSessionIdFromUrl();
	}
	public boolean isRequestedSessionIdValid() {		
		return req.isRequestedSessionIdValid();
	}
	public boolean isUserInRole(String arg0) {		
		return req.isUserInRole(arg0);
	}
	public Object getAttribute(String arg0) {		
		return req.getAttribute(arg0);
	}
	@SuppressWarnings("unchecked")
	public Enumeration getAttributeNames() {		
		return req.getAttributeNames();
	}
	public String getCharacterEncoding() {		
		return req.getCharacterEncoding();
	}
	public int getContentLength() {		
		return req.getContentLength();
	}
	public String getContentType() {		
		return req.getContentType();
	}
	public ServletInputStream getInputStream() throws IOException {		
		return req.getInputStream();
	}
	public String getLocalAddr() {
		return req.getLocalAddr();
	}
	public String getLocalName() {		
		return req.getLocalName();
	}
	public int getLocalPort() {
		return req.getLocalPort();
	}
	public Locale getLocale() {		
		return req.getLocale();
	}
	@SuppressWarnings("unchecked")
	public Enumeration getLocales() {		
		return req.getLocales();
	}
	public String getParameter(String arg0) {
		String paramValue = null;
		String[] paramValues = addedParameters.get(arg0);
		if (paramValues != null) {
			StringBuilder sb = new StringBuilder();
			for (String param: paramValues) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(param);
			}
			paramValue = sb.toString();			
		} else {
			paramValue = req.getParameter(arg0);
		}
		return paramValue;
	}
	@SuppressWarnings("unchecked")
	public Map getParameterMap() {
		if (addedParameters.size() == 0)
			return req.getParameterMap();
		else {
			Map<String, String[]> map = new TreeMap<String, String[]>();
			map.putAll(addedParameters);
			map.putAll(req.getParameterMap());			
			return Collections.unmodifiableMap(map);
		}
		
	}
	@SuppressWarnings("unchecked")
	public Enumeration getParameterNames() {
		int defaultParametersSize = addedParameters.size(); 
		if (defaultParametersSize == 0)
			return req.getParameterNames();
		else {
			int otherParameterSize = req.getParameterMap().size();
			Vector<String> allParams = new Vector<String>(defaultParametersSize + otherParameterSize);
			allParams.addAll(addedParameters.keySet());
			allParams.addAll(req.getParameterMap().keySet());
			return allParams.elements();			
		}
	}
	public String[] getParameterValues(String arg0) {
		String[] paramValues = addedParameters.get(arg0);
		if (paramValues == null)
			paramValues = req.getParameterValues(arg0);
		return paramValues;
	}
	public String getProtocol() {		
		return req.getProtocol();
	}
	public BufferedReader getReader() throws IOException {		
		return req.getReader();
	}
	@Deprecated
	public String getRealPath(String arg0) {		
		return req.getRealPath(arg0);
	}
	public String getRemoteAddr() {		
		return req.getRemoteAddr();
	}
	public String getRemoteHost() {		
		return req.getRemoteHost();
	}
	public int getRemotePort() {		
		return req.getRemotePort();
	}
	public RequestDispatcher getRequestDispatcher(String arg0) {		
		return req.getRequestDispatcher(arg0);
	}
	public String getScheme() {		
		return req.getScheme();
	}
	public String getServerName() {		
		return req.getServerName();
	}
	public int getServerPort() {		
		return req.getServerPort();
	}
	public boolean isSecure() {		
		return req.isSecure();
	}
	public void removeAttribute(String arg0) {
		req.removeAttribute(arg0);
	}
	public void setAttribute(String arg0, Object arg1) {
		req.setAttribute(arg0, arg1);
	}
	public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
		req.setCharacterEncoding(arg0);
	}

}
