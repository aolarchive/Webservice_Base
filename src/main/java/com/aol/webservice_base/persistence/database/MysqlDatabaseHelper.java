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

import com.mysql.jdbc.MysqlErrorNumbers;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * @author human
 *
 */
public class MysqlDatabaseHelper implements IDatabaseHelper {

	protected static final String MYSQL_AUTO_RECONNECT = "autoReconnect";
	protected MysqlDataSource mds;

	protected boolean initialized = false;
	protected String encoding = null;
	protected String user;
	protected String password;
	protected String host;
	protected Integer port;
	protected String dbName;	
	protected Integer loginTimeoutSec;
		
	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.database.IDatabaseHelper#getDataSource()
	 */
	public void init() {
		mds = new MysqlDataSource();		
		
		if (encoding != null)
			mds.setEncoding(encoding);
		
		mds.setUser(user);
		mds.setPassword(password);

		mds.setServerName(host);
		mds.setPort(port);
		mds.setDatabaseName(dbName);
		try {
			mds.setLoginTimeout(loginTimeoutSec);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		initialized = true;
	}	
		
	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.database.IDatabaseImplementation#isDuplicateException(java.sql.SQLException)
	 */
	public DataSource getDataSource() {
		if (!initialized)
			throw new Error("MysqlDatabaseHelper must be initialized before use");
		return mds;
	}

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.database.IDatabaseImplementation#isDuplicateException(java.sql.SQLException)
	 */	
	public String getDbInfo() {
		StringBuilder dbInfo = new StringBuilder(32);
		dbInfo.append("Host: ").append(host);
		dbInfo.append(" Port: ").append(port);
		dbInfo.append(" DB Name: ").append(dbName);
		return dbInfo.toString();
	}
		
	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.database.IDatabaseImplementation#isDuplicateException(java.sql.SQLException)
	 */
	public boolean isDuplicateException(SQLException sqlE) {
		if (sqlE.getErrorCode() == MysqlErrorNumbers.ER_DUP_ENTRY) {
			return true;
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.database.IDatabaseHelper#isCodingErrorException(java.sql.SQLException)
	 */
	public boolean isCodingErrorException(SQLException sqlE) {
		String message = sqlE.getMessage();

		// sqlE.getErrorCode() returns 0 for this - we'll examine the string
		
		if (message.startsWith("No value specified for parameter"))
			return true;

		if (message.startsWith("Column") && message.endsWith("not found."))
			return true;		
		
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.database.IDatabaseImplementation#addAutoReconnectProperty(java.util.Properties)
	 */
	public void addAutoReconnectProperty(Properties connProps) {
		connProps.put(MYSQL_AUTO_RECONNECT, "true");
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	public void setPort(Integer port) {
		this.port = port;
	}

	public void setLoginTimeoutSec(Integer loginTimeoutSec) {
		this.loginTimeoutSec = loginTimeoutSec;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
}
