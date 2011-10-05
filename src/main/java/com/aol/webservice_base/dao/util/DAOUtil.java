/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.dao.util;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

public interface DAOUtil
{
	public static short SP_EXECUTE_OK_STATUS = 0;
	
	short getStoredProcedureStatus(Map out);
	
    Map<String, Object> spExecute(DataSource dataSource, String sql, List sqlParameters,
            final Map<String, Object> inParams);
}
