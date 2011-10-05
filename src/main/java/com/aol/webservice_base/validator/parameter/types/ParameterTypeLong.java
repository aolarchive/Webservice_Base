/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.validator.parameter.types;

import javax.servlet.http.HttpServletRequest;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.validator.parameter.ParameterValidatorException;

/**
 * @author human
 *
 */
public class ParameterTypeLong extends AbstractNumericParameterType {
	protected Long min = null;
	protected Long max = null;

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.validator.parameter.types.AbstractParameterType#doValidation(java.lang.String, java.lang.String)
	 */
	@Override
	protected String doValidation(HttpServletRequest req, String name, String value) throws ParameterValidatorException {
		Long checkLong = null;
		try {
			checkLong = Long.parseLong(value);
		} catch (NumberFormatException nfe) {
			throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "not valid as long");			
		}
		if ((min != null) && (checkLong < min)) {
			if (limitMin)
				value = min.toString();
			else
				throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "< " + min);
		}
		if ((max != null) && (checkLong > max)) {
			if (limitMax)
				value = max.toString();
			else
				throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "> " + max);
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
	
	public void setMin(Long min) {
		this.min = min;
	}

	public void setMax(Long max) {
		this.max = max;
	}
}
