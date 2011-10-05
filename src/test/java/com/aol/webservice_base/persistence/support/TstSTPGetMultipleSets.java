package com.aol.webservice_base.persistence.support;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import com.aol.webservice_base.persistence.AbstractSTP;
import com.aol.webservice_base.persistence.PersistenceException;
import com.aol.webservice_base.state.RequestState;

public class TstSTPGetMultipleSets extends AbstractSTP {

	@SuppressWarnings("unchecked")
	public Integer execute(RequestState state) throws PersistenceException {
		return (Integer)doRequest(state);
	}

	/* (non-Javadoc)
	 * @see com.aol.webservice_base.persistence.AbstractSTP#parseResponse(java.sql.ResultSet)
	 */
	@Override
	protected Object parseResponse(CallableStatement statement) {
		HashSet<String> gotTypes = new HashSet<String>();
		try {
			boolean hasMore = (statement.getResultSet() != null);
			while (hasMore) {				
				//Retrieve data from the result set.
				ResultSet rs = statement.getResultSet();
				while (rs.next()) {
					TstData data = new TstData();
					try {						
						Boolean bool = rs.getBoolean("bool");						
						gotTypes.add("bool");
					} catch (Exception e) {}
					try {						
						String string = rs.getString("string");						
						gotTypes.add("string");
					} catch (Exception e) {}
				}
				rs.close();

				//Check for next result set
				hasMore = statement.getMoreResults();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		System.out.println("GOT " + gotTypes.size() + " " + gotTypes);
		return gotTypes.size();
	}
}