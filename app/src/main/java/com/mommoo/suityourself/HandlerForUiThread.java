package com.mommoo.suityourself;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by mommoo on 2016-04-07.
 */
public class HandlerForUiThread extends Handler {
    private HandlerEvent event;
    public HandlerForUiThread(){}
    public void setHandlerEvent(HandlerEvent event){
        this.event = event;
    }
    @Override
    public void handleMessage(Message msg){
            event.event();
    }
}
