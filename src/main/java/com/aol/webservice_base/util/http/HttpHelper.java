/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

 */
package com.aol.webservice_base.util.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpHelper.
 */
public class HttpHelper {
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(HttpHelper.class);
	
	/** The Constant REQUEST_CHARSET. */
	protected static final String REQUEST_CHARSET = "utf-8";
	
	/** The Constant REQUEST_CONTENT_TYPE. */
	protected static final String REQUEST_CONTENT_TYPE = "text/html; charset=" + REQUEST_CHARSET;
	
	/** The Constant X_FORWARDED_FOR. */
	public static final String X_FORWARDED_FOR = "X-Forwarded-For";
	
	/** The Constant HOST. */
	protected static final String HOST = "host";
	
	/** The Constant CONTENT_DEBUG_LIMIT. */
	protected static final int CONTENT_DEBUG_LIMIT = 200000;
	// set-able from config
	/** The max connections per host. */
	protected int maxConnectionsPerHost = 5;
	
	/** The max total connections. */
	protected int maxTotalConnections = 500;
	
	/** The connection timeout. */
	protected int connectionTimeout = 5000;
	
	/** The socket timeout. */
	protected int socketTimeout = 5000;
	
	/** The follow redirect max. */
	protected int followRedirectMax = 3;

	/**
	 * Sets the follow redirect max.
	 *
	 * @param followRedirectMax the new follow redirect max
	 */
	public void setFollowRedirectMax(int followRedirectMax) {
		this.followRedirectMax = followRedirectMax;
	}
	// internal state
	/** The inited. */
	protected boolean inited = false;
	
	/** The http client. */
	protected HttpClient httpClient = null;

	/**
	 * Inits the.
	 */
	public void init() {
		inited = true;
		ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager();
		connectionManager.setDefaultMaxPerRoute(maxConnectionsPerHost);
		connectionManager.setMaxTotal(maxTotalConnections);

		httpClient = new DefaultHttpClient(connectionManager);
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
		HttpConnectionParams.setSoTimeout(params, socketTimeout);
	}

	/**
	 * Release response.
	 *
	 * Convenience method to have the connection released to be reused.
	 *
	 * @param resp the resp
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void releaseResponse(HttpResponse resp) throws IOException {
		EntityUtils.consume(resp.getEntity());
	}

	/**
	 * Gets the data at url
	 * 
	 * @param url the url
	 * @param headers the headers
	 * @return the http response data
	 * @throws HttpException the http exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public HttpResponseData get(String url, Map<String, String> headers) throws HttpException, IOException {
		return fetchHttpResponseData("GET", url, headers, null);
	}

	/**
	 * Post.
	 * 
	 * @param url the url
	 * @param headers the headers
	 * @param content the content
	 * @return the http response data
	 * @throws HttpException the http exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public HttpResponseData post(String url, Map<String, String> headers, Object content) throws HttpException, IOException {
		return fetchHttpResponseData("POST", url, headers, content);
	}

	/**
	 * Delete.
	 * 
	 * @param url the url
	 * @param headers the headers
	 * @return the http response data
	 * @throws HttpException the http exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public HttpResponseData delete(String url, Map<String, String> headers) throws HttpException, IOException {
		return fetchHttpResponseData("DELETE", url, headers, null);
	}

	/**
	 * Put.
	 * 
	 * @param url the url
	 * @param headers the headers
	 * @param content the content
	 * @return the http response data
	 * @throws HttpException the http exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public HttpResponseData put(String url, Map<String, String> headers, Object content) throws HttpException, IOException {
		return fetchHttpResponseData("PUT", url, headers, content);
	}

	/**
	 * Gets the http method.
	 *
	 * callers to this are responsible for ensuring connection is closed properly - httpHelper.releaseResponse() 
	 *
	 * @param url the url
	 * @param headers the headers
	 * @return the http method
	 * @throws HttpException the http exception
	 */
	public HttpResponse getHttpResponse(String url, Map<String, String> headers) throws HttpException {
		return httpClientCall("GET", url, headers, null);
	}

