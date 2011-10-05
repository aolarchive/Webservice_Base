/**
 * 
 */
package com.aol.webservice_base.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.aol.webservice_base.persistence.database.MysqlDatabaseHelper;
import com.aol.webservice_base.persistence.support.TstData;
import com.aol.webservice_base.persistence.support.TstParamOrder;
import com.aol.webservice_base.persistence.support.TstSTPAdd;
import com.aol.webservice_base.persistence.support.TstSTPGetAll;
import com.aol.webservice_base.persistence.support.TstSTPGetCount;
import com.aol.webservice_base.persistence.support.TstSTPGetMultipleSets;
import com.aol.webservice_base.persistence.support.TstSTPUpdate;
import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;

/**
 * @author human
 *
 */
public class STPTst {
	JDBCConnectionManager manager;
	
	RequestState state;
	
	@Before
	public void init() throws SQLException {		
		// we can assume this works b/c of JDBCConnectionManagerTest validation
		manager = new JDBCConnectionManager();
		manager.setMinConnections(1);
		manager.setMaxConnections(2);
		manager.setExpireConnectionMs(2500L);

		MysqlDatabaseHelper helper = new MysqlDatabaseHelper();
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
	}
	
	@Test 
	public void insertRow() throws PersistenceException {
		TstSTPAdd query = new TstSTPAdd();
		query.setStp("sp_add_test");
		query.setDbConnectionMgr(manager);
		byte b = 1;
		query.execute(state, true, "String", b, 11, new Date());
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
	}

	@Test 
	public void insertRowOutOfOrder() throws PersistenceException {
		TstSTPAdd query = new TstSTPAdd();
		query.setStp("sp_add_test");
		query.setDbConnectionMgr(manager);
		byte b = 1;
		query.executeOutOfOrder(state, true, "String", b, 11, new Date());
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
	}

	@Test
	public void insertRowFail() throws PersistenceException {
		TstSTPAdd query = new TstSTPAdd();
		query.setStp("sp_add_test");
		query.setDbConnectionMgr(manager);
		int b = 2100000000;
		query.execute(state, true, "String", b, 11, new Date());
		Assert.assertEquals(Constants.SC_INTERNAL_SERVER_ERROR, state.getStatusCode());
	}	

	@Test
	public void insertRowNoDataFail() throws PersistenceException {
		TstSTPAdd query = new TstSTPAdd();
		query.setStp("sp_add_test");
		query.setDbConnectionMgr(manager);
		query.executeNoData(state);
		Assert.assertEquals(Constants.SC_INTERNAL_SERVER_ERROR, state.getStatusCode());
	}		

	@Test 
	public void insertRowPartialData() throws PersistenceException {
		TstSTPAdd query = new TstSTPAdd();
		query.setStp("sp_add_test");
		query.setDbConnectionMgr(manager);
		byte b = 1;
		query.executePartial(state, true, "String", b, 11, new Date());
		Assert.assertEquals(Constants.SC_INTERNAL_SERVER_ERROR, state.getStatusCode());
	}	

	@Test 
	public void insertRowPartialDataSkip() throws PersistenceException {
		TstSTPAdd query = new TstSTPAdd();
		query.setStp("sp_add_test");
		query.setDbConnectionMgr(manager);
		byte b = 1;
		query.executePartialSkip(state, true, "String", b, 11, new Date());
		Assert.assertEquals(Constants.SC_INTERNAL_SERVER_ERROR, state.getStatusCode());
	}		

	@Test (expected = PersistenceException.class)
	public void insertRowUnhandledType() throws PersistenceException {
		TstSTPAdd query = new TstSTPAdd();
		query.setStp("sp_add_test");
		query.setDbConnectionMgr(manager);
		byte b = 1;
		query.executeUnhandledType(state, true, "String", b, 11, new Date());
		Assert.assertEquals(Constants.SC_INTERNAL_SERVER_ERROR, state.getStatusCode());
	}			
	
	@Test 
	public void getCount() throws PersistenceException {
		TstSTPGetCount query = new TstSTPGetCount();
		query.setStp("sp_get_count");
		query.setDbConnectionMgr(manager);
		Integer count = query.execute(state);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
		System.out.println("Got count of: " + count);
	}

	@Test 
	public void getAll() throws PersistenceException {
		TstSTPGetAll query = new TstSTPGetAll();
		query.setStp("sp_get_all");
		query.setDbConnectionMgr(manager);
		ArrayList<TstData> data = query.execute(state);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
		System.out.println("Got data #: " + data.size());
		for (TstData item: data) {
			System.out.println(item.toString());
		}
	}

	@Test 
	public void getMultipleSets() throws PersistenceException {
		TstSTPGetMultipleSets query = new TstSTPGetMultipleSets();
		query.setStp("sp_get_diff_columns_two_sets");
		query.setDbConnectionMgr(manager);
		Integer dataSize = query.execute(state);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
		System.out.println("Got data #: " + dataSize);
		Assert.assertTrue(dataSize > 1);
	}
	
	@Test 
	public void update() throws PersistenceException {
		TstSTPUpdate query = new TstSTPUpdate();
		query.setStp("sp_update_test");
		query.setDbConnectionMgr(manager);
		query.execute(state, false);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());		
	}

	@Test 
	public void testParamOrder() throws PersistenceException {
		TstParamOrder query = new TstParamOrder();
		query.setStp("sp_math_subtract");
		query.setDbConnectionMgr(manager);
		query.execute(state, 5, 10);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());		
	}
}
