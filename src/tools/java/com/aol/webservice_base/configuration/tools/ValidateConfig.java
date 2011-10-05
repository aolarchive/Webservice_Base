/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.configuration.tools;

import com.aol.webservice_base.configuration.tools.ValidateConfigConfiguration;
/**
 * ValidateConfig
 * test Webservice_Base compatible config files.
 * NOTE: CLASSPATH should include all dependencies of Webservice_Base and your application
 * 
 * @author human
 *
 */
public final class ValidateConfig {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Webservice_Base Configuration Validator Tool");
		if (args.length != 1) {
			System.out.println("Usage: ValidateConfig [/path/to/config]");
			return;
		}
		
		String filename = args[0];
		
		// we use subclassed Configuration to force file reading differently
		ValidateConfigConfiguration.setUseInitialFile(filename);
		
		try {
			ValidateConfigConfiguration configuration = ValidateConfigConfiguration.getInstance();
			System.out.println("\nSUCCESS: If you can read this, it looks like things are valid.");
		} catch (Throwable t) {
			System.out.println("ERROR in config: " + t.getMessage());
			t.printStackTrace();			
			// show error again - easier to find :)
			System.out.println("ERROR in config (repeat): " + t.getMessage());
		}
		System.out.println();
	}

}
