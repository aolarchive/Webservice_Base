/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

 */
package com.aol.webservice_base.view.renderer;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractRenderer {

    /*
     * IMPORTANT!!!!
     * This member & method must be subclass-provided, due to reflection usage
     * static is not allowed to be @Override-n

    protected static AbstractRenderer renderer = null;
    protected static synchronized AbstractRenderer getInstance();
     */


    public void addHeaders(RequestState state, HttpServletResponse resp) {
        addNoCacheHeaders(state, resp);
    }

    public void addNoCacheHeaders(RequestState state, HttpServletResponse resp) {
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        resp.setHeader("Expires", "0");
    }

    protected AbstractRenderer() {
    }

    protected abstract String getContentType(HttpServletRequest req);

    protected abstract void renderData(HttpServletRequest req, HttpServletResponse resp) throws Exception;

    public final void render(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // set the content type
        resp.setHeader("Content-Type", getContentType(req));
        RequestState state = (RequestState) req.getAttribute(Constants.REQUEST_STATE);

        addHeaders(state, resp);

        // render the data
        renderData(req, resp);

        // set the return code of the request
        resp.setStatus(state.getHttpStatusCode());
    }
}
