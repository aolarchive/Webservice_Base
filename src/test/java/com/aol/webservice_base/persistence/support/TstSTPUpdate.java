/**
 * 
 */
package com.aol.webservice_base.persistence.support;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.TreeMap;

import com.aol.webservice_base.persistence.AbstractSTP;
import com.aol.webservice_base.persistence.PersistenceException;
import com.aol.webservice_base.state.RequestState;

/**
 * @author human
 *
 */
public class TstSTPUpdate extends AbstractSTP {
	public void execute(RequestState state, boolean b) throws PersistenceException {
		TreeMap<String, ValueInfo> map = new TreeMap<String, ValueInfo>();
		map.put("in_bool", new ValueInfo(b, Types.BOOLEAN));		
		this.doRequest(state, map);
	}
	
	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.AbstractSTP#parseResponse(java.sql.ResultSet)
	 */
	@Override
	protected Object parseResponse(CallableStatement statement) {
		// TODO Auto-generated method stub
		return null;
	}

}
