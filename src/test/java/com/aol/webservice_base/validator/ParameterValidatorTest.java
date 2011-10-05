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
import com.aol.webservice_base.validator.parameter.ParameterValidator;
import com.aol.webservice_base.validator.parameter.types.AbstractParameterType;
import com.aol.webservice_base.validator.parameter.types.ParameterTypeInteger;
import com.aol.webservice_base.validator.support.MockServletRequestFacade;

/**
 * @author human
 *
 */
public class ParameterValidatorTest {
	MockHttpServletRequest fakeReq;
	protected ParameterValidator validator;
	ParameterTypeInteger validNoReq1;
	ParameterTypeInteger validNoReq2; 
	ParameterTypeInteger validReq1;
	ParameterTypeInteger validReq2; 
	
	@Before
	public void init() {
		fakeReq = new MockHttpServletRequest();
		fakeReq.setAttribute(Constants.REQUEST_STATE, new RequestState(null));
		validator = new ParameterValidator();		
		
		validReq1 = new ParameterTypeInteger();
		validReq1.setName("one");
		validReq1.setRequired(true);
		
		validReq2 = new ParameterTypeInteger();
		validReq2.setName("two");
		validReq2.setRequired(true);		

		validNoReq1 = new ParameterTypeInteger();
		validNoReq1.setName("one");
		validNoReq1.setRequired(false);		
		
		validNoReq2 = new ParameterTypeInteger();
		validNoReq2.setName("two");
		validNoReq2.setRequired(false);		
	}
	
	@Test
	public void findValueFirst() {		
		ArrayList<AbstractParameterType> childValidators = new ArrayList<AbstractParameterType>();
		childValidators.add(validNoReq1);
		childValidators.add(validNoReq2);
		validator.setValidators(childValidators);
		fakeReq.addParameter("one", "1");		
		validator.validateRequest(new MockServletRequestFacade(fakeReq));
		
		RequestState state = (RequestState)fakeReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
	}

	@Test
	public void findValueFirstInvalid() {		
		ArrayList<AbstractParameterType> childValidators = new ArrayList<AbstractParameterType>();
		validNoReq1.setMin(2);
		childValidators.add(validNoReq1);
		validator.setValidators(childValidators);
		fakeReq.addParameter("one", "1");		
		validator.validateRequest(new MockServletRequestFacade(fakeReq));
		
		RequestState state = (RequestState)fakeReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(Constants.SC_INVALID_PARAMETER, state.getStatusCode());
	}
	
	@Test
	public void findValueFirstInvalidOverride() {		
		ArrayList<AbstractParameterType> childValidators = new ArrayList<AbstractParameterType>();
		validNoReq1.setMin(2);
		validNoReq1.setInvalidErrorCodeOverride(99);
		childValidators.add(validNoReq1);
		validator.setValidators(childValidators);
		fakeReq.addParameter("one", "1");		
		validator.validateRequest(new MockServletRequestFacade(fakeReq));
		
		RequestState state = (RequestState)fakeReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(99, state.getStatusCode());
	}

	
	@Test
	public void findValueSecond() {
		ArrayList<AbstractParameterType> childValidators = new ArrayList<AbstractParameterType>();
		childValidators.add(validNoReq1);
		childValidators.add(validNoReq2);
		validator.setValidators(childValidators);
		fakeReq.addParameter("two", "2");
		validator.validateRequest(new MockServletRequestFacade(fakeReq));
		
		RequestState state = (RequestState)fakeReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
	}

	@Test
	public void findValueBoth() {
		ArrayList<AbstractParameterType> childValidators = new ArrayList<AbstractParameterType>();
		childValidators.add(validNoReq1);
		childValidators.add(validNoReq2);
		validator.setValidators(childValidators);
		fakeReq.addParameter("one", "1");
		fakeReq.addParameter("two", "2");
		validator.validateRequest(new MockServletRequestFacade(fakeReq));
		
		RequestState state = (RequestState)fakeReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
	}
	
	@Test
	public void findValueFirstReqFail() {
		ArrayList<AbstractParameterType> childValidators = new ArrayList<AbstractParameterType>();
		childValidators.add(validReq1);
		childValidators.add(validReq2);
		validator.setValidators(childValidators);
		fakeReq.addParameter("one", "1");
		validator.validateRequest(new MockServletRequestFacade(fakeReq));
		
		RequestState state = (RequestState)fakeReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertFalse(state.getStatusCode() == 200);
	}

	@Test
	public void findValueSecondReqFail() {
		ArrayList<AbstractParameterType> childValidators = new ArrayList<AbstractParameterType>();
		childValidators.add(validReq1);
		childValidators.add(validReq2);
		validator.setValidators(childValidators);
		fakeReq.addParameter("two", "2");
		validator.validateRequest(new MockServletRequestFacade(fakeReq));
		
		RequestState state = (RequestState)fakeReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertFalse(state.getStatusCode() == 200);
	}

	@Test
	public void findValueBothReq() {
		ArrayList<AbstractParameterType> childValidators = new ArrayList<AbstractParameterType>();
		childValidators.add(validReq1);
		childValidators.add(validReq2);
		validator.setValidators(childValidators);
		fakeReq.addParameter("one", "1");
		fakeReq.addParameter("two", "2");
		validator.validateRequest(new MockServletRequestFacade(fakeReq));
		
		RequestState state = (RequestState)fakeReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
	}
	
	@Test
	public void unexpectedParameterAlone() {
		fakeReq.addParameter("notExpected", "1");
		validator.validateRequest(new MockServletRequestFacade(fakeReq));

		RequestState state = (RequestState)fakeReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(Constants.SC_INVALID_PARAMETER, state.getStatusCode());	
	}

	@Test
	public void findValueFirstUnexpectedAlso() {		
		ArrayList<AbstractParameterType> childValidators = new ArrayList<AbstractParameterType>();
		childValidators.add(validNoReq1);
		childValidators.add(validNoReq2);
		validator.setValidators(childValidators);
		fakeReq.addParameter("one", "1");
		fakeReq.addParameter("notExpected", "1");
		validator.validateRequest(new MockServletRequestFacade(fakeReq));
		
		RequestState state = (RequestState)fakeReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(Constants.SC_INVALID_PARAMETER, state.getStatusCode());
	}	

	@Test
	public void bothRequiredOnePrecludesOther() {		
		ArrayList<AbstractParameterType> childValidators = new ArrayList<AbstractParameterType>();
		validNoReq1.setPrecludes("two");
		childValidators.add(validNoReq1);
		childValidators.add(validNoReq2);
		validator.setValidators(childValidators);
		fakeReq.addParameter("one", "1");
		validator.validateRequest(new MockServletRequestFacade(fakeReq));
		
		RequestState state = (RequestState)fakeReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(Constants.SC_OK, state.getStatusCode());
	}		

	@Test
	public void bothRequiredOnePrecludesOtherBothExist() {		
		ArrayList<AbstractParameterType> childValidators = new ArrayList<AbstractParameterType>();
		validNoReq1.setPrecludes("two");
		childValidators.add(validNoReq1);
		childValidators.add(validNoReq2);
		validator.setValidators(childValidators);
		fakeReq.addParameter("one", "1");
		fakeReq.addParameter("two", "2");
		validator.validateRequest(new MockServletRequestFacade(fakeReq));
		
		RequestState state = (RequestState)fakeReq.getAttribute(Constants.REQUEST_STATE);
		Assert.assertEquals(Constants.SC_INVALID_PARAMETER, state.getStatusCode());
	}			
	
}
