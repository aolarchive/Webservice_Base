/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aol.webservice_base.util.misc;

/**
 *
 * @author tjj
 */
public class Sleep {

    /**
    /* Sleep N milliseconds
     */
    public static boolean milliSleep(long n) {
        boolean interrupted = false;

        if (n <= 0) {
            return false;
        }
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            interrupted = true;
        }

        return interrupted;
    }
}
