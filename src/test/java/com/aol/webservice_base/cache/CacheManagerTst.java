/**
 * 
 */
package com.aol.webservice_base.cache;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.aol.webservice_base.cache.support.CacheManagerSelfCleanup;

/**
 * @author human
 *
 */
public class CacheManagerTst {
	static final long EXPIRE_MS = 1000;
	static final long LOCK_HOLD_MS = 2000;
	static int lockCount = 0;
	String lockName;
	
	static CacheManagerSelfCleanup cm;
	
	@BeforeClass 
	public static void initCache() {
		MCHelper mcHelper = MCHelper.getInstance();
		List<String> hosts = new ArrayList<String>();
		hosts.add("localhost:11211");
		mcHelper.setHosts(hosts);
		mcHelper.init();		

		cm = new CacheManagerSelfCleanup();
		cm.setExpireMs(EXPIRE_MS);
		cm.setWriteLockHoldMs(LOCK_HOLD_MS);
		cm.setKeyPrefix("TEST");	
	}
	
	@Before
	public void setup() {		
		// determine the lock name for this test
		lockName = "data:" + lockCount++;
	}	
	
	@After
	public void cleanup() {
		cm.cleanup();
	}
	
	@Test
	public void testCache() {
		cm.set("TESTCache", Boolean.TRUE);
		Boolean result = (Boolean)cm.get("TESTCache");
		Assert.assertTrue(result);
		cm.delete("TESTCache");
	}

	@Test
	public void testVeryLargeKey() {
		StringBuilder key = new StringBuilder();
		for (int i=0; i<233; i++) {
			key.append("a");
		}
		cm.set(key.toString(), Boolean.TRUE);
		Boolean result = (Boolean)cm.get(key.toString());
		Assert.assertTrue(result);
		cm.delete(key.toString());
	}		
	
	@Test
	public void testCacheHugeKey() {
		StringBuilder key = new StringBuilder();
		for (int i=0; i<50; i++) {
			key.append("abcdefghijklmnopqrstuvwxyz");
		}
		cm.set(key.toString(), Boolean.TRUE);
		Boolean result = (Boolean)cm.get(key.toString());
		Assert.assertTrue(result);
		cm.delete(key.toString());
	}	
	
	@Test
	public void testCacheDelete() {
		testCache();
		Boolean result = (Boolean)cm.get("TESTCache");
		Assert.assertNull(result);
	}
	
	
	@Test
	public void testCacheNotFound() {
		Boolean result = (Boolean)cm.get("TESTNotFound");
		Assert.assertNull(result);
	}

	@Test
	public void testCacheTimeout() {
		cm.set("TESTTimeout", Boolean.TRUE);		
		try {
			Thread.sleep(EXPIRE_MS + 1000);
		}
		catch (InterruptedException e) {}
		Boolean result = (Boolean)cm.get("TESTTimeout");
		
		Assert.assertNull(result);
	}

	@Test
	public void testNoExpires() {
		cm.setNoExpires("TESTNoExpires", Boolean.TRUE);		
		try {
			Thread.sleep(EXPIRE_MS);
		}
		catch (InterruptedException e) {}
		Boolean result = (Boolean)cm.get("TESTNoExpires");

		Assert.assertTrue(result);
		
		cm.delete("TESTNoExpires");
	}	

	@Test
	public void testCacheNoExpiresDelete() {
		testNoExpires();				
		Boolean result = (Boolean)cm.get("TESTNoExpires");
		Assert.assertNull(result);
	}		
	
	@Test
	public void testLockUnlock() {
		Long unlockKey = cm.setWriteLock(lockName);
		Assert.assertNotNull(unlockKey);
		Assert.assertTrue(cm.removeWriteLock(lockName, unlockKey));			
	}

	@Test
	public void testLockUnlockHuge() {
		StringBuilder key = new StringBuilder();
		key.append("HUGELOCK:").append(lockCount++).append(":");
		for (int i=0; i<50; i++) {
			key.append("abcdefghijklmnopqrstuvwxyz");
		}		
		String hugeLockName = key.toString();
		Long unlockKey = cm.setWriteLock(hugeLockName);
		Assert.assertNotNull(unlockKey);
		Assert.assertTrue(cm.removeWriteLock(hugeLockName, unlockKey));			
	}	
	
	@Test
	public void testLockFail() {
		Long unlockKey = cm.setWriteLock(lockName);
		Assert.assertNotNull(unlockKey);
		Long unlockKey2 = cm.setWriteLock(lockName);
		Assert.assertNull(unlockKey2);
	}

	@Test
	public void testLockReleaseDelay() {
		Long unlockKey = cm.setWriteLock(lockName);
		Assert.assertNotNull(unlockKey);
		
		try {
			Thread.sleep(LOCK_HOLD_MS + 1000);
		} catch (Exception e) {}
		
		Assert.assertFalse(cm.removeWriteLock(lockName, unlockKey));
	}
	
	@Test
	public void testUnlockInvalid() {
		Long unlockKey = cm.setWriteLock(lockName);
		Assert.assertNotNull(unlockKey);
		
		unlockKey++;
		
		Assert.assertFalse(cm.removeWriteLock(lockName, unlockKey));
	}	
}
