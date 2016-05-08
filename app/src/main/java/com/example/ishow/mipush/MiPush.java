package com.example.ishow.mipush;

import android.content.Context;
import android.content.Intent;

import com.justalk.cloud.lemon.MtcCli;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.json.JSONObject;

public class MiPush {
    
    public static void start(Context context, String miPushAppId, String miPushAppKey) {
    	context.startService(new Intent(context, MtcService.class));
        MiPushClient.registerPush(context, miPushAppId, miPushAppKey);
    }
    
    public static void stop(Context context) {
    	context.stopService(new Intent(context, MtcService.class));
        MiPushClient.unregisterPush(context);
    }

    public static void register(Context context, String regId) {
        JSONObject json = new JSONObject();
        try {
            json.put("Notify.MiPush.AppId", context.getPackageName());
            json.put("Notify.MiPush.RegId", regId);
            json.put("Notify.MiPush.Invite.Payload", 
                    "{\"calltype\":\"${MediaType}\",\"caller\":\"${Caller}\",\"callid\":\"${CallId}\"}");
            json.put("Notify.MiPush.Invite.Expiration", "60");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String info = json.toString();
        MtcCli.Mtc_CliSetPushParm(info);
    }    
}
