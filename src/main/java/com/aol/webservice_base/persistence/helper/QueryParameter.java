/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.persistence.helper;

import com.aol.webservice_base.persistence.AbstractQuery;
import com.aol.webservice_base.util.reflection.ReflectionHelper;


/**
 * @author human
 *
 */
public class QueryParameter extends AbstractDBAccess {
	protected String getter;
	
	@Override
	public void setMember(String member) {
		if (member.equals(AbstractQuery.BUCKET_NUM))
			this.getter = AbstractQuery.BUCKET_NUM;
		else
			this.getter = ReflectionHelper.getGetter(member);
	}
	public String getGetter() {
		return getter;
	}
}
