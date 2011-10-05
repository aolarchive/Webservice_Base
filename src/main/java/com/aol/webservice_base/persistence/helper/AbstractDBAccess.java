/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.persistence.helper;

import java.sql.Types;

import org.apache.log4j.Logger;

/**
 * @author human
 *
 */
public abstract class AbstractDBAccess {
	protected static final Logger logger = Logger.getLogger(AbstractDBAccess.class);
	
	protected Integer type;
	//protected String varName; - used to generate setter
	
	abstract void setMember(String member);

	public Integer getType() {
		return type;
	}
	public void setType(String typeString) {
		/* Convert String to integer */
		typeString = typeString.toLowerCase();
		
		if (typeString.startsWith("bool")) {
			type = Types.BOOLEAN;
		} else if (typeString.startsWith("int")) {
			type = Types.INTEGER;
		} else if (typeString.equals("long") || typeString.equals("bigint")) {
			type = Types.BIGINT;
		} else if (typeString.equals("double")) {
			type = Types.DOUBLE;
		} else if (typeString.equals("float")) {
			type = Types.FLOAT;
		} else if (typeString.equals("datetime")) {
			type = Types.TIMESTAMP;
		} else if (typeString.equals("timestamp")) {
			type = Types.TIMESTAMP;
		} else if (typeString.equals("varchar") || typeString.equals("string")) {
			type = Types.VARCHAR;
		} else {
			String error = "Webservice_Base does not know Type: " + typeString;
			logger.error(error);
			throw new Error(error); 
		}
	}
	
}
