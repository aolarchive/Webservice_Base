/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

 */
package com.aol.webservice_base.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;

import com.aol.webservice_base.configuration.Configuration;
import com.aol.webservice_base.configuration.ConfigurationException;
import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;
import com.aol.webservice_base.stats.BaseStatistics;
import com.aol.webservice_base.stats.StatisticsManager;
import com.aol.webservice_base.util.misc.Pair;
import com.aol.webservice_base.validator.AbstractValidatorPlugin;
import com.aol.webservice_base.view.RenderException;
import com.aol.webservice_base.view.RenderView;

public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = 8559817022819359039L;
    protected String requestIdParameter = "r";
    protected String jsonCallbackParameter = "c";
    protected String xmlNamespace = null;
    protected String requestClass = null;
    protected List<AbstractValidatorPlugin> validators;
    private BaseStatistics stats;
    protected List<Pair<String, String>> responseHeaders;

    protected void setResponseData(HttpServletRequest req, Object data) {
        req.setAttribute(Constants.REQUEST_STATE, data);
    }

    @Override
    public void init() {
        // set up the validators
        validators = new ArrayList<AbstractValidatorPlugin>();

        // get the config for this servlet
        try {
            initializeConfig();
        } catch (ConfigurationException e) {
            throw new Error("Error: Configuration: " + e.getMessage());
        }

	  		// set up stats
	  		stats = StatisticsManager.getStatsHandler(this.getClass(), "Servlet", "Success Time", "Failure Time");
    }

    protected void initializeConfig() throws ConfigurationException {
   	 Configuration config = Configuration.getInstance();
   	 if (config == null)
   		 throw new ConfigurationException("Can't initialize servlet: " + this.getClass().getName() + " invalid config");
   	 else
   		 config.initializeServlet(this);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        long startTime = System.currentTimeMillis();

        RequestState state = new RequestState(req);
        state.setXmlNamespace(xmlNamespace);
        req.setAttribute(Constants.REQUEST_STATE, state);

        state.log(Level.DEBUG, this.getClass(), "Request started: ", req.getRequestURL());

        addResponseHeaders(resp);

        try {
            // set up the request state
            if (requestClass != null) {
                Class<?> clazz = Class.forName(requestClass);
                Object requestObject = clazz.newInstance();
                state.setRequestObject(requestObject);
            }

            // go through all configured plugins
            for (AbstractValidatorPlugin validator : validators) {
                validator.validateRequest(req);

                // stop processing validators once one fails
                if (!state.isValid()) {
                    break;
                }
            }

            // process the request
            if (state.isValid()) {
                super.service(req, resp);
            }
        } catch (Exception e) {
            // something bad happened - give user an error
            if (state.getStatusCode() == HttpServletResponse.SC_OK) {
                state.setError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
            e.printStackTrace();
        } catch (Throwable t) {
            // something bad happened - give user an error
            if (state.getStatusCode() == HttpServletResponse.SC_OK) {
                state.setError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, t.getMessage());
            }
            t.printStackTrace();
        }

        // write out response
        try {
            if (requestIdParameter != null) {
                state.setRequestId(req.getParameter(requestIdParameter));
            }
            if (jsonCallbackParameter != null) {
                state.setJsonCallback(req.getParameter(jsonCallbackParameter));
            }
            RenderView.render(req, resp);
        } catch (RenderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long duration = System.currentTimeMillis() - startTime;
        if (state.getStatusCode() == Constants.SC_OK) {
      	  stats.success(duration);
        } else {
      	  stats.failure(duration);
        }
        state.log(Level.DEBUG, this.getClass(), "Request status: ", state.getStatusCode(), " took: ", duration, "ms");
    }

    public void setRequestIdParameter(String requestIdParameter) {
        this.requestIdParameter = requestIdParameter;
    }

    public void setJsonCallbackParameter(String jsonCallbackParameter) {
        this.jsonCallbackParameter = jsonCallbackParameter;
    }

    public void setXmlNamespace(String xmlNamespace) {
        this.xmlNamespace = xmlNamespace;
    }

    public void setRequestClass(String requestClass) {
        this.requestClass = requestClass;
    }

    public void setValidators(List<AbstractValidatorPlugin> validators) {
        this.validators = validators;
    }

    public void setResponseHeader(String header) throws ConfigurationException {
        String[] parts = header.split(":\\s*", 2);
        if (parts.length != 2) {
            throw new ConfigurationException("Invalid header format: " + header);
        }

        if (responseHeaders == null) {
            responseHeaders = new ArrayList<Pair<String, String>>();
        }
        responseHeaders.add(new Pair<String, String>(parts[0], parts[1]));
    }

    private void addResponseHeaders(HttpServletResponse resp) {
        if (responseHeaders == null) {
            return;
        }

        for (Pair<String, String> h : responseHeaders) {
            resp.addHeader(h.getFirst(), h.getSecond());
        }
    }
}
