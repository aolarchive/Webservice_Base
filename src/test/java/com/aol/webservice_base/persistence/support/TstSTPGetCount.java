/**
 * 
 */
package com.aol.webservice_base.persistence.support;

import java.sql.CallableStatement;
import java.sql.ResultSet;

import com.aol.webservice_base.persistence.AbstractSTP;
import com.aol.webservice_base.persistence.PersistenceException;
import com.aol.webservice_base.state.RequestState;

/**
 * @author human
 *
 */
public class TstSTPGetCount extends AbstractSTP {

	public Integer execute(RequestState state) throws PersistenceException {
		return (Integer)this.doRequest(state);
	}
		
	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.AbstractSTP#parseResponse(java.sql.ResultSet)
	 */
	@Override
	protected Object parseResponse(CallableStatement statement) {
		try {
			ResultSet rs = statement.getResultSet();
			if (rs.next())
				return Integer.valueOf(rs.getInt(1));
			else
				throw new PersistenceException("Expected response did not exist");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	
}