	/**
	 * Post http method.
	 *
	 * callers to this are responsible for ensuring connection is closed properly - httpHelper.releaseResponse() 
	 *
	 * @param url the url
	 * @param headers the headers
	 * @param content the content
	 * @return the http response
	 * @throws HttpException the http exception
	 */
	public HttpResponse postHttpResponse(String url, Map<String, String> headers, Object content) throws HttpException {
		return httpClientCall("POST", url, headers, content);
	}

	/**
	 * Delete http method.
	 *
	 * callers to this are responsible for ensuring connection is closed properly - httpHelper.releaseResponse() 
	 *
	 * @param url the url
	 * @param headers the headers
	 * @return the http response
	 * @throws HttpException the http exception
	 */
	public HttpResponse deleteHttpResponse(String url, Map<String, String> headers) throws HttpException {
		return httpClientCall("DELETE", url, headers, null);
	}

	/**
	 * Put http method.
	 *
	 * callers to this are responsible for ensuring connection is closed properly - httpHelper.releaseResponse() 
	 *
	 * @param url the url
	 * @param headers the headers
	 * @param content the content
	 * @return the http response
	 * @throws HttpException the http exception
	 */
	public HttpResponse putHttpResponse(String url, Map<String, String> headers, Object content) throws HttpException {
		return httpClientCall("PUT", url, headers, content);
	}

	/**
	 * Output data.
	 *
	 * @param in the in
	 * @return the byte[]
	 * @throws HttpException the http exception
	 */
	private byte[] outputData(java.io.InputStream in) throws HttpException {
		final String METHOD = "HttpHelper.outputData()";
		if (logger.isDebugEnabled()) {
			logger.debug(METHOD + ": Enter");
		}
		try {
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			byte[] buf = new byte[4096];
			int len;
			while ((len = in.read(buf, 0, buf.length)) > 0) {
				data.write(buf, 0, len);
				if (logger.isDebugEnabled()) {
					logger.debug("read size = " + len);
					// logger.debug("Data size = "+data.size());
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug(METHOD + ": Leave with total data size = [" + data.size() + "]");
			}
			return data.toByteArray();

		} catch (IOException e) {
			logger.error(METHOD, e);
			throw new HttpException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
		} catch (Throwable t) {
			logger.error(METHOD, t);
			throw new HttpException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, t);
		}
	}


	/**
	 * Url connection call.
	 *
	 * @param out the out
	 * @param url the url
	 * @throws HttpException the http exception
	 */
	public void urlConnectionCall(BufferedOutputStream out, String url) throws HttpException {
		final String METHOD = "HttpHelper.urlConnectionCall()";
		BufferedInputStream urlData = null;
		try {
			HttpURLConnection urlConn = (HttpURLConnection) (new URL(url)).openConnection();
			urlConn.setRequestMethod("GET");
			urlData = new BufferedInputStream(urlConn.getInputStream());
			byte[] buf = new byte[4096];
			int sizeRead, available = 4096;
			while (available > 0 && (sizeRead = urlData.read(buf, 0, buf.length)) != -1) {
				out.write(buf, 0, Math.min(available, sizeRead));
				available -= sizeRead;
			}
		} catch (Throwable t) {
			throw new HttpException(0, t);
		} finally {
			if (urlData != null) {
				try {
					urlData.close();
					urlData = null;
				} catch (IOException e) {
					logger.debug(METHOD, e);
				}
			}
		}
	}

	/**
	 * Checks if is gets the method.
	 *
	 * @param request the request
	 * @return true, if is gets the method
	 */
	public static boolean isGetMethod(HttpServletRequest request) {
		return (request == null || "GET".equalsIgnoreCase(request.getMethod()));
	}

