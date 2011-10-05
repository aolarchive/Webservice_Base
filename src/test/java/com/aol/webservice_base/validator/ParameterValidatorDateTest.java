/**
 * 
 */
package com.aol.webservice_base.validator;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;
import com.aol.webservice_base.support.MockHttpServletRequest;
import com.aol.webservice_base.validator.parameter.ParameterValidatorException;
import com.aol.webservice_base.validator.parameter.types.ParameterTypeDate;
import com.aol.webservice_base.validator.support.MockServletRequestFacade;
import com.aol.webservice_base.validator.support.ValidatorTestBean;

/**
 * @author human
 *
 */
public class ParameterValidatorDateTest {
	protected MockServletRequestFacade mockReq;
	protected RequestState state;
	protected ValidatorTestBean requestObject;	
	protected ParameterTypeDate validator;
	protected long now;
	
	@Before
	public void initialize() {
		mockReq = new MockServletRequestFacade(new MockHttpServletRequest());
		state = new RequestState(null);
		requestObject = new ValidatorTestBean();
		state.setRequestObject(requestObject);
		mockReq.setAttribute(Constants.REQUEST_STATE, state);
		
		validator = new ParameterTypeDate();
		validator.setName("test");
		validator.setMember("longMember");
		now = new Date().getTime();
	}

	@Test
	public void testDateNow() throws ParameterValidatorException {
		Long time = new Date().getTime();
		String timeStr = Long.toString(time);
		mockReq.addParameterValue("test", timeStr);
		validator.validate(mockReq, "test");
		Assert.assertEquals(time, requestObject.getLongMember());
	}	

	@Test (expected = ParameterValidatorException.class)
	public void testInvalidDate() throws ParameterValidatorException {
		mockReq.addParameterValue("test", "blow up");
		validator.validate(mockReq, "test");
	}	
	
	@Test
	public void testDateNowValidAgo() throws ParameterValidatorException {
		validator.setMaxSecAgo(10000L);
		mockReq.addParameterValue("test", Long.toString(now - 9998L));
		validator.validate(mockReq, "test");
	}	

	@Test (expected = ParameterValidatorException.class)
	public void testDateNowInvalidAgo() throws ParameterValidatorException {
		validator.setMaxSecAgo(10000L);
		mockReq.addParameterValue("test", Long.toString(now - 10002L));
		validator.validate(mockReq, "test");
	}	
	
	@Test
	public void testDateNowValidFuture() throws ParameterValidatorException {
		validator.setMaxSecFuture(10000L);
		mockReq.addParameterValue("test", Long.toString(now + 9998L));
		validator.validate(mockReq, "test");
	}	

	@Test (expected = ParameterValidatorException.class)
	public void testDateNowInvalidFuture() throws ParameterValidatorException {
		validator.setMaxSecFuture(10000L);
		mockReq.addParameterValue("test", Long.toString(now + 10002L));
		validator.validate(mockReq, "test");
	}	
}
