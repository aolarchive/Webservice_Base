/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

 */

package com.aol.webservice_base.persistence;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;
import org.apache.log4j.Level;


/**
 * @author human
 *
 */
public abstract class AbstractSTP extends AbstractBaseQuery {
	protected static final Logger logger = Logger.getLogger(AbstractSTP.class);

	protected String stp;	

	public static class ValueInfo {		
		protected Object value;
		protected int type;
		public ValueInfo(Object value, int type) {
			this.value = value;
			this.type = type;			
		}
	}


	// the execute query will be implementation defined
	// the RequestState should be passed in
	// the logic would be as follows:
	// 1. build up a map of ArgName(string) -> Value(various)
	//    (note the conventions used to how values passed to JDBC)
	// 2. call doRequest(state, map)
	// 3. ensure the state is valid before processing results	
	protected abstract Object parseResponse(CallableStatement statement);	

	protected Object doRequest(RequestState state) throws PersistenceException  {
		return doRequest(state, null, null, null);
	}

	protected Object doRequest(RequestState state, Object object) throws PersistenceException  {
		return doRequest(state, object, null, null);
	}

	protected Object doRequest(RequestState state, Map<String, ValueInfo> args) throws PersistenceException {
		return doRequest(state, null, args, null);
	}

	protected Object doRequest(RequestState state, Map<String, ValueInfo> args, Map<String, Integer> outParams) throws PersistenceException {
		return doRequest(state, null, args, outParams);
	}

	protected Object doRequest(RequestState state, Object object, Map<String, ValueInfo> args, Map<String, Integer> outParams) throws PersistenceException {		
		// build up call statement - total params is args + outParams
		StringBuilder sbStatement = new StringBuilder(64);
		sbStatement.append("{call ").append(stp).append("(");
		int argCount = ((args != null) ? args.size() : 0) + ((outParams != null) ? outParams.size() : 0);
		for (int i=0; i<argCount; ) {
			i++;
			sbStatement.append((i<argCount) ? "?," : "?");
		}
		sbStatement.append(")}");

		Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;
		try {
			connection = getConnection(object);
			connection.setAutoCommit(true);

			statement = connection.prepareCall(sbStatement.toString());

			// populate STP arguments
			if (args != null) {
				for (Map.Entry<String, ValueInfo> nameValue: args.entrySet()) {
					String key = nameValue.getKey();
					ValueInfo data = nameValue.getValue();
					if (data.value == null) {
						statement.setNull(key, data.type);
					} else {
						try {
							switch (data.type) {
								case Types.BOOLEAN:
									statement.setBoolean(key, (Boolean)data.value);
									break;
								case Types.TINYINT:
								case Types.INTEGER:
								case Types.SMALLINT:					
									statement.setInt(key, (Integer)data.value);
									break;
								case Types.BIGINT:				
									statement.setLong(key, (Long)data.value);
									break;
								case Types.DOUBLE:
									statement.setDouble(key, (Double)data.value);
									break;
								case Types.FLOAT:
									statement.setFloat(key, (Float)data.value);
									break;
								case Types.DATE:
								case Types.TIMESTAMP:
									java.util.Date javaDate = (java.util.Date)data.value;
									java.sql.Timestamp sqlDate = null;
									if (javaDate != null) {
										long time = javaDate.getTime();
										sqlDate = new java.sql.Timestamp(time);
									}
									statement.setTimestamp(key, sqlDate);
									break;
								case Types.VARCHAR:
									statement.setString(key, (String)data.value);
									break;
								case Types.VARBINARY:
									statement.setBytes(key, (byte[])data.value);
									break;
								default:
									throw new PersistenceException("Error: Webservice_Base STP not prepared to handle Types.type: " + data.type);
							}
						} catch (NullPointerException e) {
							throw new PersistenceException("Problem populating STP parameter: " + key + " because of: " + e.getMessage());
						}
					}
				}
			}
			
			// populate expected STP output parameters
			if (outParams != null) { 
				for (Map.Entry<String, Integer> nameValue: outParams.entrySet()) {
					statement.registerOutParameter(nameValue.getKey(), nameValue.getValue());
				}
			}
			
			// run the query
                        state.log(Level.DEBUG, this.getClass(), "Execute: ", statement);
			Boolean hasResults = statement.execute();
			if (hasResults || ((outParams != null) && (outParams.size() > 0)))
				return parseResponse(statement);
			else 
				return null;
		} catch (SQLException sqlE) {
			// some sql exceptions are "expected"
			// so we examine them here to clean them out to effect the response
			if (allowDuplicates && dbConnectionMgr.isDuplicateException(connection, sqlE))
				return null;
			else {
				state.log(Level.ERROR, this.getClass(), "SQLException: " + sqlE.getMessage() + " running stp " + stp);
				state.setError(Constants.SC_INTERNAL_SERVER_ERROR, Constants.ST_INTERNAL_SERVER_ERROR);
				return null;
			}
		} catch (PersistenceException persistenceE) {
			state.log(Level.ERROR, this.getClass(), "Persistence Exception: " + persistenceE.getMessage());
			state.setError(Constants.SC_INTERNAL_SERVER_ERROR, Constants.ST_INTERNAL_SERVER_ERROR);
			throw persistenceE;
		} catch (Exception e) {
			String errorMessage = e.getClass().getName() + " message: " + e.getMessage();
			state.log(Level.ERROR, this.getClass(), errorMessage);
			state.setError(Constants.SC_INTERNAL_SERVER_ERROR, Constants.ST_INTERNAL_SERVER_ERROR);
			throw new Error(errorMessage, e);				
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
			try {
				if (statement != null)
					statement.close();
				// When we're done, try to return connection back to connection manager
				if (connection != null) {
					dbConnectionMgr.returnConnection(connection);
				}
			} catch (SQLException e) {
				if (connection != null) {
					dbConnectionMgr.failConnection(connection);
				}
			}
		}
	}

	public final void setStp(String stp) {
		this.stp = stp;
	}
}
