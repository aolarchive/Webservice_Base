/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.cache;

import java.io.Serializable;

/**
 * @author human
 *
 */
class CacheContainer implements Serializable {
	protected String key;
	protected Object object;
	
	public CacheContainer(String key, Object object) {
		this.key = key;
		this.object = object;
	}
	
	public Object getObject(String key) {
		if (this.key.equals(key)) {
			return object;
		} else 
			return null;
	}
}
