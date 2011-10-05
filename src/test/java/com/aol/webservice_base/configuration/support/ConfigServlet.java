/**
 * 
 */
package com.aol.webservice_base.configuration.support;

import com.aol.webservice_base.configuration.ConfigurationException;
import com.aol.webservice_base.servlet.BaseServlet;

/**
 * @author human
 *
 */
public class ConfigServlet extends BaseServlet {
	@Override
	protected void initializeConfig() throws ConfigurationException {
		Config.getTestInstance().initializeServlet(this);
	}
	
}
