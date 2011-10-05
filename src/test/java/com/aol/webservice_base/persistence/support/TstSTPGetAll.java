/**
 * 
 */
package com.aol.webservice_base.persistence.support;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.aol.webservice_base.persistence.AbstractSTP;
import com.aol.webservice_base.persistence.PersistenceException;
import com.aol.webservice_base.state.RequestState;

/**
 * @author human
 *
 */
public class TstSTPGetAll extends AbstractSTP {

	@SuppressWarnings("unchecked")
	public ArrayList<TstData> execute(RequestState state) throws PersistenceException {
		return (ArrayList<TstData>)doRequest(state);
	}
	
	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.AbstractSTP#parseResponse(java.sql.ResultSet)
	 */
	@Override
	protected Object parseResponse(CallableStatement statement) {
		ArrayList<TstData> dataSet = new ArrayList<TstData>();
		
		try {
			ResultSet rs = statement.getResultSet();
			while (rs.next()) {
				TstData data = new TstData();
				data.setI(rs.getInt("i"));
				data.setTi(rs.getInt("ti"));
				data.setBool(rs.getBoolean("bool"));
				data.setString(rs.getString("string"));
				data.setTime(rs.getTimestamp("time"));
				dataSet.add(data);
			}
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return dataSet;
	}

}
