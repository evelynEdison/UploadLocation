package com.xutong.uploadlocation;

/**
 * Created by Administrator on 2015/11/2.
 */
public class Constants {

    public static final String WEB_SERVICE_NAMESPACE = "http://webservice.app.eams.hm.com";
    public static final String WEB_SERVICE_URL= "http://119.145.111.140/getLocation.ws";

    public static final String DEFAULT_IP_ADDRESS = "119.145.111.140";
    public static final String DEFAULT_PORT = "8080";

    public static final String METHOD_USER_LOGIN = "userLogin";
    public static final String METHOD_SAVE_LOCATION = "saveLocationMes";

    public static final int DEFAULT_RETRY_LIMIT = 5;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAIL = 2;

    public static final String PREF_NAME = "settings";
    public  static final String KEY_IP = "ip";
    public static final String KEY_PORT = "port";

    public static final String KEY_USERID = "userid";
    public static final String KEY_PWD = "pwd";
    public static final String KEY_REM_USERID = "rem_userid";
    public static final String KEY_REM_PWD = "rem_pwd";
}
