package com.aol.webservice_base.persistence.support;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;

import com.aol.webservice_base.persistence.AbstractSTP;
import com.aol.webservice_base.persistence.PersistenceException;
import com.aol.webservice_base.state.RequestState;

public class TstParamOrder extends AbstractSTP {

	public Boolean execute(RequestState state, int i1, int i2) throws PersistenceException {		
		// query 1
		LinkedHashMap<String, ValueInfo> inParams1 = new LinkedHashMap<String, ValueInfo>();
		inParams1.put("in_i1", new ValueInfo(i1, Types.INTEGER));
		inParams1.put("in_i2", new ValueInfo(i2, Types.INTEGER));		
		LinkedHashMap<String, Integer> outParams1 = new LinkedHashMap<String, Integer>();
		outParams1.put("subed", Types.INTEGER);		
		Integer res1 = (Integer)this.doRequest(state, inParams1, outParams1);
		
		// query 2 (flip the parameter order, but keep same values)
		LinkedHashMap<String, ValueInfo> inParams2 = new LinkedHashMap<String, ValueInfo>();
		inParams2.put("in_i2", new ValueInfo(i1, Types.INTEGER));
		inParams2.put("in_i1", new ValueInfo(i2, Types.INTEGER));
		LinkedHashMap<String, Integer> outParams2 = new LinkedHashMap<String, Integer>();
		outParams2.put("subed", Types.INTEGER);
		Integer res2 = (Integer)this.doRequest(state, inParams2, outParams2);
		
		// ensure the results are different
		return (res1 != res2);
	}

	@Override
	protected Integer parseResponse(CallableStatement statement) {
		try {
			return statement.getInt("subed");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
