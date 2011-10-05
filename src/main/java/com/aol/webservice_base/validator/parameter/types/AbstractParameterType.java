/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.validator.parameter.types;

import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;
import com.aol.webservice_base.util.reflection.ReflectionHelper;
import com.aol.webservice_base.validator.parameter.ParameterValidatorException;


/**
 * @author human
 *
 */
public abstract class AbstractParameterType {
	protected String name;
	protected String defaultValue = null;
	protected Boolean required = false;
	protected boolean isNested = false; // track if this is nested (prevent alteration)
	protected String member = null;
	
	// When a parameter is invalid - use this code instead
	protected Integer invalidErrorCodeOverride = null; 

	// if this parameters is required - it is unless something here matches
	protected ArrayList<String> unless = new ArrayList<String>();	
	// requires means this requires some other parameter(s) to exist
	protected ArrayList<String> requires = new ArrayList<String>();
	// requires means this precludes some other parameter(s) from existing
	protected ArrayList<String> precludes = new ArrayList<String>();
	
	/*
	 * subclass required validation - after requireExist is checked
	 * This method returns the value (or adjusted value)
	 */
	protected abstract String doValidation(HttpServletRequest req, String name, String value) throws ParameterValidatorException;

	protected abstract Class<?> getDataClass();
	protected abstract Object parse(String value);
	
	/*
	 * validates the parameter
	 * @Return boolean true/false if this validator did validation
	 */
	/**
	 * Validate.
	 * 
	 * @param req the req
	 * @param name the name
	 * 
	 * @return true, if successful
	 * 
	 * @throws ParameterValidatorException the parameter validator exception
	 */
	public boolean validate(HttpServletRequest req, String name) throws ParameterValidatorException {
		String value = req.getParameter(name);
		return validate(req, name, value);
	}
	
	/**
	 * Validate.
	 * 
	 * @param req the req
	 * @param name the name
	 * @param value the value
	 * 
	 * @return true, if successful
	 * 
	 * @throws ParameterValidatorException the parameter validator exception
	 */
	public boolean validate(HttpServletRequest req, String name, String value) throws ParameterValidatorException {
		// this validator will pass on parameters it doesn't want
		if (!name.equals(this.name))
			return false;

		// inject default
		if ((defaultValue != null) && (value == null)) {			
			if (isNested) {
				throw new ParameterValidatorException(Constants.SC_INTERNAL_SERVER_ERROR, name, null, "Cannot default nested parameter");
			}
			value = defaultValue;
		}
		
		// check if required item does not exist
		if (required && (value == null)) {
			// check unless
			for (String check: unless) {
				if (req.getParameter(check) != null) {
					return true;
				}
			}

			throw new ParameterValidatorException(Constants.SC_MISSING_PARAMETER, name, null, "DNE");
		}
					
		
		// process entries based on if they are not required
		if (!required && (value == null))
			return true;
		
		// check requires for other headers
		for (String require: requires) {
			if (isNested) {
				throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "Cannot require parameter in nested parameter");
			}
			if (req.getParameter(require) == null) {
				throw new ParameterValidatorException(Constants.SC_MISSING_PARAMETER, name, value, "Can not find required parameter: '" + require + "'");
			}
		}
		
		// check precludes for other headers
		for (String preclude: precludes) {
			if (isNested) {
				throw new ParameterValidatorException(Constants.SC_INTERNAL_SERVER_ERROR, name, value, "Cannot preclude parameter in nested parameter");
			}
			if (req.getParameter(preclude) != null) {
				throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "Precludes existance of parameter: '" + preclude + "'");
			}
		}

		// allow the real validation to take place
		String adjustedValue = doValidation(req, name, value);
		if (adjustedValue != value) {			
			if (isNested) {
				throw new ParameterValidatorException(Constants.SC_INTERNAL_SERVER_ERROR, name, value, "Cannot change nested parameter");
			}
			value = adjustedValue;
		}
				
		// place the value in the request
		if (member != null) {
			// ensure we are not nested
			if (isNested) {
				throw new ParameterValidatorException(Constants.SC_INTERNAL_SERVER_ERROR, name, value, "Nested parameter: " + name + " may not be set as member: " + member);
			}
			
			RequestState state = (RequestState)req.getAttribute(Constants.REQUEST_STATE);			
			Object requestObject = state.getRequestObject();
			
			// ensure there is an object on which to set this parameter
			if (requestObject == null) {
				throw new ParameterValidatorException(Constants.SC_INTERNAL_SERVER_ERROR, name, value, "No request object for parameter: " + name + " may not be set as member: " + member);
			}
			
			// invoke the setter
			String setter = ReflectionHelper.getSetter(member);
			try {				
				Method set = requestObject.getClass().getMethod(setter, getDataClass());
				set.invoke(requestObject, parse(value));
			} catch (Exception e) {
				String message = state.log(Level.WARN, this.getClass(), "Problem invoking setter ", setter, " on class ", requestObject.getClass().getName(), " with exception: ", e.getMessage());
				throw new ParameterValidatorException(Constants.SC_INTERNAL_SERVER_ERROR, name, value, message);
			}		
		}				
		
		return true;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public void setRequired(Boolean required) {
		this.required = required;
	}
	public Boolean getRequired() {
		return required;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setUnless(String unless) {
		this.unless.add(unless);
	}	
	
	public void setUnless(ArrayList<String> unless) {
		this.unless = unless;
	}	
	
	public void setRequires(String requires) {
		this.requires.add(requires);
	}	
	
	public void setRequires(ArrayList<String> requires) {
		this.requires = requires;
	}

	public void setPrecludes(String precludes) {
		this.precludes.add(precludes);
	}
	
	public void setPrecludes(ArrayList<String> precludes) {
		this.precludes = precludes;
	}

	public void setNested(boolean isNested) {
		this.isNested = isNested;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public Integer getInvalidErrorCodeOverride() {
		return invalidErrorCodeOverride;
	}	
	public void setInvalidErrorCodeOverride(Integer invalidErrorCodeOverride) {
		this.invalidErrorCodeOverride = invalidErrorCodeOverride;
	}

}
