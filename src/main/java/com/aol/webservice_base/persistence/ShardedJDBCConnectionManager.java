/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.persistence;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aol.webservice_base.util.types.TypesHelper;

/**
 * @author human
 *
 */
public class ShardedJDBCConnectionManager implements IDBConnectionManager {
	protected List<ShardInfo> shards;
	protected Map<Connection, ShardInfo> inUse = Collections.synchronizedMap(new HashMap<Connection, ShardInfo>());
	protected static final int SHIFT_BYTES = 3;
	protected static final int EXPECTED_MAX_SHARD = 256*256*256-1; 

	public void setShards(List<ShardInfo> shards) {
		this.shards = shards;
		
		int check = 0;
		for (ShardInfo shard: shards) {
			int start = shard.getStartBucket();
			int end = shard.getEndBucket();
			if (end < start) {
				throw new Error("Shard end (" + end + ") before start (" + start + ")");
			}
			if (start != check) {
				throw new Error("Expected shard index: " + check + " not found, got: " + shard.getStartBucket());
			}
			// next thing we check for is 1 past the last
			check = end + 1;
		}
		
		if (check <= EXPECTED_MAX_SHARD) {
			throw new Error("Expected end shard index " + check + " is not covered by configuration");
		}
		
		this.shards = shards;
	}
	
	public Connection getConnection(Integer bucket) throws PersistenceException {
		Connection connection = null;
		
		ShardInfo shard = determineShard(bucket);
		
		if (shard != null) {
			// we've got the shard manager - the bucket to this manager is irrelevant 
			connection = shard.getDbConnectionMgr().getConnection(null);
			if (connection != null)
				inUse.put(connection, shard);
		}
		
		return connection;
	}

	protected ShardInfo determineShard(Integer bucket) throws PersistenceException {
		if (bucket == null) {
			throw new PersistenceException("Sharded DB requested with null bucket");
		}
		for (ShardInfo shard: shards) {
			if ((shard.getStartBucket() <= bucket) && (shard.getEndBucket() >= bucket))
				return shard;
		}
		
		throw new Error("COULD NOT FIND SHARD for bucket: " + bucket);
	}
	
	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.IDBConnectionManager#bucketizerRequired()
	 */
	public boolean isBucketizerRequired() {
		return true;
	}		
	
	/**
	 * Bucketize.
	 * 
	 * @param bucketItem the bucket item
	 * 
	 * @return the int (0-EXPECTED_MAX_SHARD)
	 * 
	 * @throws PersistenceException the persistence exception
	 */
	public Integer bucketize(Object bucketItem) throws PersistenceException {
		if (bucketItem == null) {
			throw new Error("Attempt to bucketize (null)");
		}
		// We need to ensure this is a simple item, otherwise blow up.
		if (!TypesHelper.isSimpleType(bucketItem.getClass())) {
			throw new Error("Object of class: " + bucketItem.getClass() + " not simple/valid for use as bucket");
		}
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new Error("Unknown Message Digest Algorithm: " + e.getMessage());
		}
		String hashText = bucketItem.toString(); 
		md.update(hashText.getBytes(), 0, hashText.length());
		byte[] hash = md.digest();	
		
		// Convert the first SHIFT_BYTES bytes to int
      int accum = 0;
      for (int shiftByte = 0; shiftByte < SHIFT_BYTES; shiftByte++) {
          accum |= ((int)(hash[shiftByte] & 0xff)) << shiftByte*8;
      }
      return accum;
	}
	
	public boolean checkConnection(Connection connection) {
		ShardInfo shard = inUse.get(connection);
		return shard.getDbConnectionMgr().checkConnection(connection);		
	}
	
	public void failConnection(Connection connection) {
		ShardInfo shard = inUse.get(connection);
		shard.getDbConnectionMgr().failConnection(connection);		
	}

	public void returnConnection(Connection connection) {
		ShardInfo shard = inUse.get(connection);
		shard.getDbConnectionMgr().returnConnection(connection);
	}

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.IDBConnectionManager#isCodingErrorException(java.sql.Connection, java.sql.SQLException)
	 */
	public boolean isCodingErrorException(Connection connection, SQLException sqlE) {
		return inUse.get(connection).getDbConnectionMgr().isCodingErrorException(connection, sqlE);
	}

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.IDBConnectionManager#isDuplicateException(java.sql.Connection, java.sql.SQLException)
	 */
	public boolean isDuplicateException(Connection connection, SQLException sqlE) {
		return inUse.get(connection).getDbConnectionMgr().isDuplicateException(connection, sqlE);
	}

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.IDBConnectionManager#terminate()
	 */
	public void terminate() {
		for (ShardInfo shard: shards) {
			shard.getDbConnectionMgr().terminate();
		}
	}
}
