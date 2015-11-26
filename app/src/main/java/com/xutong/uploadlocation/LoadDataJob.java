package com.xutong.uploadlocation;

import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/5/8.
 */
public class LoadDataJob extends Job {
    public static final int PRIORITY = 1;

    private LoadResponse mListener;

    public interface LoadResponse{
        void onAdded();
        Object onRequest()throws XmlPullParserException, IOException;
        void onResponse(String json);
        void onError();

    }

    public LoadDataJob(LoadResponse listener) {
        super(new Params(PRIORITY));
        this.mListener = listener;
    }

    @Override
    public void onAdded() {
        EventBus.getDefault().register(this);
        mListener.onAdded();
    }

    @Override
    public void onRun() throws Throwable {
        Object object = mListener.onRequest();
        if(object != null && object instanceof SoapObject){
            SoapObject soapObject = (SoapObject)object;
            String json = soapObject.getProperty(0).toString();
            LogUtil.i("Grace", "respone = " + json);
            EventBus.getDefault().post(new StatusEvent(Constants.STATUS_SUCCESS,json));
        }else {
            EventBus.getDefault().post(new StatusEvent(Constants.STATUS_FAIL));
        }
    }

    @Override
    protected void onCancel() {
        EventBus.getDefault().post(new StatusEvent(Constants.STATUS_FAIL));
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        throwable.printStackTrace();
        return true;
    }

    @Override
    protected int getRetryLimit() {
        return Constants.DEFAULT_RETRY_LIMIT;
    }

    public void onEventMainThread(StatusEvent event){
        if(event.status == Constants.STATUS_SUCCESS){
            mListener.onResponse(event.response);
        }else {
            mListener.onError();
        }

        EventBus.getDefault().unregister(this);
    }

}
