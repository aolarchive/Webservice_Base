package com.aol.webservice_base.stats;

public abstract class BaseStatistics {
	protected Class<?> clazz;
	protected String item;
	protected String successDescription;
	protected String failureDescription;
	
	public final void init(Class<?> clazz, String item, String successDescription, String failureDescription) {
		this.clazz = clazz;
		this.item = item;
		this.successDescription = successDescription;
		this.failureDescription = failureDescription;
		
		this.init();
	}
	
	public abstract void init();	
	public abstract void success(long time);
	public abstract void failure(long time);
	public void success(long time, String details) {
		success(time);
	}
	public void failure(long time, String details) {
		failure(time);
	}
}
