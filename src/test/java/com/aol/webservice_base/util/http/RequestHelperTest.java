/**
 * 
 */
package com.aol.webservice_base.util.http;

import java.util.Hashtable;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.aol.webservice_base.support.MockHttpServletRequest;

/**
 * @author human
 *
 */
public class RequestHelperTest {
	protected MockHttpServletRequest req;

	protected static final String X_FORWARDED_FOR = "X-Forwarded-For";
	protected static final String REMOTE_IP = "1.2.3.4";
	protected static final String HEADER_SIMPLE = "2.3.4.5";
	protected static final String FIRST_MULT_IP = "1.1.1.1";
	protected static final String LAST_MULT_IP = "3.3.3.3";
	protected static final String HEADER_MULTIPLE = "1.1.1.1,2.2.2.2,"+LAST_MULT_IP;
	protected static final String LAST_MULT_IP_ALT = "6.6.6.6";
	protected static final String HEADER_MULTIPLE_ALT = "4.4.4.4,5.5.5.5,"+LAST_MULT_IP_ALT;

	@Before
	public void init() {
		req = new MockHttpServletRequest();
		req.setRemoteAddr(REMOTE_IP);
	}

	@Test
	public void justIP() {
		Assert.assertEquals(REMOTE_IP, RequestHelper.getClientIP(req, true, null));
	}
	@Test
	public void justIP2() {
		Assert.assertEquals(REMOTE_IP, RequestHelper.getClientIP(req, false, null));
	}

	@Test
	public void ip1XForwardedNoTrust() {
		req.addHeader(X_FORWARDED_FOR, HEADER_SIMPLE);
		Assert.assertEquals(REMOTE_IP, RequestHelper.getClientIP(req, false, null));
	}
	@Test
	public void ip1XForwardedTrust() {
		req.addHeader(X_FORWARDED_FOR, HEADER_SIMPLE);
		Assert.assertEquals(HEADER_SIMPLE, RequestHelper.getClientIP(req, true, null));
	}

