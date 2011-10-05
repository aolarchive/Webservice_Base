/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.validator.parameter.types;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.validator.parameter.ParameterValidatorException;

/**
 * @author human
 *
 */
public class ParameterTypeDate extends AbstractParameterType {
	protected Long maxSecAgo = null;
	protected Long maxSecFuture = null;
	protected boolean needDateHandling = false; // optimization

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.validator.parameter.types.AbstractParameterType#doValidation(java.lang.String, java.lang.String)
	 */
	@Override
	protected String doValidation(HttpServletRequest req, String name, String value) throws ParameterValidatorException {
		Long checkDateLong = null;
		try {
			checkDateLong = Long.parseLong(value);
		} catch (NumberFormatException nfe) {
			throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "not valid as date (long)");			
		}

		if (needDateHandling) {
			Long now = new Date().getTime();

			if ((maxSecAgo != null) && (checkDateLong < now - maxSecAgo)) {
				throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "too long ago");
			}
			if ((maxSecFuture != null) && (checkDateLong > now + maxSecFuture)) {
				throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "too far into future");
			}
		}
		
		return value;
	}

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.validator.parameter.types.AbstractParameterType#getDataClass()
	 */
	@Override
	protected Class<?> getDataClass() {
		return Long.class;
	}

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.validator.parameter.types.AbstractParameterType#parse(java.lang.String)
	 */
	@Override
	protected Object parse(String value) {
		return Long.parseLong(value);
	}	
	
	public void setMaxSecAgo(Long maxSecAgo) {
		this.maxSecAgo = maxSecAgo;
		needDateHandling = true;
	}
	public void setMaxSecFuture(Long maxSecFuture) {
		this.maxSecFuture = maxSecFuture;
		needDateHandling = true;
	}
}
