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

public class JSONRenderer extends AbstractRenderer {

    static AbstractRenderer renderer = null;

    protected JSONRenderer() {
    }

    public static synchronized AbstractRenderer getInstance() throws RenderException {
        if (renderer == null) {
            renderer = new JSONRenderer();
        }
        return renderer;
    }

    @Override
    protected String getContentType(HttpServletRequest req) {
        RequestState state = (RequestState) req.getAttribute(Constants.REQUEST_STATE);
        if (state.getJsonCallback() == null) {
            return MvcConstants.DEFAULT_JSON_CONTENT_TYPE;
        } else {
            return MvcConstants.DEFAULT_JSONP_CONTENT_TYPE;

        }
    }

    @Override
    protected void renderData(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.setCharacterEncoding("utf-8");
        RequestState state = (RequestState) req.getAttribute(Constants.REQUEST_STATE);

        state.log(Level.DEBUG, this.getClass(), "Serializing JSON response");

        JSONSerializer serializer = new JSONSerializer();
        String jsonCallback = state.getJsonCallback();
        String jsonPrefix = "";
        String jsonSuffix = "";

        if (jsonCallback != null) {
            jsonPrefix = jsonCallback + "(";
            jsonSuffix = ");";
        }

        byte[] jsonContentBytes = serializer.process(state);
        resp.setContentLength(jsonPrefix.getBytes().length + jsonContentBytes.length + jsonSuffix.getBytes().length);

        resp.getOutputStream().write(jsonPrefix.getBytes());
        resp.getOutputStream().write(jsonContentBytes);
        resp.getOutputStream().write(jsonSuffix.getBytes());

        state.log(Level.DEBUG, this.getClass(), "JSON response sent successfully");
    }
}
