package com.example.ishow.mipush;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.justalk.cloud.lemon.MtcCli;
import com.justalk.cloud.lemon.MtcCliConstants;

public class MtcService extends Service {

    public static boolean checkLogined(Context context) {
        if (MtcCli.Mtc_CliGetState() != MtcCliConstants.EN_MTC_CLI_STATE_LOGINED) {
            return false;
        }
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }
}
