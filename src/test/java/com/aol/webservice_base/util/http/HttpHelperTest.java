package com.aol.webservice_base.util.http;

import java.io.IOException;

import junit.framework.Assert;

import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

public class HttpHelperTest {
	HttpHelper helper = null;
	
	@Before
	public void init() {
		helper = new HttpHelper();
		helper.setConnectionTimeout(2500);		
		helper.setFollowRedirectMax(0);
		helper.setMaxConnectionsPerHost(1);
		helper.setMaxTotalConnections(1);
		helper.setSocketTimeout(2500);
		
		helper.init();
	}

	@Test
	public void getResult() throws HttpException, IOException {
		HttpResponseData respData = helper.get("http://www.aol.com", null);
		Assert.assertNotNull(respData);
	}

	@Test
	public void getResultTwice() throws HttpException, IOException {
		HttpResponseData respData = helper.get("http://www.aol.com", null);
		Assert.assertNotNull(respData);
		HttpResponseData respData2 = helper.get("http://www.aol.com", null);
		Assert.assertNotNull(respData2);
	}

	@Test
	public void getHttpResponse() throws HttpException, IOException {
		HttpResponse respData = helper.httpClientCall("GET", "http://www.aol.com", null, null);
		Assert.assertNotNull(respData);
	}

	@Test (expected = HttpException.class)
	public void getHttpResponseTwiceFail() throws HttpException, IOException {
		HttpResponse respData = helper.httpClientCall("GET", "http://www.aol.com", null, null);
		Assert.assertNotNull(respData);
		HttpResponse respData2 = helper.httpClientCall("GET", "http://www.aol.com", null, null);
		Assert.assertNotNull(respData2);
	}

	@Test
	public void getHttpResponseTwice2Connections() throws HttpException, IOException {
		// reconfig helper
		helper.setMaxConnectionsPerHost(2);
		helper.setMaxTotalConnections(2);
		helper.init();
		
		HttpResponse respData = helper.httpClientCall("GET", "http://www.aol.com", null, null);
		Assert.assertNotNull(respData);
		HttpResponse respData2 = helper.httpClientCall("GET", "http://www.aol.com", null, null);
		Assert.assertNotNull(respData2);
	}
	
	@Test
	public void getHttpResponseTwiceReturn() throws HttpException, IOException {
		HttpResponse respData = helper.httpClientCall("GET", "http://www.aol.com", null, null);
		Assert.assertNotNull(respData);
		helper.releaseResponse(respData);
		HttpResponse respData2 = helper.httpClientCall("GET", "http://www.aol.com", null, null);
		Assert.assertNotNull(respData2);
	}
}
