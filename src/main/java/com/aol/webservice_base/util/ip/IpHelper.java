/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.aol.webservice_base.util.ip;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tjj
 */
public class IpHelper {

    public static byte[] getIpAddr(String s) {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(s);
        } catch (UnknownHostException ex) {
            Logger.getLogger(IpHelper.class.getName()).log(Level.SEVERE, "name= " + s, ex);
            return null;
        }
        return addr.getAddress();
    }

    public static byte[] maskIp(byte[] ip, byte[] mask) {
        if (ip == null) {
            return null;
        }

        if (mask.length != ip.length) {
            return null;
        }

        byte[] result = new byte[ip.length];
        for (int i = 0; i < ip.length; i++) {
            result[i] = (byte) (ip[i] & mask[i]);
        }
        return result;
    }
}
