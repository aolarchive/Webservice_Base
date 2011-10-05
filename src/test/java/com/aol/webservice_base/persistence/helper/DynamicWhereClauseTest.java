/**
 * 
 */
package com.aol.webservice_base.persistence.helper;

import junit.framework.Assert;

import org.junit.Test;

import com.aol.webservice_base.persistence.PersistenceException;

/**
 * @author human
 *
 */
public class DynamicWhereClauseTest {
	
	@Test
	public void simple() throws PersistenceException {
		DynamicWhereClauseFactory dynWCF = new DynamicWhereClauseFactory();
		dynWCF.setAlterWhereClause(1);
		dynWCF.setExpectedWhereCount(1);
		DynamicWhereClause dynWC = dynWCF.getInstance();
		String query = "SELECT * FROM TABLE";
		dynWC.addItem("x", "=", 3);
		String updated = dynWC.injectWhere(query);
		String expected = query + " WHERE x=3";
		Assert.assertEquals(expected, updated);
	}

	@Test
	public void simpleAppend() throws PersistenceException {
		DynamicWhereClauseFactory dynWCF = new DynamicWhereClauseFactory();
		dynWCF.setAlterWhereClause(1);
		dynWCF.setExpectedWhereCount(1);
		DynamicWhereClause dynWC = dynWCF.getInstance();
		String query = "SELECT * FROM TABLE WHERE y=96";
		dynWC.addItem("x", "=", 3);
		String updated = dynWC.injectWhere(query);
		String expected = "SELECT * FROM TABLE WHERE x=3 AND y=96";
		Assert.assertEquals(expected, updated);
	}

	@Test (expected = PersistenceException.class)
	public void simpleNotFound() throws PersistenceException {
		DynamicWhereClauseFactory dynWCF = new DynamicWhereClauseFactory();
		dynWCF.setAlterWhereClause(2);
		dynWCF.setExpectedWhereCount(2);
		DynamicWhereClause dynWC = dynWCF.getInstance();
		String query = "SELECT * FROM TABLE";
		dynWC.addItem("x", "=", 3);
		dynWC.injectWhere(query);
	}

	@Test (expected = NullPointerException.class)
	public void simpleNotInitialized() throws PersistenceException {
		DynamicWhereClauseFactory dynWCF = new DynamicWhereClauseFactory();
		dynWCF.getInstance();
	}	
	
	@Test (expected = PersistenceException.class)
	public void simpleMixedCounts() throws PersistenceException {
		DynamicWhereClauseFactory dynWCF = new DynamicWhereClauseFactory();
		dynWCF.setAlterWhereClause(3);
		dynWCF.setExpectedWhereCount(2);
		dynWCF.getInstance();
	}

	@Test (expected = PersistenceException.class)
	public void simpleDontKnowWhere() throws PersistenceException {
		DynamicWhereClauseFactory dynWCF = new DynamicWhereClauseFactory();
		dynWCF.setAlterWhereClause(1);
		dynWCF.setExpectedWhereCount(1);
		DynamicWhereClause dynWC = dynWCF.getInstance();
		// yes, I know the SQL is invalid		
		String query = "SELECT * FROM TABLE WHERE a=1 ELSE WHERE b=2";
		dynWC.addItem("x", "=", 3);
		dynWC.injectWhere(query);
	}

	@Test
	public void simple1of2() throws PersistenceException {
		DynamicWhereClauseFactory dynWCF = new DynamicWhereClauseFactory();
		dynWCF.setAlterWhereClause(1);
		dynWCF.setExpectedWhereCount(2);
		DynamicWhereClause dynWC = dynWCF.getInstance();
		// yes, I know the SQL is invalid
		String query = "SELECT * FROM TABLE WHERE a=1 ELSE WHERE b=2";
		dynWC.addItem("x", "=", 3);
		String updated = dynWC.injectWhere(query);
		String expected = "SELECT * FROM TABLE WHERE x=3 AND a=1 ELSE WHERE b=2";
		Assert.assertEquals(expected, updated);
	}
	
	@Test
	public void simple2of2() throws PersistenceException {
		DynamicWhereClauseFactory dynWCF = new DynamicWhereClauseFactory();
		dynWCF.setAlterWhereClause(2);
		dynWCF.setExpectedWhereCount(2);
		DynamicWhereClause dynWC = dynWCF.getInstance();
		// yes, I know the SQL is invalid
		String query = "SELECT * FROM TABLE WHERE a=1 ELSE WHERE b=2";
		dynWC.addItem("x", "=", 3);
		String updated = dynWC.injectWhere(query);
		String expected = "SELECT * FROM TABLE WHERE a=1 ELSE WHERE x=3 AND b=2";
		Assert.assertEquals(expected, updated);
	}

	@Test
	public void simpleOrderBy() throws PersistenceException {
		DynamicWhereClauseFactory dynWCF = new DynamicWhereClauseFactory();
		dynWCF.setAlterWhereClause(1);
		dynWCF.setExpectedWhereCount(1);
		DynamicWhereClause dynWC = dynWCF.getInstance();
		String query = "SELECT * FROM TABLE ORDER BY C";
		dynWC.addItem("x", "=", 3);
		String updated = dynWC.injectWhere(query);
		String expected = "SELECT * FROM TABLE WHERE x=3 ORDER BY C";
		Assert.assertEquals(expected, updated);
	}

	@Test
	public void simpleOrderByWithWhere() throws PersistenceException {
		DynamicWhereClauseFactory dynWCF = new DynamicWhereClauseFactory();
		dynWCF.setAlterWhereClause(1);
		dynWCF.setExpectedWhereCount(1);
		DynamicWhereClause dynWC = dynWCF.getInstance();
		String query = "SELECT * FROM TABLE WHERE D=1 ORDER BY C";
		dynWC.addItem("x", "=", 3);
		String updated = dynWC.injectWhere(query);
		String expected = "SELECT * FROM TABLE WHERE x=3 AND D=1 ORDER BY C";
		Assert.assertEquals(expected, updated);
	}
	
	
}
