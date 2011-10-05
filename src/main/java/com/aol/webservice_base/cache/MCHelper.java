/*
Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.
*/

package com.aol.webservice_base.cache;

import java.util.List;

import org.apache.log4j.Logger;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

/**
 * Memcached daemon instance
 * @author Kedar Bhave
 *
 */
public class MCHelper {
	private static Logger memcachedLog = Logger.getLogger(MCHelper.class);
	
	private static MCHelper _instance = null;
	private static boolean initialized = false;
	
	protected MemCachedClient client = null;
	protected SockIOPool pool = null;

	// Initial number of connections per server in the available pool.
	protected int initialConnections = -1;

	// Min number of spare connections in available pool.
	protected int minSpareConnections = -1;

	// Max number of spare connections allowed in our available pool.
	protected int maxSpareConnections = -1;

	// Max idle time for threads in the available pool (in ms).
	protected int maxIdleTime = -1;

	// Max busy time for threads in the busy pool (in ms)
	protected int maxBusyTime = -1;

	// Sleep time between runs of the pool maintenance thread. If set
	// to 0, then the maint thread will not be started (in ms)
	protected int maintSleep = -1;

	// Socket timeout for connect (in ms)
	protected int socketConnectTimeOut = -1;

	// Socket timeout for read (in ms)
	protected int socketReadTimeOut = -1;

	// If this is true and we have marked a host as dead, will try to
	// bring it back. If it is false, we will never try to resurrect a dead host.
	protected boolean defaultFailback = true;
	protected boolean failback = defaultFailback;

	// If this flag is set to true, and a socket fails to connect,
	// the pool will attempt to return a socket from another server
	// if one exists. If set to false, then getting a socket
	// will return null if it fails to connect to the requested server.
	protected boolean defaultFailover = true;
	protected boolean failover = defaultFailover;

	// Sets the Nagle alg flag for the pool. If false, will turn off Nagle's
	// algorithm on all sockets created.
	protected boolean defaultUseNagleAlgorithm = true;
	protected boolean useNagleAlgorithm = defaultUseNagleAlgorithm;

	// When true, this will attempt to talk to the server on every connection
	// checkout to make sure the connection is still valid. This adds extra
	// network chatter and thus is defaulted off. May be useful if you want to
	// ensure you do not have any problems talking to the server on a dead connection.
	protected boolean defaultAliveCheck = false;
	protected boolean aliveCheck = defaultAliveCheck;

	protected boolean compressEnable = true;
	protected long compressThreshold = 30720; // in bytes
	protected String defaultEncoding = "UTF-8";
	protected boolean primitiveAsString = false;
	protected boolean flushAllOnInit = false;
		
	protected boolean isCacheEnabled = true;

	protected List<String> hosts;
	//protected static String[] hosts;

	// make constructor protected so that nobody creates this
	protected MCHelper()
	{
	}

	public void init()
	{
		memcachedLog.debug("MCHelper::init()");
		if(MCHelper.initialized)
			return;

		String[] servers = new String[hosts.size()];
		for (int i = 0; i < hosts.size(); i++) {
			servers[i] = hosts.get(i);
			memcachedLog.debug(servers[i]);
		}

		if(servers.length > 0)
		{
			if(pool == null || (pool != null && !pool.isInitialized()))
			{
				pool = SockIOPool.getInstance();

				pool.setServers(servers);
				if(initialConnections > -1)
					pool.setInitConn(initialConnections);
				if(minSpareConnections > -1)
					pool.setMinConn(minSpareConnections);
				if(maxSpareConnections > -1)
					pool.setMaxConn(maxSpareConnections);
				if(maxIdleTime > -1)
					pool.setMaxIdle(maxIdleTime);
				if(maxBusyTime > -1)
					pool.setMaxBusyTime(maxBusyTime);
				if(maintSleep > -1)
					pool.setMaintSleep(maintSleep);
				if(socketConnectTimeOut > -1)
					pool.setSocketConnectTO(socketConnectTimeOut);
				if(socketReadTimeOut > -1)
					pool.setSocketTO(socketReadTimeOut);

				pool.setFailback(failback);
				pool.setFailover(failover);
				pool.setNagle(useNagleAlgorithm);
				pool.setAliveCheck(aliveCheck);
				pool.initialize();
			}
		}

		if(pool != null && pool.isInitialized())
		{
			memcachedLog.debug(" ***** Memcached pool initialized");
			client = new MemCachedClient();
			client.setCompressEnable(compressEnable);
			client.setCompressThreshold(compressThreshold);
			client.setDefaultEncoding(defaultEncoding);
			client.setPrimitiveAsString(primitiveAsString);

			if(flushAllOnInit)
				client.flushAll();
		}

		MCHelper.initialized = true;
		return;
	} // end-init

