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
import com.aol.webservice_base.view.serializer.PHPSerializer;
import com.aol.webservice_base.view.util.MvcConstants;

public class PHPRenderer extends AbstractRenderer {
	static AbstractRenderer renderer = null;
	
	protected PHPRenderer() {}

	public static synchronized AbstractRenderer getInstance() throws RenderException {
		if (renderer == null)
			renderer = new PHPRenderer();
		return renderer;
	}

	@Override
	protected String getContentType(HttpServletRequest req) {
		return MvcConstants.DEFAULT_PHP_CONTENT_TYPE;
	}			

	@Override
	protected void renderData(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		resp.setCharacterEncoding("utf-8");
		RequestState state = (RequestState)req.getAttribute(Constants.REQUEST_STATE);

		state.log(Level.DEBUG, this.getClass(), "Serializing PHP response");
		
		PHPSerializer serializer = new PHPSerializer();
		byte[] phpBytes = serializer.process(state);

		resp.setContentLength(phpBytes.length);
		resp.getOutputStream().write(phpBytes);

		state.log(Level.DEBUG, this.getClass(), "PHP response sent successfully");
	}

}
