/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.persistence.helper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.aol.webservice_base.persistence.PersistenceException;

/**
 * @author human
 *
 */
public class SqlHelper {
	public static int determineSqlType(Class<?> clazz) {
		String expectedType = clazz.getName();
		int sqlType = -1;
		if (expectedType.equals("boolean") || expectedType.equals("java.lang.Boolean"))
			sqlType = Types.BOOLEAN;
		else if (expectedType.equals("int") || expectedType.equals("java.lang.Integer"))
			sqlType = Types.INTEGER;
		else if (expectedType.equals("long") || expectedType.equals("java.lang.Long"))
			sqlType = Types.BIGINT;
		else if (expectedType.equals("double") || expectedType.equals("java.lang.Double"))
			sqlType = Types.DOUBLE;
		else if (expectedType.equals("float") || expectedType.equals("java.lang.Float"))
			sqlType = Types.FLOAT;
		return sqlType;
	}		
	
	public static Object getDbColumn(int itemType, ResultSet rs, int colNum) throws SQLException, PersistenceException {
		Object returnData = null; 
		switch (itemType) {
			// in -1 case - let SQL attempt to determine the correct object
			case -1:
				returnData = rs.getObject(colNum);
				break;
			case Types.BOOLEAN:
				returnData = rs.getBoolean(colNum);
				break;
			case Types.TINYINT:
			case Types.INTEGER:
			case Types.SMALLINT:					
				returnData = rs.getInt(colNum);
				break;
			case Types.BIGINT:
				returnData = rs.getLong(colNum);
				break;
			case Types.DOUBLE:
				returnData = rs.getDouble(colNum);
				break;
			case Types.FLOAT:
				returnData = rs.getFloat(colNum);
				break;
			case Types.DATE:
			case Types.TIMESTAMP:
				returnData = rs.getTimestamp(colNum);
				break;
			case Types.VARCHAR:
				returnData = rs.getString(colNum);
				break;
			default:
				throw new PersistenceException("Error: Webservice_Base Query not prepared to handle Types.type: " + itemType);				
		}
		
		// check database null value and ensure response set properly
		if (rs.wasNull())
			returnData = null;
		
		return returnData;		
	}
}
