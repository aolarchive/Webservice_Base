/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

 */
package com.aol.webservice_base.util.http;

import com.aol.webservice_base.configuration.Configuration;
import com.aol.webservice_base.state.Constants;
import com.aol.webservice_base.state.RequestState;
import com.aol.webservice_base.util.ip.IpHelper;
import com.aol.webservice_base.util.ip.IpMaskSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Level;

/**
 * @author human
 *
 */
public class RequestHelper {

    public static String getClientIP(HttpServletRequest req) {
        String ip = req.getRemoteAddr();
        Configuration config = Configuration.getInstance();
        IpMaskSet trustedIps = (IpMaskSet) config.getObjectById("systemTrustedIps");

        if (trustedIps == null) {
            return ip;
        }

        // If client IP isn't trusted, we are done
        byte[] peerIp = IpHelper.getIpAddr(ip);
        if (!trustedIps.matches(peerIp)) {
            return ip;
        }


        List<String> xForwardedFor = getXForwardedFor(req);
        if (xForwardedFor.size() > 0) {
            for (int xffCheck = xForwardedFor.size() - 1; xffCheck >= 0; xffCheck--) {
                String xffItem = xForwardedFor.get(xffCheck);
                byte[] xffIp = IpHelper.getIpAddr(xffItem);

                if (xffIp == null) {
                    return ip;  // don't trust messed up header;
                }

                if (!trustedIps.matches(xffIp)) {
                    return xffItem;   // Not trusted, so it is as close to the client as we can get
                }
            }

            // no untrusted IP found - just use the first one
            ip = xForwardedFor.get(0);
        }

        return ip;
    }

    /**
     * Gets the client ip.
     *
     * @param req the request
     * @param trustXForwardedFor the trust x forwarded for header
     *
     * @return the client ip
     */
    public static String getClientIP(HttpServletRequest req, boolean trustXForwardedFor, Hashtable<List<Integer>, List<Integer>> trustedIpMaskChecks) {
        String ip = req.getRemoteAddr();

        if (trustXForwardedFor) {
            List<String> xForwardedFor = getXForwardedFor(req);

            if (xForwardedFor.size() > 0) {
                if (trustedIpMaskChecks != null) {
                    for (int xffCheck = xForwardedFor.size() - 1; xffCheck >= 0; xffCheck--) {
                        String xffItem = xForwardedFor.get(xffCheck);
                        try {
                            List<Integer> xffIpData = getIPSet(xffItem, "validating xforwarded for header data");

                            boolean unknownIP = true;
                            for (List<Integer> keyData : trustedIpMaskChecks.keySet()) {
                                List<Integer> maskData = trustedIpMaskChecks.get(keyData);

                                // validate each segment of key by the mask
                                for (int i = 0; i < 4; i++) {
                                    if ((xffIpData.get(i) & maskData.get(i)) != (keyData.get(i) & maskData.get(i))) {
                                        break;
                                    } else if (i == 3) {
                                        unknownIP = false;
                                    }
                                }
                            }
                            if (unknownIP) {
                                return xffItem;
                            }
                        } catch (Exception e) {
                            // invalid IP should not lead to anything being trusted
                            RequestState state = (RequestState) req.getAttribute(Constants.REQUEST_STATE);
                            state.log(Level.DEBUG, RequestHelper.class, "Invalid XFF Header data item received: ", xffItem);
                        }
                    }
                }

                // no untrusted IP found - just use the first one
                ip = xForwardedFor.get(0);
            }
        }

        return ip;
    }

    public static List<Integer> getIPSet(String ip, String errorType) throws Exception {
        String[] ipData = ip.split("\\.");
        if (ipData.length != 4) {
            throw new Exception(errorType + " must be w.x.y.z format");
        }

        List<Integer> ipInts = new ArrayList<Integer>(4);
        try {
            for (int i = 0; i < 4; i++) {
                ipInts.add(Integer.parseInt(ipData[i]));
            }
        } catch (NumberFormatException nfe) {
            throw new Exception(errorType + " must be w.x.y.z (numeric) format");
        }
        return ipInts;
    }

    /**
     * Gets the client ips.
     *
     * @param req the request
     *
     * @return the client ips (closest is first)
     */
    public static List<String> getClientIPs(HttpServletRequest req) {
        // put the X-Forwarded-For headers in a list.
        List<String> xForwardedFor = getXForwardedFor(req);
        List<String> ips = new ArrayList<String>(xForwardedFor.size() + 1);

        // put the real IP & forwarded ips into the list
        ips.addAll(xForwardedFor);
        ips.add(req.getRemoteAddr());

        return Collections.unmodifiableList(ips);
    }

    /**
     * Gets the x forwarded for.
     *
     * @param req the request
     *
     * @return the x forwarded for header values - most recent is first
     */
    @SuppressWarnings("unchecked")
    protected static List<String> getXForwardedFor(HttpServletRequest req) {
        Enumeration<String> headerEnum = (Enumeration<String>) req.getHeaders("X-Forwarded-For");
        List<String> headerList = (null == headerEnum) ? new ArrayList<String>() : java.util.Collections.list(headerEnum);

        // now put any of the forwarded for headers on the list too (skipping duplicates)
        // we insert to front so the most applicable is in the front
        ArrayList<String> ips = new ArrayList<String>(headerList.size() + 1);
        for (String xForwardedHeaders : headerList) {
            String[] splitXForwardedHeaders = xForwardedHeaders.split(",");
            for (int i = 0; i < splitXForwardedHeaders.length; i++) {
                String xHeader = splitXForwardedHeaders[i].trim();
                if (!ips.contains(xHeader)) {
                    ips.add(xHeader);
                }
            }
        }

        return Collections.unmodifiableList(ips);
    }
}
