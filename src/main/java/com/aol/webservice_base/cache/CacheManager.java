/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.cache;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.danga.MemCached.MemCachedClient;

public class CacheManager {
	protected static final Logger logger = Logger.getLogger(CacheManager.class);	
	
	protected int maxKeyLength = 240;
	
	// TODO: When key is too big, we hash the key
	//       we might want to wrap the cached object so we can verify with 100% 
	//       accuracy that we got back what we wanted
	
	protected MemCachedClient mc = MCHelper.getInstance().getMemCachedClient();
	// private static HashMap<String, ServiceScheduler> schedulerPool;

	protected static final String LOCK_PREFIX = "LOCK:";
	protected static final String COMPRESSED_PREFIX = "CMPRSD:";
	
	// When storing data in the cache, we need a keyPrefix to prevent
	// collisions between different data being saved with the same keys
	// (ex room history and room users cached with same key "roomName")
	protected String keyPrefix;
	
	// Cache expiring time
	protected Long expireMs;
	
	// how long a lock is held before it is released
	protected Long writeLockHoldMs;

	public Object delete(String key) {
		StringBuilder keyBuilder = new StringBuilder(32);
		keyBuilder.append(keyPrefix).append(key);		
		return mc.delete(ensureKeySafe(keyBuilder.toString(), false));
	}	

	protected String hashKey(String key, String hashMechanism) {
		try {
			MessageDigest md = MessageDigest.getInstance(hashMechanism);
	      md.update(key.getBytes());
	      Base64 b64 = new Base64();
	      return new String(b64.encode(md.digest()), "UTF-8");
		} catch (Exception e) {
			logger.error("Problem hashing: " + key + " with " + hashMechanism);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Ensure key safe for using with memcache.
	 * 
	 * @param key the key to check
	 * @param isLock indicates the key is a lock
	 * 
	 * @return the key to use (either original or modified)
	 */
	protected String ensureKeySafe(String key, boolean isLock) {
		String encodedKey = null;
		// memcached uses encoded keys, so we need to ensure that we calculate similarly		
		try {
			encodedKey = URLEncoder.encode(key, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Problem URL Encoding Key");
			e.printStackTrace();
		}
		if (encodedKey.length() > maxKeyLength) {
			StringBuilder newKey = new StringBuilder(64);
			if (isLock)
				newKey.append(LOCK_PREFIX);
			newKey.append(COMPRESSED_PREFIX);
			newKey.append(hashKey(key, "SHA-512"));
			key = newKey.toString();
		}
		
		return key;
	}
	
	public Object get(String key) {
		StringBuilder keyBuilder = new StringBuilder(32);
		keyBuilder.append(keyPrefix).append(key);
		
		key = keyBuilder.toString();
		CacheContainer container = (CacheContainer)mc.get(ensureKeySafe(keyBuilder.toString(), false));
		return (container == null) ? null : container.getObject(key);
	}

	/**
	 * Send whatever needs to be cached (key, value)
	 * The data is expired after the configured time
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value) {
		StringBuilder keyBuilder = new StringBuilder(32);
		keyBuilder.append(keyPrefix).append(key);
		
		Date now = new Date();
		Date expiresAt = new Date(now.getTime() + expireMs.longValue());

		if (logger.isDebugEnabled())
			logger.debug("Creating a Memcache entry: " + keyBuilder.toString() + " Expiring at " + expiresAt.toString());

		key = keyBuilder.toString();
		mc.set(ensureKeySafe(key, false), new CacheContainer(key, value), expiresAt);
	}

	/**
	 * Send whatever needs to be cached (key, value)
	 * The data is NOT expired
	 *
	 * @param key
	 * @param value
	 */
	public void setNoExpires(String key, Object value) {
		StringBuilder keyBuilder = new StringBuilder(32);
		keyBuilder.append(keyPrefix).append(key);

		if (logger.isDebugEnabled())
			logger.debug("Creating a Memcache entry: " + keyBuilder.toString() + " NEVER Expiring.");

		key = keyBuilder.toString();
		mc.set(ensureKeySafe(key, false), new CacheContainer(key, value));
	}

	// this returns the key validator (or null when can't grab lock)
	// to be release lock upon completion
	// This prevents other threads from removing the lock
	public Long setWriteLock(String key) {
		StringBuilder keyBuilder = new StringBuilder(32);
		keyBuilder.append(LOCK_PREFIX).append(keyPrefix).append(key);
		String cacheKey = keyBuilder.toString();
		Date now = new Date();
		Long releaseTime = now.getTime() + writeLockHoldMs.longValue();
		Date expiresAt = new Date(releaseTime);		
		boolean added = mc.add(ensureKeySafe(cacheKey, true), new CacheContainer(cacheKey, releaseTime), expiresAt);
		if (added) {
			if (logger.isDebugEnabled())
				 logger.debug("Got a lock on " +  cacheKey);
			return releaseTime;
		} else {
			if (logger.isDebugEnabled())
				logger.debug("Failed to get a lock on " +  cacheKey);
			return null;
		}
	}
	
	// the caller must know the validator (returned on setWriteLock) 
	// before being able to remove the lock.
	public boolean removeWriteLock(String key, long validator) {
		StringBuilder keyBuilder = new StringBuilder(32);
		keyBuilder.append(LOCK_PREFIX).append(keyPrefix).append(key);
		String cacheKey = keyBuilder.toString();
		if (logger.isDebugEnabled())
			logger.debug("Releasing the lock on " +  cacheKey);

		// only someone who knows the validator can remove the lock
		String safeKey = ensureKeySafe(cacheKey, true);
		CacheContainer container = (CacheContainer)mc.get(safeKey);
		Long creatorValidator = null;
		if (container != null) {
			creatorValidator = (Long)container.getObject(cacheKey);	
		}
		 
		if (creatorValidator != null) {
			if  (creatorValidator.longValue() == validator) {
				boolean released = mc.delete(safeKey);
				if (!released)
					logger.warn("Attempt to delete lock failed (likely edge case)" + key);
				return true;
			} else 
				logger.warn("Invalid State: Do not own the lock being released");
		} else
			logger.warn("Invalid State: Write Lock with null key");
		
		return false;
	}
	
	// write locks should be held for as short a period of time as possible
	public void setWriteLockHoldMs(Long writeLockHoldMs) {
		this.writeLockHoldMs = writeLockHoldMs;
	}	
	
	// prefix all keys so they don't collide
	public void setKeyPrefix(String keyPrefix) {
		// ensure the keyPrefix ends with a colon(:)
		if (!keyPrefix.endsWith(":"))
			keyPrefix += ":";
		// for safety, ensure keyPrefix isn't LOCK_PREFIX
		if (keyPrefix != LOCK_PREFIX)
			this.keyPrefix = keyPrefix;
	}

	// time request should expire in
	public void setExpireMs(Long expireMs) {
		this.expireMs = expireMs;
	}
}
