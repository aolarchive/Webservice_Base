/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.validator;

import javax.servlet.http.HttpServletRequest;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;
import com.aol.webservice_base.stats.BaseStatistics;
import com.aol.webservice_base.stats.StatisticsManager;

/*
 * These are to be configured as non-singletons
 * so that the Configuration can control their state
 */

public abstract class AbstractValidatorPlugin {
	/** stats */
	private BaseStatistics stats;	

	public AbstractValidatorPlugin() {
  		// set up stats
  		stats = StatisticsManager.getStatsHandler(this.getClass(), null, "Success Time", "Failure Time");
	}
	
	public void validateRequest(HttpServletRequest req) {
		long startTime = System.currentTimeMillis();
		
		doValidateRequest(req);

		RequestState state = (RequestState)req.getAttribute(Constants.REQUEST_STATE); 
		long duration = System.currentTimeMillis() - startTime;
		if (state.getStatusCode() == Constants.SC_OK) {
			stats.success(duration);			
		} else {
			stats.failure(duration);
		}			
	}
	
	public abstract void doValidateRequest(HttpServletRequest req);
}
