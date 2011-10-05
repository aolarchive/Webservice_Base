/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.validator.parameter.types;


/**
 * @author human
 *
 */
public abstract class AbstractNumericParameterType extends AbstractParameterType {
	protected boolean limitMin = false;
	protected boolean limitMax = false;

	public void setLimitMin(boolean limitMin) {
		this.limitMin = limitMin;
	}

	public void setLimitMax(boolean limitMax) {
		this.limitMax = limitMax;
	}	
}
