package com.example.ishow.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.ishow.Bean.MsgEntry;
import com.example.ishow.Utils.Interface.TextUtil;
import com.example.ishow.iShowConfig.iShowConfig;
import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcBuddyConstants;
import com.justalk.cloud.lemon.MtcCliConstants;
import com.justalk.cloud.lemon.MtcImConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by MRME on 2016-03-02.
 */
public class JustalkStateCheckReciver extends BroadcastReceiver {
    private int teacherStatus;
    private boolean isSaved =false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent!=null){
            if ("MtcBuddyQueryLoginInfoOkNotification".equals(intent.getAction())){
                int cookie = intent.getIntExtra(MtcApi.EXTRA_COOKIE, -1);
                String extraInfo = intent.getStringExtra(MtcApi.EXTRA_INFO);
                try {
                    JSONObject json = (JSONObject) new JSONTokener(extraInfo).nextValue();
                    teacherStatus = json.optInt(MtcBuddyConstants.MtcBuddyStatusKey);
                    if (teacherStatus == MtcCliConstants.MTC_ACCOUNT_STATUS_ONLINE){
                        if (checkListner!=null){
                            checkListner.onCheckJustalkStateNotification(true);
                        }
                    }else{
                        if (checkListner!=null)checkListner.onCheckJustalkStateNotification(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else if("MtcBuddyQueryLoginInfoDidFailNotification".equals(intent.getAction())){
                if(checkListner!=null)
                checkListner.onCheckJustalkStateNotification(false);
            }else if(MtcImConstants.MtcImSendDidFailNotification.equals(intent.getAction())){
                if(checkListner!=null)
                    checkListner.onTextSendFaild(intent);
                //消息发送失败的接口
            }else if (MtcImConstants.MtcImSendOkNotification.equals(intent.getAction())){
                if(checkListner!=null)
                    checkListner.onTextSendOK(intent);
                //消息发送成功回调Bundle[{extra_info={"MtcImUserUriKey":"[username:15555043403@863597281_qq.com]","MtcImMsgIdKey":63,"MtcImTextKey":"{\"msg\":\"同学在吗⊙_⊙\",\"msgTime\":1461754413492,\"fromId\":\"20757\",\"fromImg\":\"http:\\\/\\\/7xlm33.com1.z0.glb.clouddn.com\\\/2016-03-17\\\/56ea6691dcdf5.jpg\",\"fromPhone\":\"15555043403\",\"fromName\":\"LOL\"}"}, extra_cookie=0}]
            }else if (MtcImConstants.MtcImTextDidReceiveNotification.equals(intent.getAction())){
                try {
                    String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                    JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                    String text = json.getString(MtcImConstants.MtcImTextKey);
                    JSONObject j = new JSONObject(text);
                    MsgEntry entry = new MsgEntry();
                    entry.setFromUserid(j.getString("fromId"));
                    entry.setFromNick(j.getString("fromName"));
                    entry.setFromMobile(j.getString("fromPhone"));
                    entry.setFromImg(j.getString("fromImg"));
                    entry.setMsgTime(j.getLong("msgTime"));
                    entry.setRead(TextUtils.equals(iShowConfig.talingUid,entry.getFromUserid())&&iShowConfig.chatISOpen?true:false);
                    entry.setState(1);
                    entry.setTextMsg(j.getString("msg"));
                    entry.setMsgType("txt");
                    if(checkListner!=null)
                        checkListner.onTextRecevier(entry);
                    //接到消息通知
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public JustalkStateCheckListner checkListner;
    public interface  JustalkStateCheckListner {
        public void onCheckJustalkStateNotification(boolean state);
        public void onTextSendFaild(Intent In);
        public void onTextSendOK(Intent In);
        public void onTextRecevier(MsgEntry In);
    }
    public void setOnJustalkStateCheckListner(JustalkStateCheckListner l){
        this.checkListner =l;
    }

}

