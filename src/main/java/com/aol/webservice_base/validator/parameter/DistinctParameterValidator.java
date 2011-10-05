/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

 */
package com.aol.webservice_base.validator.parameter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;
import com.aol.webservice_base.validator.AbstractValidatorPlugin;

/**
 * The Class DistinctParameterValidator.
 * 
 * This class validates to ensure that only a single element in the list
 * is a parameter.  It does not validate values.
 * 
 * @author human
 */
public class DistinctParameterValidator extends AbstractValidatorPlugin {
    //protected String paramName = null; - encapsulated by paramNames

    protected List<String> paramNames = null;
    protected boolean required = true;
    protected static final String ONE_ALLOWED_FROM = "Only one parameter allowed from:";
    protected static final String ONE_REQUIRED_FROM = "One parameter required from:";

    public void setError(HttpServletRequest req, String prefix) {
        // we already found one - place error
        RequestState state = (RequestState) req.getAttribute(Constants.REQUEST_STATE);
        StringBuilder sbError = new StringBuilder(64);
        sbError.append(prefix);
        for (String paramName : paramNames) {
            sbError.append(' ').append(paramName);
        }

        int errCode = Constants.SC_MISSING_PARAMETER;
        if (prefix.equals(ONE_ALLOWED_FROM)) {
            errCode = Constants.SC_INVALID_PARAMETER;
        }
        state.addParameterError(errCode, sbError.toString());
    }

    @Override
    public void doValidateRequest(HttpServletRequest req) {
        boolean found = false;
        for (String param : paramNames) {
            if (req.getParameter(param) != null) {
                if (!found) {
                    found = true;
                } else {
                    setError(req, ONE_ALLOWED_FROM);
                    return;
                }
            }
        }

        if (!found && required) {
            setError(req, ONE_REQUIRED_FROM);
        }
    }

    public void setParamName(String paramName) {
        if (paramNames == null) {
            paramNames = new ArrayList<String>();
        }
        this.paramNames.add(paramName);
    }

    public void setParamNames(List<String> paramNames) {
        this.paramNames = paramNames;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }


}
