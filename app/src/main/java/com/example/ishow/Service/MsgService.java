package com.example.ishow.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by MRME on 2016-04-25.
 */
public class MsgService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return new MsgServiceBind();
    }

    public class MsgServiceBind extends Binder {
        public MsgService getInstace() {
            return MsgService.this;
        }
    }
}
