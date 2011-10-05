/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.persistence.database;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * @author human
 *
 */
public interface IDatabaseHelper{
	public void init();
	public String getDbInfo();
	public DataSource getDataSource();
	public boolean isDuplicateException(SQLException sqlE);
	public boolean isCodingErrorException(SQLException sqlE);
	public void addAutoReconnectProperty(Properties connProps);
}
