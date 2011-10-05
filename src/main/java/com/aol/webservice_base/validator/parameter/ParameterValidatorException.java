/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.validator.parameter;


/**
 * @author human
 *
 */
public class ParameterValidatorException extends Exception {
	protected int errorCode;
	
	public ParameterValidatorException(int errorCode, String name, String value, String message) {
		super(generateErrorMessage(name, value, message));
		this.errorCode = errorCode;
	}

	public static String generateErrorMessage(String name, String value, String message) {
		StringBuilder sb = new StringBuilder(128);
		sb.append("param: '").append(name).append("' value: '").append(value).append("' error: ").append(message);
		return sb.toString();
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
