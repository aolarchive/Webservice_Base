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
public class ParameterTypeInteger extends AbstractNumericParameterType {
	protected Integer min = null;
	protected Integer max = null;
	
	/* (non-Javadoc)
	 * @see com.aol.webservice_base.validator.param_helper.AbstractParameterValidator#validate(java.lang.String)
	 */
	@Override
	protected String doValidation(HttpServletRequest req, String name, String value) throws ParameterValidatorException {
		Integer checkInt = null;
		try {
			checkInt = Integer.parseInt(value);
		} catch (NumberFormatException nfe) {
			throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "not valid as int");			
		}
		
		if ((min != null) && (checkInt < min)) {
			if (limitMin)
				value = min.toString();
			else
				throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "< " + min);
		}
		if ((max != null) && (checkInt > max)) {
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
		return Integer.class;
	}

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.validator.parameter.types.AbstractParameterType#parse(java.lang.String)
	 */
	@Override
	protected Object parse(String value) {
		return Integer.parseInt(value);
	}		
	
	public void setMin(Integer min) {
		this.min = min;
	}

	public void setMax(Integer max) {
		this.max = max;
	}
}