	// WARN: content should be byte[] or String.
	/**
	 * Gets the http request.
	 *
	 * @param requestMethod the request method
	 * @param url the url
	 * @param content the content
	 * @return the http request
	 * @throws HttpException the http exception
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	private HttpRequestBase getHttpRequest(String requestMethod, String url, Object content) throws HttpException, UnsupportedEncodingException {
		HttpRequestBase httpRequest = null;

		if (requestMethod.equalsIgnoreCase("GET")) {
			httpRequest = new HttpGet(url);
		} else if (requestMethod.equalsIgnoreCase("POST")) {
			HttpPost postMethod = new HttpPost(url);
			if (content != null) {
				if (content instanceof byte[]) {
					postMethod.setEntity(new ByteArrayEntity((byte[]) content));
				} else if (content instanceof String) {
					postMethod.setEntity(new StringEntity((String) content, REQUEST_CONTENT_TYPE, REQUEST_CHARSET));
				} else {
					throw new HttpException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "content must be String or byte[]");
				}
				httpRequest = postMethod;
			}
		} else if (requestMethod.equalsIgnoreCase("DELETE")) {
			httpRequest = new HttpDelete(url);
		} else if (requestMethod.equalsIgnoreCase("PUT")) {
			HttpPut putMethod = new HttpPut(url);
			if (content != null) {
				if (content instanceof byte[]) {
					putMethod.setEntity(new ByteArrayEntity((byte[]) content));
				} else if (content instanceof String) {
					putMethod.setEntity(new StringEntity((String) content, REQUEST_CONTENT_TYPE, REQUEST_CHARSET));
				} else {
					throw new HttpException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "content must be String or byte[]");

				}
				httpRequest = putMethod;
			}
		} else {
			throw new HttpException(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
					"Unsupported request method [" + requestMethod + "]");
		}
		return httpRequest;

	}

	/**
	 * Fetch http response data.
	 *
	 * @param requestMethod the request method
	 * @param url the url
	 * @param headers the headers
	 * @param content the content
	 * @return the http response data
	 * @throws HttpException the http exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected HttpResponseData fetchHttpResponseData(String requestMethod, String url,
			Map<String, String> headers, Object content) throws HttpException, IOException {
		HttpResponse httpResponse = null;

		try {
			httpResponse = httpClientCall(requestMethod, url, headers, content);
			return new HttpResponseData(httpResponse.getAllHeaders(), outputData(httpResponse.getEntity().getContent()));
		} finally {
			if (httpResponse != null)
				releaseResponse(httpResponse);
		}
	}

	/**
	 * Http client call.
	 *
	 * callers to this are responsible for ensuring connection is closed properly - httpHelper.releaseResponse() 
	 *
	 * @param requestMethod the request method
	 * @param url the url
	 * @param headers the headers
	 * @param content the content
	 * @return the http response
	 * @throws HttpException the http exception
	 */
	protected HttpResponse httpClientCall(String requestMethod, String url, Map<String, String> headers, Object content) throws HttpException {
		HttpResponse response = null;
		if (!inited) {
			throw new Error("HttpHelper used when not initialized (call init) for " + url);
		}

		final String METHOD = "HttpHelper.httpClientCall()";
		HttpRequestBase httpRequest = null;
		boolean success = false;

		int iter = 0;
		int status;
		String statusMsg;
		do {
			try {
				long begin = System.currentTimeMillis();
				new URL(url);
				httpRequest = getHttpRequest(requestMethod, url, content);

				if (headers != null && headers.size() > 0) {
					for (Map.Entry<String, String> headerSet : headers.entrySet()) {
						httpRequest.addHeader(headerSet.getKey(), headerSet.getValue());
					}
				}
				Header[] requestHeaders = httpRequest.getAllHeaders();
				for (int i = 0; i < requestHeaders.length; i++) {
					String name = requestHeaders[i].getName();
					String value = requestHeaders[i].getValue();
					if (logger.isDebugEnabled()) {
						logger.debug("Request header " + name + " = [" + value + "]");
					}
				}

				// make the request
				//httpRequest.setFollowRedirects(false);
				response = httpClient.execute(httpRequest);
				status = response.getStatusLine().getStatusCode();
				statusMsg = response.getStatusLine().getReasonPhrase();
				if (logger.isDebugEnabled()) {
					logger.debug(METHOD + " status=" + status
							+ " status desc: [" + statusMsg 
							+ "] url=[" + url + "] took="
							+ (System.currentTimeMillis() - begin) + " ms");
				}
				if (status == 302 || status == 301) {
					Header loc = httpRequest.getFirstHeader("Location");
					if (loc != null) {
						String origUrl = url;
						url = loc.getValue();
						if (!url.startsWith("http")) {
							url = addHost(origUrl, url);
						}
						continue;
					}
				}
				if (status != 200 /* && status != 304 */) {
					throw new HttpException(status, statusMsg);
				}

				if (logger.isDebugEnabled()) {
					Header[] responseHeaders = response.getAllHeaders();
					for (int i = 0; i < responseHeaders.length; i++) {
						String name = responseHeaders[i].getName();
						String value = responseHeaders[i].getValue();
						logger.debug("Response header " + name + " = [" + value + "]");
					}
				}

				success = true;
				return response;
			} catch (MalformedURLException e) {
				String msg = "target URL = [" + url + "] is invalid.";
				logger.error(msg);
				throw new HttpException(HttpServletResponse.SC_NOT_FOUND, msg);
			} catch (HttpException e) {
				logger.error("HttpException " + METHOD + " url=" + url + " exception=" + e.getMessage());
				throw e;
			} catch (java.net.SocketTimeoutException e) {
				logger.error("SocketTimeoutException " + METHOD + " url=" + url + " exception=" + e.getMessage());
				throw new HttpException(HttpServletResponse.SC_GATEWAY_TIMEOUT,
						"Connection or Read timeout exception: " + e);
			} catch (Throwable t) {
				logger.error("HttpException " + METHOD + " url=" + url + " exception=" + t.getMessage());
				throw new HttpException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, t);
			} finally {
				// release the connection whenever we are not successful
				if ((!success) && (response != null)) {
					try {
						releaseResponse(response);
						response = null;
					} catch (IOException e) {
						logger.error("HttpHelper - problem releasing connection", e);
					}					
				}
			}			
		} while (!success && (++iter <= this.followRedirectMax));