	@Test
	public void ip1XForwardedNoTrustSingleHeaderMultValue() {
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE);
		Assert.assertEquals(REMOTE_IP, RequestHelper.getClientIP(req, false, null));
	}
	@Test
	public void ip1XForwardedTrustSingleHeaderMultValue() {
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE);
		Assert.assertEquals(FIRST_MULT_IP, RequestHelper.getClientIP(req, true, null));
	}

	@Test
	public void ip1XForwardedNoTrustMultHeaderMultValue() {
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE);
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE_ALT);
		Assert.assertEquals(REMOTE_IP, RequestHelper.getClientIP(req, false, null));
	}
	@Test
	public void ip1XForwardedTrustMultHeaderMultValue() {
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE);
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE_ALT);
		Assert.assertEquals(FIRST_MULT_IP, RequestHelper.getClientIP(req, true, null));
	}

	@Test
	public void getClientIpsJustIp() {
		List<String> clientIps = RequestHelper.getClientIPs(req);
		Assert.assertEquals(1, clientIps.size());
		Assert.assertEquals(REMOTE_IP, clientIps.get(0));
	}

	@Test
	public void getClientIpsSingleXForwardedSingleValue() {
		req.addHeader(X_FORWARDED_FOR, HEADER_SIMPLE);
		List<String> clientIps = RequestHelper.getClientIPs(req);
		Assert.assertEquals(2, clientIps.size());		
		Assert.assertEquals(HEADER_SIMPLE, clientIps.get(0));
		Assert.assertEquals(REMOTE_IP, clientIps.get(1));
	}

	@Test
	public void getClientIpsSingleXForwardedMulValue() {
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE);
		List<String> clientIps = RequestHelper.getClientIPs(req);
		Assert.assertEquals(4, clientIps.size());
		
		int i=0;
		Assert.assertEquals(FIRST_MULT_IP, clientIps.get(i++));
		Assert.assertEquals("2.2.2.2", clientIps.get(i++));
		Assert.assertEquals(LAST_MULT_IP, clientIps.get(i++));
		Assert.assertEquals(REMOTE_IP, clientIps.get(i++));
	}

	@Test
	public void getClientIpsMultipleXForwardedMulValue() {
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE);
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE_ALT);
		List<String> clientIps = RequestHelper.getClientIPs(req);
		Assert.assertEquals(7, clientIps.size());
		
		int i=0;
		
		Assert.assertEquals(FIRST_MULT_IP, clientIps.get(i++));
		Assert.assertEquals("2.2.2.2", clientIps.get(i++));
		Assert.assertEquals(LAST_MULT_IP, clientIps.get(i++));
		Assert.assertEquals("4.4.4.4", clientIps.get(i++));
		Assert.assertEquals("5.5.5.5", clientIps.get(i++));
		Assert.assertEquals(LAST_MULT_IP_ALT, clientIps.get(i++));
		Assert.assertEquals(REMOTE_IP, clientIps.get(i++));
	}	

	@Test
	public void getIPSet() throws Exception {
		List<Integer> ipData = RequestHelper.getIPSet(REMOTE_IP , "test");
		for (int i=0; i<4; i++)
			Assert.assertEquals(new Integer(i+1), ipData.get(i));
	}

	@Test (expected = Exception.class)
	public void getIPSetTooBig() throws Exception {
		List<Integer> ipData = RequestHelper.getIPSet(REMOTE_IP + ".5", "test");
	}

	@Test (expected = Exception.class)
	public void getIPSetTooSmall() throws Exception {
		List<Integer> ipData = RequestHelper.getIPSet("1.2.3", "test");
	}

	@Test (expected = Exception.class)
	public void getIPSetNotInt() throws Exception {
		List<Integer> ipData = RequestHelper.getIPSet("1.2.3.four", "test");
	}

	@Test
	public void justIPAllow() throws Exception {
		Hashtable<List<Integer>,List<Integer>> validIpFilter = new Hashtable<List<Integer>,List<Integer>>();
		req.addHeader(X_FORWARDED_FOR, HEADER_SIMPLE);
		validIpFilter.put(RequestHelper.getIPSet(REMOTE_IP, "test1"), RequestHelper.getIPSet(REMOTE_IP, "test2"));
		Assert.assertEquals(HEADER_SIMPLE, RequestHelper.getClientIP(req, true, validIpFilter));
	}

	@Test
	public void justIPAllowAllValid() throws Exception {
		Hashtable<List<Integer>,List<Integer>> validIpFilter = new Hashtable<List<Integer>,List<Integer>>();
		req.addHeader(X_FORWARDED_FOR, HEADER_SIMPLE);
		validIpFilter.put(RequestHelper.getIPSet("0.0.0.0", "test1"), RequestHelper.getIPSet("0.0.0.0", "test2"));
		Assert.assertEquals(HEADER_SIMPLE, RequestHelper.getClientIP(req, true, validIpFilter));
	}	

	@Test
	public void justIPAllowNoMatch() throws Exception {
		Hashtable<List<Integer>,List<Integer>> validIpFilter = new Hashtable<List<Integer>,List<Integer>>();
		req.addHeader(X_FORWARDED_FOR, HEADER_SIMPLE);
		validIpFilter.put(RequestHelper.getIPSet(HEADER_SIMPLE, "test1"), RequestHelper.getIPSet("255.255.255.255", "test2"));
		Assert.assertEquals(HEADER_SIMPLE, RequestHelper.getClientIP(req, true, validIpFilter));
	}
	
	@Test
	public void justIPAllowAllValidMultipleFirstValid() throws Exception {
		Hashtable<List<Integer>,List<Integer>> validIpFilter = new Hashtable<List<Integer>,List<Integer>>();
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE);
		validIpFilter.put(RequestHelper.getIPSet(FIRST_MULT_IP, "test1"), RequestHelper.getIPSet("255.255.255.255", "test2"));
		Assert.assertEquals(LAST_MULT_IP, RequestHelper.getClientIP(req, true, validIpFilter));
	}		

	@Test
	public void justIPAllowAllValidMultipleSecondUnknown() throws Exception {
		Hashtable<List<Integer>,List<Integer>> validIpFilter = new Hashtable<List<Integer>,List<Integer>>();
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE);
		validIpFilter.put(RequestHelper.getIPSet(LAST_MULT_IP, "test1"), RequestHelper.getIPSet(LAST_MULT_IP, "test2"));
		Assert.assertEquals("2.2.2.2", RequestHelper.getClientIP(req, true, validIpFilter));
	}		

	@Test
	public void justIPAllowAllValidMultipleSecondUnknown2() throws Exception {
		Hashtable<List<Integer>,List<Integer>> validIpFilter = new Hashtable<List<Integer>,List<Integer>>();
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE);
		validIpFilter.put(RequestHelper.getIPSet("1.1.1.1", "test1"), RequestHelper.getIPSet("1.1.1.1", "test2"));
		Assert.assertEquals("2.2.2.2", RequestHelper.getClientIP(req, true, validIpFilter));
	}		

	@Test
	public void justIPAllowAllValidMultipleBadBitmask() throws Exception {
		Hashtable<List<Integer>,List<Integer>> validIpFilter = new Hashtable<List<Integer>,List<Integer>>();
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE);
		validIpFilter.put(RequestHelper.getIPSet("1.1.1.1", "test1"), RequestHelper.getIPSet("0.0.0.0", "test2"));
		Assert.assertEquals(FIRST_MULT_IP, RequestHelper.getClientIP(req, true, validIpFilter));
	}		

	@Test
	public void justIPAllowAllValidMultipleMatchers() throws Exception {
		Hashtable<List<Integer>,List<Integer>> validIpFilter = new Hashtable<List<Integer>,List<Integer>>();
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE);
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE_ALT);		
		validIpFilter.put(RequestHelper.getIPSet(LAST_MULT_IP_ALT, "test1"), RequestHelper.getIPSet(LAST_MULT_IP_ALT, "test2"));
		validIpFilter.put(RequestHelper.getIPSet("5.5.5.5", "test1"), RequestHelper.getIPSet("5.5.5.5", "test2"));
		Assert.assertEquals("4.4.4.4", RequestHelper.getClientIP(req, true, validIpFilter));
	}		

	@Test
	public void justIPAllowAllValidFixupBitmask() throws Exception {
		Hashtable<List<Integer>,List<Integer>> validIpFilter = new Hashtable<List<Integer>,List<Integer>>();
		req.addHeader(X_FORWARDED_FOR, HEADER_MULTIPLE);
		validIpFilter.put(RequestHelper.getIPSet("3.3.3.3", "test1"), RequestHelper.getIPSet("1.1.1.1", "test2"));
		Assert.assertEquals("2.2.2.2", RequestHelper.getClientIP(req, true, validIpFilter));
	}		
}
