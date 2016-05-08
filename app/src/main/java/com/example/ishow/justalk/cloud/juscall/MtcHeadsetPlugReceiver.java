package com.example.ishow.justalk.cloud.juscall;

import java.lang.ref.WeakReference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class MtcHeadsetPlugReceiver extends BroadcastReceiver {
    
    public boolean mPlugged;
    
    public interface Callback {
        void mtcHeadsetStateChanged(boolean plugged);
    }

    public void setCallback(Callback callback) {
        mCallback = (callback == null) ? null : new WeakReference<Callback>(callback);
    }
    
    public Callback getCallback() {
        return (mCallback == null) ? null : mCallback.get();
    }
    
    public void start(Context context) {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        context.registerReceiver(this, intentFilter);
    }
    
    public void stop(Context context) {
        try {
            context.unregisterReceiver(this);
        } catch (Exception e) {
            // stop twice cause Exception
        }
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Callback callback = getCallback();
        if (callback == null) {
            stop(context);
            return;
        }
        int state = intent.getIntExtra("state", 0);
        mPlugged = (state == 1);
        callback.mtcHeadsetStateChanged(mPlugged);
    }
    
    private WeakReference<Callback> mCallback;
}