		throw new HttpException(status, statusMsg);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() {
		ClientConnectionManager connectionManager = httpClient.getConnectionManager();
		if (connectionManager != null) {
			connectionManager.shutdown();
		}
		httpClient.getConnectionManager().shutdown();
	}

	/**
	 * Sets the max connections per host.
	 *
	 * @param maxConnectionsPerHost the new max connections per host
	 */
	public void setMaxConnectionsPerHost(int maxConnectionsPerHost) {
		this.maxConnectionsPerHost = maxConnectionsPerHost;
	}

	/**
	 * Sets the max total connections.
	 *
	 * @param maxTotalConnections the new max total connections
	 */
	public void setMaxTotalConnections(int maxTotalConnections) {
		this.maxTotalConnections = maxTotalConnections;
	}

	/**
	 * Sets the connection timeout.
	 *
	 * @param connectionTimeout the new connection timeout
	 */
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	/**
	 * Sets the socket timeout.
	 *
	 * @param socketTimeout the new socket timeout
	 */
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	/**
	 * Adds the host.
	 *
	 * @param url the url
	 * @param path the path
	 * @return the string
	 */
	private String addHost(String url, String path) {

		int start = url.indexOf("://");
		if (start == -1) {
			return path;
		}
		start += 3;
		if (path.startsWith("/")) {
			int i = url.indexOf("/", start);
			if (i == -1) {
				path = url + path;
			} else {
				path = url.substring(0, i) + path;
			}
		} else {
			int i = url.lastIndexOf("/");
			if ((i == -1) || (i < start)) {
				path = url + "/" + path;
			} else {
				path = url.substring(0, i + 1) + path;
			}
		}

		return path;
	}
}
