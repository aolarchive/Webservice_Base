/**
 * 
 */
package com.aol.webservice_base.persistence;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.aol.webservice_base.persistence.database.MysqlDatabaseHelper;

/**
 * @author human
 *
 */
public class JDBCConnectionManagerTst {
	JDBCConnectionManager manager = null;

	@Before
	public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		manager = new JDBCConnectionManager();
		manager.setMinConnections(1);
		manager.setMaxConnections(2);
		manager.setExpireConnectionMs(1000L);
		
		MysqlDatabaseHelper helper = new MysqlDatabaseHelper(); 		
		helper.setUser("test_admin");
		helper.setPassword("admin");
		helper.setHost("localhost");
		helper.setPort(3306);
		helper.setLoginTimeoutSec(5);
		helper.setDbName("test");
		helper.init();		
		manager.setDbHelper(helper);
		// tests responsible for manager.init();		 
	}

	@Test
	public void getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, PersistenceException {
		manager.init();
		
		// sleep to allow db connections to establish
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Connection connection = manager.getConnection(null);
		Assert.assertNotNull(connection);
	}

	@Test (expected=PersistenceException.class)
	public void getFailConnection() throws SQLException, PersistenceException {
		manager.setMinConnections(0);
		manager.setMaxConnections(0);

		manager.init();
		// sleep to allow db connections to establish
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		manager.getConnection(null);
	}

	@Test
	public void getConnection2() throws SQLException, PersistenceException {
		manager.init();

		// sleep to allow db connections to establish
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Connection connection = manager.getConnection(null);
		Assert.assertNotNull(connection);
		connection = manager.getConnection(null);
		Assert.assertNotNull(connection);
	}

	@Test (expected = PersistenceException.class)
	public void getConnection3Fail() throws SQLException, PersistenceException {
		manager.init();

		// sleep to allow db connections to establish
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Connection connection = manager.getConnection(null);
		Assert.assertNotNull(connection);
		connection = manager.getConnection(null);
		Assert.assertNotNull(connection);
		manager.getConnection(null);
	}	

	@Test
	public void getConnection3Returned() throws SQLException, PersistenceException {
		manager.init();
		
		// sleep to allow db connections to establish
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Connection connection = manager.getConnection(null);
		Assert.assertNotNull(connection);
		connection = manager.getConnection(null);
		Assert.assertNotNull(connection);
		manager.returnConnection(connection);
		connection = manager.getConnection(null);
		Assert.assertNotNull(connection);
	}	

	@Test
	public void getConnectionExpiry() throws SQLException, PersistenceException {
		manager.setMaxConnections(1);
		manager.init();

		// sleep to allow db connections to establish
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Connection connection1 = manager.getConnection(null);
		Assert.assertNotNull(connection1);
		manager.returnConnection(connection1);
		Connection connection1a = manager.getConnection(null);
		Assert.assertNotNull(connection1a);
		Assert.assertSame(connection1, connection1a);
		manager.returnConnection(connection1a);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Connection connection2 = manager.getConnection(null);
		Assert.assertNotNull(connection2);
		Assert.assertNotSame(connection1, connection2);
	}	

	@Test
	public void getConnectionStale() throws SQLException, PersistenceException {
		manager.setExpireConnectionMs(null);
		manager.setStaleConnectionMs(1000L);
		manager.setMaxConnections(1);
		manager.init();

		// sleep to allow db connections to establish
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Connection connection1 = manager.getConnection(null);
		Assert.assertNotNull(connection1);
		manager.returnConnection(connection1);
		Connection connection1a = manager.getConnection(null);
		Assert.assertNotNull(connection1a);
		Assert.assertSame(connection1, connection1a);
		manager.returnConnection(connection1a);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Connection connection2 = manager.getConnection(null);
		Assert.assertNotNull(connection2);
		Assert.assertNotSame(connection1, connection2);
	}
	
	@Test
	public void getConnectionExpiredNotStale() throws SQLException, PersistenceException {
		manager.setStaleConnectionMs(750L);
		manager.setMaxConnections(1);
		manager.init();

		// sleep to allow db connections to establish
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Connection connection1 = manager.getConnection(null);
		Assert.assertNotNull(connection1);
		manager.returnConnection(connection1);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Connection connection1a = manager.getConnection(null);
		Assert.assertNotNull(connection1a);
		Assert.assertSame(connection1, connection1a);
		manager.returnConnection(connection1a);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Connection connection2 = manager.getConnection(null);
		Assert.assertNotNull(connection2);
		Assert.assertNotSame(connection1, connection2);
	}		
}
