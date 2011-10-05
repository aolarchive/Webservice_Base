/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.persistence;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

import com.aol.webservice_base.util.reflection.ReflectionHelper;

public class AbstractBaseQuery {
	/** The db connection mgr. */
	protected IDBConnectionManager dbConnectionMgr;		

	/** The bucket member (if the bucket value is known) - method must return Integer */
	protected String bucket = null;

	/** The bucketizer (calculates bucket based on specified member) */
	protected String bucketizer = null;

	/** The allow duplicates. */
	protected boolean allowDuplicates = false;
	
	/**
	 * Gets the connection.
	 *
	 * @param object the object
	 * @return the connection
	 * @throws PersistenceException the persistence exception
	 * @throws SecurityException the security exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	protected Connection getConnection(Object object) throws PersistenceException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Integer bucket = null;
		if (dbConnectionMgr.isBucketizerRequired()) {
			bucket = bucketize(object);
			if (bucket == null) {
				throw new PersistenceException("Expected bucket and got none for " + object);
			}
		}
		return dbConnectionMgr.getConnection(bucket);
	}
	
	/**
	 * Bucketize.
	 * 
	 * @param object the object
	 * 
	 * @return the int
	 * 
	 * @throws SecurityException the security exception
	 * @throws NoSuchMethodException the no such method exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws PersistenceException the persistence exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	public final Integer bucketize(Object object) throws SecurityException, NoSuchMethodException, IllegalArgumentException, PersistenceException, IllegalAccessException, InvocationTargetException {
		if (object != null) {
			if (bucket != null) {
				// Ensure both bucket & bucketizer not specified
				if (bucketizer != null)
					throw new PersistenceException("Both bucket & bucketizer specified for class: " + object.getClass().getName());

				Method bucketMethod = object.getClass().getMethod(bucket);				
				if (bucketMethod != null) {
					return (Integer)bucketMethod.invoke(object);
				} else {		
					throw new PersistenceException("Unable to get bucket object of class: " + object.getClass().getName());
				}
			} else if (bucketizer != null) {
				Method bucketizerMethod = object.getClass().getMethod(bucketizer);				
				if (bucketizerMethod != null) {
					return dbConnectionMgr.bucketize(bucketizerMethod.invoke(object));
				} else {		
					throw new PersistenceException("Unable to bucketize object of class: " + object.getClass().getName());
				}
			} else {
				return dbConnectionMgr.bucketize(object);
			}
		}
		return null;
	}	

	/**
	 * Sets the db connection mgr.
	 * 
	 * @param dbConnectionMgr the new db connection mgr
	 */
	public final void setDbConnectionMgr(IDBConnectionManager dbConnectionMgr) {
		this.dbConnectionMgr = dbConnectionMgr;
	}

	/**
	 * Sets the bucket member.
	 * This method is used to determine what member to use as bucket
	 * This method must exist on the object being passed in.
	 * Cannot be used with "bucketizer"
	 *  
	 * @param bucket the bucket member
	 */
	public final void setBucket(String bucket) {
		this.bucket =  ReflectionHelper.getGetter(bucket);
	}

	/**
	 * Sets the bucketizer member.
	 * This method is used to determine what data to bucket off of
	 * This method must exist on the object being passed in.
	 * Cannot be used with "bucket"
	 * 
	 * @param bucketizer the bucketizer
	 */
	public final void setBucketizer(String bucketizer) {
		this.bucketizer =  ReflectionHelper.getGetter(bucketizer);
	}
	
	/**
	 * Sets the allow duplicates.
	 * 
	 * @param allowDuplicates the new allow duplicates
	 */
	public final void setAllowDuplicates(boolean allowDuplicates) {
		this.allowDuplicates = allowDuplicates;
	}
}
