/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.persistence.helper;

import com.aol.webservice_base.util.reflection.ReflectionHelper;

/**
 * @author human
 *
 */
public class ResponsePopulator extends AbstractDBAccess {
	protected String column;	
	protected String member; // this is translated into setter
	
	protected String setter;
	
	public void setColumn(String column) {
		this.column = column;
	}	
	public String getColumn() {
		return column;
	}		

	@Override
	public void setMember(String member) {
		this.member = member;
		this.setter = ReflectionHelper.getSetter(member);
	}
	public String getSetter() {
		return setter;
	}
}
