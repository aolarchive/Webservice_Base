/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.validator.parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;
import com.aol.webservice_base.validator.AbstractValidatorPlugin;
import com.aol.webservice_base.validator.parameter.types.AbstractParameterType;

// TODO: Auto-generated Javadoc
/**
 * The Class ParameterValidator.
 * 
 * This goes through and ensures that parameters match those as configured
 * Note all expected parameters must be configured, as unrecognized parameters
 * cause an INVALID_PARAMETER status
 */
public class ParameterValidator extends AbstractValidatorPlugin {	
	
	/** The validators. */
	protected ArrayList<AbstractParameterType> validators = new ArrayList<AbstractParameterType>();
	
	/** The Constant EMPTY_PARAMS. */
	protected static final String[] EMPTY_PARAMS = new String[]{null};
	
	protected static Set<String> systemIgnoreUnexpectedParams = null;
	
	/* (non-Javadoc)
	 * @see com.aol.webservice_base.validator.AbstractValidatorPlugin#validateRequest(javax.servlet.http.HttpServletRequest)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void doValidateRequest(HttpServletRequest req) {
		RequestState state = (RequestState)req.getAttribute(Constants.REQUEST_STATE);
		
		// go through all the validators and match parameters
		List<String> unusedParamNames = Collections.list(req.getParameterNames());
		for (AbstractParameterType validator: validators) {
			boolean validatorUsed = false;
						
			String[] paramValues = req.getParameterValues(validator.getName());
			if (paramValues == null) 
				paramValues = new String[] {null}; 

			for (String paramValue: paramValues) {
				try {
					if (validator.validate(req, validator.getName(), paramValue)) {
						unusedParamNames.remove(validator.getName());
						validatorUsed = true;
					}
				} catch (ParameterValidatorException ve) {
					validatorUsed = true;
					// place the error in our state
					int errorCode = ve.getErrorCode();
					if ((validator.getInvalidErrorCodeOverride() != null) && 
						 (errorCode == Constants.SC_INVALID_PARAMETER)) {
						errorCode = validator.getInvalidErrorCodeOverride();
					}
					state.addParameterError(errorCode, ve.getMessage());
				}
			} 
			
			// validator required, but not used - error
			if (validator.getRequired() && !validatorUsed) {
				// place the error in our state					
				state.addParameterError(Constants.SC_MISSING_PARAMETER, ParameterValidatorException.generateErrorMessage(validator.getName(), null, "DNE"));					
			}
		}

		// if there are unused parameters, throw an error
		for (String unusedParam: unusedParamNames) {
			if ((systemIgnoreUnexpectedParams == null) || !systemIgnoreUnexpectedParams.contains(unusedParam)) {
				state.addParameterError(Constants.SC_INVALID_PARAMETER, ParameterValidatorException.generateErrorMessage(unusedParam, req.getParameter(unusedParam), "Unexpected parameter"));
			}
		}
		
	}

	/**
	 * Sets the validators.
	 * 
	 * @param validators the new validators
	 */
	public void setValidators(ArrayList<AbstractParameterType> validators) {
		this.validators = validators;
	}

	public static void setSystemIgnoreUnexpectedParams(List<String> systemIgnoreUnexpectedParams) {
		ParameterValidator.systemIgnoreUnexpectedParams = new HashSet<String>();
		for (String ignorableParam: systemIgnoreUnexpectedParams) {
			ParameterValidator.systemIgnoreUnexpectedParams.add(ignorableParam);
		}
	}
}
