/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.persistence;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.aol.webservice_base.persistence.helper.DynamicWhereClause;
import com.aol.webservice_base.persistence.helper.DynamicWhereClauseFactory;
import com.aol.webservice_base.persistence.helper.QueryParameter;
import com.aol.webservice_base.persistence.helper.ResponsePopulator;
import com.aol.webservice_base.persistence.helper.SqlHelper;
import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;
import com.aol.webservice_base.stats.BaseStatistics;
import com.aol.webservice_base.stats.StatisticsManager;
import com.aol.webservice_base.util.reflection.ReflectionHelper;
import com.aol.webservice_base.util.types.TypesHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractQuery.
 * 
 * @author human
 * 
 * This class is the base of all SQL Queries.
 * It always returns the data in a List.
 * 
 * Note, when generating queries, BUCKET_NUM has a special action in that it will
 * automatically use the value of the generated bucket (from bucketizer) as the value
 */
public abstract class AbstractQuery extends AbstractBaseQuery {
	/* Special (config driven) indicator to use generated value in SQL Query */
	/** The Constant BUCKET_NUM. */
	public static final String BUCKET_NUM = "BUCKET_NUM";
	
	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(AbstractQuery.class);

	/** The query. */
	protected String query;
	
	/** The parameters. */
	protected ArrayList<QueryParameter> parameters = null; /* must be in order */	
	
	/** The response data class. */
	protected String responseDataClass = null;
	
	/** The response population. */
	protected ArrayList<ResponsePopulator> responsePopulation = null; /* must be in order */
	
	/** The where clause factory. */
	protected DynamicWhereClauseFactory whereClauseFactory = null;
	
	/** Determines if the application wants the generated keys (for INSERT only). */
	protected boolean wantsGeneratedKeys = false;

	/** statistics */
	private BaseStatistics stats;
	
	/**
	 * Parses the response.
	 * 
	 * @param objects the objects
	 * 
	 * @return the object
	 */
	protected abstract Object parseResponse(List<Object> objects);	

	/**
	 * Parses the generated keys.
	 *
	 * @param resultSetMetaData the result set meta data
	 * @return the object
	 * @throws SQLException 
	 */
	protected List<Object> parseGeneratedKeys(ResultSet rs) throws SQLException {
		List<Object> generatedKeys = new ArrayList<Object>();
		if (rs.next()) {
		    ResultSetMetaData rsmd = rs.getMetaData();
		    int colCount = rsmd.getColumnCount();
		    do {
		        for (int i = 1; i <= colCount; i++) {
		      	  generatedKeys.add(rs.getObject(i));
		        }
		    }
		    while (rs.next());
		}
		return generatedKeys;
	}
	
	/**
	 * Gets a new DynamicWhereClause for adjusting query upon execute.
	 * 
	 * @return the DynamicWhereClause
	 * 
	 * @throws PersistenceException the persistence exception (if not properly configured)
	 */
	public DynamicWhereClause getDynamicWhereClause() throws PersistenceException {
		return whereClauseFactory.getInstance();
	}
	
	public AbstractQuery() {
		// set up stats
		stats = StatisticsManager.getStatsHandler(this.getClass(), null, "DB Query Success Time", "DB Query Failure Time");
	}
	
	/**
	 * Execute Query
	 * DO NOT USE when using Sharded DB
	 * 
	 * @param state the state
	 * 
	 * @return the object
	 * 
	 * @throws PersistenceException the persistence exception
	 */
	public Object execute(RequestState state) throws PersistenceException  {
		return execute(state, null, null);
	}
	public Object executeOnConnection(RequestState state, IDBConnectionManager connectionManager, Connection connection) throws PersistenceException  {
		return executeOnConnection(state, connectionManager, connection, null, null);
	}
	
