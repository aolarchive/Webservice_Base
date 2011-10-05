/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.validator.parameter.types;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.validator.parameter.ParameterValidatorException;


/**
 * @author human
 *
 */
public class ParameterTypeString extends AbstractParameterType {	
	protected Integer minLength = null;
	protected Integer maxLength = null;	
	protected boolean caseInsensitive = false;
	protected boolean spaceInsensitive = false;
	// protected String pattern = null; - call "setPattern()"
	protected ArrayList<String> patterns = new ArrayList<String>();	
	protected ArrayList<Pattern> internalPatterns = new ArrayList<Pattern>();
	protected ArrayList<String> blockPatterns = new ArrayList<String>();	
	protected ArrayList<Pattern> internalBlockPatterns = new ArrayList<Pattern>();

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.validator.param_helper.AbstractParameterValidator#validate(java.lang.String)
	 */
	@Override
	protected String doValidation(HttpServletRequest req, String name, String value) throws ParameterValidatorException {
		if (minLength != null) {
			if (value.length() < minLength)
				throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "length < " + minLength);
		}
		if (maxLength != null) {
			if (value.length() > maxLength)
				throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "length > " + maxLength);
		}
		
        String  noSpaceValue = spaceInsensitive ? value.replaceAll("\\s", "") : null;
        
		// if we are matching patterns, only one must match
		if (internalPatterns.size() > 0) {
			boolean foundMatch = false;
			for (Pattern patMatch: internalPatterns) {
				if (patMatch.matcher(value).matches()) {
    				if (noSpaceValue == null) {
    				        foundMatch = true;
    				        break;
    				}
       				if (patMatch.matcher(noSpaceValue).matches()) {
       					foundMatch = true;
       					break;
       				}
				}
			}
			
			if (!foundMatch)
				throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "did not match pattern");
		}
		
		// if block list, any match is an error
		if (internalBlockPatterns.size() > 0) {
			for (Pattern patMatch: internalBlockPatterns) {
				if (patMatch.matcher(value).matches()) {
    				throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "blocked");
				}
				if (noSpaceValue != null) {
    				if (patMatch.matcher(noSpaceValue).matches()) {
        				throw new ParameterValidatorException(Constants.SC_INVALID_PARAMETER, name, value, "blocked");
    				}
				}
			}
			
		}
		
		// got this far, we're good
		return value;
	}

	protected void compilePatterns() {
		int flags = 0;
		if (caseInsensitive) {
			flags |= Pattern.CASE_INSENSITIVE;
		}
		
		internalPatterns = new ArrayList<Pattern>();
		for (String pattern: patterns) {
			Pattern patMatch = Pattern.compile(pattern, flags);
			internalPatterns.add(patMatch);			
		}
		
		internalBlockPatterns = new ArrayList<Pattern>();
		for (String pattern: blockPatterns) {
			Pattern patMatch = Pattern.compile(pattern, flags);
			internalBlockPatterns.add(patMatch);			
		}
	}
	
	public void setCaseInsensitive(boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
		compilePatterns();
	}	
	
	public void setSpaceInsensitive(boolean spaceInsensitive) {
		this.spaceInsensitive = spaceInsensitive;
	}	
	
	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public Integer getMaxLength() {
		return maxLength;
	}
	
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public void setPattern(String pattern) {
		patterns.add(pattern);
		compilePatterns();
	}

	public void setPatterns(ArrayList<String> patternList) {
		patterns = patternList;
		compilePatterns();
	}

	public void setBlockPattern(String pattern) {
		blockPatterns.add(pattern);
		compilePatterns();
	}

	public void setBlockPatterns(ArrayList<String> patternList) {
		blockPatterns = patternList;
		compilePatterns();
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
