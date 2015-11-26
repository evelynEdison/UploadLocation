package com.xutong.uploadlocation;

import android.app.Application;
import android.util.Log;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;

/**
 * Created by Administrator on 2015/11/2.
 */
public class UploadApp extends Application{
    private static UploadApp instance;
    private JobManager mJobManager;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        configureJobManager();
    }

    private void configureJobManager() {
        Configuration configuration = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";
                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);

                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));

                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));

                    }
                })
                .minConsumerCount(1)
                .maxConsumerCount(6)
                .loadFactor(3)
                .consumerKeepAlive(120)
                .build();
        mJobManager = new JobManager(this, configuration);
    }

    public JobManager getJobManager(){
        return mJobManager;
    }

    public static UploadApp getInstance(){
        return instance;
    }
}
