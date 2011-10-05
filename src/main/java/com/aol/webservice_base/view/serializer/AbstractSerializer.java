/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.view.serializer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;

public abstract class AbstractSerializer {	
	// this abstract method is used to actually render the content
	abstract protected void doRenderMergedOutputModel(HttpServletRequest request,
																	HttpServletResponse response) throws Exception;
	
	// this will determine if we need no-caching headers and then render content
	public void renderMergedOutputModel(HttpServletRequest request,
														HttpServletResponse response) throws Exception {
		// determine if we need no caching headers
		boolean reqNoCache = false;
		if (request.getParameter("rnd") != null)
			reqNoCache = true;
		else {
			RequestState state = (RequestState) request.getAttribute(Constants.REQUEST_STATE);
			if (!state.isValid()) {
				reqNoCache = true;
			}
		}

		// set the content type for the response
		response.setContentType(getContentType());
		
		// include no caching headers
		if (reqNoCache) {
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
			response.setHeader("Expires", "0");
		}
		
		// let the view render
		doRenderMergedOutputModel(request, response);
	}
	
	public abstract String getContentType();
}
