/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

 */
package com.aol.webservice_base.state;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/*
 * Note that the output renders all public variables to the output (if defined)
 * That is important to note when you define your data as it bubbles all through
 * when the data is processed for output
 * 
 * Logging processed with this class will retain an identifier so that 
 * request flow can be more easily followed.
 */
public class RequestState {

    protected static final Logger log = Logger.getLogger(RequestState.class);
    protected HttpServletRequest httpServletRequest;
    protected Object requestObject = null;
    public String requestId = null;
    public int statusCode = HttpServletResponse.SC_OK;
    public String statusText = Constants.ST_OK;
    public Integer statusDetailCode = null;
    public String statusDetailText = null;
    public Object data = new Object();  // "data" field always returned even if empty
    protected String jsonCallback = null;
    protected String xmlNamespace = null;
    private String label = null;
    private int httpStatusCode = -1;
    private String responseContentType = null; // Only used by type NATIVE ?

    public RequestState(HttpServletRequest request) {
        this.httpServletRequest = request;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public void setRequestObject(Object requestObject) {
        this.requestObject = requestObject;
    }

    public boolean logEnabled(Level level) {
        return log.isEnabledFor(level);
    }

    /**
     * Log.
     *
     * @param level the level
     * @param clazz the clazz
     * @param items the items to be logged
     *
     * @return the log message as a convenience to the caller
     */
    public String log(Level level, Class<?> clazz, Object... items) {
        StringBuilder logMessageBuilder = new StringBuilder(64);
        logMessageBuilder.append("Id: ");
        logMessageBuilder.append(this.hashCode());
        logMessageBuilder.append(" Class: ");
        logMessageBuilder.append(clazz.getName());
        logMessageBuilder.append(":");
        logMessageBuilder.append(getLineNumber());

        if (this.getLabel() != null) {
            logMessageBuilder.append(" Label: ");
            logMessageBuilder.append(this.getLabel());
        }

        logMessageBuilder.append(" Msg: ");
        for (Object item : items) {
            logMessageBuilder.append(item);
        }

        String logMessage = logMessageBuilder.toString();
        log.log(level, logMessage);
        return logMessage;
    }

    /** Get the current line number.
     * @return int - Current line number.
     */
    private static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[3].getLineNumber();
    }

    public Object getRequestObject() {
        return requestObject;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String s) {
        this.label = s;
    }

    public boolean isValid() {
        return (statusCode == HttpServletResponse.SC_OK);
    }

    public void setError(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Integer getStatusDetailCode() {
        return statusDetailCode;
    }

    public void setStatusDetailCode(Integer statusDetailCode) {
        this.statusDetailCode = statusDetailCode;
    }

    public String getStatusDetailText() {
        return statusDetailText;
    }

    public void setStatusDetailText(String statusDetailText) {
        this.statusDetailText = statusDetailText;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getHttpStatusCode() {
        return httpStatusCode == -1 ? getStatusCode() : httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public void setResponseContentType(String responseContentType) {
        this.responseContentType = responseContentType;
    }


    protected void putErrorInList(Integer errorCode, String errorText) {
        // only add / set error if we weren't in error before or status matches (pile on error)
        if (statusCode == HttpServletResponse.SC_OK || statusCode == errorCode) {
            // either change the statusText or append to it
            if (statusCode == HttpServletResponse.SC_OK) {
                statusText = errorText;
            } else {
                statusText += ", " + errorText;
            }

            // store away our status code
            statusCode = errorCode;
        }
    }

    public void addParameterError(Integer errorCode, String errorText) {
        putErrorInList(errorCode, errorText);
    }

    public void addProcessingError(Integer errorCode, String errorText) {
        statusCode = HttpServletResponse.SC_NOT_FOUND;
        putErrorInList(errorCode, errorText);
    }

    public String getJsonCallback() {
        return jsonCallback;
    }

    public void setJsonCallback(String jsonCallback) {
        this.jsonCallback = jsonCallback;
    }

    public String getXmlNamespace() {
        return xmlNamespace;
    }

    public void setXmlNamespace(String xmlNamespace) {
        this.xmlNamespace = xmlNamespace;
    }
}
