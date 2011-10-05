/**
 * 
 */
package com.aol.webservice_base.persistence.support;

import java.sql.CallableStatement;
import java.sql.Types;
import java.util.Date;
import java.util.TreeMap;

import com.aol.webservice_base.persistence.AbstractSTP;
import com.aol.webservice_base.persistence.PersistenceException;
import com.aol.webservice_base.state.RequestState;

/**
 * @author human
 *
 */
public class TstSTPAdd extends AbstractSTP {
	public Object execute(RequestState state, boolean bool, String str, int tiny, int integer, Date date) throws PersistenceException {
		TreeMap<String, ValueInfo> map = new TreeMap<String, ValueInfo>();
		map.put("in_bool", new ValueInfo(bool, Types.BOOLEAN));
		map.put("in_string", new ValueInfo(str, Types.VARCHAR));
		map.put("in_ti", new ValueInfo(tiny, Types.TINYINT));
		map.put("in_i", new ValueInfo(integer, Types.INTEGER));
		map.put("in_time", new ValueInfo(date, Types.DATE));		
		return this.doRequest(state, map);
	}

	public Object executeOutOfOrder(RequestState state, boolean bool, String str, int tiny, int integer, Date date) throws PersistenceException {
		TreeMap<String, ValueInfo> map = new TreeMap<String, ValueInfo>();
		map.put("in_time", new ValueInfo(date, Types.DATE));
		map.put("in_i", new ValueInfo(integer, Types.INTEGER));
		map.put("in_ti", new ValueInfo(tiny, Types.TINYINT));		
		map.put("in_string", new ValueInfo(str, Types.VARCHAR));
		map.put("in_bool", new ValueInfo(bool, Types.BOOLEAN));		
		return this.doRequest(state, map);
	}	
	
	public Object executePartial(RequestState state, boolean bool, String str, int tiny, int integer, Date date) throws PersistenceException {
		TreeMap<String, ValueInfo> map = new TreeMap<String, ValueInfo>();
		map.put("in_bool", new ValueInfo(bool, Types.BOOLEAN));
		return this.doRequest(state, map);
	}	

	public Object executePartialSkip(RequestState state, boolean bool, String str, int tiny, int integer, Date date) throws PersistenceException {
		TreeMap<String, ValueInfo> map = new TreeMap<String, ValueInfo>();
		map.put("in_i", new ValueInfo(integer, Types.INTEGER));
		return this.doRequest(state, map);
	}		
	
	public Object executeNoData(RequestState state) throws PersistenceException {
		return this.doRequest(state);
	}	

	public Object executeNoData2(RequestState state) throws PersistenceException {
		return this.doRequest(state, null);
	}	
	
	public Object executeUnhandledType(RequestState state, boolean bool, String str, int tiny, int integer, Date date) throws PersistenceException {
		TreeMap<String, ValueInfo> map = new TreeMap<String, ValueInfo>();
		map.put("in_bool", new ValueInfo(bool, Types.BINARY));
		return this.doRequest(state, map);
	}	
	
	@Override
	protected Object parseResponse(CallableStatement statement) {		
		return null;
	}
}
