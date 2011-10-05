/**
 * 
 */
package com.aol.webservice_base.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.aol.webservice_base.persistence.database.MysqlDatabaseHelper;
import com.aol.webservice_base.persistence.helper.QueryParameter;
import com.aol.webservice_base.persistence.helper.ResponsePopulator;
import com.aol.webservice_base.persistence.support.TstData;
import com.aol.webservice_base.persistence.support.TstDataNotAll;
import com.aol.webservice_base.persistence.support.TstDataWrongType;
import com.aol.webservice_base.persistence.support.TstQueryAdd;
import com.aol.webservice_base.persistence.support.TstQueryGetAll;
import com.aol.webservice_base.persistence.support.TstQueryGetCount;
import com.aol.webservice_base.persistence.support.TstQueryUpdate;
import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;

/**
 * @author human
 *
 */
public class QueryTst {
	JDBCConnectionManager manager;
	
	RequestState state;
	
	ArrayList<QueryParameter> params;	
	
	@Before
	public void init() throws SQLException {		
		// we can assume this works b/c of JDBCConnectionManagerTest validation
		manager = new JDBCConnectionManager();
		manager.setMinConnections(1);
		manager.setMaxConnections(2);
		manager.setExpireConnectionMs(2500L);
		
		MysqlDatabaseHelper helper = new MysqlDatabaseHelper();
		helper.setEncoding("utf8");
		helper.setUser("test_admin");
		helper.setPassword("admin");
		helper.setHost("localhost");
		helper.setPort(3306);
		helper.setLoginTimeoutSec(5);
		helper.setDbName("test");	
		helper.init();
		manager.setDbHelper(helper);
		
		manager.init();
		
		state = new RequestState(null);
		
		params = new ArrayList<QueryParameter>();
	}

	protected QueryParameter createParameter(String type, String member) {
		QueryParameter parameter = new QueryParameter();
		parameter.setType(type);
		parameter.setMember(member);
		return parameter;
	}
	
	@Test 
	public void insertRow() throws PersistenceException {
		TstQueryAdd query = new TstQueryAdd();		
		query.setDbConnectionMgr(manager);

		query.setQuery("insert into test (bool, string, ti, i, time) VALUES (?, ?, ?, ?, ?)");
		query.setAllowDuplicates(true);
		params.add(createParameter("bool", "bool"));
		params.add(createParameter("varchar", "string"));
		params.add(createParameter("int", "ti"));
		params.add(createParameter("int", "i"));
		params.add(createParameter("timestamp", "time"));
		query.setParameters(params);
		
		TstData object = new TstData(true, "string", 1, 11, new Date()); 
		query.execute(state, object);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
	}

	@Test 
	public void insertRowOutOfOrderFails() throws PersistenceException {
		TstQueryAdd query = new TstQueryAdd();		
		query.setDbConnectionMgr(manager);

		query.setQuery("insert into test (bool, string, ti, i, time) VALUES (?, ?, ?, ?, ?)");
		query.setAllowDuplicates(true);
		params.add(createParameter("timestamp", "time"));
		params.add(createParameter("bool", "bool"));
		params.add(createParameter("varchar", "string"));
		params.add(createParameter("int", "ti"));
		params.add(createParameter("int", "i"));
		query.setParameters(params);
		
		TstData object = new TstData(true, "string", 1, 11, new Date()); 
		query.execute(state, object);
		Assert.assertEquals(Constants.SC_INTERNAL_SERVER_ERROR, state.getStatusCode());
	}

	@Test
	public void insertRowFail() throws PersistenceException {
		TstQueryAdd query = new TstQueryAdd();		
		query.setDbConnectionMgr(manager);

		query.setQuery("insert into test (bool, string, ti, i, time) VALUES (?, ?, ?, ?, ?)");
		query.setAllowDuplicates(true);
		params.add(createParameter("timestamp", "time"));
		params.add(createParameter("bool", "bool"));
		params.add(createParameter("varchar", "string"));
		params.add(createParameter("int", "ti"));
		params.add(createParameter("int", "i"));
		query.setParameters(params);
		
		TstData object = new TstData(true, "String, ", 2100000000, 11, new Date()); 
		query.execute(state, object);
		Assert.assertEquals(Constants.SC_INTERNAL_SERVER_ERROR, state.getStatusCode());
	}	

