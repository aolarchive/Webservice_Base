/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

 */
package com.aol.webservice_base.view;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aol.webservice_base.view.renderer.AMF3Renderer;
import com.aol.webservice_base.view.renderer.AbstractRenderer;
import com.aol.webservice_base.view.renderer.JSONRenderer;
import com.aol.webservice_base.view.renderer.NativeRenderer;
import com.aol.webservice_base.view.renderer.PHPRenderer;
import com.aol.webservice_base.view.renderer.XMLRenderer;

public final class RenderView {

    private RenderView() {
    }

    @SuppressWarnings("unchecked")
    public static final void render(HttpServletRequest req, HttpServletResponse resp) throws RenderException {
        String format = null;
        int statusReturn = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

        try {
            format = req.getParameter("f").toLowerCase();

            // Determine the class we are rendering
            Class renderClass = null;
            if (format != null) {
                if (format.equals("amf3")) {
                    renderClass = AMF3Renderer.class;
                } else if (format.equals("json")) {
                    renderClass = JSONRenderer.class;
                } else if (format.equals("php")) {
                    renderClass = PHPRenderer.class;
                } else if (format.equals("xml")) {
                    renderClass = XMLRenderer.class;
                } else if (format.equals("native")) {
                    renderClass = NativeRenderer.class;
                }
            }

            if (renderClass != null) {
                statusReturn = HttpServletResponse.SC_OK;
                // Leverage reflection to get the renderer to use
                Class noParams[] = new Class[0];
                Method instanceMethod = renderClass.getMethod("getInstance", noParams);
                AbstractRenderer renderer = (AbstractRenderer) instanceMethod.invoke(renderClass);
                renderer.render(req, resp);
            } else {
                // "Unknown renderer format specified " + format;
                statusReturn = HttpServletResponse.SC_BAD_REQUEST;
            }
        } catch (Exception e) {
            statusReturn = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }

        // something else bad happened
        if (statusReturn != HttpServletResponse.SC_OK) {
            resp.setStatus(statusReturn);
            try {
                resp.getOutputStream().print(statusReturn);
            } catch (IOException e1) {
            }
        }

        try {
            resp.flushBuffer();
        } catch (IOException e) {
        }
    }
}
