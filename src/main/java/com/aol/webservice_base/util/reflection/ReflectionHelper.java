/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.util.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author human
 *
 */
public class ReflectionHelper {
	public static String getGetter(String varName) {
		StringBuilder funcName = new StringBuilder(32);
		funcName.append("get").append(varName.substring(0,1).toUpperCase()).append(varName.substring(1));
		return funcName.toString();
	}
	
	public static String getSetter(String varName) {
		StringBuilder funcName = new StringBuilder(32);
		funcName.append("set").append(varName.substring(0,1).toUpperCase()).append(varName.substring(1));
		return funcName.toString();
	}
	
	public static List<Method> getSetterMethods(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		ArrayList<Method> setterMethods = new ArrayList<Method>(methods.length);
		
		for (Method method: methods) {
			// remove: 
			// non-public methods
			// methods with not-1 parameters
			// methods that do not begin with the "set" convention
			if (!Modifier.isPublic(method.getModifiers()) ||
				 (method.getParameterTypes().length != 1) ||
				 !method.getName().startsWith("set")) {
				continue;
			}
			
			setterMethods.add(method);
		}
		
		return Collections.unmodifiableList(setterMethods);		
	}
}
