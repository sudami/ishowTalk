package com.example.ishow.justalk.cloud.juscall;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcCall;
import com.justalk.cloud.lemon.MtcCallConstants;
import com.justalk.cloud.lemon.MtcConstants;
import com.justalk.cloud.lemon.MtcMdm;
import com.justalk.cloud.lemon.MtcMediaConstants;
import com.justalk.cloud.zmf.ZmfAudio;
import com.justalk.cloud.zmf.ZmfVideo;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MtcCallDelegate {

    public interface State {
        public static final int CALL_STATE_NONE = 0;
        public static final int CALL_STATE_INCOMING = CALL_STATE_NONE + 1;
        public static final int CALL_STATE_ANSWERING = CALL_STATE_INCOMING + 1;
        public static final int CALL_STATE_CALLING = CALL_STATE_ANSWERING + 1;
        public static final int CALL_STATE_OUTGOING = CALL_STATE_CALLING + 1;
        public static final int CALL_STATE_ALERTED_RINGING = CALL_STATE_OUTGOING + 1;
        public static final int CALL_STATE_CONNECTING = CALL_STATE_ALERTED_RINGING + 1;
        public static final int CALL_STATE_TALKING = CALL_STATE_CONNECTING + 1;
        public static final int CALL_STATE_TIMING = CALL_STATE_TALKING + 1;
        public static final int CALL_STATE_PAUSED = CALL_STATE_TIMING + 1;
        public static final int CALL_STATE_DISCONNECTED = CALL_STATE_PAUSED + 1;
        public static final int CALL_STATE_TERM_RINGING = CALL_STATE_DISCONNECTED + 1;
        public static final int CALL_STATE_ENDING = CALL_STATE_TERM_RINGING + 1;
        public static final int CALL_STATE_DECLINING = CALL_STATE_ENDING + 1;

        public static final int CALL_FAIL_CALL = -1;
        public static final int CALL_FAIL_ANSWER = -2;
        public static final int CALL_FAIL_CALL_DISCONNECTED = -3;
        public static final int CALL_FAIL_CALL_PICKUPX = -4;
        public static final int CALL_FAIL_AUDIO_DEVICE = -5;
        public static final int CALL_FAIL_CALL_AUDIO_INIT = -6;
        public static final int CALL_FAIL_ANSWER_AUDIO_INIT = -7;
    }

    public interface Info {
        public static final String CALL_INFO_CALL_PAUSE = "Call Pause";
        public static final String CALL_INFO_CALL_INTERRUPT = "Call Interrupt";
        public static final String CALL_INFO_CALL_RESUME = "Call Resume";
        public static final String CALL_INFO_VIDEO_PAUSE = "Video Pause";
        public static final String CALL_INFO_VIDEO_RESUME = "Video Resume";
        public static final String CALL_INFO_VIDEO_OFF = "Video Off";
        public static final String CALL_INFO_VIDEO_ON = "Video On";
    }

    public interface Callback extends State, Info {

        public void mtcCallDelegateIncoming(int dwCallId);
        public void mtcCallDelegateCall(String number, String name, String peerName, boolean isVideo);
        public void mtcCallDelegateOutgoing(int dwCallId);
        public void mtcCallDelegateAlerted(int dwCallId, int dwAlertType);
        public void mtcCallDelegateConnecting(int dwCallId);
        public void mtcCallDelegateTalking(int dwCallId);
        public void mtcCallDelegateTermed(int dwCallId, int dwStatCode, String pcReason);
        public void mtcCallDelegateTermAll();

        public void mtcCallDelegateLogouted();
        
        public void mtcCallDelegateStartPreview();
        public void mtcCallDelegateStartVideo(int dwCallId);
        public void mtcCallDelegateStopVideo(int dwCallId);
        
        public void mtcCallDelegateNetStaChanged(int dwCallId, boolean bVideo, boolean bSend, int iStatus);
        public void mtcCallDelegateInfo(int dwCallId, String info);

        public void mtcCallDelegateVideoReceiveStaChanged(int dwCallId, int dwStatus);

        public void mtcCallDelegatePhoneCallBegan();
        public void mtcCallDelegatePhoneCallEnded();

        public boolean mtcCallDelegateIsCalling();
        public boolean mtcCallDelegateIsExisting(String number);

        public int mtcCallDelegateGetCallId();
    }

    public static void init(Context context) {
    	MtcResource.init(context);
        sContext = context;
        sCallListener = new CallListener();
        sTelephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        sTelephonyManager.listen(sCallListener, PhoneStateListener.LISTEN_CALL_STATE);
        setCallActivityClass(CallActivity.class);
        setBitrateMode(MtcMediaConstants.EN_MTC_AN_MID);
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);

        if (sMtcCallIncomingReceiver == null) {
            sMtcCallIncomingReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int dwCallId = MtcConstants.INVALIDID;
                    try {
                        String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                        JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                        dwCallId = json.getInt(MtcCallConstants.MtcCallIdKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    if (FloatWindowService.sIsShow) {
                    	MtcCall.Mtc_CallTerm(dwCallId,
        						MtcCall.EN_MTC_CALL_TERM_STATUS_BUSY, null);
        				return;
					}
                    
                    Callback callback = getActiveCallback();
                    if (callback != null) {
                        callback.mtcCallDelegateIncoming(dwCallId);
                        return;
                    }
                    callback = getCallback();
                    if (callback != null) {
                        if (sPendingCall == null) {
                            sPendingCall = new ArrayList<Integer>();
                        }
                        sPendingCall.add(dwCallId);
                        return;
                    }
                    callIncoming(dwCallId);
                }
            };
            broadcastManager.registerReceiver(sMtcCallIncomingReceiver,
                    new IntentFilter(MtcCallConstants.MtcCallIncomingNotification));
        }

        if (sMtcCallOutgoingReceiver == null) {
            sMtcCallOutgoingReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int dwCallId = MtcConstants.INVALIDID;
                    try {
                        String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                        JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                        dwCallId = json.getInt(MtcCallConstants.MtcCallIdKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    Callback callback = getActiveCallback();
                    if (callback != null) {
                        callback.mtcCallDelegateOutgoing(dwCallId);
                    }
                }
            };
            broadcastManager.registerReceiver(sMtcCallOutgoingReceiver,
                    new IntentFilter(MtcCallConstants.MtcCallOutgoingNotification));
        }


        if (sMtcCallAlertedReceiver == null) {
            sMtcCallAlertedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int dwCallId = MtcConstants.INVALIDID;
                    int dwAlertType = -1;
                    try {
                        String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                        JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                        dwCallId = json.getInt(MtcCallConstants.MtcCallIdKey);
                        dwAlertType = json.getInt(MtcCallConstants.MtcCallAlertTypeKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    Callback callback = getActiveCallback();
                    if (callback != null) {
                        callback.mtcCallDelegateAlerted(dwCallId, dwAlertType);
                    }
                }
            };
            broadcastManager.registerReceiver(sMtcCallAlertedReceiver,
                    new IntentFilter(MtcCallConstants.MtcCallAlertedNotification));
        }

        if (sMtcCallConnectingReceiver == null) {
            sMtcCallConnectingReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int dwCallId = MtcConstants.INVALIDID;
                    try {
                        String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                        JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                        dwCallId = json.getInt(MtcCallConstants.MtcCallIdKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    Callback callback = getActiveCallback();
                    if (callback != null) {
                        callback.mtcCallDelegateConnecting(dwCallId);
                    }
                }
            };
            broadcastManager.registerReceiver(sMtcCallConnectingReceiver,
                    new IntentFilter(MtcCallConstants.MtcCallConnectingNotification));
        }

        if (sMtcCallTalkingReceiver == null) {
            sMtcCallTalkingReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int dwCallId = MtcConstants.INVALIDID;
                    try {
                        String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                        JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                        dwCallId = json.getInt(MtcCallConstants.MtcCallIdKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    Callback callback = getActiveCallback();
                    if (callback != null) {
                        callback.mtcCallDelegateTalking(dwCallId);
                    }
                }
            };
            broadcastManager.registerReceiver(sMtcCallTalkingReceiver,
                    new IntentFilter(MtcCallConstants.MtcCallTalkingNotification));
        }

        if (sMtcCallTermedReceiver == null) {
            sMtcCallTermedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int dwCallId = MtcConstants.INVALIDID;
                    int dwStatusCode = -1;
                    String pcReason = null;
                    try {
                        String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                        JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                        dwCallId = json.getInt(MtcCallConstants.MtcCallIdKey);
                        dwStatusCode = json.getInt(MtcCallConstants.MtcCallStatusCodeKey);
                        pcReason = json.optString(MtcCallConstants.MtcCallDescriptionKey, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    if (sPendingCall != null) {
                        if (sPendingCall.remove((Integer) dwCallId)) {
                            return;
                        }
                    }

                    Callback callback = getActiveCallback();
                    if (callback != null) {
                        callback.mtcCallDelegateTermed(dwCallId, dwStatusCode, pcReason);
                    } else {
                        Intent intentCallActivity = new Intent(sContext, sCallActivityClass);
                        intentCallActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intentCallActivity.putExtra(TERMED, true);
                        intentCallActivity.putExtra(CALL_ID, dwCallId);
                        intentCallActivity.putExtra(STAT_CODE, dwStatusCode);
                        intentCallActivity.putExtra(TERMED_REASON, pcReason);
                        sContext.startActivity(intentCallActivity);
                    }
                }
            };
            broadcastManager.registerReceiver(sMtcCallTermedReceiver,
                    new IntentFilter(MtcCallConstants.MtcCallTermedNotification));
        }

        if (sMtcCallNetworkStatusChangedReceiver == null) {
            sMtcCallNetworkStatusChangedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int dwCallId = MtcConstants.INVALIDID;
                    boolean bVideo = false;
                    boolean bSend = false;
                    int iStatus = 0;
                    try {
                        String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                        JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                        dwCallId = json.getInt(MtcCallConstants.MtcCallIdKey);
                        bVideo = json.getBoolean(MtcCallConstants.MtcCallIsVideoKey);
                        bSend = json.getBoolean(MtcCallConstants.MtcCallIsSendKey);
                        iStatus = json.getInt(MtcCallConstants.MtcCallNetworkStatusKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    Callback callback = getActiveCallback();
                    if (callback != null) {
                        callback.mtcCallDelegateNetStaChanged(dwCallId, bVideo, bSend, iStatus);
                    } else {
                    	if (FloatWindowService.sIsShow) {
                    		if (bSend)
                    			return;
                    		
                    		if (!bVideo && iStatus == MtcCallConstants.EN_MTC_NET_STATUS_DISCONNECTED) {
                    			FloatWindowService.dismiss(context);
                        		FloatWindowService.destroy(context);
                            	ZmfVideo.captureStopAll();
                            	ZmfAudio.inputStopAll();
                    			ZmfAudio.outputStopAll();
							}
						}
                    }
                }
            };
            broadcastManager.registerReceiver(sMtcCallNetworkStatusChangedReceiver,
                    new IntentFilter(MtcCallConstants.MtcCallNetworkStatusChangedNotification));
        }

        if (sMtcCallInfoReceivedReceiver == null) {
            sMtcCallInfoReceivedReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int dwCallId = MtcConstants.INVALIDID;
                    String pcInfo = null;
                    try {
                        String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                        JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                        dwCallId = json.getInt(MtcCallConstants.MtcCallIdKey);
                        pcInfo = json.getString(MtcCallConstants.MtcCallBodyKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    Callback callback = getActiveCallback();
                    if (callback != null) {
                        callback.mtcCallDelegateInfo(dwCallId, pcInfo);
                    }
                }
            };
            broadcastManager.registerReceiver(sMtcCallInfoReceivedReceiver,
                    new IntentFilter(MtcCallConstants.MtcCallInfoReceivedNotification));
        }

        if (sMtcCallVideoReceiveStatusChangedReceiver == null) {
            sMtcCallVideoReceiveStatusChangedReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    int dwCallId = MtcConstants.INVALIDID;
                    int dwStatus = -1;
                    try {
                        String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
                        JSONObject json = (JSONObject) new JSONTokener(info).nextValue();
                        dwCallId = json.getInt(MtcCallConstants.MtcCallIdKey);
                        dwStatus = json.getInt(MtcCallConstants.MtcCallVideoStatusKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    Callback callback = getActiveCallback();
                    if (callback != null) {
                        callback.mtcCallDelegateVideoReceiveStaChanged(dwCallId, dwStatus);
                    }
                }
            };
            broadcastManager.registerReceiver(sMtcCallVideoReceiveStatusChangedReceiver,
                    new IntentFilter(MtcCallConstants.MtcCallVideoReceiveStatusChangedNotification));
        }
        
        if (sMtcLogoutedReceiver == null) {
        	sMtcLogoutedReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Callback callback = getActiveCallback();
                    if (callback != null) {
                        callback.mtcCallDelegateLogouted();
                    } else {
                    	if (FloatWindowService.sIsShow) {
                    		FloatWindowService.dismiss(context);
                    		FloatWindowService.destroy(context);
                        	ZmfVideo.captureStopAll();
                        	ZmfAudio.inputStopAll();
                			ZmfAudio.outputStopAll();
						}
                    }
                }
            };
            broadcastManager.registerReceiver(sMtcLogoutedReceiver,
                    new IntentFilter(MtcApi.MtcLogoutedNotification));
        }
        
        if (sMtcDidLogoutReceiver == null) {
        	sMtcDidLogoutReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                	if (FloatWindowService.sIsShow) {
                		FloatWindowService.dismiss(context);
                		FloatWindowService.destroy(context);
                    	ZmfVideo.captureStopAll();
                    	ZmfAudio.inputStopAll();
            			ZmfAudio.outputStopAll();
					}
                }
            };
            broadcastManager.registerReceiver(sMtcDidLogoutReceiver,
                    new IntentFilter(MtcApi.MtcDidLogoutNotification));
        }
    }

    public static void destroy() {
        if (sTelephonyManager != null) {
            sTelephonyManager.listen(null, PhoneStateListener.LISTEN_CALL_STATE);
            sCallListener = null;
            sTelephonyManager = null;
        }
    }

    private static void callIncoming(int dwCallId) {
        Intent intentCallActivity = new Intent(sContext, sCallActivityClass);
        intentCallActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intentCallActivity.putExtra(VIDEO, MtcCall.Mtc_CallPeerOfferVideo(dwCallId));
        intentCallActivity.putExtra(CALL_ID, dwCallId);
        sContext.startActivity(intentCallActivity);
    }
    
    public static void setEnabled(String key, boolean value) {
    	if (key == null || key.length() == 0) return;
			
    	if (configMap == null) {
    		configMap = new HashMap<String, String>();
		}
    	configMap.put(key, value ? "true" : "false");
    }
    
    public static boolean getEnabled(String key) {
    	if (key == null || key.length() == 0) return false;
    	if (configMap == null) return false;
			
    	try {
    		String value = configMap.get(key);
    		return value.equals("true") ? true : false;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
    }
    
    public static void setSmallViewSize(float scale) {
    	if (scale > 0.25) 
			scale = 0.25f;
    	else if (scale < 0.04) 
			scale = 0.04f;
    	
    	if (configMap == null) {
    		configMap = new HashMap<String, String>();
		}
    	configMap.put(SMALL_VIEW_SIZE, Float.toString(scale));	
    }

    public static float getSmallViewSize() {
    	if (configMap == null) return 0.042f;
    	
    	try {
    		String str = configMap.get(SMALL_VIEW_SIZE);
    		return Float.valueOf(str).floatValue();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0.042f;
		}
    }
    
    public static void setSmallViewLocate(float x, float y) {
    	if (x > 1) 
			x = 1;
		else if (x < 0)
			x = 0;
    	
    	if (y > 1) 
			y = 1;
		else if (y < 0)
			y = 0;
    	
    	if (configMap == null) {
    		configMap = new HashMap<String, String>();
		}
    	configMap.put(SMALL_VIEW_X, Float.toString(x));
    	configMap.put(SMALL_VIEW_Y, Float.toString(y));
    }
    
    public static Map<String, String> getSmallViewLocate() {
    	return configMap;
    }
    
    public static void setCallback(Callback callback) {
        sCallback = (callback == null) ? null : new WeakReference<Callback>(callback);

        if (callback == null) {
            if (sPendingCall != null) {
                for (int dwCallId : sPendingCall) {
                    callIncoming(dwCallId);
                }
                sPendingCall = null;
            }
        }
    }

    public static Callback getCallback() {
        return (sCallback == null) ? null : sCallback.get();
    }

    public static Callback getActiveCallback() {
        Callback callback = getCallback();
        if (callback != null) {
            Activity activity = (Activity)callback;
            if (activity.isFinishing()) {
                callback = null;
            }
        }
        return callback;
    }

    private static void setCallActivityClass(Class<?> cls) {
        sCallActivityClass = cls;
    }

    public static void call(String number, String displayName, String peerDisplayName, boolean isVideo) {
    	if (FloatWindowService.sIsShow) {
			Toast.makeText(sContext, "Now in the calling...", Toast.LENGTH_SHORT).show();
			return;
		}
    	
        Intent intent = new Intent(sContext, sCallActivityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(VIDEO, isVideo);
        intent.putExtra(NUMBER, number);
        intent.putExtra(DISPLAY_NAME, displayName);
        intent.putExtra(PEER_DISPLAY_NAME, peerDisplayName);
        sContext.startActivity(intent);
    }
    
    /*public static void call(String number, String displayName, String peerDisplayName, boolean isVideo) {
    	if (FloatWindowService.sIsShow) {
			Toast.makeText(sContext, "Now in the calling...", Toast.LENGTH_SHORT).show();
			return;
		}
    	
        Intent intent = new Intent(sContext, sCallActivityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(VIDEO, isVideo);
        intent.putExtra(NUMBER, number);
        intent.putExtra(DISPLAY_NAME, displayName);
        intent.putExtra(PEER_DISPLAY_NAME, peerDisplayName);
        sContext.startActivity(intent);
    }*/

    public static void termAll() {
        Callback callback = (sCallback == null) ? null : sCallback.get();
        if (callback != null) {
            callback.mtcCallDelegateTermAll();
        }
    }

    public static boolean isCalling() {
    	Callback callback = getActiveCallback();
        if (callback != null) {
            return callback.mtcCallDelegateIsCalling();
        }
        return false;
    }

    public static boolean isExisting(String number) {
        Callback callback = getActiveCallback();
        if (callback != null) {
            return callback.mtcCallDelegateIsExisting(number);
        }
        return false;
    }

    public static int getCallId() {
        Callback callback = getActiveCallback();
        if (callback != null) {
            return callback.mtcCallDelegateGetCallId();
        }
        return -1;
    }

    public static Context sContext;
    private static WeakReference<Callback> sCallback;
    private static Class<?> sCallActivityClass;

    private static boolean sIsInPhoneCall = false;
    private static CallListener sCallListener;
    private static TelephonyManager sTelephonyManager;

    private static ArrayList<Integer> sPendingCall;
    private static Map<String, String> configMap;
    public static final String CALL_ID = "call_id";
    public static final String VIDEO = "video";
    public static final String NUMBER = "number";
    public static final String DISPLAY_NAME = "name";
    public static final String PEER_DISPLAY_NAME = "peer_name";
    public static final String TERMED = "termed";
    public static final String STAT_CODE = "stat_code";
    public static final String TERMED_REASON = "term_reason";
    
    public static final String MAGNIFIER_ENABLED_KEY = "magnifier_enabled_key";
    public static final String UPLOAD_LOG_KEY = "upload_log_key";
    public static final String SMALL_VIEW_SIZE = "small_view_size";
    public static final String SMALL_VIEW_X = "small_view_x";
    public static final String SMALL_VIEW_Y = "small_view_y";

    private static BroadcastReceiver sMtcCallIncomingReceiver;
    private static BroadcastReceiver sMtcCallOutgoingReceiver;
    private static BroadcastReceiver sMtcCallAlertedReceiver;
    private static BroadcastReceiver sMtcCallConnectingReceiver;
    private static BroadcastReceiver sMtcCallTalkingReceiver;
    private static BroadcastReceiver sMtcCallTermedReceiver;
    private static BroadcastReceiver sMtcCallNetworkStatusChangedReceiver;
    private static BroadcastReceiver sMtcCallInfoReceivedReceiver;
    private static BroadcastReceiver sMtcCallVideoReceiveStatusChangedReceiver;
    private static BroadcastReceiver sMtcLogoutedReceiver;
    private static BroadcastReceiver sMtcDidLogoutReceiver;
    
    public static boolean isInPhoneCall()   {
        return sIsInPhoneCall;
    }

    public static final void setBitrateMode(int value) {
    	MtcMdm.Mtc_MdmAnSetBitrateMode(value);
    }

    public static final int getBitrateMode() {
    	return MtcMdm.Mtc_MdmAnGetBitrateMode();
    }

    public static String getStateString(Context context, int state, boolean isVideo, boolean forShort) {
        switch (state) {
            case State.CALL_STATE_INCOMING:
                if (forShort) {
                	return context.getString(MtcResource.getIdByName("string", "Incoming"));
              } else {
                  if (isVideo) {
                  		return context.getString(MtcResource.getIdByName("string", "Video_incoming"));
                  } else {
                  		return context.getString(MtcResource.getIdByName("string", "Voice_incoming"));
                  }
              }
          case State.CALL_STATE_ANSWERING:
          		return context.getString(MtcResource.getIdByName("string", "Answering"));
          case State.CALL_STATE_CALLING:
          		return context.getString(MtcResource.getIdByName("string", "Calling"));
          case State.CALL_STATE_ALERTED_RINGING:
              	return context.getString(MtcResource.getIdByName("string", "Ringing"));
          case State.CALL_STATE_CONNECTING:
              	return context.getString(MtcResource.getIdByName("string", "Connecting"));
          case State.CALL_STATE_TALKING:
              	return context.getString(MtcResource.getIdByName("string", "Talking"));
          case State.CALL_STATE_PAUSED:
              	return context.getString(MtcResource.getIdByName("string", "Paused"));
          case State.CALL_STATE_TERM_RINGING:
          case State.CALL_STATE_ENDING:
              	return context.getString(MtcResource.getIdByName("string", "Call_ended"));
        }
        return "";
    }

    private static class CallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if(TelephonyManager.CALL_STATE_OFFHOOK == state ||
                    TelephonyManager.CALL_STATE_RINGING == state) {
                sIsInPhoneCall = true;
            } else if(TelephonyManager.CALL_STATE_IDLE == state) {
                if (!sIsInPhoneCall) return;
                sIsInPhoneCall = false;
            } else {
            	return;
            }
            Callback callback = getActiveCallback();
            if (callback == null) {
                return;
            }
            if (sIsInPhoneCall) {
                callback.mtcCallDelegatePhoneCallBegan();
            } else {
                callback.mtcCallDelegatePhoneCallEnded();
            }
        }
    }
}
