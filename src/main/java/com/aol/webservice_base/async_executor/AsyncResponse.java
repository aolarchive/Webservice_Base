/*
Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.
*/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aol.webservice_base.async_executor;

/**
 *
 * @author elenfurman
 */
public abstract class AsyncResponse {
    public abstract Object getResult();
    public abstract String getResultSet();
    public abstract void setResult (Object result);
    public abstract void setResultSet (String resultSet);
    public abstract void setUrl (String url);
    public abstract String getUrl ();
}
