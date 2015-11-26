package com.xutong.uploadlocation;

/**
 * Created by Rabbpig on 2015/4/11.
 */
public class StatusEvent {
    public int status;
    public String response;

    public StatusEvent(int status){
        this.status = status;
    }

    public StatusEvent(int status, String response){
        this.status = status;
        this.response = response;
    }
}