	/**
	 * Execute Query
	 * DO NOT USE when using Sharded DB
	 * 
	 * @param state the state
	 * @param whereClause the where clause
	 * 
	 * @return the object
	 * 
	 * @throws PersistenceException the persistence exception
	 */
	public Object execute(RequestState state, DynamicWhereClause whereClause) throws PersistenceException  {
		return execute(state, null, whereClause);
	}
	public Object executeOnConnection(RequestState state, IDBConnectionManager connectionManager, Connection connection, DynamicWhereClause whereClause) throws PersistenceException  {
		return executeOnConnection(state, connectionManager, connection, null, whereClause);
	}
	
	/**
	 * Execute Query
	 * 
	 * @param state the state
	 * @param object the object
	 * 
	 * @return the object
	 * 
	 * @throws PersistenceException the persistence exception
	 */
	public Object execute(RequestState state, Object object) throws PersistenceException  {
		return execute(state, object, null);
	}
	public Object executeOnConnection(RequestState state, IDBConnectionManager connectionManager, Connection connection, Object object) throws PersistenceException  {
		return executeOnConnection(state, connectionManager, connection, object, null);
	}
	
	/**
	 * Execute Query
	 * 
	 * @param state the state
	 * @param object the object
	 * @param whereInfo the where info
	 * 
	 * @return the object
	 * 
	 * @throws PersistenceException the persistence exception
	 */
	public Object execute(RequestState state, Object object, DynamicWhereClause whereInfo) throws PersistenceException {
		long startTime = System.currentTimeMillis();

		// build up call statement
		Connection connection = null;

		try {
			Integer bucketNum = null;
			if (dbConnectionMgr.isBucketizerRequired()) {
				bucketNum = bucketize(object);
				if (bucketNum == null) {
					throw new PersistenceException("Expected bucket and got none for " + object);
				}
			}
			connection = getConnection(bucketNum);
			connection.setAutoCommit(true);
			
			return executeOnConnection(state, dbConnectionMgr, connection, object, whereInfo);
		} catch (Exception e) {
			String errorMessage = e.getClass().getName() + " message: " + e.getMessage();
			logger.error(errorMessage);
			state.setError(Constants.SC_INTERNAL_SERVER_ERROR, Constants.ST_INTERNAL_SERVER_ERROR);
			throw new Error(errorMessage);
		} finally {
			// When we're done, try to return connection back to connection manager
			if (connection != null) {
				dbConnectionMgr.returnConnection(connection);
			}
			
			// update hog row as appropriate
			long duration = System.currentTimeMillis() - startTime;
			if (state.getStatusCode() == Constants.SC_OK) {
				stats.success(duration);
			} else {
				stats.failure(duration);
			}
		}
	}
	
	
	public Object executeOnConnection(RequestState state, IDBConnectionManager connectionManager, Connection connection, Object object, DynamicWhereClause whereInfo) throws PersistenceException {
		if (!connectionManager.checkConnection(connection))
			throw new PersistenceException("Application using a failed connection");
		
		long startTime = System.currentTimeMillis();
		
		// build up call statement
		PreparedStatement statement = null;
		ResultSet rs = null;
		String getter = null;
		String customQuery = getQuery(state);

		// inject any dynamic WHERE info
		if (whereInfo != null) {
			customQuery = whereInfo.injectWhere(customQuery);
		} else {
			// TODO - attempt to retrieve prepared statement
		}
	
		try {
			if (wantsGeneratedKeys)
				statement = connection.prepareStatement(customQuery, Statement.RETURN_GENERATED_KEYS);
			else 
				statement = connection.prepareStatement(customQuery);
			
			// no dynamic where clause means we can save this prepared query
			if (whereInfo == null) {
				// TODO: Store query for reuse
				// see if this affects closing the prepared statement
				// in the finally clause, below
			}
			
			// populate query parameters
			if (object != null) {
				Integer bucketNum = null;
				if (connectionManager.isBucketizerRequired()) {
					bucketNum = bucketize(object);
					if (bucketNum == null) {
						throw new PersistenceException("Expected bucket and got none for " + object);
					}
				}

				populateParameters(statement, object, bucketNum);
			}

			// run the query
			boolean hasData = statement.execute();
			if (hasData) {
				// if there's data - do not allow retrieval of generated keys
				if (wantsGeneratedKeys) {
					String message = "Improper Configuration - " + this.getClass().getName() + " wantsGeneratedKeys when data is returned";
					state.log(Level.ERROR, this.getClass(), message);
					throw new RuntimeException(message);
				}
				rs = statement.getResultSet();
				return parseResponse(createResults(rs)); 
			} else if (wantsGeneratedKeys) {
				rs = statement.getGeneratedKeys();
				return parseGeneratedKeys(rs);
			}
			return null;
		} catch (SQLException sqlE) {
			// some sql exceptions are "expected"
			// so we examine them here to clean them out to effect the response
			if (connectionManager.isDuplicateException(connection, sqlE)) {
				if (allowDuplicates) {
					return null;
				} else {
					logger.error("SQLException - Duplicate: " + sqlE.getMessage() + " running query " + customQuery);
					state.setError(Constants.SC_DUPLICATE_ROW, Constants.ST_DUPLICATE_ROW);
					return null;
				}
			} else {
				boolean codingError = connectionManager.isCodingErrorException(connection, sqlE); 
				String errorMessage = "SQLException: " + sqlE.getMessage() + " running query " + customQuery; 
				logger.error(errorMessage);
				state.setError(Constants.SC_INTERNAL_SERVER_ERROR, Constants.ST_INTERNAL_SERVER_ERROR);
				if (codingError)
					throw new Error(errorMessage);
				else {
					connectionManager.failConnection(connection);
					return null;
				}
			}
		} catch (Exception e) {
			String errorMessage = e.getClass().getName() + " message: " + e.getMessage() + " with Query: " + customQuery;
			if (object != null) 
				errorMessage +=  " on " + object.getClass().getName() + "." + getter;
			logger.error(errorMessage);
			state.setError(Constants.SC_INTERNAL_SERVER_ERROR, Constants.ST_INTERNAL_SERVER_ERROR);
			throw new Error(errorMessage);
		} finally {
			// when we're done - close the results and statement			
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			// and either return or fail the connection accordingly
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					connectionManager.failConnection(connection);
				}
			}
			
