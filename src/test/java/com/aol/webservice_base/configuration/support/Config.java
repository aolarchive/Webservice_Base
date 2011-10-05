package com.aol.webservice_base.configuration.support;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.aol.webservice_base.configuration.Configuration;
import com.aol.webservice_base.configuration.ConfigurationException;

public class Config extends Configuration {		
	// config data (xml) to be used for tests
	static String configData = null;
	
	// clears out the instance so each test starts a new
	public static void resetInstance() {
		config = null;		
	}

	public static synchronized Configuration getTestInstance() {
		if (config == null)
			config = new Config();
		return config;
	}	
	
	// changes the configuration between tests
	public static void setConfiguration(String configXML) {
		configData = null;
		if (configXML != null) {
			// ensure the XML starts with prefix
			if (!configXML.startsWith("<?xml")) {
				configXML = "<?xml version='1.0' encoding='utf-8'?>" + configXML;
			}
			configData = configXML;
		}		
	}

	// used to retrieve an object by name
	public Object getObject(String name) {
		return objects.get(name);
	}
	
	// overridden method to get the input stream from the configData string
	// this ignores the filename
	@Override
	protected InputStream getConfigurationStream(String filename) {
		try {
			return new ByteArrayInputStream(configData.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void exposedParseTextIntoDocument(String text) throws ConfigurationException {
		parseTextIntoDocument("TEST", text);
	}
}
