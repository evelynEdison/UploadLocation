package com.xutong.uploadlocation;

import android.util.Log;

public class LogUtil {
    private static final boolean DEBUG = true;
//exec setprop log.tag.TAG VERBOSE to open log(just this TAG)
//then you can use logcat -s TAG to catch log 
    public static void i(String TAG, String msg) {
        if (BuildConfig.DEBUG && DEBUG) {
                Log.i(TAG, msg);
        }
    }
}
