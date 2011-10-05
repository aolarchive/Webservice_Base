/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.persistence;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The Interface IDBConnectionManager.
 * This specifies how connections to the database are managed for the application.
 * There are (currently) two implementations: 
 * JDBCConnectionManager and ShardedJDBCConnectionManager
 * 
 * NOTE: The bucketize method needs to be implemented even if it is not applicable.
 * For example: The bucket returned is used by ShardedJDBCConnectionManager to determine 
 * which underlying JDBCConnectionManager to use.
 * 
 * @author human
 */
public interface IDBConnectionManager {
	/**
	 * Gets the connection.
	 * Connections must always be returned
	 * 
	 * @param bucketItem the bucket item
	 * 
	 * @return the connection
	 * 
	 * @throws PersistenceException the persistence exception
	 */
	public Connection getConnection(Integer bucket) throws PersistenceException;
	
	/**
	 * Return connection.
	 * 
	 * @param connection the connection
	 */
	public void returnConnection(Connection connection);
	
	/**
	 * Fail connection.
	 * 
	 * @param connection the connection
	 */
	public void failConnection(Connection connection);

	/**
	 * Check connection is not failed.
	 *
	 * @param connection the connection
	 * @return true, if not failed
	 */
	public boolean checkConnection(Connection connection);
	
	/**
	 * Checks if is duplicate exception.
	 * 
	 * @param sqlE the sql e
	 * 
	 * @return true, if is duplicate exception
	 */
	public boolean isDuplicateException(Connection connection, SQLException sqlE);

	/**
	 * Checks if is coding error exception.
	 * 
	 * @param sqlE the sql e
	 * 
	 * @return true, if is coding error exception
	 */
	public boolean isCodingErrorException(Connection connection, SQLException sqlE);
	
	/**
	 * Is Bucketizer required.
	 * Indicates if this db implementation requires a bucketizer
	 * 
	 * @return true, if successful
	 */
	public boolean isBucketizerRequired();
	
	/**
	 * Bucketize.  Determines the bucket for this item
	 * 
	 * @param bucketItem the item for which to determine bucket
	 * 
	 * @return the int
	 * 
	 * @throws PersistenceException the persistence exception
	 */
	public Integer bucketize(Object bucketItem) throws PersistenceException;
	
	/**
	 * Terminate - terminates all connections
	 */
	public void terminate();
}
