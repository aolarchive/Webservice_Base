/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.view.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ObjectUtils {
	public static List<Field> getAll(Object pThis)
	{
		if (pThis == null) return null;
/*		if (logger.isDebugEnabled())
			logger.debug("Getting all fields for " + pThis.getClass());
*/
		List<Field> objResult = new ArrayList<Field>();
		Field objEnums[] = pThis.getClass().getFields();
		for (int i = 0; i < objEnums.length; i++) 
		{
			Field objEnum = objEnums[i];

			int iModifier = objEnum.getModifiers();
			/* Do not include static and private members.. 
			 * there is an 'id' which we do not want to use here 
			 */
			if (Modifier.isStatic(iModifier) || Modifier.isPrivate(iModifier))
				continue;

			objResult.add(objEnum);
		}

/*		if (logger.isDebugEnabled())
			logger.debug("Found " + objResult.size() + " fields in the object");
*/
		return objResult;
	}

}
