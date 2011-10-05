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

import com.aol.webservice_base.state.RequestState;

/**
 *
 * @author elenfurman
 */
public abstract class AsyncRequest {
    public abstract AsyncResponse process(RequestState state);
}
