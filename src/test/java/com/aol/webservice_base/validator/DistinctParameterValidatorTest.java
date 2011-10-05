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
import com.aol.webservice_base.validator.parameter.DistinctParameterValidator;
import com.aol.webservice_base.validator.support.MockServletRequestFacade;

/**
 * @author human
 *
 */
public class DistinctParameterValidatorTest {
	protected DistinctParameterValidator validator;
	protected MockServletRequestFacade mockReq;
	
	@Before
	public void init() {
		validator = new DistinctParameterValidator();
		mockReq = new MockServletRequestFacade(new MockHttpServletRequest());
		mockReq.setAttribute(Constants.REQUEST_STATE, new RequestState(null));
	}
	
	@Test
	public void testOneMissing() {		
		validator.setParamName("one");
		validator.validateRequest(mockReq);
		RequestState state = (RequestState)mockReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(460, state.getStatusCode());
	}
	
	@Test
	public void testOneExists() {		
		validator.setParamName("one");
		mockReq.addParameterValue("one", "1");
		validator.validateRequest(mockReq);
		RequestState state = (RequestState)mockReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(200, state.getStatusCode());
	}

	@Test
	public void testTwoHasNone() {		
		validator.setParamName("one");
		validator.setParamName("two");
		validator.validateRequest(mockReq);
		RequestState state = (RequestState)mockReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(460, state.getStatusCode());
	}	
	
	@Test
	public void testTwoHasOne() {		
		validator.setParamName("one");
		validator.setParamName("two");
		mockReq.addParameterValue("one", "1");
		validator.validateRequest(mockReq);
		RequestState state = (RequestState)mockReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(200, state.getStatusCode());
	}

	@Test
	public void testTwoHasTwo() {		
		validator.setParamName("one");
		validator.setParamName("two");
		mockReq.addParameterValue("two", "2");
		validator.validateRequest(mockReq);
		RequestState state = (RequestState)mockReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(200, state.getStatusCode());
	}
	
	@Test
	public void testTwoHasBothFail() {		
		validator.setParamName("one");
		validator.setParamName("two");
		mockReq.addParameterValue("one", "1");
		mockReq.addParameterValue("two", "2");
		validator.validateRequest(mockReq);
		RequestState state = (RequestState)mockReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(462, state.getStatusCode());
	}
}
