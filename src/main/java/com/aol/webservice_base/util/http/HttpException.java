/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.util.http;

public class HttpException extends Exception {
	private int status;

	public HttpException(int status, String msg) {
		super(msg);
		setStatus(status);
	}

	public HttpException(int status, Throwable cause) {
		super(cause);
		setStatus(status);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
