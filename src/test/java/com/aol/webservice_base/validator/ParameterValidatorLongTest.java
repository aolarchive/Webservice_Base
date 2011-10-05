/**
 * 
 */
package com.aol.webservice_base.validator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;
import com.aol.webservice_base.support.MockHttpServletRequest;
import com.aol.webservice_base.validator.parameter.ParameterValidatorException;
import com.aol.webservice_base.validator.parameter.types.ParameterTypeLong;
import com.aol.webservice_base.validator.support.MockServletRequestFacade;
import com.aol.webservice_base.validator.support.ValidatorTestBean;

/**
 * @author human
 *
 */
public class ParameterValidatorLongTest {
	protected ParameterTypeLong validator;
	protected RequestState state;
	protected ValidatorTestBean requestObject;
	protected MockServletRequestFacade mockReq;
	
	@Before
	public void initialize() {
		mockReq = new MockServletRequestFacade(new MockHttpServletRequest());
		state = new RequestState(null);
		requestObject = new ValidatorTestBean();
		state.setRequestObject(requestObject);
		mockReq.setAttribute(Constants.REQUEST_STATE, state);		
		validator = new ParameterTypeLong();
		validator.setName("test");
		validator.setRequired(true);
		validator.setMax(10L);
		validator.setMin(1L);
		validator.setMember("longMember");
	}

	@Test
	public void testAllowNullParam() throws ParameterValidatorException {
		validator.setRequired(false);
		validator.validate(mockReq, "test");
		Assert.assertEquals(null, requestObject.getLongMember());
	}	
	
	@Test (expected = ParameterValidatorException.class)
	public void testNullParam() throws ParameterValidatorException {
		validator.validate(mockReq, "test");
	}

	@Test (expected = ParameterValidatorException.class)
	public void testEmptyFail() throws ParameterValidatorException {
		mockReq.addParameterValue("test", "");
		validator.validate(mockReq, "test");
	}	

	@Test (expected = ParameterValidatorException.class)
	public void testNonNumberFail() throws ParameterValidatorException {
		mockReq.addParameterValue("test", "one");
		validator.validate(mockReq, "test");
	}	
	
	@Test
	public void testMinParam() throws ParameterValidatorException {
		mockReq.addParameterValue("test", "1");
		validator.validate(mockReq, "test");
		Assert.assertEquals(Long.valueOf(1), requestObject.getLongMember());
	}

	@Test (expected = ParameterValidatorException.class)
	public void testMinParamFail() throws ParameterValidatorException {
		mockReq.addParameterValue("test", "0");
		validator.validate(mockReq, "test");
	}

	@Test
	public void testMinParamTooLowAdjust() throws ParameterValidatorException {
		mockReq.addParameterValue("test", "0");
		validator.setLimitMin(true);
		validator.validate(mockReq, "test");
		Assert.assertEquals(Long.valueOf(1), requestObject.getLongMember());
	}	
	
	@Test
	public void testMaxParam() throws ParameterValidatorException {
		mockReq.addParameterValue("test", "10");
		validator.validate(mockReq, "test");
		Assert.assertEquals(Long.valueOf(10), requestObject.getLongMember());
	}

	@Test (expected = ParameterValidatorException.class)
	public void testMaxParamFail() throws ParameterValidatorException {
		mockReq.addParameterValue("test", "11");
		validator.validate(mockReq, "test");		
	}
	
	@Test
	public void testMaxParamTooHighAdjust() throws ParameterValidatorException {
		mockReq.addParameterValue("test", "11");
		validator.setLimitMax(true);
		validator.validate(mockReq, "test");
		Assert.assertEquals(Long.valueOf(10), requestObject.getLongMember());
	}		
	
	@Test
	public void testDefaultNullParam() throws ParameterValidatorException {
		validator.setDefaultValue("4");
		validator.validate(mockReq, "test");
		Assert.assertEquals(Long.valueOf(4), requestObject.getLongMember());
	}	
}
