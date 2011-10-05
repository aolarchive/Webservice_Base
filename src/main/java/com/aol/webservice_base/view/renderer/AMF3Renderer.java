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
import com.aol.webservice_base.view.serializer.AMF3Serializer;
import com.aol.webservice_base.view.util.MvcConstants;

public class AMF3Renderer extends AbstractRenderer {
	static AbstractRenderer renderer = null;

	protected AMF3Renderer() {}

	public static synchronized AbstractRenderer getInstance() throws RenderException {
		if (renderer == null)
			renderer = new AMF3Renderer();
		return renderer;
	}

	@Override
	protected String getContentType(HttpServletRequest req) {
		return MvcConstants.DEFAULT_AMF3_CONTENT_TYPE;
	}

	@Override
	protected void renderData(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		RequestState state = (RequestState)req.getAttribute(Constants.REQUEST_STATE);

		resp.setContentType(getContentType(req));

		state.log(Level.DEBUG, this.getClass(), "Serializing AMF3 resp..");
		AMF3Serializer amf3 = new AMF3Serializer();
		byte[] amf3Resp = amf3.process(state);

		resp.setContentLength(amf3Resp.length);
		resp.getOutputStream().write(amf3Resp);

		state.log(Level.DEBUG, this.getClass(), "AMF3 Response sent successfully..");
	}
}