	@Test (expected = Error.class)
	public void insertRowNoDataFail() throws PersistenceException {
		TstQueryAdd query = new TstQueryAdd();		
		query.setDbConnectionMgr(manager);

		query.setQuery("insert into test (bool, string, ti, i, time) VALUES (?, ?, ?, ?, ?)");
		query.setAllowDuplicates(true);
		params.add(createParameter("timestamp", "time"));
		params.add(createParameter("bool", "bool"));
		params.add(createParameter("varchar", "string"));
		params.add(createParameter("int", "ti"));
		params.add(createParameter("int", "i"));
		query.setParameters(params);
		
		query.execute(state);
		Assert.assertEquals(Constants.SC_INTERNAL_SERVER_ERROR, state.getStatusCode());
	}		

	@Test (expected = Error.class)
	public void insertRowPartialData() throws PersistenceException {
		TstQueryAdd query = new TstQueryAdd();		
		query.setDbConnectionMgr(manager);

		query.setQuery("insert into test (bool, string, ti, i, time) VALUES (?, ?, ?, ?, ?)");
		query.setAllowDuplicates(true);
		params.add(createParameter("timestamp", "time"));
		params.add(createParameter("bool", "bool"));
		params.add(createParameter("varchar", "string"));
		params.add(createParameter("int", "ti"));
		params.add(createParameter("int", "i"));
		query.setParameters(params);
		
		TstDataNotAll object = new TstDataNotAll("string", 1, 11, new Date());
		
		query.execute(state, object);
	}	

	@Test (expected = Error.class)
	public void insertRowUnhandledType() throws PersistenceException {
		TstQueryAdd query = new TstQueryAdd();		
		query.setDbConnectionMgr(manager);

		query.setQuery("insert into test (bool, string, ti, i, time) VALUES (?, ?, ?, ?, ?)");
		query.setAllowDuplicates(true);
		params.add(createParameter("timestamp", "time"));
		params.add(createParameter("bool", "bool"));
		params.add(createParameter("varchar", "string"));
		params.add(createParameter("int", "ti"));
		params.add(createParameter("int", "i"));
		query.setParameters(params);
		
		TstDataWrongType object = new TstDataWrongType("BOOLEAN", "string", 1, 11, new Date());
		
		query.execute(state, object);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
	}			
	
	protected int getCountFromDb() throws PersistenceException {
		TstQueryGetCount query = new TstQueryGetCount();
		query.setDbConnectionMgr(manager);
		
		query.setQuery("select count(*) from test");
		query.setResponseDataClass(Integer.class.getName());
		
		Integer count = (Integer)query.execute(state);
		Assert.assertNotNull(count);
		Assert.assertTrue(count > 0);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
		System.out.println("Got count of: " + count);	
		return count;
	}
	
	@Test
	public void getCount() throws PersistenceException {
		int count = getCountFromDb();
	}

	@Test (expected = Error.class)
	public void getCountTooManyCols() throws PersistenceException {
		TstQueryGetCount query = new TstQueryGetCount();
		query.setDbConnectionMgr(manager);
		
		query.setQuery("select * from test");
		query.setResponseDataClass(Integer.class.getName());
		
		query.execute(state);
	}	
	
	protected ResponsePopulator createPopulator(String column, String typeString, String member) {
		ResponsePopulator populator = new ResponsePopulator();
		populator.setColumn(column);
		populator.setType(typeString);
		populator.setMember(member);
		return populator;
	}
	
	@SuppressWarnings("unchecked")
	@Test 
	public void getAll() throws PersistenceException {
		TstQueryGetAll query = new TstQueryGetAll();
		query.setResponseDataClass(TstData.class.getName());
		query.setDbConnectionMgr(manager);
		
		ArrayList<ResponsePopulator> responseData = new ArrayList<ResponsePopulator>();
		responseData.add(createPopulator("bool", "BOOLEAN", "bool"));
		responseData.add(createPopulator("string", "VARCHAR", "string"));
		responseData.add(createPopulator("ti", "INT", "ti"));
		responseData.add(createPopulator("time", "DATETIME", "time"));
		responseData.add(createPopulator("i", "INT", "i"));
		query.setResponsePopulation(responseData);
		
		query.setQuery("select * from test");
		
		List<TstData> data = (List<TstData>)query.execute(state);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
		System.out.println("Got data #: " + data.size());
		for (TstData item: data) {
			System.out.println(item.toString());
		}
	}

	@Test (expected = Error.class)
	public void getAllWrongTypes() throws PersistenceException {
		TstQueryGetAll query = new TstQueryGetAll();
		query.setResponseDataClass(TstData.class.getName());
		query.setDbConnectionMgr(manager);
		
		ArrayList<ResponsePopulator> responseData = new ArrayList<ResponsePopulator>();
		responseData.add(createPopulator("bool", "VARCHAR", "bool"));
		responseData.add(createPopulator("string", "BOOLEAN", "string"));
		responseData.add(createPopulator("ti", "VARCHAR", "ti"));
		responseData.add(createPopulator("time", "VARCHAR", "time"));
		responseData.add(createPopulator("i", "VARCHAR", "i"));
		query.setResponsePopulation(responseData);
		
		query.setQuery("select * from test");
		
		query.execute(state);
	}
	
