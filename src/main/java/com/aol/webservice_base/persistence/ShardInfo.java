/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.persistence;

/**
 * @author human
 *
 */
public class ShardInfo {
	protected Integer startBucket;
	protected Integer endBucket;
	protected IDBConnectionManager dbConnectionMgr;
	
	public Integer getStartBucket() {
		return startBucket;
	}
	public void setStartBucket(Integer startBucket) {
		this.startBucket = startBucket;
	}
	public Integer getEndBucket() {
		return endBucket;
	}
	public void setEndBucket(Integer endBucket) {
		this.endBucket = endBucket;
	}
	public IDBConnectionManager getDbConnectionMgr() {
		return dbConnectionMgr;
	}
	public void setDbConnectionMgr(IDBConnectionManager dbConnectionMgr) {
		this.dbConnectionMgr = dbConnectionMgr;
	}
}
