package com.aol.webservice_base.stats;

public class StatisticsManager {
	// defaults to simple built in stats logger
	protected static String statisticsClass = LogStatistics.class.getName();
	
	public StatisticsManager() {}

	public void setStatisticsClass(String statisticsClass) {
		StatisticsManager.statisticsClass = statisticsClass;
		try {
			Class.forName(statisticsClass);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static BaseStatistics getStatsHandler(Class<?> clazz, String item, String successDescription, String failureDescription) {
		try {
			BaseStatistics statistics = (BaseStatistics)Class.forName(statisticsClass).newInstance();
			statistics.init(clazz, item, successDescription, failureDescription);			
			return statistics;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
