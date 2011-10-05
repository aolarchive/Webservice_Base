/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.configuration.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.aol.webservice_base.configuration.Configuration;

/**
 * @author human
 *
 */
public class ValidateConfigConfiguration extends Configuration {
	static ValidateConfigConfiguration config;
	static String useInitialFile = null;
	String initialPath = null;
	
	public static void setUseInitialFile(String useInitialFile) {
		ValidateConfigConfiguration.useInitialFile = useInitialFile;
	}	
	
	public static synchronized ValidateConfigConfiguration getInstance() {
		if (config == null)
			config = new ValidateConfigConfiguration();
		return config;
	}
	
	
	@Override
	protected InputStream getConfigurationStream(String filename) {
		if (useInitialFile == null) {
			throw new Error("Must call setUseInitialFile() first");
		}
		// this implementation will track the directory of the first file requested
		// so subsequent imports will leverage the same base path
		if (initialPath == null) {
			// don't respect the default forced by the parent class (config.xml)
			// so respect the initial file specified
			filename = useInitialFile;
			
			// break up the initial file as detailed above
			int slashLoc = filename.lastIndexOf('/');
			initialPath = (slashLoc < 0) ? "" : filename.substring(0, slashLoc+1);
			filename = (slashLoc < 0) ? filename : filename.substring(slashLoc+1);

		}

		// build the filename 
		filename = initialPath + filename;
			
		try {
			return new FileInputStream(filename);
		}
		catch (FileNotFoundException e) {
			throw new Error("Could not find file: " + filename);
		}
		//return Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
	}
}
