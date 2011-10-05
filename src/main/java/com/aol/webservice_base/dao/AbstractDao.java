/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.dao;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.aol.webservice_base.dao.util.DAOUtil;

public abstract class AbstractDao {
   protected DAOUtil daoUtil;
   protected DataSource dataSource;
   protected final Logger log = Logger.getLogger(getClass());
   protected String sql;
	protected int defaultRowsDesired = 10; // not always used
	protected int rowLimit = 10; // not always used
   
   public void setDaoUtil(DAOUtil daoUtil) {
       this.daoUtil = daoUtil;
   }

   public void setDataSource(DataSource dataSource) {
       this.dataSource = dataSource;
   }

   public void setSql(String sql) {
       this.sql = sql;
   }

   public void setDefaultRowsDesired(int defaultRowsDesired) {
		this.defaultRowsDesired = defaultRowsDesired;
	}

	public void setRowLimit(int rowLimit) {
		this.rowLimit = rowLimit;
	}      
}
