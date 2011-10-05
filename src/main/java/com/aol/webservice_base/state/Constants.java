/*

Copyright (c) 2009-2011, AOL Inc.
All rights reserved.

This code is licensed under a BSD license.

Howard Uman

*/

package com.aol.webservice_base.state;

public final class Constants {

    public static final String CONFIG_FILE = "config.xml";
    public static final String REQUEST_STATE = "requestState";
    
    public static final int SC_OK = 200;
    public static final int SC_PARTIAL_SUCCESS = 201;
    public static final int SC_INTERNAL_SERVER_ERROR = 400;
    public static final int SC_AUTHORIZATION_REQUIRED = 401;
    public static final int SC_FORBIDDEN = 403;
    public static final int SC_DUPLICATE_ROW = 403;
    public static final int SC_NOT_FOUND = 404;
    public static final int SC_RATE_LIMITED = 430;
    public static final int SC_INVALID_DEVID = 440;
    public static final int SC_PERMISSION_DENIED = 451;
    public static final int SC_MISSING_PARAMETER = 460;
    public static final int SC_INVALID_PARAMETER = 462;
    public static final int SC_TARGET_DOES_NOT_EXIST = 601;
    public static final int SC_FILE_TOO_BIG = 606;
    public static final int SC_DATA_LIMIT = 607;

    public static final String ST_OK = "Ok";
    public static final String ST_PARTIAL_SUCCESS = "Partial Success";
    public static final String ST_INTERNAL_SERVER_ERROR = "Server Error";
    public static final String ST_AUTHORIZATION_REQUIRED = "Authorization required";
    public static final String ST_FORBIDDEN = "Forbidden";
    public static final String ST_NOT_FOUND = "Not found";
    public static final String ST_RATE_LIMITED = "Rate limited";
    public static final String ST_PERMISSION_DENIED = "Permission Denied";
    public static final String ST_DUPLICATE_ROW = "Duplicate entry";
    public static final String ST_INVALID_DEVID = "Invalid DevId";
    public static final String ST_TARGET_DOES_NOT_EXIST = "Target does not exist";
    public static final String ST_FILE_NOT_FOUND = "File not found";
    public static final String ST_FILE_TOO_BIG = "File too big";
    public static final String ST_DATA_LIMIT = "Data limit reached.";
}
