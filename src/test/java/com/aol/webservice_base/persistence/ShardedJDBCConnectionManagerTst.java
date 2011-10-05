/**
 * 
 */
package com.aol.webservice_base.persistence;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * @author human
 *
 */
public class ShardedJDBCConnectionManagerTst {
	protected ShardedJDBCConnectionManager mgr = null;
	protected List<ShardInfo> shards = null;
	protected IDBConnectionManager mgr1;
	protected IDBConnectionManager mgr2;
	protected Connection mgr1conn = null;
	protected Connection mgr2conn = null;
	
	@Before
	public void init() throws PersistenceException {
		mgr = new ShardedJDBCConnectionManager();
		shards = new ArrayList<ShardInfo>();

		mgr1 = new MockDbConnectionManager();
		mgr2 = new MockDbConnectionManager();
		
		mgr1conn = mgr1.getConnection(null);
		mgr1.returnConnection(mgr1conn);
		mgr2conn = mgr2.getConnection(null);
		mgr2.returnConnection(mgr2conn);
	}
	
	protected ShardInfo createShard(int startBucket, int endBucket, IDBConnectionManager connection) {
		ShardInfo shard = new ShardInfo();
		shard.setStartBucket(startBucket);
		shard.setEndBucket(endBucket);
		shard.setDbConnectionMgr(connection);
		return shard;
	}
	
	@Test (expected = Error.class)
	public void testInvalidShardsEmpty() throws PersistenceException {
		mgr.setShards(shards);
	}

	@Test (expected = Error.class)
	public void testInvalidShardsBadShard() throws PersistenceException {
		shards.add(createShard(1, 0, null));
		mgr.setShards(shards);
	}		
	
	@Test (expected = Error.class)
	public void testInvalidShardsNotStart0() throws PersistenceException {
		shards.add(createShard(1, 2, null));
		mgr.setShards(shards);
	}

	@Test (expected = Error.class)
	public void testInvalidShardsIncomplete() throws PersistenceException {
		shards.add(createShard(0, 65534, null));
		mgr.setShards(shards);
	}

	@Test (expected = Error.class)
	public void testInvalidShardsOverlap() throws PersistenceException {
		shards.add(createShard(0, 32767, null));
		shards.add(createShard(32767, 16777215, null));
		mgr.setShards(shards);
	}	
	
	@Test
	public void testShardsComplete() throws PersistenceException {
		shards.add(createShard(0, 16777215, mgr1));
		mgr.setShards(shards);
	}
	
	@Test 
	public void testShardsFind() throws PersistenceException {
		testShardsComplete();
		Connection connectionGot = null;

		connectionGot = mgr.getConnection(0);
		Assert.assertEquals(mgr1conn, connectionGot);
		mgr.returnConnection(connectionGot);
		
		connectionGot = mgr.getConnection(32767);
		Assert.assertEquals(mgr1conn, connectionGot);
		mgr.returnConnection(connectionGot);
		
		connectionGot = mgr.getConnection(32768);
		Assert.assertEquals(mgr1conn, connectionGot);
		mgr.returnConnection(connectionGot);
		
		connectionGot = mgr.getConnection(16777215);
		Assert.assertEquals(mgr1conn, connectionGot);
		mgr.returnConnection(connectionGot);
	}

	@Test
	public void testShardsExtra() throws PersistenceException {
		shards.add(createShard(0, 16777216, null));
		mgr.setShards(shards);
	}

	@Test
	public void testShards2Complete() throws PersistenceException {
		shards.add(createShard(0, 32767, mgr1));
		shards.add(createShard(32768, 16777215, mgr2));
		mgr.setShards(shards);
	}
	
	@Test
	public void testShards2Find() throws PersistenceException {
		testShards2Complete();
		Connection connectionGot = null;
		
		connectionGot = mgr.getConnection(0);
		Assert.assertEquals(mgr1conn, connectionGot);
		mgr.returnConnection(connectionGot);
		
		connectionGot = mgr.getConnection(32767);
		Assert.assertEquals(mgr1conn, connectionGot);
		mgr.returnConnection(connectionGot);
		
		connectionGot = mgr.getConnection(32768);
		Assert.assertEquals(mgr2conn, connectionGot);
		mgr.returnConnection(connectionGot);
		
		connectionGot = mgr.getConnection(16777215);
		Assert.assertEquals(mgr2conn, connectionGot);
		mgr.returnConnection(connectionGot);
	}
	
}
