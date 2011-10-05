/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

 */
package com.aol.webservice_base.view.renderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;
import com.aol.webservice_base.view.RenderException;
import com.aol.webservice_base.view.serializer.JSONSerializer;
import com.aol.webservice_base.view.util.MvcConstants;

public class NativeRenderer extends AbstractRenderer {

    static AbstractRenderer renderer = null;

    protected NativeRenderer() {
    }

    public static synchronized AbstractRenderer getInstance() throws RenderException {
        if (renderer == null) {
            renderer = new NativeRenderer();
        }
        return renderer;
    }

    @Override
    public void addHeaders(RequestState state, HttpServletResponse resp) {
        if (!state.isValid()) {
            addNoCacheHeaders(state, resp);
        } else {
            // TO-DO should be configurable
            long now = System.currentTimeMillis();
            long age = 7 * 24 * 60 * 60; // 7 days in seconds
            resp.setHeader("Cache-Control", "max age=" + age);
            resp.setDateHeader("Expires", now + (age * 1000));
        }
    }

    @Override
    protected String getContentType(HttpServletRequest req) {
        RequestState state = (RequestState) req.getAttribute(Constants.REQUEST_STATE);
        if (state.getResponseContentType() == null) {
            return "text/plain";
        }
        return state.getResponseContentType();
    }

    @Override
    protected void renderData(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        RequestState state = (RequestState) req.getAttribute(Constants.REQUEST_STATE);

        state.log(Level.DEBUG, this.getClass(), "Serializing native response");

        // Got to set this before we write the response data
        // subsequnet chnages don't get used !

        resp.setStatus(state.getHttpStatusCode());

        Object rawData = state.getData();
        if (rawData.getClass() == Object.class) {
            rawData = state.getStatusText() + "\n";
        }

        byte[] responseBytes = null;

        if (rawData instanceof byte[]) {
            responseBytes = (byte[]) rawData;
        } else if (rawData instanceof String) {
            responseBytes = ((String) rawData).getBytes("UTF-8");
        } else {
            throw new Exception("Can't handle data type for native response");
        }

        resp.setContentLength(responseBytes.length);
        resp.getOutputStream().write(responseBytes);

        state.log(Level.DEBUG, this.getClass(), "Native response sent successfully");
    }
}