	@Test (expected = Error.class)
	public void getAllNotFound() throws PersistenceException {
		TstQueryGetAll query = new TstQueryGetAll();
		query.setResponseDataClass(TstData.class.getName());
		query.setDbConnectionMgr(manager);
		
		ArrayList<ResponsePopulator> responseData = new ArrayList<ResponsePopulator>();
		responseData.add(createPopulator("boolNE", "VARCHAR", "bool"));
		responseData.add(createPopulator("stringNE", "BOOLEAN", "string"));
		responseData.add(createPopulator("tiNE", "VARCHAR", "ti"));
		responseData.add(createPopulator("timeNE", "VARCHAR", "time"));
		responseData.add(createPopulator("iNE", "VARCHAR", "i"));
		query.setResponsePopulation(responseData);
		
		query.setQuery("select * from test");
		
		query.execute(state);
	}
	
	@Test 
	public void update() throws PersistenceException {
		TstQueryUpdate query = new TstQueryUpdate();
		query.setDbConnectionMgr(manager);
		
		query.setQuery("UPDATE test SET bool = ?");
				
		params.add(createParameter("bool", "bool"));
		query.setParameters(params);
		
		TstData object = new TstData(false, "string", 1, 11, new Date());
		query.execute(state, object);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
	}
	
	@Test 
	public void rollbackTest() throws PersistenceException, SQLException {
		int beforeCount = getCountFromDb();
		
		TstQueryAdd query = new TstQueryAdd();		

		query.setQuery("insert into test (bool, string, ti, i, time) VALUES (?, ?, ?, ?, ?)");
		query.setAllowDuplicates(true);
		params.add(createParameter("bool", "bool"));
		params.add(createParameter("varchar", "string"));
		params.add(createParameter("int", "ti"));
		params.add(createParameter("int", "i"));
		params.add(createParameter("timestamp", "time"));
		query.setParameters(params);
	
		Connection connection = null;
		try {
			connection = manager.getConnection(null);
			connection.setAutoCommit(false);
			TstData object = new TstData(true, "string", 1, 11, new Date()); 
			query.executeOnConnection(state, manager, connection, object);
			Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
			connection.rollback();
		} finally {
			if (connection != null)
				manager.returnConnection(connection);
		}
		
		int afterCount = getCountFromDb();
		
		Assert.assertEquals(beforeCount, afterCount);
	}
	
	@Test 
	public void commitTest() throws PersistenceException, SQLException {
		int beforeCount = getCountFromDb();
		
		TstQueryAdd query = new TstQueryAdd();		

		query.setQuery("insert into test (bool, string, ti, i, time) VALUES (?, ?, ?, ?, ?)");
		query.setAllowDuplicates(true);
		params.add(createParameter("bool", "bool"));
		params.add(createParameter("varchar", "string"));
		params.add(createParameter("int", "ti"));
		params.add(createParameter("int", "i"));
		params.add(createParameter("timestamp", "time"));
		query.setParameters(params);
	
		Connection connection = null;
		try {
			connection = manager.getConnection(null);
			connection.setAutoCommit(false);
			TstData object = new TstData(true, "string", 1, 11, new Date()); 
			query.executeOnConnection(state, manager, connection, object);
			Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
			connection.commit();
		} finally {
			if (connection != null)
				manager.returnConnection(connection);
		}
		
		int afterCount = getCountFromDb();
		
		Assert.assertEquals(beforeCount+1, afterCount);
	}

	@Test (expected = PersistenceException.class)
	public void failedConnectionTest() throws PersistenceException, SQLException {
		TstQueryAdd query = new TstQueryAdd();		

		query.setQuery("insert into test (bool, string, ti, i, time) VALUES (?, ?, ?, ?, ?)");
		query.setAllowDuplicates(true);
		params.add(createParameter("bool", "bool"));
		params.add(createParameter("varchar", "string"));
		params.add(createParameter("int", "ti"));
		params.add(createParameter("int", "i"));
		params.add(createParameter("timestamp", "time"));
		query.setParameters(params);
	
		Connection connection = null;
		try {
			connection = manager.getConnection(null);
			connection.setAutoCommit(false);
			manager.failConnection(connection);
			TstData object = new TstData(true, "string", 1, 11, new Date()); 
			query.executeOnConnection(state, manager, connection, object);
			Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
			connection.commit();
		} finally {
			if (connection != null)
				manager.returnConnection(connection);
		}
	}
}
