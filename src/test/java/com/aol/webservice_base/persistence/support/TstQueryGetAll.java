/**
 * 
 */
package com.aol.webservice_base.persistence.support;

import java.util.List;

import com.aol.webservice_base.persistence.AbstractQuery;

/**
 * @author human
 *
 */
public class TstQueryGetAll extends AbstractQuery {

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.AbstractQuery#parseResponse(java.sql.ResultSet)
	 */
	@Override
	protected Object parseResponse(List<Object> objects) {
		return objects;
	}

}
