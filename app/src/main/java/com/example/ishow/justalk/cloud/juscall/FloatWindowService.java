package com.example.ishow.justalk.cloud.juscall;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.ishow.justalk.cloud.juscall.MtcCallDelegate.State;
import com.justalk.cloud.lemon.MtcCall;
import com.justalk.cloud.lemon.MtcConstants;
import com.justalk.cloud.zmf.Zmf;
import com.justalk.cloud.zmf.ZmfObserver;
import com.justalk.cloud.zmf.ZmfVideo;

public class FloatWindowService extends Service implements State {

    public static final int TYPE_VOICE = 1;
    public static final int TYPE_VIDEO = 2;
    
    public static final String ACTION_SHOW = "action_show";
    public static final String ACTION_DISMISS = "action_dismiss";
    
    public static boolean sIsShow = false;
    private static int sState = CALL_STATE_NONE;
    private static long sBaseTime = 0;
    private static int sType = TYPE_VOICE;
    private static int sSessId = MtcConstants.INVALIDID;
    private static FloatWindow sFloatWindow;
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        sFloatWindow = new FloatWindow(getApplicationContext());
        
    }

    @Override
    public void onDestroy() {
        if (sFloatWindow != null){
            sFloatWindow.destroy();
            sFloatWindow = null;
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        if (intent == null)   {
            return result;
        }
        if (ACTION_SHOW.equals(intent.getAction())) {
            if (sBaseTime != 0) {
                sFloatWindow.setBaseTime(sBaseTime);
            }
            sFloatWindow.setSessId(sSessId);
            sFloatWindow.setState(sState, sType == TYPE_VIDEO);
            sFloatWindow.show();
        } else if (ACTION_DISMISS.equals(intent.getAction())) {
            sFloatWindow.dismiss();
        }
        return result;
    }
    
    public static void setBaseTime(long baseTime) {
        sBaseTime = baseTime;
        if (sFloatWindow != null) {
            if (baseTime != 0) {
                sFloatWindow.setBaseTime(baseTime);
            }
        }
    }
    
    public static void setState(int state, boolean isVideo) {
        sState = state;
        sType = isVideo ? TYPE_VIDEO : TYPE_VOICE;
        if (sFloatWindow != null) {
            sFloatWindow.setState(state, isVideo);
        }
    }
    
    public static void setSessId(int sessId) {
        sSessId = sessId;
        if (sFloatWindow != null) {
            sFloatWindow.setSessId(sessId);
        }
    }
    
    public static void setCallStatus(String number, String name, String displayName, int video, int audio) {
    	if (sFloatWindow != null) {
            sFloatWindow.setCallStatus(number, name, displayName, video, audio);
        }
    }
    
    public static void setButtonStatus(String key, boolean value) {
    	if (sFloatWindow != null) {
            sFloatWindow.setButtonStatus(key, value);
        }
    }
    
    public static void show(Context context) {
        Intent intent = new Intent(context, FloatWindowService.class);
        intent.setAction(ACTION_SHOW);
        context.startService(intent);
        sIsShow = true;
    }
    
    public static void dismiss(Context context) {
        Intent intent = new Intent(context, FloatWindowService.class);
        intent.setAction(ACTION_DISMISS);
        context.startService(intent);
        sIsShow = false;
    }
    
    public static void destroy(Context context) {
        Intent intent = new Intent(context, FloatWindowService.class);
        context.stopService(intent);
        sIsShow = false;
    }
    
    static class FloatWindow implements OnTouchListener, ZmfObserver {

        public FloatWindow(Context context) {
            mContext = context;

            mWindowManager = (WindowManager) MtcCallDelegate.sContext.getSystemService("window");

            mParams = new LayoutParams();
            mParams.type = LayoutParams.TYPE_SYSTEM_ERROR;
            mParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE;
            mParams.gravity = Gravity.RIGHT | Gravity.TOP;
            
            mParams.x = mContext.getResources().getDimensionPixelSize(MtcResource.getIdByName("dimen", "call_float_margin_right"));
            mParams.y = mContext.getResources().getDimensionPixelSize(MtcResource.getIdByName("dimen", "call_float_margin_top"));
            
            mParams.alpha = 1.0f;
            mParams.width = LayoutParams.WRAP_CONTENT;
            mParams.height = LayoutParams.WRAP_CONTENT;
            mParams.format = PixelFormat.RGBA_8888;

            mIsShowing = false;

            initViews();
        }

        public void show() {
            if (mIsShowing) {
                return;
            }
            mWindowManager.addView(mRootView, mParams);
            mIsShowing = true;

            updateState();
        }

        public void dismiss() {
            if (!mIsShowing) {
                return;
            }
            mWindowManager.removeView(mRootView);
            mIsShowing = false;

            updateState();
        }

        public void setBaseTime(long time) {
            mChronometer.setBase(time);
        }

        public void setVideoState(int state) {
            switch (state) {
                case CALL_STATE_INCOMING:
                case CALL_STATE_ANSWERING:
                case CALL_STATE_CALLING:
                case CALL_STATE_ALERTED_RINGING:
                case CALL_STATE_CONNECTING:
                case CALL_STATE_PAUSED:
                    mChronometer.stop();
                    mChronometer.setVisibility(View.VISIBLE);
                    mChronometer.setText(MtcCallDelegate.getStateString(mContext, state, mIsVideo, true));
                    mIcon.setVisibility(View.VISIBLE);
                    mIcon.setImageResource(MtcResource.getIdByName("drawable", "call_float_video_answer"));
                    break;
                case CALL_STATE_TALKING:
                case CALL_STATE_TIMING:
                    mChronometer.start();
                    mChronometer.setVisibility(View.VISIBLE);
                    mIcon.setVisibility(View.VISIBLE);
                    mIcon.setImageResource(MtcResource.getIdByName("drawable", "call_float_video_answer"));
                    break;
                case CALL_STATE_TERM_RINGING:
                case CALL_STATE_ENDING:
                    mChronometer.stop();
                    mChronometer.setVisibility(View.VISIBLE);
                    mChronometer.setText(MtcCallDelegate.getStateString(mContext, state, mIsVideo, true));
                    mIcon.setVisibility(View.VISIBLE);
                    mIcon.setImageResource(MtcResource.getIdByName("drawable", "call_float_video_end"));
                    break;
                default:
                    break;
            }
        }

        public void setVoiceState(int state) {
            switch (state) {
                case CALL_STATE_INCOMING:
                case CALL_STATE_ANSWERING:
                case CALL_STATE_CALLING:
                case CALL_STATE_ALERTED_RINGING:
                case CALL_STATE_CONNECTING:
                case CALL_STATE_PAUSED:
                    mChronometer.stop();
                    mChronometer.setText(MtcCallDelegate.getStateString(mContext, state, mIsVideo, true));
                    mIcon.setImageResource(MtcResource.getIdByName("drawable", "call_float_voice_answer"));
                    break;
                case CALL_STATE_TALKING:
                case CALL_STATE_TIMING:
                    mChronometer.start();
                    mIcon.setImageResource(MtcResource.getIdByName("drawable", "call_float_voice_answer"));
                    break;
                case CALL_STATE_TERM_RINGING:
                case CALL_STATE_ENDING:
                    mChronometer.stop();
                    mChronometer.setText(MtcCallDelegate.getStateString(mContext, state, mIsVideo, true));
                    mIcon.setImageResource(MtcResource.getIdByName("drawable", "call_float_voice_end"));
                    break;
                default:
                    break;
            }
        }

        public void setState(int state, boolean isVideo) {
            mState = state;
            mIsVideo = isVideo;

            if (mIsShowing) {
                updateState();
            }
        }
        
        public void setSessId(int sessId) {
            if (sessId == MtcConstants.INVALIDID || sessId == mSessId) {
                return;
            }
            mSessId = sessId;
            if (mRemoteView != null) {
                ZmfVideo.renderRemoveAll(mRemoteView);
                
                ZmfVideo.renderAdd(mRemoteView, MtcCall.Mtc_CallGetName(mSessId), 0, ZmfVideo.RENDER_AUTO);
            }
        }
        
        public void setCallStatus(String number, String name, String displayName, int video, int audio) {
        	mNumber = number;
        	mName = name;
        	mPeerName = displayName;
        	mVideo = video;
        	mAudio = audio;
        }

        public void setButtonStatus(String key, boolean value) {
        	if (mBtnStatus == null) {
                mBtnStatus = new HashMap<String, Boolean>();
            }
        	mBtnStatus.put(key, Boolean.valueOf(value));
        }
        
        public void destroy() {
            dismiss();
            removeVideoView();
            mRootView.setOnTouchListener(null);
            mContext = null;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mTouchX = event.getRawX();
            mTouchY = event.getRawY()-getStatusBarHeight();  // height of status bar
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchStartX = event.getX();
                    mTouchStartY = event.getY();
                    mStartTimestamp = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateViewPosition();
                    break;
                case MotionEvent.ACTION_UP:
                    float endX = event.getX();
                    float endY = event.getY();
                    boolean isMoved = (Math.abs(endX - mTouchStartX) + Math
                            .abs(endY - mTouchStartY)) > 5;

                    if (!isMoved
                            && System.currentTimeMillis() - mStartTimestamp < 300) {
                        backToJustalk();
                    }
                    break;
            }
            return true;
        }
        
        // control
        private Context mContext;
        private boolean mIsShowing = false;
        private int mState;
        private int mVideo;
        private int mAudio;
        private boolean mIsVideo;
        private int mSessId = MtcConstants.INVALIDID;
        private String mNumber;
    	private String mName;
    	private String mPeerName;
    	private Map<String, Boolean> mBtnStatus;
    	
        private final WindowManager mWindowManager;
        private final LayoutParams mParams;

        // views
        private View mRootView;

        // voice
        private View mStateView;
        private ImageView mIcon;
        private Chronometer mChronometer;

        // video
        private RelativeLayout mVideoStreamView;
        private SurfaceView mRemoteView;
        
        private SurfaceView mOppsiteView;
        
        private float mTouchStartX;
        private float mTouchStartY;
        private long mStartTimestamp;
        private float mTouchX;
        private float mTouchY;

        @SuppressLint("InflateParams")
        private void initViews() {
        	mRootView = LayoutInflater.from(mContext).inflate(MtcResource.getIdByName("layout", "call_float"),
                    null);
            mStateView = mRootView.findViewById(MtcResource.getIdByName("id", "float_circle"));
            mChronometer = (Chronometer) mStateView  .findViewById(MtcResource.getIdByName("id", "float_chronometer"));
            mIcon = (ImageView) mStateView.findViewById(MtcResource.getIdByName("id", "float_icon"));

            mVideoStreamView = (RelativeLayout) mRootView.findViewById(MtcResource.getIdByName("id", "float_video_stream"));
            mRootView.setOnTouchListener(this);
            
        }
        
        @SuppressWarnings("deprecation")
        private int getScreenWidth() {
            return mWindowManager.getDefaultDisplay().getWidth();
        }
        
        private int getStatusBarHeight() {
            Rect rect = new Rect();
            mRootView.getWindowVisibleDisplayFrame(rect);
            return rect.top; 
        }
        
        private void updateViewPosition() {
            if (mIsShowing) {
                mParams.x = getScreenWidth() - mRootView.getWidth() - ((int) (mTouchX - mTouchStartX));
                mParams.y = (int) (mTouchY - mTouchStartY);
                mWindowManager.updateViewLayout(mRootView, mParams);
            }
        }
        
		private  void backToJustalk() {
			Intent intent = new Intent(mContext, CallActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			intent.putExtra(CallActivity.EXTRA_FLOAT_WINDOW_CALL, true);
			intent.putExtra("FloatCallId", mSessId);
			intent.putExtra("FloatIsVideo", mIsVideo);
			intent.putExtra("FloatState", mState);
			intent.putExtra("FloatVideo", mVideo);
			intent.putExtra("FloatAudio", mAudio);
			intent.putExtra("FloatNumber", mNumber);
			intent.putExtra("FloatName", mName);
			intent.putExtra("FloatDisplayName", mPeerName);
			intent.putExtra("FloatBasetime", mChronometer.getBase());
			try {
				for (String key : mBtnStatus.keySet()) {  
					intent.putExtra(key, mBtnStatus.get(key).booleanValue());
				} 
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			mContext.startActivity(intent);
		}
        
        private void addVideoView() {
            if (mRemoteView != null || mSessId == MtcConstants.INVALIDID) {
                return;
            }
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            
            RelativeLayout.LayoutParams lpOpsiteView = new RelativeLayout.LayoutParams(dip2px(mContext,70),dip2px(mContext,100));
            lpOpsiteView.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
           
            lpOpsiteView.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
            lpOpsiteView.rightMargin=dip2px(mContext,30);
            mRemoteView = ZmfVideo.renderNew(mContext);
            mRemoteView.setLayoutParams(lp);
         
            mOppsiteView = ZmfVideo.renderNew(mContext);
            mOppsiteView.setZOrderMediaOverlay(true);
            mOppsiteView.setSoundEffectsEnabled(false);
            mOppsiteView.setLayoutParams(lpOpsiteView);
            Zmf.addObserver(this);
            
            mVideoStreamView.addView(mOppsiteView, 0);
            mVideoStreamView.addView(mRemoteView, 1);
             
            
            mStateView.setVisibility(View.GONE);
            mVideoStreamView.setVisibility(View.GONE);
           
            ZmfVideo.renderStart(mOppsiteView); 
            ZmfVideo.renderStart(mRemoteView);
           
          
           if(ZmfVideo.captureGetCount()>1){
        	   ZmfVideo.renderAdd(mOppsiteView, ZmfVideo.CaptureFront, 0, ZmfVideo.RENDER_AUTO);
            }
            ZmfVideo.renderAdd(mRemoteView, MtcCall.Mtc_CallGetName(mSessId), 1, ZmfVideo.RENDER_EFFECT_NONE);
         
           
        }
        
        private void removeVideoView() {
            if (mRemoteView == null) {
                return;
            }
            Zmf.removeObserver(this);
            ZmfVideo.renderRemoveAll(mRemoteView);
            ZmfVideo.renderStop(mRemoteView);
            
            ZmfVideo.renderRemoveAll(mOppsiteView);
            ZmfVideo.renderStop(mOppsiteView);
            
            if (mRemoteView.getParent() == mVideoStreamView) {
                mVideoStreamView.removeView(mRemoteView);
            }
            
            if (mOppsiteView.getParent() == mVideoStreamView) {
                mVideoStreamView.removeView(mOppsiteView);
            }
            mVideoStreamView.setVisibility(View.GONE);
            mRemoteView = null;
            
            mOppsiteView =null;
        }

        private void updateState() {
            if (mIsVideo) {
                if (mState == CALL_STATE_TALKING) {
                    addVideoView();
                } else {
                    mStateView.setVisibility(View.VISIBLE);
                    removeVideoView();
                }
                setVideoState(mState);
            } else {
                mStateView.setVisibility(View.VISIBLE);
                removeVideoView();
                setVoiceState(mState);
            }
        }

        // ZmfObserver
        @Override
        public void handleNotification(int arg0, JSONObject arg1) {
            switch (arg0) {
                case Zmf.VideoRenderDidReceive: {
                    SurfaceView zView = (SurfaceView)arg1.opt(Zmf.Window);
                    renderDidReceive(zView);
                    break;
                }
            }
             
        }
        
        public void renderDidReceive(SurfaceView zView) {
            if (mRemoteView == zView && mIsVideo && mState == CALL_STATE_TALKING) {
                mStateView.setVisibility(View.GONE);
                mVideoStreamView.setVisibility(View.VISIBLE);
            }
        }

    }
    
    
    
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
}
