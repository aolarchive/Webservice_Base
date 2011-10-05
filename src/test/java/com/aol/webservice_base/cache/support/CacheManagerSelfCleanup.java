/**
 * 
 */
package com.aol.webservice_base.cache.support;

import java.util.ArrayList;

import com.aol.webservice_base.cache.CacheManager;

/**
 * @author human
 *
 */
public class CacheManagerSelfCleanup extends CacheManager {
	ArrayList<String> keysInCache = new ArrayList<String>();
	@Override
	public void set(String key, Object value) {
		// We duplicate the generation of the key for tracking - oh well
		StringBuilder keyBuilder = new StringBuilder(32);
		keyBuilder.append(keyPrefix).append(key);
		String cacheKey = keyBuilder.toString();
		keysInCache.add(cacheKey);

		super.set(key, value);
	}
	
	@Override
	public void setNoExpires(String key, Object value) {
		// We duplicate the generation of the key for tracking - oh well
		StringBuilder keyBuilder = new StringBuilder(32);
		keyBuilder.append(keyPrefix).append(key);
		String cacheKey = keyBuilder.toString();
		keysInCache.add(cacheKey);

		super.setNoExpires(key, value);
	}
	
	@Override
	public Long setWriteLock(String key) {
		// We duplicate the generation of the key for tracking - oh well
		StringBuilder keyBuilder = new StringBuilder(32);
		keyBuilder.append(LOCK_PREFIX).append(keyPrefix).append(key);
		String cacheKey = keyBuilder.toString();
		keysInCache.add(cacheKey);

		return super.setWriteLock(key);
	}
	
	public void cleanup() {
		for (String key: keysInCache) {
			// delete the key as is...  Don't use delete as we've kept things in here with
			// the prefixes
			mc.delete(ensureKeySafe(key, false));
		}
		keysInCache.clear();
	}
}