	public static synchronized MCHelper getInstance()
	{
		if (_instance == null)
			_instance = new MCHelper();
		return _instance;
	}

	public static int shutdown()
	{
		int state = 0;

		SockIOPool pool = getInstance().pool;		
		if(pool != null)
		{
			if(!pool.isInitialized())
				memcachedLog.info(" ***** Memcache pool is not initialized.  Nothing to shutdown");
			else
			{
				memcachedLog.info(" ***** Memcache pool shutdown...");
				pool.shutDown();
			}
		}

		return state;
	}

	public MemCachedClient getMemCachedClient()
	{
		return isCacheEnabled ? client : null;
	}

	public boolean isCompressEnable() {
		return compressEnable;
	}

	public void setCompressEnable(boolean compressEnable) {
		this.compressEnable = compressEnable;
	}

	public long getCompressThreshold() {
		return compressThreshold;
	}

	public void setCompressThreshold(long compressThreshold) {
		this.compressThreshold = compressThreshold;
	}

	public boolean isFailback() {
		return failback;
	}

	public void setFailback(boolean failback) {
		this.failback = failback;
	}

	public boolean isFailover() {
		return failover;
	}

	public void setFailover(boolean failover) {
		this.failover = failover;
	}

	public boolean isFlushAllOnInit() {
		return flushAllOnInit;
	}

	public void setFlushAllOnInit(boolean flushAllOnInit) {
		this.flushAllOnInit = flushAllOnInit;
	}

	public int getInitialConnections() {
		return initialConnections;
	}

	public void setInitialConnections(int initialConnections) {
		this.initialConnections = initialConnections;
	}

	public boolean isCacheEnabled() {
		return isCacheEnabled;
	}

	public void setCacheEnabled(boolean isCacheEnabled) {
		this.isCacheEnabled = isCacheEnabled;
	}

	public int getMaintSleep() {
		return maintSleep;
	}

	public void setMaintSleep(int maintSleep) {
		this.maintSleep = maintSleep;
	}

	public int getMaxBusyTime() {
		return maxBusyTime;
	}

	public void setMaxBusyTime(int maxBusyTime) {
		this.maxBusyTime = maxBusyTime;
	}

	public int getMaxIdleTime() {
		return maxIdleTime;
	}

	public void setMaxIdleTime(int maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}

	public int getMaxSpareConnections() {
		return maxSpareConnections;
	}

	public void setMaxSpareConnections(int maxSpareConnections) {
		this.maxSpareConnections = maxSpareConnections;
	}

	public int getMinSpareConnections() {
		return minSpareConnections;
	}

	public void setMinSpareConnections(int minSpareConnections) {
		this.minSpareConnections = minSpareConnections;
	}

	public boolean isPrimitiveAsString() {
		return primitiveAsString;
	}

	public void setPrimitiveAsString(boolean primitiveAsString) {
		this.primitiveAsString = primitiveAsString;
	}

	public boolean isAliveCheck() {
		return aliveCheck;
	}

	public void setAliveCheck(boolean check) {
		this.aliveCheck = check;
	}

	public int getSocketConnectTimeOut() {
		return socketConnectTimeOut;
	}

	public void setSocketConnectTimeOut(int socketConnectTimeOut) {
		this.socketConnectTimeOut = socketConnectTimeOut;
	}

	public int getSocketReadTimeOut() {
		return socketReadTimeOut;
	}

	public void setSocketReadTimeOut(int socketReadTimeOut) {
		this.socketReadTimeOut = socketReadTimeOut;
	}

	public boolean isUseNagleAlgorithm() {
		return useNagleAlgorithm;
	}

	public void setUseNagleAlgorithm(boolean useNagleAlgorithm) {
		this.useNagleAlgorithm = useNagleAlgorithm;
	}

	public void setHosts(List<String> hosts) {
		this.hosts = hosts; 
	}

	public List<String> getHosts() {
		return hosts;
	}

}


