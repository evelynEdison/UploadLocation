package com.xutong.uploadlocation;

import org.ksoap2.serialization.PropertyInfo;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/11/2.
 */
public class WebserviceUtil {

    public static Object userLogin(String loginId , String password) throws XmlPullParserException, IOException {
        List<PropertyInfo> params = new ArrayList<>();
        params.add(Utilities.getPropertyInfo("loginId",loginId));
        params.add(Utilities.getPropertyInfo("password",password));
        return Utilities.getResponseNotCatchException(params,Constants.METHOD_USER_LOGIN);
    }

    public static Object saveLocationMes(String loginId , String lng , String lat , String address) throws XmlPullParserException, IOException {
        List<PropertyInfo> params = new ArrayList<>();
        params.add(Utilities.getPropertyInfo("loginId",loginId));
        params.add(Utilities.getPropertyInfo("lng",lng));
        params.add(Utilities.getPropertyInfo("lat",lat));
        params.add(Utilities.getPropertyInfo("address",address));
        return Utilities.getResponseNotCatchException(params,Constants.METHOD_SAVE_LOCATION);
    }

}
