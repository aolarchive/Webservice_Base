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
import com.aol.webservice_base.state.RequestState;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import java.util.List;
import java.util.ArrayList;

public class AsyncExecutor {

    protected  ExecutorService executorService = null;

    public AsyncResponse[] executeThreads (final RequestState state, List<AsyncRequest> requests) {

        List<Callable<AsyncResponse>> callableList = new ArrayList<Callable<AsyncResponse>>();
        List<Future<AsyncResponse>> resultList = null;
        AsyncResponse[] resultArray = null;

        for (final AsyncRequest request : requests) {
            Callable<AsyncResponse> c = new Callable<AsyncResponse>() {
                public AsyncResponse call() throws Exception {
                    return request.process(state);
                }
            };
            callableList.add(c);
        }
        try {
            resultList = executorService.invokeAll(callableList);
            resultArray = new AsyncResponse[resultList.size()];

            for (int i = 0; i < resultList.size(); i++) {
                resultArray[i] = resultList.get(i).get();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //     e.printStackTrace();
        }
        return resultArray;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
    
}
