/**
 * 
 */
package com.aol.webservice_base.validator;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;
import com.aol.webservice_base.support.MockHttpServletRequest;
import com.aol.webservice_base.validator.parameter.ParameterValidatorException;
import com.aol.webservice_base.validator.parameter.types.ParameterTypeString;
import com.aol.webservice_base.validator.support.MockServletRequestFacade;
import com.aol.webservice_base.validator.support.ValidatorTestBean;

/**
 * @author human
 *
 */
public class ParameterValidatorStringTest {
	protected MockServletRequestFacade mockReq;
	protected RequestState state;
	protected ValidatorTestBean requestObject;	
	protected ParameterTypeString validator;	
	
	@Before
	public void init() {
		mockReq = new MockServletRequestFacade(new MockHttpServletRequest());
		state = new RequestState(null);
		requestObject = new ValidatorTestBean();
		state.setRequestObject(requestObject);
		mockReq.setAttribute(Constants.REQUEST_STATE, state);
		validator = new ParameterTypeString();
		validator.setName("test");
		validator.setRequired(true);
		validator.setMember("stringMember");
	}
	
	@Test
	public void testAllowNullParam() throws ParameterValidatorException {
		validator.setRequired(false);
		validator.validate(mockReq, "test");
		Assert.assertEquals(null, requestObject.getStringMember());
	}	
	
	@Test (expected = ParameterValidatorException.class)
	public void testNullParam() throws ParameterValidatorException {
		validator.validate(mockReq, "test");
	}

	@Test
	public void testEmptySuccess() throws ParameterValidatorException {
		mockReq.addParameterValue("test", "");
		validator.validate(mockReq, "test");
		Assert.assertEquals("", requestObject.getStringMember());
	}		
	
	@Test 
	public void testSinglePatternMatch() throws ParameterValidatorException {
		validator.setPattern("[a-m]+");
		mockReq.addParameterValue("test", "amma");
		validator.validate(mockReq, "test");
		Assert.assertEquals("amma", requestObject.getStringMember());
	}	

	@Test (expected = ParameterValidatorException.class)
	public void testSinglePatternFail() throws ParameterValidatorException {
		validator.setPattern("[a-m]+");
		mockReq.addParameterValue("test", "nosir");
		validator.validate(mockReq, "test");
	}	

	@Test  (expected = ParameterValidatorException.class)
	public void testSinglePatternSensitive() throws ParameterValidatorException {
		validator.setPattern("[a-m]+");
		validator.setCaseInsensitive(false);
		mockReq.addParameterValue("test", "AMMA");
		validator.validate(mockReq, "test");
	}				
	
	@Test 
	public void testSinglePatternInsensitive() throws ParameterValidatorException {
		validator.setPattern("[a-m]+");
		validator.setCaseInsensitive(true);
		mockReq.addParameterValue("test", "AMMA");
		validator.validate(mockReq, "test");
		Assert.assertEquals("AMMA", requestObject.getStringMember());
	}			

	@Test 
	public void testSinglePatternInsensitive2() throws ParameterValidatorException {
		validator.setCaseInsensitive(true);
		validator.setPattern("[a-m]+");
		mockReq.addParameterValue("test", "AMMA");
		validator.validate(mockReq, "test");
		Assert.assertEquals("AMMA", requestObject.getStringMember());
	}				
	
	@Test 
	public void testMultiplePatternMatch() throws ParameterValidatorException {
		ArrayList<String> patterns = new ArrayList<String>();
		patterns.add("one");
		patterns.add("two");
		patterns.add("three");
		validator.setPatterns(patterns);
		mockReq.addParameterValue("test", "two");
		validator.validate(mockReq, "test");
		Assert.assertEquals("two", requestObject.getStringMember());
	}	

	@Test  (expected = ParameterValidatorException.class)
	public void testMultiplePatternFail() throws ParameterValidatorException {
		ArrayList<String> patterns = new ArrayList<String>();
		patterns.add("one");
		patterns.add("two");
		patterns.add("three");
		validator.setPatterns(patterns);
		mockReq.addParameterValue("test", "four");
		validator.validate(mockReq, "test");
	}
	
	@Test  (expected = ParameterValidatorException.class)
	public void testTooShort() throws ParameterValidatorException {
		validator.setMinLength(2);
		validator.setCaseInsensitive(false);
		mockReq.addParameterValue("test", "A");
		validator.validate(mockReq, "test");
	}				

	@Test  (expected = ParameterValidatorException.class)
	public void testTooLong() throws ParameterValidatorException {
		validator.setMaxLength(2);
		validator.setCaseInsensitive(false);
		mockReq.addParameterValue("test", "AAA");
		validator.validate(mockReq, "test");
	}					

	@Test
	public void testRightSize() throws ParameterValidatorException {
		validator.setMinLength(2);
		validator.setMaxLength(2);
		validator.setCaseInsensitive(false);
		mockReq.addParameterValue("test", "AA");
		validator.validate(mockReq, "test");
	}						
}
