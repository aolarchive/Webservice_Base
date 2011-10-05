/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.persistence.helper;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aol.webservice_base.persistence.PersistenceException;

// TODO: Auto-generated Javadoc
/**
 * The Class DynamicWhereClause.
 * 
 * Usage is to alter a SQL Query with application injected data
 * This class attempts to prevent SQL injection on Strings
 * 
 * @author human
 */
public class DynamicWhereClause {
	
	/** The where items. */
	protected ArrayList<WhereItem> whereItems;
	protected Integer alterWhereClause = null;
	protected Integer expectedWhereCount = null;
	
	/**
	 * The Class WhereItem.
	 */
	private static class WhereItem {
		
		/** The column. */
		protected String column;
		
		/** The operation. */
		protected String operation;
		
		/** The value. */
		protected Object value;

		/** The Constant QUOTE_1. */
		protected static final char QUOTE_1 = '\"';
		
		/** The Constant QUOTE_2. */
		protected static final char QUOTE_2 = '\"';

		/**
		 * Prevent injection.
		 * 
		 * @param value the value
		 * 
		 * @return the object
		 * 
		 * @throws PersistenceException the persistence exception
		 */
		protected Object ensureValidValueContent(Object value) throws PersistenceException {
			if (value instanceof String) {
				String strValue = (String)value;
				char quote = QUOTE_1;
				if (strValue.indexOf(QUOTE_1) >= 0) {
					if (strValue.indexOf(QUOTE_2) >= 0) {
						throw new PersistenceException("SQL Injection possibility detected: " + strValue);
					} else {
						quote = QUOTE_2;
					}
				}				
				StringBuilder valueBuilder = new StringBuilder(32);
				valueBuilder.append(quote);
				valueBuilder.append(value.toString());
				valueBuilder.append(quote);				
				return valueBuilder.toString();
			} else if (value instanceof java.util.Date) {
				StringBuilder valueBuilder = new StringBuilder(32);
				valueBuilder.append(QUOTE_1);
				valueBuilder.append(value.toString());
				valueBuilder.append(QUOTE_1);
				return valueBuilder.toString();
			} else 
				return value;			
		}

		/**
		 * Instantiates a new where item.
		 * 
		 * @param column the column
		 * @param operation the operation
		 * @param value the value
		 * 
		 * @throws PersistenceException the persistence exception
		 */
		protected WhereItem(String column, String operation, Object value) throws PersistenceException {
			this.column = column;
			this.operation = operation;
			this.value = ensureValidValueContent(value);
		}
	}

	/**
	 * Instantiates a new DynamicWhereClause.
	 * 
	 * @param alterWhereClause the where clause (index) to alter
	 * @param expectedWhereCount the expected # of where clauses in query
	 * 
	 * @throws PersistenceException the persistence exception
	 */
	protected DynamicWhereClause(int alterWhereClause, int expectedWhereCount) throws PersistenceException {
		if (alterWhereClause > expectedWhereCount) {
			throw new PersistenceException("Injecting Where clause whereNum: " + alterWhereClause + " > " + expectedWhereCount);
		}		
		
		whereItems = new ArrayList<WhereItem>();
		this.alterWhereClause = alterWhereClause;
		this.expectedWhereCount = expectedWhereCount;
	}
	
	/**
	 * Adds the data set as WHERE specifier
	 * 
	 * @param column the column name
	 * @param operation the compare operation
	 * @param value the value to which comparison is done
	 * 
	 * @throws PersistenceException the persistence exception
	 */
	public void addItem(String column, String operation, Object value) throws PersistenceException {
		whereItems.add(new WhereItem(column, operation, value));
	}

	/**
	 * Gets the where clause.
	 * 
	 * @return the where clause
	 */
	protected String getWhereClause() {
		StringBuilder sb = new StringBuilder(whereItems.size()*32);
		sb.append(" WHERE ");
		for (int i=0; i< whereItems.size(); i++) {
			if (i > 0) {
				sb.append(" AND ");
			}
			WhereItem item = whereItems.get(i);
			sb.append(item.column);
			sb.append(item.operation);
			sb.append(item.value);
		}

		return sb.toString();
	}

	/**
	 * Inject a where clause into a query
	 * This will alter the specified WHERE clause to include data specified
	 * 
	 * SPECIAL CASE:
	 * when there is no WHERE clause is not in original query and 1/1 is specified.
	 * 
	 * @param query the query to alter
	 * 
	 * @return the string
	 * 
	 * @throws PersistenceException the persistence exception
	 */
	public String injectWhere(String query) throws PersistenceException {
		// skip processing if there's nothing to do
		if (whereItems.size() == 0)
			return query;

		// detect existing WHERE clauses in query (alter as appropriate)
		Pattern wherePattern = Pattern.compile("\\s+where\\s+", Pattern.CASE_INSENSITIVE);
		String whereClause = getWhereClause();
		int newQuerySize = query.length() + whereClause.length();
		StringBuffer sbInject = new StringBuffer(newQuerySize);
		Matcher whereMatcher = wherePattern.matcher(query);
		int foundCount = 0;
		while (whereMatcher.find()) {
			if (++foundCount == alterWhereClause)
				whereMatcher.appendReplacement(sbInject, whereClause + " AND ");
		}
		whereMatcher.appendTail(sbInject);

		// ensure we found the right number of expectedWhereClauses
		if (foundCount == expectedWhereCount) {
			return sbInject.toString();
		} else if ((foundCount > expectedWhereCount) || (expectedWhereCount > 1)) {
			// the implementation made a mistake here
			throw new PersistenceException("Problem with injecting WHERE expected=" + expectedWhereCount + " found=" + foundCount + " with query: " + query);
		} else {
			// no WHERE clause found and 1/1 specified			
			StringBuilder newQuery = new StringBuilder(newQuerySize);
			
			// special case - we need to inject before ORDER BY - if it's there
			Pattern orderByPattern = Pattern.compile("(.*)?(\\s+order\\s+by\\s+.*)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = orderByPattern.matcher(query);
			if (matcher.matches()) {
				newQuery.append(matcher.group(1)).append(whereClause).append(matcher.group(2));
			} else {
				newQuery.append(query).append(whereClause);
			}
				
			return newQuery.toString();
		}
	}
}
