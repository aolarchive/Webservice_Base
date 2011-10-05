/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.persistence.helper;

import com.aol.webservice_base.persistence.PersistenceException;

/**
 * @author human
 *
 */
public class DynamicWhereClauseFactory {
	protected Integer alterWhereClause = null;
	protected Integer expectedWhereCount = null;

	public void setAlterWhereClause(Integer alterWhereClause) {
		this.alterWhereClause = alterWhereClause;
	}

	public void setExpectedWhereCount(Integer expectedWhereCount) {
		this.expectedWhereCount = expectedWhereCount;
	}
	
	public DynamicWhereClause getInstance() throws PersistenceException {
		return new DynamicWhereClause(alterWhereClause, expectedWhereCount);
	}
}
