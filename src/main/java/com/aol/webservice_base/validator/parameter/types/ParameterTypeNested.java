/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.validator.parameter.types;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.validator.parameter.ParameterValidatorException;

/**
 * The Class ParameterTypeNested.
 * 
 * This class supports having nested parameters
 * NOTE: This will not respect/push any altered values.  It only respects validity.
 * 
 * @author human
 */
public class ParameterTypeNested extends AbstractParameterType {
	protected List<AbstractParameterType> validators;
	
	@Override
	protected String doValidation(HttpServletRequest req, String name, String value) throws ParameterValidatorException {
		if (isNested) {
			throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "Nested Parameter can not be nested");
		}
		
		String params[] = value.split("&");
		for (String param: params) {
			for (AbstractParameterType validator: validators) {
				String[] nvPair = param.split("=", 2);
				validator.validate(req, nvPair[0], (nvPair.length == 2) ? nvPair[1] : null);
			}
		}		
		
		return value;
	}

	public void setValidators(List<AbstractParameterType> validators) {
		this.validators = validators;
		// flag these parameters are "nested"
		for (AbstractParameterType validator : validators) {
			validator.setNested(true);
		}
	}

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.validator.parameter.types.AbstractParameterType#getDataClass()
	 */
	@Override
	protected Class<?> getDataClass() {
		return String.class;
	}

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.validator.parameter.types.AbstractParameterType#parse(java.lang.String)
	 */
	@Override
	protected Object parse(String value) {
		return value;
	}

}
