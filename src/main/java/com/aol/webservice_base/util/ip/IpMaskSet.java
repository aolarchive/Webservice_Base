/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aol.webservice_base.util.ip;

import com.aol.webservice_base.configuration.ConfigurationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tjj
 */
public class IpMaskSet {

    protected Map<byte[], byte[]> ipMaskData = new HashMap<byte[], byte[]>();

    public void put(byte[] ip, byte[] mask) {
        ipMaskData.put(IpHelper.maskIp(ip, mask), mask);
    }

    public boolean matches(byte[] ip) {
        if (ip == null) {
            return false;
        }
        for (byte[] keyIp : ipMaskData.keySet()) {
            byte[] mask = ipMaskData.get(keyIp);
            byte[] maskedIp = IpHelper.maskIp(ip, mask);

            if (Arrays.equals(keyIp, maskedIp)) {
                return true;
            }
        }

        return false;
    }

    // Look for a string of the form "ip mask" so we can build this from config
    public void setIpMask(String s) throws ConfigurationException {
        String[] parts = s.split("\\s+");
        if (parts.length != 2) {
            throw new ConfigurationException("bad IP/MASK=" + s);
        }

        byte[] ip = IpHelper.getIpAddr(parts[0]);
        if (ip == null) {
            throw new ConfigurationException("bad IP in IP/MASK=" + parts[0]);
        }


        byte[] mask = IpHelper.getIpAddr(parts[1]);
        if (mask == null) {
            throw new ConfigurationException("bad mask in IP/MASK=" + parts[1]);
        }

        put(ip, mask);

    }
}