			// update hog row as appropriate
			long duration = System.currentTimeMillis() - startTime;
			if (state.getStatusCode() == Constants.SC_OK) {
				stats.success(duration);
			} else {
				stats.failure(duration);
			}
		}
	}
	
	/**
	 * Populate parameters.
	 * 
	 * @param statement the statement
	 * @param object the object
	 * @param bucket the bucket
	 * 
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws SecurityException the security exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws SQLException the SQL exception
	 * @throws PersistenceException the persistence exception
	 */
	protected void populateParameters(PreparedStatement statement, Object object, Integer bucketNum) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException, PersistenceException {
		if (parameters == null) 
			return;
		
		for (int i = 0; i < parameters.size(); i++) {
			QueryParameter parameter = parameters.get(i);
			int index = i+1;

			// obtain the value for this parameter (respecting BUCKET_NUM special case)
			String getter = parameter.getGetter();
			Object value = null;
			if (getter.equals(BUCKET_NUM))
				value = bucketNum;
			else
				value = object.getClass().getMethod(getter).invoke(object, (Object[])null);				

			// handle null specially so it doesn't blow up the work if
			// value is internal / altered below
			if (value == null)  {
				statement.setObject(index, null);
			} else switch (parameter.getType()) {
				case Types.BOOLEAN:
					statement.setBoolean(index, (Boolean)value);
					break;
				case Types.TINYINT:
				case Types.INTEGER:
				case Types.SMALLINT:					
					statement.setInt(index, (Integer)value);
					break;
				case Types.BIGINT:				
					statement.setLong(index, (Long)value);
					break;
				case Types.DOUBLE:
					statement.setDouble(index, (Double)value);
					break;
				case Types.FLOAT:
					statement.setFloat(index, (Float)value);
					break;
				case Types.DATE:
				case Types.TIMESTAMP:
					java.util.Date javaDate = (java.util.Date)value;
					java.sql.Timestamp sqlDate = null;
					if (javaDate != null) {
						long time = javaDate.getTime();
						sqlDate = new java.sql.Timestamp(time);
					}
					statement.setTimestamp(index, sqlDate);
					break;
				case Types.VARCHAR:
					statement.setString(index, (String)value);
					break;
				case Types.VARBINARY:
					statement.setBytes(index, (byte[])value);
					break;
				default:
					throw new PersistenceException("Error: Webservice_Base Query not prepared to handle Types.type: " + parameter.getType());
			}
		}

	}

	/**
	 * Creates the results.
	 * TODO: There's too much magic here - we should have the parsing
	 * be implementation defined.
	 * 
	 * @param rs the rs
	 * 
	 * @return the list< object>
	 * 
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws SQLException the SQL exception
	 * @throws SecurityException the security exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws PersistenceException the persistence exception
	 */
	protected List<Object> createResults(ResultSet rs) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, PersistenceException {
		// check if nothing desired to return
		if (responseDataClass == null) {
			return null;			
		}

		Class<?> clazz = Class.forName(responseDataClass);
		boolean simpleResultsClass = TypesHelper.isSimpleType(clazz);
		
		// if we are return something, but have no rules...
		if ((responsePopulation == null) && !simpleResultsClass) {
			String error = "Query Error: " + this.getClass().getName() + " Desired results are: " + responseDataClass + " but there are no responsePopulation rules"; 
			logger.error(error);
			throw new PersistenceException(error);
		}		
				
		List<Method> methods = ReflectionHelper.getSetterMethods(clazz);
		
		ArrayList<Object> results = new ArrayList<Object>();
				
		while (rs.next()) {			
			Object responseRow = null;
			
			if (simpleResultsClass) {
				int colCount = rs.getMetaData().getColumnCount();
				if (colCount != 1) {
					throw new PersistenceException("Not 1 Column as expected");
				}
				responseRow = SqlHelper.getDbColumn(SqlHelper.determineSqlType(clazz), rs, 1); 
			} else {
				responseRow = clazz.newInstance();
				for (ResponsePopulator item:responsePopulation) {
					int iCol = rs.findColumn(item.getColumn());
					
					// find the right method
					Method method = null;
					String setterDesired = item.getSetter();
					for (Method checkMethod: methods) {
						// determine information about this
						if (setterDesired.equals(checkMethod.getName())) {						
							method = checkMethod;
							break;
						}
					}
	
					if (method == null) {
						String error = "Query Error: " + clazz + " could not find setter: " + setterDesired;
						logger.error(error);
						throw new PersistenceException(error);
					}
					
					int itemType = item.getType();					
					method.invoke(responseRow, SqlHelper.getDbColumn(itemType, rs, iCol));
				}
			}
			results.add(responseRow);
		}
		return results;
	}
	
	protected String getQuery(RequestState state) {
		return query;
	}
		
	/**
	 * Sets the query.
	 * 
	 * @param query the new query
	 */
	public final void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Sets the parameters.
	 * 
	 * @param parameters the new parameters
	 */
	public final void setParameters(ArrayList<QueryParameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Sets the response data class.
	 * 
	 * @param responseDataClass the new response data class
	 */
	public final void setResponseDataClass(String responseDataClass) {
		this.responseDataClass = responseDataClass;
	}

	/**
	 * Sets the response population.
	 * 
	 * @param responsePopulation the new response population
	 */
	public final void setResponsePopulation(ArrayList<ResponsePopulator> responsePopulation) {
		this.responsePopulation = responsePopulation;
	}

	/**
	 * Sets the where clause factory.
	 * 
	 * @param whereClauseFactory the new where clause factory
	 */
	public final void setWhereClauseFactory(DynamicWhereClauseFactory whereClauseFactory) {
		this.whereClauseFactory = whereClauseFactory;
	}

	/**
	 * Sets the wants generated keys.
	 *
	 * @param wantsGeneratedKeys the new wants generated keys
	 */
	public final void setWantsGeneratedKeys(boolean wantsGeneratedKeys) {
		this.wantsGeneratedKeys = wantsGeneratedKeys;
	}
}
