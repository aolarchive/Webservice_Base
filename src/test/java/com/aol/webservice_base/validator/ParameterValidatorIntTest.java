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
import com.aol.webservice_base.validator.parameter.types.ParameterTypeInteger;
import com.aol.webservice_base.validator.support.MockServletRequestFacade;
import com.aol.webservice_base.validator.support.ValidatorTestBean;

/**
 * @author human
 *
 */
public class ParameterValidatorIntTest {
	protected MockServletRequestFacade mockReq;
	protected RequestState state;
	protected ValidatorTestBean requestObject;
	protected ParameterTypeInteger validator;
	
	@Before
	public void initialize() {
		mockReq = new MockServletRequestFacade(new MockHttpServletRequest());
		state = new RequestState(null);
		requestObject = new ValidatorTestBean();
		state.setRequestObject(requestObject);
		mockReq.setAttribute(Constants.REQUEST_STATE, state);
		
		validator = new ParameterTypeInteger();
		validator.setName("test");
		validator.setRequired(true);
		validator.setMax(10);
		validator.setMin(1);
		validator.setMember("intMember");
	}

	@Test
	public void testAllowNullParam() throws ParameterValidatorException {
		validator.setRequired(false);
		validator.validate(mockReq, "test");
		Assert.assertEquals(null, requestObject.getIntMember());
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
		Assert.assertEquals(Integer.valueOf(1), requestObject.getIntMember());		
	}

	@Test
	public void testMinParamTooLowAdjust() throws ParameterValidatorException {
		mockReq.addParameterValue("test", "0");
		validator.setLimitMin(true);
		validator.validate(mockReq, "test");
		Assert.assertEquals(Integer.valueOf(1), requestObject.getIntMember());
	}

	@Test (expected = ParameterValidatorException.class)
	public void testMinParamFail() throws ParameterValidatorException {
		mockReq.addParameterValue("test", "0");
		validator.validate(mockReq, "test");
	}	
	
	@Test
	public void testMaxParam() throws ParameterValidatorException {
		mockReq.addParameterValue("test", "10");
		validator.validate(mockReq, "test");
		Assert.assertEquals(Integer.valueOf(10), requestObject.getIntMember());
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
		Assert.assertEquals(Integer.valueOf(10), requestObject.getIntMember());
	}	
	
	@Test
	public void testDefaultNullParam() throws ParameterValidatorException {
		validator.setDefaultValue("4");
		validator.validate(mockReq, "test");
		Assert.assertEquals(Integer.valueOf(4), requestObject.getIntMember());
	}	

	@Test (expected = ParameterValidatorException.class)
	public void testRequiresMissing() throws ParameterValidatorException {
		validator.setRequires("required");
		mockReq.addParameterValue("test", "10");
		validator.validate(mockReq, "test");
	}	
	
	@Test
	public void testRequiresExists() throws ParameterValidatorException {
		validator.setRequires("required");
		mockReq.addParameterValue("required", "here");
		mockReq.addParameterValue("test", "10");
		validator.validate(mockReq, "test");
		Assert.assertEquals(Integer.valueOf(10), requestObject.getIntMember());		
	}	

	@Test
	public void testPrecludesMissing() throws ParameterValidatorException {
		validator.setPrecludes("precludes");
		mockReq.addParameterValue("test", "10");
		validator.validate(mockReq, "test");
		Assert.assertEquals(Integer.valueOf(10), requestObject.getIntMember());		
	}	
	
	@Test (expected = ParameterValidatorException.class)
	public void testPrecludesExists() throws ParameterValidatorException {
		validator.setPrecludes("precludes");
		mockReq.addParameterValue("precludes", "yes");
		mockReq.addParameterValue("test", "10");
		validator.validate(mockReq, "test");
	}	
}
