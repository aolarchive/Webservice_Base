/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.persistence.tools;

import com.aol.webservice_base.persistence.PersistenceException;
import com.aol.webservice_base.persistence.ShardedJDBCConnectionManager;

public class BucketFinder {
	public static void main(String[] args) {
		System.out.println("Bucket ID Finder");
		if (args.length != 1) {
			System.out.println("You must provide a single parameter for which to generate a bucket number");
			System.out.println();
			return;
		}
		
		ShardedJDBCConnectionManager shardedMgr = new ShardedJDBCConnectionManager();
		System.out.println("Your value: '" + args[0] + "'");
		try {
			System.out.println("has bucket: " + shardedMgr.bucketize(args[0]));
		} catch (PersistenceException e) {
			System.out.println("** ERROR Processing");
		}
		System.out.println();
	}
}
