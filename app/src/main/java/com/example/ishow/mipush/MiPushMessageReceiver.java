package com.example.ishow.mipush;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.ToastUtil;
import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcCli;
import com.justalk.cloud.lemon.MtcCliConstants;
import com.justalk.cloud.lemon.MtcConstants;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MiPushMessageReceiver extends PushMessageReceiver {
   
    @Override
    public void onReceiveMessage(Context context, MiPushMessage message) {
    	
        MtcWakeLock.acquire(context, 180000);    	
        context.startService(new Intent(context, MtcService.class));
        if (MtcCli.Mtc_CliGetState() == MtcCliConstants.EN_MTC_CLI_STATE_LOGINED) {
        	MtcCli.Mtc_CliRefresh();
        } else {
        	//SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
	    	String username = SharePrefrence.getInstance().getStudentName(context);
			String password = SharePrefrence.getInstance().getStudentPassword(context);
			String server = "sudp:ae.justalkcloud.com:9851";
			JSONObject json = new JSONObject();
	        try {
	            json.put(MtcApi.KEY_SERVER_ADDRESS, server);
	            json.put(MtcApi.KEY_PASSWORD, password);
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
	        
	        if (MtcApi.login(username, json) == MtcConstants.ZOK) {
                ToastUtil.showToast(context,"重新登录成功!");
	        	//BaseUtil.showText(context, "��½�ɹ�");
	        }
        }
    }
    
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
       
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                List<String> arguments = message.getCommandArguments();
                String regId = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
                if (!TextUtils.isEmpty(regId)) {
                   MiPushClient.setAlias(context, "JusTalkCloudSample", null);
                   MiPush.register(context, regId);
                }
            }
        }
    }
}
