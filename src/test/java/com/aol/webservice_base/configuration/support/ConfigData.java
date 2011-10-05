/**
 * 
 */
package com.aol.webservice_base.configuration.support;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author human
 *
 */

public class ConfigData {
	public boolean bool;
	public Boolean boolClass;
	public int numInt;
	public long numLong;
	public double numDouble;
	public float numFloat;
	public Integer numIntClass;
	public Long numLongClass;
	public Double numDoubleClass;
	public Float numFloatClass;
	public String string;
	public Object userObj;
	public ArrayList<String> listStrings;
	public ArrayList<ConfigData> listUserObj;
	public ArrayList<Integer> listIntClass;	
	public Map<String,String> mapStrings;
	public Map<ConfigData,ConfigData> mapUserObj;
	public Map<Integer,Integer> mapIntClass;	
	public ArrayList<String> items;
	public ArrayList<String> items2;
	
	// TODO: arrays not yet supported
	//public String[] arrayStrings;
	//public TestConfigData[] arrayUserObj;
	
	static ConfigData instance = null;

	public ConfigData() {}
	
	public static void resetInstance() {
		instance = null;
	}
	public static ConfigData getInstance() {
		if (instance == null)
			instance = new ConfigData();
		return instance;
	}
	protected static ConfigData getInstanceProtected() {
		if (instance == null)
			instance = new ConfigData();
		return instance;
	}
	
	public void init() {
		System.out.println("INIT!");
	}

	public static void initStatic() {
		System.out.println("INIT STATIC!");
	}	
	
	public void setBool(boolean bool) {
		this.bool = bool;
	}
	public void setBoolClass(Boolean boolClass) {
		this.boolClass = boolClass;
	}		
	public void setNumInt(int numInt) {
		this.numInt = numInt;
	}
	public void setNumLong(long numLong) {
		this.numLong = numLong;
	}
	public void setNumDouble(double numDouble) {
		this.numDouble = numDouble;
	}
	public void setNumFloat(float numFloat) {
		this.numFloat = numFloat;
	}
	public void setNumIntClass(Integer numIntClass) {
		this.numIntClass = numIntClass;
	}
	public void setNumLongClass(Long numLongClass) {
		this.numLongClass = numLongClass;
	}
	public void setNumDoubleClass(Double numDoubleClass) {
		this.numDoubleClass = numDoubleClass;
	}
	public void setNumFloatClass(Float numFloatClass) {
		this.numFloatClass = numFloatClass;
	}
	public void setString(String string) {
		this.string = string;
	}
	public void setUserObj(Object userObj) {
		this.userObj = userObj;
	}

	public void setListStrings(ArrayList<String> listStrings) {
		this.listStrings = listStrings;
	}

	public void setListUserObj(ArrayList<ConfigData> listUserObj) {
		this.listUserObj = listUserObj;
	}

	public void setListIntClass(ArrayList<Integer> listIntClass) {
		this.listIntClass = listIntClass;
	}
	
	public void setMapStrings(Map<String,String> mapStrings) {
		this.mapStrings = mapStrings;
	}

	public void setMapUserObj(Map<ConfigData,ConfigData> mapUserObj) {
		this.mapUserObj = mapUserObj;
	}

	public void setMapIntClass(Map<Integer,Integer> mapIntClass) {
		this.mapIntClass = mapIntClass;
	}		
	
	public void setItems(ArrayList<String> items) {
		this.items = items;
	}
	public void setItems(String items) {
		this.items = new ArrayList<String>();
		this.items.add(items);
	}

	// flip order of methods - testing the alt technique
	public void setItems2(String items) {
		this.items2 = new ArrayList<String>();
		this.items2.add(items);
	}	
	public void setItems2(ArrayList<String> items) {
		this.items2 = items;
	}

	public static ConfigData configDataFactory(int i) {
		ConfigData item = new ConfigData();
		item.numInt = i;
		return item;
	}

	public static ConfigData configDataFactory(int i, long l) {
		ConfigData item = new ConfigData();
		item.numInt = i;
		item.numLong = l;
		return item;
	}
}
