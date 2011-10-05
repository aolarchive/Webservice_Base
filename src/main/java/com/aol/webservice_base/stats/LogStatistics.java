package com.aol.webservice_base.stats;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class LogStatistics extends BaseStatistics {
	protected Logger logger;
	protected Level successLogLevel = Level.INFO;
	protected Level failureLogLevel = Level.INFO;
	
	@Override
	public void init() {
		logger = Logger.getLogger(clazz);
	}
	
	protected void doLog(Level level, String details, String description, long time) {
		if (logger.isEnabledFor(level)) {
			StringBuilder message = new StringBuilder();
			message.append(clazz.getName()).append(", ");
			if (item != null)
				message.append(item).append(", ");
			if (details != null)
				message.append(details).append(", ");
			if (description != null)
				message.append(description).append(", ");
			message.append(time).append("ms");
			logger.log(level, message.toString());
		}
	}
	
	@Override
	public void success(long time) {
		doLog(successLogLevel, null, successDescription, time);
	}
	@Override
	public void success(long time, String details) {
		doLog(successLogLevel, details, successDescription, time);
	}
	@Override
	public void failure(long time) {
		doLog(successLogLevel, null, failureDescription, time);	
	}
	@Override
	public void failure(long time, String details) {
		doLog(failureLogLevel, details, failureDescription, time);
	}
}
