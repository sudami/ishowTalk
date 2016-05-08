package com.example.ishow.justalk.cloud.juscall;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ishow.Bean.MsgEntry;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;

import com.example.ishow.Service.JustalkStateCheckReciver;
import com.example.ishow.UIActivity.ChatActivity;
import com.example.ishow.Utils.ChatManager;
import com.example.ishow.Utils.PixlesUtils;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.iShowConfig.iShowConfig;
import com.justalk.cloud.lemon.MtcAcv;
import com.justalk.cloud.lemon.MtcAcvConstants;
import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcCall;
import com.justalk.cloud.lemon.MtcCallConstants;
import com.justalk.cloud.lemon.MtcCallExt;
import com.justalk.cloud.lemon.MtcCli;
import com.justalk.cloud.lemon.MtcCliConstants;
import com.justalk.cloud.lemon.MtcConstants;
import com.justalk.cloud.lemon.MtcIm;
import com.justalk.cloud.lemon.MtcImConstants;
import com.justalk.cloud.lemon.MtcMdm;
import com.justalk.cloud.lemon.MtcNumber;
import com.justalk.cloud.lemon.MtcUeDb;
import com.justalk.cloud.lemon.MtcUser;
import com.justalk.cloud.lemon.MtcUserConstants;
import com.justalk.cloud.lemon.ST_MTC_RECT;
import com.justalk.cloud.zmf.Zmf;
import com.justalk.cloud.zmf.ZmfAudio;
import com.justalk.cloud.zmf.ZmfObserver;
import com.justalk.cloud.zmf.ZmfVideo;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CallActivity extends Activity implements MtcCallDelegate.Callback,
		MtcHeadsetPlugReceiver.Callback, MtcBluetoothHelper.Callback,
		MtcOrientationListener.Callback, ZmfObserver,
		CallIncomingSlideView.Callback, MagnifierListener.Callback, JustalkStateCheckReciver.JustalkStateCheckListner {


	private static final int AUDIO_RECEIVER = 0;
	private static final int AUDIO_HEADSET = 1;
	private static final int AUDIO_SPEAKER = 2;
	private static final int AUDIO_BLUETOOTH = 3;

	private static final int VIDEO_CURRENT = -1;
	private static final int VIDEO_CAMERA_FRONT = 0;
	private static final int VIDEO_CAMERA_REAR = 1;
	private static final int VIDEO_CAMERA_OFF = 2;
	private static final int VIDEO_VOICE_ONLY = 3;

	private static int STROKE_WIDTH = 2;
	private static final int REQUEST_MAIN = 1;
	private static final int REQUEST_CAMERA = 2;
	private static final int REQUEST_SNS_MESSAGE = 3;

	// peer info
	private String mNumber;
	private String mName;
	private String mPeerName;

	// session state
	private int mSessId = MtcConstants.INVALIDID;
	private int mSessState = CALL_STATE_NONE;
	private int mAudio;
	private int mVideo = VIDEO_VOICE_ONLY;
	private boolean mIsRtpConnected = false;
	private long mBaseTime;
	private boolean mFloatWindowDestroy = true;

	private ViewGroup mViewMain;
	private ImageView mImgBg;
	private ImageView head;
	private View mViewSubOperation;

	private View mViewUser;
	private TextView mTxtName;
	private Chronometer mChrState;

	private boolean mIsCameraOff = false;
	private boolean mIsFrontCamera = getDefaultVideo() == VIDEO_CAMERA_FRONT;

	private View mViewMenu;
	private CircleButton mViewEnd;
	private CircleButton mBtnMute;
	private CircleButton mBtnAudio;
	private CircleButton mBtnSwitch;
	private CircleButton mBtnCameraOff;
	// private CircleButton mBtnOnChat;
	private CircleButton mBtnRedial;
	private CircleButton mBtnCancel;
	private CircleButton mBtnCameraOn;
	private View mViewMute;
	private View mViewAudio;
	private View mViewSwitch;
	private View mViewCameraOff;
	private View mViewRedial;
	private View mViewCancel;
	private View mViewCameraOn;
	private ImageView mImgSignal;
	private CallIncomingSlideView mViewIncoming;
	private ViewGroup mViewPager;
	private View mCallPrimaryView;
	private Button mBtnShrink;

	private List<TextView> mBtnTxtList;

	private CancelableTextView mTxtError;
	private RotateLayout mTxtErrorContainer;

	private int mViewStatisticClicked;
	private Statistics mStatistics;

	private boolean mOperationShown = true;

	private SurfaceView mSmallSurfaceView;
	private SurfaceView mLargeSurfaceView;

	private SurfaceView mRemovedSmallSurfaceView;
	private SurfaceView mRemovedLargeSurfaceView;

	private boolean mCallMode;
	private AudioManager mAudioManager;
	private MtcHeadsetPlugReceiver mHeadsetPlugReceiver;
	private MtcBluetoothHelper mBluetoothHelper;
	private MtcOrientationListener mOrientationListener;
	private MagnifierListener mMagnifierListener;

	private MtcRing mRing;
	private AlertDialog mAlertDialog;
	private AlertDialog mAnswerAlertDialog;
	private AlertDialog mCameraErrorDialog;
	private AlertDialog mAlertPermissionDialog;
	private AlertDialog mEndAndCallDialog;
	private AlertDialog mSnsMessageDialog;
	private MtcTermRing mTermRing;
	private ToneGenerator mToneGenerator;
	private static final int TONE_RELATIVE_VOLUME = 80;

	private boolean mShowNotification = false;
	private long mNotificationBase;
	private static final String EXTRA_NOTIFY_CALL = "com.justalk.cloud.CallActivity.notify_call";

	private boolean mReconnecting;
	private boolean mPaused;
	private boolean mPausedByCS;
	private int mVideoReceiveStatus;

	public static final String EXTRA_FLOAT_WINDOW_CALL = "com.justalk.cloud.CallActivity.float_window_call";
	public static final String EXTRA_SHOW_PERMISSION_DIALOG = "com.justalk.cloud.CallActivity.show_permission_dialog";

	private long mConnectingStartTime;

	private boolean mResumed;
	private boolean mLocalViewInSmallSurfaceView = true;
	private boolean isTalking = false;
	private boolean mRemoteViewFreezed = false;
	private int mRemoteRotationDegree = 0;
	private boolean mLocalViewShrinked = false;

	private int mCallAudioErrorTime = 0;
	private int mMinMusicVolume;

	private boolean mDisconnect = false;
	//private RoundedCornerImageView mImageAvatar;
	public TextView unread;

	public static CallActivity instance;
	private JustalkStateCheckReciver stateCheckReciver;
	private LocalBroadcastManager broadcastManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Window window = getWindow();

		WindowManager.LayoutParams attrs = window.getAttributes();
		attrs.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

		window.setAttributes(attrs);
		showSystemUI();
		super.onCreate(savedInstanceState);
		instance = this;
		Intent intent = getIntent();
		int flags = intent.getFlags();
		if ((flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) > 0) {
			try {
				Intent backIntent = new Intent(
						"com.justalk.cloud.action.backfromcall");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(backIntent);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Toast.makeText(
						getApplicationContext(),
						"You can jump to activity with intent action: com.justalk.cloud.action.backfromcall",
						Toast.LENGTH_SHORT).show();
			}

			finish();
			return;
		}
		int callId = intent.getIntExtra(MtcCallDelegate.CALL_ID,
				MtcConstants.INVALIDID);
		if (callId != MtcConstants.INVALIDID
				&& MtcCall.Mtc_CallGetState(callId) != MtcCallConstants.EN_MTC_CALL_STATE_INCOMING) {
			ZmfVideo.captureStopAll();

			finish();
			return;
		}

		mAudioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
		mMinMusicVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 3;
		mHeadsetPlugReceiver = new MtcHeadsetPlugReceiver();
		mHeadsetPlugReceiver.setCallback(this);
		mBluetoothHelper = new MtcBluetoothHelper(getApplicationContext());
		mBluetoothHelper.setCallback(this);

		setContentView(MtcResource.getIdByName("layout", "call"));
		initViews();

		// localRank = IShowApplication.getInstance().getLocalUser(this);

		MtcCallDelegate.setCallback(this);
		Zmf.addObserver(this);
		handleIntent(intent);
		stateCheckReciver = new JustalkStateCheckReciver();
		stateCheckReciver.setOnJustalkStateCheckListner(this);
		broadcastManager = LocalBroadcastManager.getInstance(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MtcImConstants.MtcImTextDidReceiveNotification);
		broadcastManager.registerReceiver(stateCheckReciver, intentFilter);
	}



	@Override
	protected void onDestroy() {
		// just in case


		ringStop();
		if (mShowNotification) {
			removeNotification();
		}


		sHandler.removeMessages(MSG_ALERT_PERMISSION);

		finishActivity(REQUEST_MAIN);
		finishActivity(REQUEST_SNS_MESSAGE);

		if (mTermRing != null) {
			mTermRing.release();
			mTermRing = null;
		}
		if (mToneGenerator != null) {
			mToneGenerator.stopTone();
			mToneGenerator.release();
			mToneGenerator = null;
		}

		if (mFloatWindowDestroy) {
			FloatWindowService.destroy(this);
			clearCallMode();
		}

		Zmf.removeObserver(this);
		if (mHeadsetPlugReceiver != null) {
			mHeadsetPlugReceiver.setCallback(null);
		}
		if (mBluetoothHelper != null) {
			mBluetoothHelper.setCallback(null);
		}
		if (mMagnifierListener != null) {
			mMagnifierListener.setCallback(null);
			mMagnifierListener.unregisterReceiver();
			mMagnifierListener = null;

		}

		startChatactivity();
		broadcastManager.unregisterReceiver(stateCheckReciver);
		stateCheckReciver = null;
		super.onDestroy();
		if (MtcCallDelegate.getCallback() == this)
			MtcCallDelegate.setCallback(null);


	}

	private void startChatactivity() {

		try {
			UserEntry recentMsg =new UserEntry();
			JSONObject j = new JSONObject(mPeerName);
			recentMsg.setName(j.getString("username"));
			recentMsg.setUserid(j.getString("userid"));
			recentMsg.setMobile(j.getString("userphone"));
			recentMsg.setImg(j.getString("userimg"));
			Bundle bundle = new Bundle();
			bundle.putParcelable("userEntry", recentMsg);
			Intent intent1 = new Intent(this, ChatActivity.class);
			intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);

			intent1.putExtras(bundle);
			startActivity(intent1);
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CAMERA:
				if (isCalling()) {
					String capture = null;
					switch (mVideo) {
						case VIDEO_CAMERA_FRONT:
							capture = ZmfVideo.CaptureFront;
							break;
						case VIDEO_CAMERA_REAR:
							capture = ZmfVideo.CaptureBack;
							break;
						default:
							return;
					}
					MtcNumber width = new MtcNumber();
					MtcNumber height = new MtcNumber();
					MtcNumber framerate = new MtcNumber();
					MtcMdm.Mtc_MdmGetCaptureParms(width, height, framerate);
					captureStart(capture, width.getValue(), height.getValue(),
							framerate.getValue());
				}
				break;
		}
	}

	@Override
	public void finish() {
		finishActivity(REQUEST_MAIN);
		finishActivity(REQUEST_SNS_MESSAGE);
		if (mTermRing != null) {
			mTermRing.release();
			mTermRing = null;
		}
		if (mToneGenerator != null) {
			mToneGenerator.stopTone();
			mToneGenerator.release();
			mToneGenerator = null;
		}

		if (mFloatWindowDestroy) {
			FloatWindowService.destroy(this);
			clearCallMode();
		}

		Zmf.removeObserver(this);
		if (mHeadsetPlugReceiver != null) {
			mHeadsetPlugReceiver.setCallback(null);
		}
		if (mBluetoothHelper != null) {
			mBluetoothHelper.setCallback(null);
		}
		if (mMagnifierListener != null) {
			mMagnifierListener.setCallback(null);
			mMagnifierListener.unregisterReceiver();
			mMagnifierListener = null;
		}

		if (mSmallSurfaceView != null) {
			ZmfVideo.renderRemoveAll(mSmallSurfaceView);
			ZmfVideo.renderStop(mSmallSurfaceView);
			mSmallSurfaceView = null;
		}
		if (mLargeSurfaceView != null) {
			ZmfVideo.renderRemoveAll(mLargeSurfaceView);
			ZmfVideo.renderStop(mLargeSurfaceView);
			mLargeSurfaceView = null;
		}
		super.finish();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (sHandler.hasMessages(MSG_SHOW_FLOAT_WINDOW)) {
			sHandler.removeMessages(MSG_SHOW_FLOAT_WINDOW);
		}
		FloatWindowService.dismiss(this);
		sHandler.removeMessages(MSG_ALERT_PERMISSION);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (isCalling()) {
			sHandler.sendEmptyMessageDelayed(MSG_SHOW_FLOAT_WINDOW, 500);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mShowNotification)
			removeNotification();
		checkMusicVolume(false);

		if(isShouldShow){
			mViewSubOperation.setVisibility(View.VISIBLE);
			unread.setVisibility(View.GONE);
			isShouldShow =false;
			setOperationShown(true);
		}
		if(FloatWindowService.sIsShow){
			FloatWindowService.dismiss(this);
		}
		sHandler.sendEmptyMessageDelayed(MSG_DID_RESUME, 500);
	}

	public void setUnreadVisibility(){

		findViewById(R.id.call_unreadCount).setVisibility(View.VISIBLE);
	}
	@Override
	protected void onPause() {
		super.onPause();
		sHandler.removeMessages(MSG_DID_RESUME);
		mResumed = false;
		if (isCalling()) {
			postNotification();
		} else if (mShowNotification)
			removeNotification();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.getBooleanExtra(MtcCallDelegate.TERMED, false)) {
			mtcCallDelegateTermed(intent.getIntExtra(MtcCallDelegate.CALL_ID,
					MtcConstants.INVALIDID), intent.getIntExtra(
					MtcCallDelegate.STAT_CODE, 0),
					intent.getStringExtra(MtcCallDelegate.TERMED_REASON));
		} else {
			if (intent.getBooleanExtra(EXTRA_FLOAT_WINDOW_CALL, false)
					|| intent.getBooleanExtra(EXTRA_NOTIFY_CALL, false)
					|| intent.getBooleanExtra(EXTRA_SHOW_PERMISSION_DIALOG,
					false)) {
				return;
			}

			handleIntent(intent);
		}
	}

	@Override
	public void onBackPressed() {
		if (mStatistics != null && mStatistics.isShow()) {
			mStatistics.hideStat();
		} else {
			moveTaskToBack(true);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			if (mSessState == CALL_STATE_INCOMING) {
				ringStop();
				return true;
			}
			boolean ret = false;
			int mode = mAudioManager.getMode();
			int streamType = getStreamType();
			if (mode != AudioManager.MODE_IN_COMMUNICATION
					|| streamType != AudioManager.STREAM_VOICE_CALL) {
				int direction = keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ? AudioManager.ADJUST_LOWER
						: AudioManager.ADJUST_RAISE;
				if (streamType == AudioManager.STREAM_MUSIC
						&& direction == AudioManager.ADJUST_LOWER
						&& mMinMusicVolume >= mAudioManager
						.getStreamVolume(AudioManager.STREAM_MUSIC)) {
					checkMusicVolume(true);
				} else {
					int flags = AudioManager.FLAG_SHOW_UI;
					mAudioManager.adjustStreamVolume(streamType, direction,
							flags);
				}
				ret = true;
			} else {
				ret = super.onKeyDown(keyCode, event);
			}
			return ret;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void checkMusicVolume(boolean showUI) {
		int streamType = getStreamType();
		if (streamType != AudioManager.STREAM_MUSIC) {
			return;
		}
		int currentMusicVolume = mAudioManager
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (mMinMusicVolume >= currentMusicVolume) {
			mAudioManager.setStreamVolume(streamType, mMinMusicVolume,
					showUI ? AudioManager.FLAG_SHOW_UI : 0);
		}
	}

	private void adjustMusicVolumeToMax() {
		int streamType = getStreamType();
		if (streamType != AudioManager.STREAM_MUSIC) {
			return;
		}
		mAudioManager.setStreamVolume(streamType,
				mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setErrorText(false);
		((TextView) findViewById(MtcResource.getIdByName("id",
				"call_redial_text"))).setText(MtcResource.getIdByName("string","Redial"));
		((TextView) findViewById(MtcResource.getIdByName("id", "call_end_text")))
				.setText(MtcResource.getIdByName("string", "Cancel"));

		// TODO update text of buttons in viewpager here

		switch (mSessState) {
			case CALL_STATE_ANSWERING:
				setStateText(getString(MtcResource.getIdByName("string", "Answering")),
						true, false);
				break;
			case CALL_STATE_CALLING:
				setStateText(getString(MtcResource.getIdByName("string", "Calling")),
						true, false);
				break;
			case CALL_STATE_ALERTED_RINGING:
				setStateText(getString(MtcResource.getIdByName("string", "Ringing")),
						true, false);
				break;
			case CALL_STATE_CONNECTING:
				setStateText(getString(MtcResource.getIdByName("string", "Connecting")),
						true, false);
				break;
			case CALL_STATE_DISCONNECTED:
				setStateText(getString(MtcResource.getIdByName("string","Call_disconnected")), false, false);
				break;
			default:
				if (getNet() == MtcCliConstants.MTC_ANET_UNAVAILABLE) {
					setStateText(getString(MtcResource.getIdByName("string",
							"Please_check_the_network_connection")), false, false);
				}
				break;
		}
	}

	public boolean isCalling() {
		return (mSessState > CALL_STATE_NONE && mSessState <= CALL_STATE_PAUSED);
	}

	public boolean isTalking() {
		return (mSessState >= CALL_STATE_CONNECTING && mSessState <= CALL_STATE_PAUSED);
	}


	@Override
	public void mtcCallDelegateIncoming(int dwSessId) {

		if (mSessId == dwSessId) {
			return;
		}

		String pcPhone = MtcCall.Mtc_CallGetPeerName(dwSessId);

		if (mSessId != MtcConstants.INVALIDID) {
			String prePhone = MtcCall.Mtc_CallGetPeerName(mSessId);
			if (TextUtils.equals(pcPhone, prePhone)) {
				MtcCall.Mtc_CallTerm(mSessId,
						MtcCall.EN_MTC_CALL_TERM_STATUS_NORMAL, null);
			} else {
				MtcCall.Mtc_CallTerm(dwSessId,
						MtcCall.EN_MTC_CALL_TERM_STATUS_BUSY, null);
				return;
			}
		}

		if (sHandler.hasMessages(MSG_SESS_TERM)) {
			sHandler.removeMessages(MSG_SESS_TERM);
		}
		if (sHandler.hasMessages(MSG_SESS_CALL))
			sHandler.removeMessages(MSG_SESS_CALL);

		mSessId = dwSessId;
		mSessState = CALL_STATE_INCOMING;
		mIsRtpConnected = false;
		mNumber = pcPhone;
		mName = MtcUeDb.Mtc_UeDbGetUserName();
		mPeerName = MtcCall.Mtc_CallGetPeerDisplayName(dwSessId);
		mTxtName.setText(mPeerName);
		boolean isVideo = MtcCall.Mtc_CallPeerOfferVideo(dwSessId);
		int video = isVideo ? getDefaultVideo() : VIDEO_VOICE_ONLY;
		setVideo(video);
		resetStateText();
		FloatWindowService.setSessId(mSessId);
		FloatWindowService.setState(CALL_STATE_INCOMING, isVideo());
		if (mShowNotification)
			postNotification();
		setViewEnabled(mViewEnd, true);
		setViewEnabled(mViewMute, true);
		setViewEnabled(mViewAudio, true);
		mChrState.setText("正在振铃...");
		setIncomingView(true);

		mReconnecting = false;
		mPaused = false;
		mPausedByCS = false;
		mVideoReceiveStatus = MtcCallConstants.EN_MTC_CALL_TRANSMISSION_NORMAL;
		setErrorText();

		if (isVideo) {
			if (mResumed) {
				mtcCallDelegateStartPreview();
			} else {
				// createVideoViews();
			}
		}
		ringTermStop();

		if (!"samsung".equals(getMeta(this, "UMENG_CHANNEL"))
				|| !MtcCallDelegate.isInPhoneCall()) {
			ring(false);
		}
		try {

			JSONObject jsonObject = new JSONObject(mPeerName);
			iShowConfig.talingUid = jsonObject.getString("userid");
			ImageLoader.getInstance().displayImage(jsonObject.getString("userimg"), head);
			head.setVisibility(View.VISIBLE);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		MtcCall.Mtc_CallAlert(dwSessId, 0, MtcCallConstants.EN_MTC_CALL_ALERT_RING, false);
	}

	@Override
	public void mtcCallDelegateCall(String number, String displayName,
									String peerDisplayName, boolean isVideo) {

		if (TextUtils.isEmpty(number)) {
			return;
		}
		ringTermStop();
		if (sHandler.hasMessages(MSG_SESS_TERM))
			sHandler.removeMessages(MSG_SESS_TERM);

		mSessState = CALL_STATE_CALLING;
		int video = isVideo ? getDefaultVideo() : VIDEO_VOICE_ONLY;
		setVideo(video);
		mNumber = number;
		mName = displayName;
		mPeerName = peerDisplayName;

		//LogUtil.IShowLg(mName + "---mtcCallDelegateCall--" + mPeerName, null);

		mTxtName.setText(TextUtils.isEmpty(peerDisplayName) ? number : peerDisplayName);
		setStateText(getString(MtcResource.getIdByName("string", "Calling")), true, false);
		FloatWindowService.setState(CALL_STATE_CALLING, isVideo());
		if (mShowNotification)
			postNotification();
		setViewEnabled(mViewEnd, true);
		setViewEnabled(mViewMute, true);
		setViewEnabled(mViewAudio, true);
		setIncomingView(false);

		mReconnecting = false;
		mPaused = false;
		mPausedByCS = false;
		mVideoReceiveStatus = MtcCallConstants.EN_MTC_CALL_TRANSMISSION_NORMAL;

		//  if (isVideo) { createVideoViews(); }

		setCallMode(false);
		setAudio(getDefaultAudio());

		Message msg = sHandler.obtainMessage(MSG_SESS_CALL, 0, isVideo ? 1 : 0,
				new Contact(number, displayName, peerDisplayName));
		sHandler.sendMessageDelayed(msg, 200);

		try {
			JSONObject object = new JSONObject(mPeerName);

			iShowConfig.talingUid = object.getString("userid");
			ImageLoader.getInstance().displayImage(object.getString("userimg"),head);
		} catch (JSONException e) {

			e.printStackTrace();
		}

	}

	@Override
	public void mtcCallDelegateOutgoing(int dwSessId) {

		/*if (dwSessId != mSessId)
			return;
		ringBack();

		mSessState = CALL_STATE_OUTGOING;*/

		if (dwSessId != mSessId)
			return;
		ringBack();
		mSessState = CALL_STATE_OUTGOING;

		/*if (isVideo()) {
			mtcCallDelegateStartPreview();
		}*/

	}

	@Override
	public void mtcCallDelegateAlerted(int dwSessId, int dwAlertType) {

		if (dwSessId != mSessId)
			return;

		if (dwAlertType == MtcCallConstants.EN_MTC_CALL_ALERT_RING
				|| dwAlertType == MtcCallConstants.EN_MTC_CALL_ALERT_QUEUED) {
			if (mSessState == CALL_STATE_OUTGOING) {
				mSessState = CALL_STATE_ALERTED_RINGING;
				setStateText(
						getString(MtcResource.getIdByName("string", "Ringing")),
						true, false);
				FloatWindowService.setState(CALL_STATE_ALERTED_RINGING,
						isVideo());
				if (mShowNotification) {
					postNotification();
				}
				return;
			}
		}
	}

	@Override
	public void mtcCallDelegateConnecting(int dwSessId) {

		if (dwSessId != mSessId)
			return;

		if (isVideo()) {
			head.setVisibility(View.GONE);
			mtcCallDelegateStartPreview();
		}
		mConnectingStartTime = System.currentTimeMillis();
		ringBackStop();
		if (mSessState > CALL_STATE_ANSWERING) {
			vibrate();
		}
		mSessState = CALL_STATE_CONNECTING;
		setStateText(getString(MtcResource.getIdByName("string", "Connecting")),
				true, false);
		FloatWindowService.setState(CALL_STATE_CONNECTING, isVideo());
		if (mShowNotification)
			postNotification();

		if (MtcCall.Mtc_CallHasVideo(dwSessId)) {
			mtcCallDelegateStartVideo(dwSessId);
			if (mVideo == VIDEO_CAMERA_OFF) {
				sendVideoPausedForCameraOff();
			}
		} else {
			if (isVideo()) {
				mtcCallDelegateStopVideo(dwSessId);
			}
		}
	}

	@Override
	public void mtcCallDelegateTalking(int dwSessId) {

		if (dwSessId != mSessId)
			return;
		if (sHandler.hasMessages(MSG_CALL_DISCONNECTED)) {
			sHandler.removeMessages(MSG_CALL_DISCONNECTED);
		}
		mIsRtpConnected = true;

		if (mSessState < CALL_STATE_CONNECTING) {
			mtcCallDelegateConnecting(dwSessId);
		}

		resetStateText();
		mReconnecting = false;
		mPaused = false;
		mPausedByCS = false;

		if (mSessState == CALL_STATE_CONNECTING) {
			mSessState = CALL_STATE_TALKING;
			resetStateText();
			mBaseTime = SystemClock.elapsedRealtime();
			mChrState.setBase(mBaseTime);
			mChrState.start();

			FloatWindowService.setBaseTime(mBaseTime);
			FloatWindowService.setState(CALL_STATE_TALKING, isVideo());
			mImgSignal.setVisibility(View.VISIBLE);
			mBtnShrink.setVisibility(View.VISIBLE);
			mNotificationBase = System.currentTimeMillis();
			if (mShowNotification)
				postNotification();
		}
		setErrorText();
	}

	// �����Ҷϵ绰
	@Override
	public void mtcCallDelegateTermed(int dwSessId, int dwStatCode,
									  String pcReason) {

		if (mSessId != dwSessId) {
			return;
		}

		checkToUpload();
		mBtnShrink.setVisibility(View.GONE);
		int state = mSessState;
		term();

		if (getNet() == MtcCliConstants.MTC_ANET_UNAVAILABLE) {
			setLayoutState(STATE_NETWORK_UNAVAILABLE);
			setStateText(getString(MtcResource.getIdByName("string","Please_check_the_network_connection")), false, false);
			ringTerm();
			sHandler.sendEmptyMessageDelayed(MSG_SESS_TERM, 5000);
			return;
		}

		if (state == CALL_STATE_INCOMING) {
			finish();
			return;
		}

		if (state == CALL_STATE_DISCONNECTED) {
			setLayoutState(STATE_DISCONNECTED);
			setStateText(getString(MtcResource.getIdByName("string",
					"Call_disconnected")), false, false);
			ringTerm();
			FloatWindowService.setState(CALL_STATE_TERM_RINGING, isVideo());
			return;
		}

		int delay = 0;
		String stateText = null;
		if (dwStatCode < 0) {
			dwStatCode = MtcCallConstants.EN_MTC_CALL_TERM_STATUS_NORMAL;
		}
		switch (dwStatCode) {

			case MtcCallConstants.EN_MTC_CALL_TERM_STATUS_NORMAL:
			case MtcCallConstants.EN_MTC_CALL_TERM_STATUS_REPLACED: {
				if (state == CALL_STATE_ENDING) {
					delay = 2000;
					stateText = getString(MtcResource.getIdByName("string", "Call_ending"));
				} else if (state == CALL_STATE_INCOMING) {

				} else {
					delay = 2000;
					stateText = getString(MtcResource.getIdByName("string","Call_ended"));
					ringTerm();
				}

				//sendCallStateMsg("已取消");
				//LogUtil.IShowLg("EN_MTC_CALL_TERM_STATUS_REPLACED",null);
				break;
			}

			case MtcCallConstants.EN_MTC_CALL_TERM_STATUS_DECLINE: {
				if (state == CALL_STATE_DECLINING) {
					delay = 2000;
					break;
				}
			}
			//�Է���æ
			case MtcCallConstants.EN_MTC_CALL_TERM_STATUS_BUSY:
				delay = 2000;
				if (TextUtils.isEmpty(pcReason)) {
					stateText = getString(MtcResource.getIdByName("string", "Busy"));
				} else {
					stateText = pcReason;
				}
				ringTerm(MtcResource.getIdByName("raw", "busy"), 2, false);
				setLayoutState(STATE_BUSY);
				//sendCallStateMsg("对方正忙");
				//LogUtil.IShowLg("对方正忙", null);
				break;

			case MtcCallConstants.EN_MTC_CALL_TERM_STATUS_NOT_FOUND:
				delay = 2000;
				stateText = getString(MtcResource.getIdByName("string",
						"app_label_hasnot_been_installed"),
						getString(MtcResource.getIdByName("string", "app_name")));
				ringTerm();
				setLayoutState(STATE_TEMPORARILY_UNAVAILABLE);
				//sendCallStateMsg("用户不存在");
				//LogUtil.IShowLg("用户不存在", null);
				break;

			case MtcCallConstants.EN_MTC_CALL_TERM_STATUS_USER_OFFLINE:
				delay = 2000;
				stateText = getString(MtcResource.getIdByName("string", "Offline"));
				ringTerm(MtcResource.getIdByName("raw", "offline"), 2, false);
				setLayoutState(STATE_OFFLINE);
				//sendCallStateMsg("对方不在线");
				//LogUtil.IShowLg("对方不在线", null);
				break;

			//�Է���Ӧ��ʱ
			case MtcCallConstants.EN_MTC_CALL_TERM_STATUS_TIMEOUT:
				if (state >= CALL_STATE_CALLING && state < CALL_STATE_CONNECTING) {
					delay = 2000;;
					stateText = getString(MtcResource.getIdByName("string","No_answer"));
					ringTerm(MtcResource.getIdByName("raw", "not_answered"), 2,false);
					setLayoutState(STATE_NO_ANSWER);
				} else {
					delay = 2000;
					stateText = getString(MtcResource.getIdByName("string","Temporarily_unavailable"));
					ringTerm();
					setLayoutState(STATE_TEMPORARILY_UNAVAILABLE);
				}
				//sendCallStateMsg("超时无应答");
				//LogUtil.IShowLg("超时无应答", null);
				break;
			//��ͨʧ��
			case MtcCallConstants.EN_MTC_CALL_TERM_STATUS_ERROR_TRANSACTION_FAIL:
				delay = 5000;
				if (state >= CALL_STATE_CALLING && state < CALL_STATE_CONNECTING) {
					stateText = getString(MtcResource.getIdByName("string",
							"No_internet_connection"));
				} else {
					stateText = getString(MtcResource.getIdByName("string","Temporarily_unavailable"));
				}
				ringTerm();
				setLayoutState(STATE_NETWORK_UNAVAILABLE);
				//	sendCallStateMsg("网络错误");
				//	LogUtil.IShowLg("网络错误", null);
				break;
			default:

				if (state >= CALL_STATE_CALLING && state < CALL_STATE_CONNECTING) {
					delay = 1000;
					ringTerm(MtcResource.getIdByName("raw", "can_not_be_connected"),2, true);
					//		sendCallStateMsg("通话已结束" + mChrState.getText());
				} else {
					delay = 5000;
					ringTerm();
					//		sendCallStateMsg("通话已结束");
					//		LogUtil.IShowLg("通话已结束" , null);
				}
				stateText = getString(MtcResource.getIdByName("string","Temporarily_unavailable"));
				setLayoutState(STATE_TEMPORARILY_UNAVAILABLE);

				break;
		}
		if (delay == 0) {
			finish();
		} else {
			setStateText(stateText, false, false);
			if (delay > 0) {
				setViewEnabled(mViewMute, false);
				setViewEnabled(mViewAudio, false);
				setViewEnabled(mViewSwitch, false);
				setViewEnabled(mViewCameraOff, false);
				setViewEnabled(mViewCameraOn, false);
				setViewEnabled(mViewEnd, false);
				sHandler.sendEmptyMessageDelayed(MSG_SESS_TERM, delay);
			}
		}
	}

	/**
	 *
	 */
	private void sendCallStateMsg(String msg) {
		String userUri = MtcUser.Mtc_UserFormUri(MtcUserConstants.EN_MTC_USER_ID_USERNAME,mNumber);

		JSONObject object = new JSONObject();
		UserEntry localRank = SharePrefrence.getInstance().getStudentInfo(getApplicationContext());
		try {
			object.put("msg", msg);
			object.put("msgTime", new Date().getTime());
			object.put("fromId", localRank.getUserid());
			object.put("msgType", "video");
			object.put("fromImg", localRank.getImg());
			object.put("fromPhone", localRank.getMobile());													// ����Ϣ���Է���
			object.put("fromName", localRank.getName());
		    int info = MtcIm.Mtc_ImSendText(0, userUri, object.toString(), null);

			JSONObject j = new JSONObject(mPeerName);
			UserEntry recentMsg =new UserEntry();
			recentMsg.setName(j.getString("username"));
			recentMsg.setUserid(j.getString("userid"));
			recentMsg.setUserid(j.getString("userid"));
			recentMsg.setMobile(j.getString("userphone"));
			recentMsg.setImg(j.getString("userimg"));

			MsgEntry rank = new MsgEntry();
		    long msgTime = new Date().getTime();
			rank.setFromMobile(localRank.getUserid());
			rank.setFromMobile(recentMsg.getMobile());

			rank.setToUserid(recentMsg.getUserid());
			rank.setTextMsg(msg);
			rank.setMsgTime(msgTime);
			rank.setFromNick(localRank.getName());
			rank.setRead(true);
			rank.setFromImg(localRank.getImg());
			rank.setState(1);
			ChatManager helper =new ChatManager();
			helper.saveTextMessage(rank, recentMsg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mtcCallDelegateTermAll() {
		if (mSessState == CALL_STATE_INCOMING) {
			declineWithText("");
			return;
		}

		if (mSessState == CALL_STATE_NONE) {
			finish();
			return;
		}

		mSessState = CALL_STATE_ENDING;
		ringBackStop();
		MtcCall.Mtc_CallTerm(mSessId,
				MtcCallConstants.EN_MTC_CALL_TERM_STATUS_NORMAL, "");
		term();
		finish();
	}

	@Override
	public void mtcCallDelegateLogouted() {
		if (mSessState >= CALL_STATE_INCOMING) {
			term();
			finish();
		}
	}

	@Override
	public void mtcCallDelegateStartPreview() {
		if (mStatistics != null) {
			mStatistics.hideStat();
		}
		String capture = null;
		switch (mVideo) {
			case VIDEO_CAMERA_FRONT:
				capture = ZmfVideo.CaptureFront;
				break;
			case VIDEO_CAMERA_REAR:
				capture = ZmfVideo.CaptureBack;
				break;
			case VIDEO_CAMERA_OFF:
				capture = mIsFrontCamera ? ZmfVideo.CaptureFront
						: ZmfVideo.CaptureBack;
				break;
			default:
				return;
		}
		videoCaptureStart();
		MtcCall.Mtc_CallCameraAttach(mSessId, capture);
	}

	@Override
	public void mtcCallDelegateStartVideo(int dwSessId) {
		if (mSessId != dwSessId)
			return;

		if (mStatistics != null) {
			mStatistics.hideStat();
		}

		if (mOrientationListener == null) {
			mOrientationListener = new MtcOrientationListener(
					getApplicationContext(), sHandler);
			mOrientationListener.setCallback(this);
		}


		mOrientationListener.disable();

		ZmfVideo.renderAdd(getRemoteView(), MtcCall.Mtc_CallGetName(dwSessId),
				0, ZmfVideo.RENDER_AUTO);

		boolean enable = MtcCallDelegate
				.getEnabled(MtcCallDelegate.MAGNIFIER_ENABLED_KEY);
		if (enable) {
			mMagnifierListener = new MagnifierListener(this, mSessId,
					mLargeSurfaceView);
			mMagnifierListener.setCallback(this);
			mLargeSurfaceView.setOnTouchListener(mMagnifierListener);
			mLargeSurfaceView.setLongClickable(true);
		}
	}

	@Override
	public void mtcCallDelegateStopVideo(int dwSessId) {
		if (mSessId != dwSessId)
			return;

		if (isTalking()) {
			if (isVideo()) {
				Toast.makeText(
						getApplicationContext(),
						MtcResource.getIdByName("string",
								"Switched_to_voice_call"), Toast.LENGTH_LONG)
						.show();
			}
		}
		if (isCalling()) {
			setVideo(VIDEO_VOICE_ONLY);
		}

		if (mOrientationListener != null) {
			mOrientationListener.disable();
		}
		MtcCall.Mtc_CallCameraDetach(dwSessId);
		ZmfVideo.captureStopAll();

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(0, 0);
		if (mSmallSurfaceView != null) {
			ZmfVideo.renderRemoveAll(mSmallSurfaceView);
			ZmfVideo.renderStop(mSmallSurfaceView);
			mSmallSurfaceView.setLayoutParams(lp);
			mRemovedSmallSurfaceView = mSmallSurfaceView;
			mSmallSurfaceView = null;
		}
		if (mLargeSurfaceView != null) {
			ZmfVideo.renderRemoveAll(mLargeSurfaceView);
			ZmfVideo.renderStop(mLargeSurfaceView);
			mLargeSurfaceView.setLayoutParams(lp);
			mRemovedLargeSurfaceView = mLargeSurfaceView;
			mLargeSurfaceView = null;
		}
		if (isTalking()) {
			setOperationShown(true);
		}
		mImgBg.setVisibility(View.VISIBLE);

		if (mSessState == CALL_STATE_NONE) {
			setErrorText();
		}
	}

	@Override
	public void mtcCallDelegateInfo(int dwSessId, String info) {

		if (mSessId != dwSessId) {
			return;
		}

		if (info.equals(CALL_INFO_CALL_PAUSE)) {
			mPaused = true;
			setErrorText();
		} else if (info.equals(CALL_INFO_CALL_INTERRUPT)) {
			mPausedByCS = true;
			setErrorText();
		} else if (info.equals(CALL_INFO_CALL_RESUME)) {
			if (mPaused || mPausedByCS) {
				mPaused = false;
				mPausedByCS = false;
				setErrorText();
			}
		} else if (info.equals(CALL_INFO_VIDEO_PAUSE)) {
			if (mVideoReceiveStatus != MtcCallConstants.EN_MTC_CALL_TRANSMISSION_PAUSE) {
				mVideoReceiveStatus = MtcCallConstants.EN_MTC_CALL_TRANSMISSION_PAUSE;
				setErrorText();
				shrinkPreview();
				updateSurfaceView();
			}
		} else if (info.equals(CALL_INFO_VIDEO_RESUME)) {
			if (mVideoReceiveStatus != MtcCallConstants.EN_MTC_CALL_TRANSMISSION_NORMAL) {
				mVideoReceiveStatus = MtcCallConstants.EN_MTC_CALL_TRANSMISSION_NORMAL;
				setErrorText();
				updateSurfaceView();
			}
		} else if (info.equals(CALL_INFO_VIDEO_OFF)) {
			if (mVideoReceiveStatus != MtcCallConstants.EN_MTC_CALL_TRANSMISSION_CAMOFF) {
				mVideoReceiveStatus = MtcCallConstants.EN_MTC_CALL_TRANSMISSION_CAMOFF;
				setErrorText();
				shrinkPreview();
				updateSurfaceView();
				if (mIsCameraOff) {
					setOperationShown(true);
				}
			}
		} else if (info.equals(CALL_INFO_VIDEO_ON)) {
			if (mVideoReceiveStatus != MtcCallConstants.EN_MTC_CALL_TRANSMISSION_NORMAL) {
				mVideoReceiveStatus = MtcCallConstants.EN_MTC_CALL_TRANSMISSION_NORMAL;
				setErrorText();
				updateSurfaceView();
			}
		}
	}



	@Override
	public void mtcCallDelegateNetStaChanged(int dwSessId, boolean bVideo,
											 boolean bSend, int iStatus) {
		if (bSend)
			return;

		if (mSessId != dwSessId) {
			return;
		}

		if (!bVideo) {
			setErrorText();
			if (iStatus > MtcCallConstants.EN_MTC_NET_STATUS_DISCONNECTED) {
				sHandler.removeMessages(MSG_CALL_DISCONNECTED);
			}
		}

		if (isVideo()) {
			if (mVideoReceiveStatus == MtcCallConstants.EN_MTC_CALL_TRANSMISSION_CAMOFF
					|| mVideoReceiveStatus == MtcCallConstants.EN_MTC_CALL_TRANSMISSION_PAUSE) {
				if (bVideo) {
					return;
				}
			} else {
				if (!bVideo) {
					return;
				}
			}
		}

		mImgSignal.setImageResource(SIGNAL_DRAWABLES[iStatus
				- MtcCallConstants.EN_MTC_NET_STATUS_DISCONNECTED]);
	}



	@Override
	public void mtcCallDelegatePhoneCallBegan() {
		if (mSessState < CALL_STATE_TALKING) {
			onEnd(null);
		} else {
			clearCallMode();
			MtcCall.Mtc_CallInfo(mSessId, CALL_INFO_CALL_INTERRUPT);
			if (mSessState == CALL_STATE_TALKING) {
				setErrorText();
			}
		}
	}


	@Override
	public void mtcCallDelegatePhoneCallEnded() {
		sHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setCallMode(false);
				setAudio(mAudio);
			}
		}, 1000);

		if (mSessState > CALL_STATE_CONNECTING)
			MtcCall.Mtc_CallInfo(mSessId, CALL_INFO_CALL_RESUME);

		if (mIsRtpConnected) {
			mSessState = CALL_STATE_TALKING;
			mReconnecting = false;
			setErrorText();
		} else {
			setErrorText();
		}
	}



	@Override
	public boolean mtcCallDelegateIsCalling() {
		return isCalling();
	}

	@Override
	public boolean mtcCallDelegateIsExisting(String number) {
		return TextUtils.equals(mNumber, number);
	}

	@Override
	public int mtcCallDelegateGetCallId() {
		return mSessId;
	}

	@Override
	public void mtcHeadsetStateChanged(boolean plugged) {
		int audio = AUDIO_HEADSET;
		if (!plugged) {
			if (mAudio != AUDIO_HEADSET)
				return;
			audio = getDefaultAudio();
		}
		setAudio(audio);
	}

	@Override
	public void mtcBluetoothChanged() {
		int audio = AUDIO_BLUETOOTH;
		if (mBluetoothHelper.getCount() == 0) {
			if (mAudio == AUDIO_BLUETOOTH) {
				mAudio = getDefaultAudio();
			} else {
				audio = mAudio;
			}
		}
		setAudio(audio);
	}

	@Override
	public void mtcOrientationChanged(int orientation, int previousOrientation) {
		if (getRemoteView() != null) {
			mRemoteRotationDegree = 360 - orientation * 90;
			ZmfVideo.renderRotate(getRemoteView(), mRemoteRotationDegree);
		}

		if (orientation * 90 == 180) {
			return;
		}

		updateErrorTextView(orientation);
		updateButtons(orientation);

		mViewUser.setVisibility(orientation == 0 ? View.VISIBLE : View.GONE);
		if (orientation == 0) {
			setActivityFullScreen(!mOperationShown
					|| mTxtErrorContainer.getVisibility() == View.VISIBLE);
		} else {
			setActivityFullScreen(true);
		}
	}


	@Override
	public void mtcCallDelegateVideoReceiveStaChanged(int dwSessId, int dwStatus) {
		if (mSessId != dwSessId) {
			return;
		}
		if (mVideoReceiveStatus != dwStatus) {
			mVideoReceiveStatus = dwStatus;
			if (mVideoReceiveStatus == MtcCallConstants.EN_MTC_CALL_TRANSMISSION_CAMOFF
					|| mVideoReceiveStatus == MtcCallConstants.EN_MTC_CALL_TRANSMISSION_PAUSE
					|| mVideoReceiveStatus == MtcCallConstants.EN_MTC_CALL_TRANSMISSION_PAUSE4QOS) {
				shrinkPreview();
				updateSurfaceView();
				if (mVideoReceiveStatus == MtcCallConstants.EN_MTC_CALL_TRANSMISSION_CAMOFF
						&& mIsCameraOff) {
					setOperationShown(true);
				}
			} else if (mVideoReceiveStatus == MtcCallConstants.EN_MTC_CALL_TRANSMISSION_NORMAL) {
				updateSurfaceView();
			}
			setErrorText();
		}
	}

	// magnifier callback
	@Override
	public void tapGesture() {
		setOperationShown(!mOperationShown);
	}

	public void onEnd(View v) {
		if (v != null && getCallDuration() >= 3 * 60 * 1000
				&& isAudioNetStateOk()) {
		}

      /*if(mImageAvatar.getVisibility()== View.GONE){
		  sendCallStateMsg("通话时长："+mChrState.getText());
	  }else{
		  sendCallStateMsg("通话已取消");
	  }*/

		if (mSessState == CALL_STATE_NONE) {
			finish();
			return;
		}

		checkToUpload();

		mSessState = CALL_STATE_ENDING;
		MtcCall.Mtc_CallTerm(mSessId,
				MtcCallConstants.EN_MTC_CALL_TERM_STATUS_NORMAL, "");
		mtcCallDelegateTermed(mSessId,
				MtcCallConstants.EN_MTC_CALL_TERM_STATUS_NORMAL, "");

	}


	public boolean isShouldShow=false;
	public void onMute(View v) {

		try {
			UserEntry chat = makeIshowRecent();
			Intent intent = new Intent(this, ChatActivity.class);
			Bundle bundle = new Bundle();
			bundle.putParcelable("userEntry", chat);
			intent.putExtras(bundle);
			intent.putExtra("isChating", true);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

			startActivity(intent);
			setOperationShown(false);
			isShouldShow =true;
			unread.setVisibility(View.GONE);
		} catch (JSONException e) {
			e.printStackTrace();
		}


	}



	/**
	 * @return
	 * @throws JSONException
	 */
	private UserEntry makeIshowRecent() throws JSONException {

		JSONObject object = new JSONObject(mPeerName);
		UserEntry recent = new UserEntry();
		recent.setUserid(object.getString("userid"));
		recent.setName(object.getString("username"));
		recent.setImg(object.getString("userimg"));
		recent.setMobile(object.getString("userphone"));

		return recent;
	}

	public void onAudio(View v) {
		if (mBluetoothHelper.getCount() > 0) {
			selectAudio();
		} else {
			boolean speaker = !mBtnAudio.isSelected();
			mBtnAudio.setSelected(speaker);
			if (sHandler.hasMessages(MSG_AUDIO_SWITCH)) {
				sHandler.removeMessages(MSG_AUDIO_SWITCH);
				sHandler.sendEmptyMessageDelayed(MSG_AUDIO_SWITCH, 1000);
			} else {
				speaker();
				sHandler.sendEmptyMessageDelayed(MSG_AUDIO_SWITCH, 1000);
			}
		}
		adjustMusicVolumeToMax();
	}

	public void onStatistic(View v) {
		sHandler.removeMessages(MSG_STATISTICS_CLICKED_TIME_OUT);
		if (++mViewStatisticClicked < 3) {
			sHandler.sendEmptyMessageDelayed(MSG_STATISTICS_CLICKED_TIME_OUT,
					500);
			return;
		}
		mViewStatisticClicked = 0;
		if (mStatistics == null) {
			mStatistics = new Statistics(getApplicationContext(), mSessId);
			mViewMain.addView(mStatistics, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
		} else {
			Statistics.sSessionId = mSessId;
		}
		if (mStatistics.isShow()) {
			mStatistics.hideStat();
		} else {
			mStatistics.showStat(isVideo());
		}
	}

	public void onShrink(View v) {
		CallActivity callActivity = (CallActivity) MtcCallDelegate
				.getActiveCallback();
		FloatWindowService.setState(mSessState, isVideo());
		FloatWindowService.setCallStatus(mNumber, mName, mPeerName, mVideo, mAudio);
		FloatWindowService.setButtonStatus("mBtnMute", mBtnMute.isSelected());
		FloatWindowService.setButtonStatus("mBtnAudio", mBtnAudio.isSelected());
		FloatWindowService.setButtonStatus("mBtnSwitch", mIsFrontCamera);
		FloatWindowService.setButtonStatus("mBtnCameraOff", mIsCameraOff);
		FloatWindowService.setButtonStatus("mCallMode", mCallMode);
		FloatWindowService.show(callActivity);
		mFloatWindowDestroy = false;
		isShouldShow =true;
		finish();
	}

	public void showAnswerAlertDialog() {
		if (mAnswerAlertDialog != null && mAnswerAlertDialog.isShowing()) {
			return;
		}
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mAnswerAlertDialog = null;
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(MtcResource.getIdByName("string",
				"Please_hang_up_the_regular_call_before_you_answer"),
				getString(MtcResource.getIdByName("string", "app_name"))));
		builder.setPositiveButton(MtcResource.getIdByName("string", "OK"),
				listener);
		builder.setCancelable(false);
		mAnswerAlertDialog = builder.create();
		mAnswerAlertDialog.show();
	}

	private void statisticClickedTimeOut() {
		mViewStatisticClicked = 0;
	}

	private void callDisconnected() {
		MtcCall.Mtc_CallTerm(mSessId, CALL_FAIL_CALL_DISCONNECTED, "");
		mSessState = CALL_STATE_DISCONNECTED;
		mtcCallDelegateTermed(mSessId, CALL_FAIL_CALL_DISCONNECTED, "");
	}

	@SuppressLint("InflateParams")
	private void initViews() {
		mCallPrimaryView = getLayoutInflater().inflate(
				MtcResource.getIdByName("layout", "call_primary"), null);
		mViewMain = (ViewGroup) findViewById(MtcResource.getIdByName("id",
				"call_main"));
		mViewSubOperation = findViewById(MtcResource.getIdByName("id",
				"call_sub_operation"));
		mImgBg = (ImageView) findViewById(MtcResource.getIdByName("id",
				"call_bg"));
		mImgBg.setBackgroundResource(R.drawable.call_bg);

		mViewMenu = mCallPrimaryView.findViewById(MtcResource.getIdByName("id",
				"call_menu"));
		mViewUser = findViewById(MtcResource.getIdByName("id", "call_user"));
		mTxtName = (TextView) findViewById(MtcResource.getIdByName("id",
				"call_name"));
		mChrState = (Chronometer) findViewById(MtcResource.getIdByName("id",
				"call_state"));

		head = (ImageView) findViewById(R.id.call_avatar);



		mTxtError = (CancelableTextView) findViewById(MtcResource.getIdByName(
				"id", "call_error"));
		mTxtError
				.setDrawableRightListener(new CancelableTextView.DrawableRightListener() {
					@Override
					public void onDrawableRightClick(View view) {
						mTxtErrorContainer.setVisibility(View.GONE);
					}
				});
		mTxtErrorContainer = (RotateLayout) findViewById(MtcResource
				.getIdByName("id", "call_error_container"));

		mViewEnd = (CircleButton) findViewById(MtcResource.getIdByName("id",
				"call_menu_end"));
		mBtnMute = (CircleButton) findViewById(MtcResource.getIdByName("id",
				"call_menu_mute"));
		mBtnAudio = (CircleButton) findViewById(MtcResource.getIdByName("id",
				"call_menu_audio"));


		mBtnCameraOff = (CircleButton) mCallPrimaryView
				.findViewById(MtcResource.getIdByName("id",
						"call_menu_camera_off"));
		mBtnSwitch = (CircleButton) mCallPrimaryView.findViewById(MtcResource
				.getIdByName("id", "call_menu_switch"));
		unread = (TextView) findViewById(R.id.call_unreadCount);

		mBtnRedial = (CircleButton) findViewById(MtcResource.getIdByName("id",
				"call_end_redial"));
		mBtnCancel = (CircleButton) findViewById(MtcResource.getIdByName("id",
				"call_menu_cancel"));
		mBtnCameraOn = (CircleButton) mCallPrimaryView.findViewById(MtcResource
				.getIdByName("id", "call_menu_camera_on"));

		mImgSignal = (ImageView) findViewById(MtcResource.getIdByName("id",
				"call_signal"));

		mBtnShrink = (Button) findViewById(MtcResource.getIdByName("id",
				"call_shrink"));
		mBtnShrink.setBackgroundResource(MtcResource.getIdByName("drawable",
				"call_float_shrink"));

		mViewMute = findViewById(MtcResource.getIdByName("id", "call_mute"));
		mViewAudio = findViewById(MtcResource.getIdByName("id", "call_audio"));
		mViewCameraOff = mCallPrimaryView.findViewById(MtcResource.getIdByName(
				"id", "call_camera_off"));
		mViewSwitch = mCallPrimaryView.findViewById(MtcResource.getIdByName(
				"id", "call_switch"));
		mViewRedial = findViewById(MtcResource.getIdByName("id", "call_redial"));
		mViewCancel = findViewById(MtcResource.getIdByName("id", "call_cancel"));
		mViewCameraOn = mCallPrimaryView.findViewById(MtcResource.getIdByName(
				"id", "call_camera_on"));

		mBtnTxtList = new ArrayList<TextView>();
		mBtnTxtList.add((TextView) findViewById(MtcResource.getIdByName("id",
				"call_redial_text")));
		mBtnTxtList.add((TextView) mCallPrimaryView.findViewById(MtcResource
				.getIdByName("id", "call_camera_off_text")));
		mBtnTxtList.add((TextView) mCallPrimaryView.findViewById(MtcResource
				.getIdByName("id", "call_camera_on_text")));
		mBtnTxtList.add((TextView) mCallPrimaryView.findViewById(MtcResource
				.getIdByName("id", "call_camera_switch_text")));
		mBtnTxtList.add((TextView) findViewById(MtcResource.getIdByName("id",
				"call_end_text")));

		colorNormalCircleButton(mBtnMute,
				MtcResource.getIdByName("drawable", "call_mute_state"));
		colorNormalCircleButton(mBtnAudio,
				MtcResource.getIdByName("drawable", "call_speaker_state"));
		colorNormalCircleButton(mBtnCameraOff,
				MtcResource.getIdByName("drawable", "call_camera_off_state"));
		colorNormalCircleButton(mBtnSwitch,
				MtcResource.getIdByName("drawable", "call_switch_state"));
		colorNormalCircleButton(mBtnCancel,
				MtcResource.getIdByName("drawable", "call_end_normal"));
		colorEndCircleButton(mViewEnd, MtcResource.getIdByName("drawable", "call_end_state"));
		colorRedialCircleButton(mBtnRedial,
				MtcResource.getIdByName("drawable", "call_redial_voice_state"));
		colorNormalCircleButton(mBtnCameraOn,
				MtcResource.getIdByName("drawable", "call_camera_on_state"));

		setViewEnabled(mViewCameraOff, false);
		setViewEnabled(mViewCameraOn, false);
		setViewEnabled(mViewSwitch, false);

		mViewPager = (ViewGroup) findViewById(MtcResource.getIdByName("id",
				"pager"));
		mViewPager.addView(mCallPrimaryView);

		View.OnClickListener l = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isVideo() && isTalking()) {
					setOperationShown(!mOperationShown);
				}
			}
		};
		mViewMain.setOnClickListener(l);

		setupOperationPadding();
		setupStatusBarColor();
	}

	private void handleIntent(Intent intent) {

		if (intent.getBooleanExtra(EXTRA_NOTIFY_CALL, false)
				|| intent.getBooleanExtra(EXTRA_SHOW_PERMISSION_DIALOG, false)) {
			return;
		}

		if (intent.getBooleanExtra(EXTRA_FLOAT_WINDOW_CALL, false)) {

			if (head != null && head.getVisibility() == View.VISIBLE) {
				head.setVisibility(View.GONE);
			}

			mSessId = intent.getIntExtra("FloatCallId", MtcConstants.INVALIDID);
			mSessState = intent.getIntExtra("FloatState", CALL_STATE_NONE);
			mVideo = intent.getIntExtra("FloatVideo", VIDEO_VOICE_ONLY);
			setVideo(mVideo);
			mAudio = intent.getIntExtra("FloatVideo", -1);
			setCallMode(false);
			setAudio(mAudio);
			mNumber = intent.getStringExtra("FloatNumber");
			mName = intent.getStringExtra("FloatName");
			mPeerName = intent.getStringExtra("FloatDisplayName");
			mTxtName.setText(TextUtils.isEmpty(mPeerName) ? mNumber : mPeerName);
			mBaseTime = intent.getLongExtra("FloatBasetime", 0);
			mChrState.setBase(mBaseTime);
			mChrState.start();
			mImgSignal.setVisibility(View.VISIBLE);
			mBtnShrink.setVisibility(View.VISIBLE);
			mBtnMute.setSelected(intent.getBooleanExtra("mBtnMute", false));
			mute();
			mCallMode = intent.getBooleanExtra("mCallMode", false);
			mBtnAudio.setSelected(intent.getBooleanExtra("mBtnAudio", false));
			speaker();
			mIsFrontCamera = intent.getBooleanExtra("mBtnSwitch", false);
			String capture = null;
			if (intent.getBooleanExtra("mBtnSwitch", false)) {
				capture = ZmfVideo.CaptureFront;
			} else {
				capture = ZmfVideo.CaptureBack;
			}
			mIsCameraOff = intent.getBooleanExtra("mBtnCameraOff", false);
			boolean isVideo = intent.getBooleanExtra("FloatIsVideo", false);
			if (isVideo) {
				createVideoViews();
				ZmfVideo.renderAdd(getLocalView(), capture, 0, ZmfVideo.RENDER_FULL_SCREEN);
				mtcCallDelegateStartVideo(mSessId);
				shrinkPreview();
				captureDidStart();
				updateSurfaceView();
			}
			return;
		}

		int sessId = intent.getIntExtra(MtcCallDelegate.CALL_ID,
				MtcConstants.INVALIDID);
		if (sessId == MtcConstants.INVALIDID) {
			boolean isVideo = intent.getBooleanExtra(MtcCallDelegate.VIDEO,
					false);
			String number = intent.getStringExtra(MtcCallDelegate.NUMBER);
			String displayName = intent.getStringExtra(MtcCallDelegate.DISPLAY_NAME);
			String peerDisplayName = intent.getStringExtra(MtcCallDelegate.PEER_DISPLAY_NAME);
			mtcCallDelegateCall(number, displayName, peerDisplayName, isVideo);
		} else {
			mtcCallDelegateIncoming(sessId);
		}
	}

	private void setStateText(CharSequence text, boolean animated, boolean warn) {
		int textColor = Color.WHITE;
		if (TextUtils.isEmpty(text)) {
			if (mChrState.getBase() != 0) {
				mChrState.start();
				mImgSignal.setVisibility(View.VISIBLE);
			} else {
				mChrState.setText("");
				mImgSignal.setVisibility(View.GONE);
			}
		} else {
			mChrState.stop();
			mImgSignal.setVisibility(View.GONE);
			if (animated) {
				text = text.toString().concat("...");
			}
			if (warn) {
				textColor = getResources().getColor(
						MtcResource
								.getIdByName("color", "call_poor_network_bg"));
			}
			mChrState.setText(text);
		}
		mChrState.setTextColor(textColor);
	}

	private void resetStateText() {
		mChrState.stop();
		mChrState.setBase(0);
		mChrState.setText("");
		mChrState.setTextColor(Color.WHITE);
		mImgSignal.setVisibility(View.GONE);
	}

	private void setIncomingView(boolean incoming) {
		if (incoming) {
			if (mViewIncoming == null) {
				mViewIncoming = (CallIncomingSlideView) ((ViewStub) findViewById(MtcResource
						.getIdByName("id", "call_incoming_import"))).inflate();
			} else {
				mViewIncoming.reset();
				mViewIncoming.setVisibility(View.VISIBLE);
			}
			mViewIncoming.setCallback(this);
			mViewIncoming.setVideo(isVideo());
			mViewEnd.setVisibility(View.INVISIBLE);
			mViewMenu.setVisibility(View.INVISIBLE);
			mViewSubOperation.setVisibility(View.INVISIBLE);

		} else {
			if (mViewIncoming != null) {
				mViewIncoming.destroy();
				mViewIncoming.setVisibility(View.GONE);
				mViewIncoming.setCallback(null);
			}
			mViewEnd.setVisibility(View.VISIBLE);
			if (mViewMenu != null) {
				mViewMenu.setVisibility(View.VISIBLE);
			}
			mViewSubOperation.setVisibility(View.VISIBLE);
		}
	}

	public void setOperationShown(boolean show) {
		boolean showFullScreen = !show
				|| mTxtErrorContainer.getVisibility() == View.VISIBLE;
		if (show) {
			showSystemUI();
		} else {
			hideSystemUI();
		}
		setActivityFullScreen(showFullScreen);
		int visibility = show ? View.VISIBLE : View.INVISIBLE;
		mViewEnd.setVisibility(visibility);
		mChrState.setVisibility(visibility);
		mViewMenu.setVisibility(visibility);
		findViewById(MtcResource.getIdByName("id", "call_statistic")).setVisibility(visibility);
		mOperationShown = show;
		findViewById(MtcResource.getIdByName("id", "call_operation")).setVisibility(visibility);
	}

	private void postNotification() {
		Context context = getApplicationContext();
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		String title =null;
		if(head.getVisibility()== View.VISIBLE){
			 title = "正在呼叫...";
		}else{
			 title = "正在通话...";
		}
		builder.setContentTitle(title);
		builder.setSmallIcon(MtcResource.getIdByName("drawable", "ic_launcher"));
		builder.setOngoing(true);
		builder.setPriority(NotificationCompat.PRIORITY_HIGH);

		String state = MtcCallDelegate.getStateString(this, mSessState, isVideo(), false);
		builder.setTicker(state);
		builder.setContentText(state);
		if (isTalking()) {
			builder.setWhen(mNotificationBase);
			builder.setUsesChronometer(true);
		}

		Intent intent = new Intent(context, CallActivity.class);
		intent.putExtra(EXTRA_NOTIFY_CALL, true);
		PendingIntent pending = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(pending);

		NotificationManager notificationManager = (NotificationManager) getApplication()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(
				getResources().getInteger(
						MtcResource.getIdByName("integer", "notify_call")),
				builder.build());
		mShowNotification = true;
	}

	private void removeNotification() {
		NotificationManager notificationManager = (NotificationManager) getApplication()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(getResources().getInteger(
				MtcResource.getIdByName("integer", "notify_call")));
		mShowNotification = false;
	}


	private void vibrate() {
		Vibrator vibrator = (Vibrator) getApplication().getSystemService(
				Context.VIBRATOR_SERVICE);
		vibrator.vibrate(123);
	}

	private void ring(boolean vibrateOnly) {
		if (mRing == null) {
			mRing = new MtcRing();
		}
		if (vibrateOnly) {
			mRing.vibrate(getApplicationContext());
		} else {
			mRing.ring(getApplicationContext());
		}

	}

	private void ringStop() {
		if (mRing != null) {
			mRing.stop();
		}

	}

	public void ringBack() {
		if (mToneGenerator == null) {
			mToneGenerator = new ToneGenerator(getStreamType(),
					TONE_RELATIVE_VOLUME);
		}
		mToneGenerator.startTone(ToneGenerator.TONE_SUP_RINGTONE);

	/*	if (mToneGenerator == null) {
			mToneGenerator = new ToneGenerator(getStreamType(),TONE_RELATIVE_VOLUME);
		}
		mToneGenerator.startTone(ToneGenerator.TONE_SUP_DIAL);*/
	}

	public void ringBackStop() {
		if (mToneGenerator != null) {
			mToneGenerator.stopTone();
		}
	}

	public void ringAlert() {
		if (mToneGenerator == null) {
			mToneGenerator = new ToneGenerator(getStreamType(),
					TONE_RELATIVE_VOLUME);
		}
		mToneGenerator.startTone(ToneGenerator.TONE_SUP_CONFIRM);
	}

	public void ringTerm() {
		if (mToneGenerator == null) {
			mToneGenerator = new ToneGenerator(getStreamType(),	TONE_RELATIVE_VOLUME);
		}
		mToneGenerator.startTone(ToneGenerator.TONE_SUP_RADIO_NOTAVAIL);
	}

	private void ringTerm(int resId, int times, final boolean finish) {
		if (mTermRing == null) {
			mTermRing = new MtcTermRing();
		} else {
			mTermRing.stop();
		}
		mTermRing.start(getApplicationContext(), resId, 2, 0, new Runnable() {
			@Override
			public void run() {
				if (mSessId != MtcConstants.INVALIDID
						|| mSessState != CALL_STATE_NONE || !finish) {
					return;
				}
				finish();
			}
		});
		FloatWindowService.setState(CALL_STATE_TERM_RINGING, isVideo());
	}

	private void ringTermStop() {
		if (mTermRing != null) {
			mTermRing.stop();
		}
	}

	private int getStreamType() {
		return ZmfAudio.outputGetStreamType(MtcMdm
				.Mtc_MdmGetAndroidAudioOutputDevice());
	}

	private static final int MSG_SESS_CALL = 0;
	private static final int MSG_SESS_TERM = 1;
	private static final int MSG_AUDIO_SWITCH = 4;
	private static final int MSG_STATISTICS_CLICKED_TIME_OUT = 5;
	private static final int MSG_SHOW_FLOAT_WINDOW = 6;
	private static final int MSG_CALL_DISCONNECTED = 7;
	private static final int MSG_DID_RESUME = 8;
	private static final int MSG_ALERT_PERMISSION = 9;
	private static final int MSG_ANSWER = 11;

	private static Handler sHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
				case MSG_SESS_CALL: {
					CallActivity callActivity = (CallActivity) MtcCallDelegate
							.getActiveCallback();
					if (callActivity != null) {
						Contact contact = (Contact) message.obj;
						callActivity.call(contact.mNumber, contact.mName,contact.mPeerName, message.arg2 == 1);
					}
					break;
				}
				case MSG_SESS_TERM: {
					CallActivity callActivity = (CallActivity) MtcCallDelegate
							.getActiveCallback();
					if (callActivity != null) {
						callActivity.finish();
					}
					break;
				}
				case MSG_AUDIO_SWITCH: {
					CallActivity callActivity = (CallActivity) MtcCallDelegate
							.getActiveCallback();
					if (callActivity != null) {
						callActivity.speaker();
					}
					break;
				}
				case MSG_STATISTICS_CLICKED_TIME_OUT: {
					CallActivity callActivity = (CallActivity) MtcCallDelegate
							.getActiveCallback();
					if (callActivity != null) {
						callActivity.statisticClickedTimeOut();
					}
					break;
				}
				case MSG_SHOW_FLOAT_WINDOW: {
					CallActivity callActivity = (CallActivity) MtcCallDelegate.getActiveCallback();
					if (callActivity != null) {
						FloatWindowService.show(callActivity);
						if (!isOnTop()) {
							sHandler.sendEmptyMessage(MSG_ALERT_PERMISSION);
						}
					}
					break;
				}
				case MSG_CALL_DISCONNECTED: {
					CallActivity callActivity = (CallActivity) MtcCallDelegate
							.getActiveCallback();
					if (callActivity != null) {
						callActivity.callDisconnected();
					}
					break;
				}
				case MSG_DID_RESUME: {
					CallActivity callActivity = (CallActivity) MtcCallDelegate.getActiveCallback();
					if (callActivity != null) {
						callActivity.didResume();
					}
					break;
				}
				case MSG_ALERT_PERMISSION: {
					if (!CallActivity.isOnTop()
							&& MiuiUtils
							.needShowAlertPermissionDialog(MtcCallDelegate.sContext)) {
						Intent intent = new Intent(MtcCallDelegate.sContext,
								CallActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_SINGLE_TOP
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(EXTRA_SHOW_PERMISSION_DIALOG, true);
						MtcCallDelegate.sContext.startActivity(intent);

						CallActivity callActivity = (CallActivity) MtcCallDelegate
								.getActiveCallback();
						if (callActivity != null) {
							callActivity.showAlertPermissionDialog();
						}
					}
					break;
				}
				case MSG_ANSWER: {
					CallActivity callActivity = (CallActivity) MtcCallDelegate
							.getActiveCallback();
					if (callActivity != null) {
						callActivity.answer(message.arg2);
					}
					break;
				}
			}
		}
	};

	// ZmfObserver
	@Override
	public void handleNotification(int arg0, JSONObject arg1) {
		switch (arg0) {
			case Zmf.VideoCaptureRequestChange: {
				String sId = arg1.optString(Zmf.Capture);
				int iWidth = arg1.optInt(Zmf.Width);
				int iHeight = arg1.optInt(Zmf.Height);
				int iFps = arg1.optInt(Zmf.FrameRate);
				captureRequestChange(sId, iWidth, iHeight, iFps);
				break;
			}
			case Zmf.VideoCaptureDidStart: {
				captureDidStart();
				break;
			}
			case Zmf.VideoRenderDidStart: {
				SurfaceView zView = (SurfaceView) arg1.opt(Zmf.Window);
				renderDidStart(zView);
				break;
			}
			case Zmf.VideoErrorOccurred: {
				String error = arg1.optString(Zmf.VideoError);
				videoErrorOccurred(error);
				break;
			}
			case Zmf.AudioErrorOccurred: {
				String error = arg1.optString(Zmf.AudioError);
				audioErrorOccurred(error);
				break;
			}
			case Zmf.AudioInputDidStart: {
				int mode = MtcMdm.Mtc_MdmGetAndroidAudioMode();
				if (mode != mAudioManager.getMode()) {
					mAudioManager.setMode(mode);
				}
				break;
			}
		}

	}

	public void captureRequestChange(String sId, int iWidth, int iHeight,
									 int iFps) {
		ZmfVideo.captureStopAll();
		captureStart(sId, iWidth, iHeight, iFps);
	}

	public void captureDidStart() {
		setViewEnabled(mViewSwitch, true);
		setViewEnabled(mViewCameraOff, true);
		setViewEnabled(mViewCameraOn, true);
	}

	public void renderDidStart(SurfaceView zView) {
		if (zView == getLocalView()) {
			if (mImgBg.getVisibility() == View.VISIBLE) {
				mImgBg.setVisibility(View.GONE);
				if (mSessState == CALL_STATE_INCOMING) {
				}
			}
		} else if (zView == getRemoteView()) {
			shrinkPreview();
		}
	}

	public void videoErrorOccurred(String arg0) {
		ZmfVideo.captureStopAll();
		if (isCalling() && isVideo()) {
			showVideoErrorDialog();
		}
	}

	private void showVideoErrorDialog() {
		if (mCameraErrorDialog != null) {
			if (mCameraErrorDialog.isShowing())
				return;
		} else {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle(MtcResource.getIdByName("string", "Camera_device_error"));
			b.setMessage(MtcResource.getIdByName("string",
					"Open_Camera_app_to_fix_device_error"));
			b.setPositiveButton(
					MtcResource.getIdByName("string", "Open_Camera"),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							mCameraErrorDialog = null;
							openCameraApp();
						}
					});
			b.setNegativeButton(MtcResource.getIdByName("string", "Cancel"),
					null);
			mCameraErrorDialog = b.create();
			mCameraErrorDialog.setCanceledOnTouchOutside(true);
		}

		mCameraErrorDialog.show();
	}

	public void audioErrorOccurred(String arg0) {
		if (MtcCallDelegate.isInPhoneCall() || !isCalling()) {
			return;
		}

		if (mCallAudioErrorTime >= 1) {
			Toast.makeText(this,
					MtcResource.getIdByName("string", "Audio_device_error"),
					Toast.LENGTH_LONG).show();

			mSessState = CALL_STATE_ENDING;
			MtcCall.Mtc_CallTerm(mSessId, CALL_FAIL_AUDIO_DEVICE, "");
			mtcCallDelegateTermed(mSessId, CALL_FAIL_AUDIO_DEVICE, "");
			return;
		}

		mCallAudioErrorTime++;
		clearCallMode();
		setCallMode(false);
		setAudio(mAudio);
	}

	private void openCameraApp() {
		startActivityForResult(new Intent(
				MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA), REQUEST_CAMERA);
	}

	private void videoCaptureStart() {
		String capture = null;
		switch (mVideo) {
			case VIDEO_CAMERA_FRONT:
				capture = ZmfVideo.CaptureFront;
				break;
			case VIDEO_CAMERA_REAR:
				capture = ZmfVideo.CaptureBack;
				break;
			default:
				return;
		}
		MtcNumber width = new MtcNumber();
		MtcNumber height = new MtcNumber();
		MtcNumber framerate = new MtcNumber();
		MtcMdm.Mtc_MdmGetCaptureParms(width, height, framerate);
		captureStart(capture, width.getValue(), height.getValue(),
				framerate.getValue());
		// ��֮ͨ�󴴽���Ƶ
		createVideoViews();
		getLocalView().setVisibility(View.VISIBLE);
		ZmfVideo.renderAdd(getLocalView(), capture, 0,
				ZmfVideo.RENDER_FULL_SCREEN);
	}

	private void captureStart(String capture, int width, int height,
							  int frameRate) {
		int ret;
		boolean enable = MtcCallDelegate
				.getEnabled(MtcCallDelegate.MAGNIFIER_ENABLED_KEY);
		if (enable)
			ret = ZmfVideo.captureStart(capture, 1280, 720, frameRate);
		else
			ret = ZmfVideo.captureStart(capture, 640, 480, frameRate);

		if (ret != 0) {
			if (isCalling() && isVideo()) {
				showVideoErrorDialog();
			}
		}
	}

	private void createVideoViews() {
		removeVideoViewsIfNeeded();
		if (mLargeSurfaceView != null)
			return;
		Context context = getApplicationContext();
		RelativeLayout.LayoutParams flp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mLargeSurfaceView = ZmfVideo.renderNew(context);
		mLargeSurfaceView.setSoundEffectsEnabled(false);
		mLargeSurfaceView.setLayoutParams(flp);
		mViewMain.addView(mLargeSurfaceView, 0);

		ZmfVideo.renderStart(mLargeSurfaceView);

		RelativeLayout.LayoutParams mSmallFlp = new RelativeLayout.LayoutParams(PixlesUtils.dip2px(this,180), PixlesUtils.dip2px(this,200));
		mSmallSurfaceView = ZmfVideo.renderNew(context);
		mSmallSurfaceView.setLayoutParams(mSmallFlp);
		mSmallSurfaceView.setZOrderMediaOverlay(true);
		mSmallSurfaceView.setSoundEffectsEnabled(false);
		// mLargeSurfaceView
		mViewMain.addView(mSmallSurfaceView, 1);
		ZmfVideo.renderStart(mSmallSurfaceView);
		mSmallSurfaceView.setVisibility(View.GONE);
		mLocalViewInSmallSurfaceView = true;

	}

	private void shrinkPreview() {
		if (!mLocalViewInSmallSurfaceView) {
			return;
		}
		MtcNumber localWidth = new MtcNumber();
		MtcNumber localHeight = new MtcNumber();
		MtcCallExt.Mtc_CallGetVideoLocalSize(mSessId, localHeight, localWidth);

		SurfaceView localView = getLocalView();
		int screenWidth = mViewMain.getWidth();
		int screenHeight = mViewMain.getHeight();
		ST_MTC_RECT localRect = MtcVideo.calcSmallViewRect(this,
				localWidth.getValue(), localHeight.getValue(), screenWidth,
				screenHeight);
		if (localView.getWidth() == localRect.getIWidth()
				&& localView.getHeight() == localRect.getIHeight()) {
			return;
		}
		MtcVideo.setViewRect(localView, localRect);
		localView.setOnTouchListener(new MtcVideo.OnTouchMoveListener());
		localView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				switchSurfaceView();
			}
		});

		setOperationShown(false);
		mImgBg.setVisibility(View.GONE);
		mLocalViewShrinked = true;
	}

	private void setVideo(int video) {
		mVideo = video;
		if (isVideo()) {
			setCameraState(mVideo);
			setLayoutState(STATE_VIDEO);
		} else {
			setCameraState(mVideo);
			setLayoutState(STATE_AUDIO);
		}
	}

	private boolean isVideo() {
		return mVideo < VIDEO_VOICE_ONLY;}

	private int getDefaultVideo() {
		if (ZmfVideo.CaptureFront != null) {
			return VIDEO_CAMERA_FRONT;
		}
		return VIDEO_CAMERA_REAR;
	}

	private void cameraFront() {
		int beforeVideo = mVideo;
		switch (mVideo) {
			case VIDEO_CAMERA_FRONT:
				break;
			case VIDEO_CAMERA_REAR:
				ZmfVideo.renderRemoveAll(getLocalView());
				ZmfVideo.captureStopAll();
				break;
			case VIDEO_CAMERA_OFF:
				SurfaceView localView = getLocalView();
				if (localView != null) {
					localView.setVisibility(View.VISIBLE);
				}
				if (isTalking()) {
					sendVideoResumeForCameraOn();
				}
				break;
			case VIDEO_VOICE_ONLY: {
			/*if (isTalking()) {
				MtcCall.Mtc_CallUpdate(mSessId, true, true);
			}*/
				break;
			}
		}
		setVideo(VIDEO_CAMERA_FRONT);
		if (beforeVideo != VIDEO_CAMERA_OFF || getLocalView() == null) {
			videoCaptureStart();
		}
		MtcCall.Mtc_CallCameraAttach(mSessId, ZmfVideo.CaptureFront);
	}

	private void cameraRear() {
		int beforeVideo = mVideo;
		switch (mVideo) {
			case VIDEO_CAMERA_FRONT:
				ZmfVideo.renderRemoveAll(getLocalView());
				ZmfVideo.captureStopAll();
				break;
			case VIDEO_CAMERA_REAR:
				break;
			case VIDEO_CAMERA_OFF:
				SurfaceView localView = getLocalView();
				if (localView != null) {
					localView.setVisibility(View.VISIBLE);
				}
				if (isTalking()) {
					sendVideoResumeForCameraOn();
				}
				break;
			case VIDEO_VOICE_ONLY: {
			/*if (isTalking()) {
				MtcCall.Mtc_CallUpdate(mSessId, true, true);
			}*/
				break;
			}
		}
		setVideo(VIDEO_CAMERA_REAR);
		if (beforeVideo != VIDEO_CAMERA_OFF || getLocalView() == null) {
			videoCaptureStart();
		}
		MtcCall.Mtc_CallCameraAttach(mSessId, ZmfVideo.CaptureBack);
	}

	private void cameraOff() {
		if (mVideo < VIDEO_CAMERA_OFF) {
			SurfaceView localView = getLocalView();
			if (localView != null) {
				localView.setVisibility(View.GONE);
			}
			if (isTalking()) {
				MtcCall.Mtc_CallCameraDetach(mSessId);
				sendVideoPausedForCameraOff();
			}
			setVideo(VIDEO_CAMERA_OFF);
		}
	}

	private void voiceOnly() {
		/*if (isTalking()) {
			MtcCall.Mtc_CallUpdate(mSessId, true, false);
		}*/
		setVideo(VIDEO_VOICE_ONLY);
		mtcCallDelegateStopVideo(mSessId);
		setErrorText();
	}


	private void answer(int video) {
		if (MtcCallDelegate.isInPhoneCall()) {
			showAnswerAlertDialog();
			return;
		}

		ringStop();


		sHandler.removeMessages(MSG_SESS_TERM);
		mSessState = CALL_STATE_ANSWERING;

		switch (video) {
			case VIDEO_CAMERA_FRONT:
				cameraFront();
				break;
			case VIDEO_CAMERA_REAR:
				cameraRear();
				break;
			case VIDEO_CAMERA_OFF:
				cameraOff();
				break;
			case VIDEO_VOICE_ONLY:
				voiceOnly();
				break;
		}
		setStateText(getString(MtcResource.getIdByName("string", "Answering")),
				true, false);
		FloatWindowService.setState(CALL_STATE_ANSWERING, isVideo());
		if (mShowNotification)
			postNotification();
		setIncomingView(false);
		setViewEnabled(mViewEnd, true);
		setViewEnabled(mViewMute, true);
		setViewEnabled(mViewAudio, true);

		setCallMode(true);
		setAudio(getDefaultAudio());
		if (MtcConstants.ZOK != MtcCall.Mtc_CallAnswer(mSessId, 0, true,
				isVideo())) {
			MtcCall.Mtc_CallTerm(mSessId, CALL_FAIL_ANSWER, "");
			mtcCallDelegateTermed(mSessId, CALL_FAIL_ANSWER, "");
		}
	}


	private void call(String number, String displayName,
					  String peerDisplayName, boolean isVideo) {
		if (mSessId != MtcConstants.INVALIDID
				|| mSessState != CALL_STATE_CALLING) {
			return;
		}

		String uri = MtcUser.Mtc_UserFormUri(
				MtcUserConstants.EN_MTC_USER_ID_USERNAME, number);
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(MtcCall.MtcCallInfoHasVideoKey, isVideo());
			jsonObject.put(MtcCall.MtcCallInfoDisplayNameKey, displayName);
			jsonObject.put(MtcCall.MtcCallInfoPeerDisplayNameKey,
					peerDisplayName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mSessId = MtcCall.Mtc_CallJ(uri, 0, jsonObject.toString());
		FloatWindowService.setSessId(mSessId);
		if (mSessId == MtcConstants.INVALIDID) {
			mtcCallDelegateTermed(mSessId, CALL_FAIL_CALL, "");
		} else {
			mIsRtpConnected = false;
			mute();
		}
	}

	private void setCallMode(boolean answering) {
		if (mCallMode)
			return;
		mCallMode = true;
		int mode = MtcMdm.Mtc_MdmGetAndroidAudioMode();
		if (mode != mAudioManager.getMode()) {
			mAudioManager.setMode(mode);
		}
		mAudioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);
		audioStart(answering);
		adjustMusicVolumeToMax();
		mHeadsetPlugReceiver.start(getApplicationContext());
		mBluetoothHelper.start();
	}

	private void clearCallMode() {
		mCallMode = false;
		if (mAudioManager == null) {
			return;
		}
		mHeadsetPlugReceiver.stop(getApplicationContext());
		mBluetoothHelper.stop();
		synchronized (this) {
			ZmfAudio.inputStopAll();
			ZmfAudio.outputStopAll();
		}
		mAudioManager.abandonAudioFocus(null);
		if (AudioManager.MODE_NORMAL != mAudioManager.getMode()) {
			mAudioManager.setMode(AudioManager.MODE_NORMAL);
		}
	}

	private boolean inCallMode() {
		if (mAudioManager == null)
			return false;
		return mCallMode;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void audioStart(final boolean answering) {
		if (CallActivity.this.isCalling()) {
			syncAudioStart(answering);
		}
	}

	private void syncAudioStart(boolean answering) {
		int ret = ZmfAudio.inputStart(
				MtcMdm.Mtc_MdmGetAndroidAudioInputDevice(), 0, 0,
				MtcMdm.Mtc_MdmGetOsAec() ? ZmfAudio.AEC_ON : ZmfAudio.AEC_OFF,
				MtcMdm.Mtc_MdmGetOsAgc() ? ZmfAudio.AGC_ON : ZmfAudio.AGC_OFF);
		if (ret == 0) {
			ret = ZmfAudio.outputStart(
					MtcMdm.Mtc_MdmGetAndroidAudioOutputDevice(), 0, 0);
		}
		if (ret != 0) {

			// audio restart
			ZmfAudio.inputStopAll();
			ZmfAudio.outputStopAll();
			if (mAudioManager != null) {
				if (AudioManager.MODE_NORMAL != mAudioManager.getMode()) {
					mAudioManager.setMode(AudioManager.MODE_NORMAL);
				}
			}
			ret = ZmfAudio.inputStart(MtcMdm
							.Mtc_MdmGetAndroidAudioInputDevice(), 0, 0, MtcMdm
							.Mtc_MdmGetOsAec() ? ZmfAudio.AEC_ON : ZmfAudio.AEC_OFF,
					MtcMdm.Mtc_MdmGetOsAgc() ? ZmfAudio.AGC_ON
							: ZmfAudio.AGC_OFF);
			if (ret == 0) {
				ret = ZmfAudio.outputStart(
						MtcMdm.Mtc_MdmGetAndroidAudioOutputDevice(), 0, 0);
			}
		}
	}

	private void mute() {
		if (mSessId != MtcConstants.INVALIDID) {
			MtcCall.Mtc_CallSetMicMute(mSessId, mBtnMute.isSelected());
		}
	}

	private void speaker() {
		if (mBtnAudio.isSelected()) {
			if (inCallMode() && !mAudioManager.isSpeakerphoneOn())
				mAudioManager.setSpeakerphoneOn(true);
			mAudio = AUDIO_SPEAKER;
		} else {
			if (inCallMode() && mAudioManager.isSpeakerphoneOn())
				mAudioManager.setSpeakerphoneOn(false);
			mAudio = mHeadsetPlugReceiver.mPlugged ? AUDIO_HEADSET
					: AUDIO_RECEIVER;
		}
	}

	private int getDefaultAudio() {
		if (mBluetoothHelper.getCount() > 0)
			return AUDIO_BLUETOOTH;

		if (mBtnAudio.isSelected())
			return AUDIO_SPEAKER;

		if (mHeadsetPlugReceiver.mPlugged)
			return AUDIO_HEADSET;

		if (isVideo())
			return AUDIO_SPEAKER;

		return AUDIO_RECEIVER;
	}

	private void setAudio(int audio) {
		if (mBluetoothHelper.getCount() > 0) {
			if (inCallMode()) {
				switch (audio) {
					case AUDIO_RECEIVER:
					case AUDIO_HEADSET:
						mBtnAudio.setImageResource(MtcResource.getIdByName(
								"drawable", "call_receiver_normal"));
						mBluetoothHelper.unlink(false);
						break;
					case AUDIO_SPEAKER:
						mBtnAudio.setImageResource(MtcResource.getIdByName(
								"drawable", "call_speaker_normal"));
						mBluetoothHelper.unlink(true);
						break;
					case AUDIO_BLUETOOTH:
						mBtnAudio.setImageResource(MtcResource.getIdByName(
								"drawable", "call_bluetooth_normal"));
						mBluetoothHelper.link(mBluetoothHelper.mAddressList.get(0));
						break;
				}
			}
			mBtnAudio.setSelected(false);
			mAudio = audio;
		} else {
			mBtnAudio.setImageResource(MtcResource.getIdByName("drawable",
					"call_speaker_state"));
			mBtnAudio.setSelected(audio == AUDIO_SPEAKER);
			speaker();
		}
	}

	private void selectAudio() {
		final CallAudioAdapter audioAdapter = new CallAudioAdapter();
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mAlertDialog = null;
				if (which < 0)
					return;
				int audio = audioAdapter.mAudioArray[which];
				setAudio(audio);
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setNegativeButton(MtcResource.getIdByName("string", "Cancel"),
				listener);
		builder.setAdapter(audioAdapter, listener);
		mAlertDialog = builder.create();
		mAlertDialog.show();
	}

	private static final int AUDIO_STRINGS[] = new int[] {
			MtcResource.getIdByName("string", "Receiver"),
			MtcResource.getIdByName("string", "Headset"),
			MtcResource.getIdByName("string", "Speaker") };

	private static final int AUDIO_DRAWABLES[] = new int[] {
			MtcResource.getIdByName("drawable", "call_audio_receiver"),
			MtcResource.getIdByName("drawable", "call_audio_headset"),
			MtcResource.getIdByName("drawable", "call_audio_speaker"),
			MtcResource.getIdByName("drawable", "call_audio_bluetooth") };

	private static final int SIGNAL_DRAWABLES[] = new int[] {
			MtcResource.getIdByName("drawable", "call_signal_0"),
			MtcResource.getIdByName("drawable", "call_signal_1"),
			MtcResource.getIdByName("drawable", "call_signal_2"),
			MtcResource.getIdByName("drawable", "call_signal_3"),
			MtcResource.getIdByName("drawable", "call_signal_4"),
			MtcResource.getIdByName("drawable", "call_signal_5") };

	@Override
	public void onCheckJustalkStateNotification(boolean state) {

	}

	@Override
	public void onTextSendFaild(Intent In) {

	}

	@Override
	public void onTextSendOK(Intent In) {

	}

	@Override
	public void onTextRecevier(MsgEntry In) {

		if (TextUtils.equals(In.getFromUserid(),iShowConfig.talingUid))
			unread.setVisibility(View.VISIBLE);
	}

	class CallAudioAdapter extends BaseAdapter {

		int mAudioArray[];

		CallAudioAdapter() {
			mAudioArray = new int[] {
					mHeadsetPlugReceiver.mPlugged ? AUDIO_HEADSET
							: AUDIO_RECEIVER, AUDIO_SPEAKER, AUDIO_BLUETOOTH };
		}

		@Override
		public int getCount() {
			return mAudioArray.length;
		}

		@Override
		public Object getItem(int position) {
			return mAudioArray[position];
		}

		@Override
		public long getItemId(int position) {
			return mAudioArray[position];
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(CallActivity.this).inflate(
						android.R.layout.select_dialog_singlechoice, null);
			}
			CheckedTextView tv = (CheckedTextView) convertView
					.findViewById(android.R.id.text1);
			int audio = mAudioArray[position];
			if (audio == AUDIO_BLUETOOTH) {
				tv.setText(mBluetoothHelper.mNameList.get(0));
			} else {
				tv.setText(AUDIO_STRINGS[audio]);
			}
			tv.setCompoundDrawablesWithIntrinsicBounds(AUDIO_DRAWABLES[audio],
					0, 0, 0);
			tv.setCompoundDrawablePadding((int) (CallActivity.this
					.getResources().getDisplayMetrics().density * 10));
			tv.setChecked(audio == mAudio);
			return convertView;
		}

	}

	private void setCameraState(int video) {
		switch (video) {
			case VIDEO_CAMERA_FRONT:
				if (!mIsFrontCamera) {
					setViewEnabled(mViewSwitch, false);
					mIsFrontCamera = true;
				}
				mIsCameraOff = false;
				break;
			case VIDEO_CAMERA_REAR:
				if (mIsFrontCamera) {
					setViewEnabled(mViewSwitch, false);
					mIsFrontCamera = false;
				}
				mIsCameraOff = false;
				break;
			case VIDEO_CAMERA_OFF:
				mIsCameraOff = true;
				setViewEnabled(mViewCameraOn, true);
				break;
			case VIDEO_VOICE_ONLY:
				mIsCameraOff = true;
				break;
		}
	}

	void colorNormalCircleButton(CircleButton button, int resId) {
		Resources res = getResources();
		button.setStroke(STROKE_WIDTH, res.getColor(MtcResource.getIdByName(
				"color", "call_menu_default_stroke_color")));
		button.setDisabledStroke(STROKE_WIDTH,
				res.getColor(MtcResource.getIdByName("color",
						"call_menu_default_disabled_stroke_color")));
		button.setBackgroundNormalColor(res.getColor(MtcResource.getIdByName(
				"color", "call_menu_default_bg_normal_color")));
		button.setBackgroundPressedColor(res.getColor(MtcResource.getIdByName(
				"color", "call_menu_default_bg_pressed_color")));
		button.setBackgroundSelectedColor(res.getColor(MtcResource.getIdByName(
				"color", "call_menu_default_bg_selected_color")));
		button.setBackgroundDisabledColor(res.getColor(MtcResource.getIdByName(
				"color", "call_menu_default_bg_disabled_color")));
		button.setImageResource(resId);
	}

	void colorEndCircleButton(CircleButton button, int resId) {
		Resources res = getResources();
		button.setStroke(0, 0);
		button.setDisabledStroke(0, 0);
		button.setBackgroundNormalColor(res.getColor(MtcResource.getIdByName(
				"color", "call_menu_end_bg_normal_color")));
		button.setBackgroundPressedColor(res.getColor(MtcResource.getIdByName(
				"color", "call_menu_end_bg_pressed_color")));
		button.setBackgroundDisabledColor(res.getColor(MtcResource.getIdByName(
				"color", "call_menu_end_bg_disabled_color")));
		button.setImageResource(resId);
	}

	void colorRedialCircleButton(CircleButton button, int resId) {
		Resources res = getResources();
		button.setStroke(0, 0);
		button.setDisabledStroke(0, 0);
		button.setBackgroundNormalColor(res.getColor(MtcResource.getIdByName(
				"color", "call_menu_redial_bg_normal_color")));
		button.setBackgroundPressedColor(res.getColor(MtcResource.getIdByName(
				"color", "call_menu_redial_bg_pressed_color")));
		button.setBackgroundDisabledColor(res.getColor(MtcResource.getIdByName(
				"color", "call_menu_redial_bg_disabled_color")));
		button.setImageResource(resId);
	}

	public void onVideoSwitch(View v) {
		if (mIsFrontCamera) {
			cameraRear();
		} else {
			cameraFront();
		}
	}

	public void onCameraSwitch(View v) {
		if (mIsCameraOff) {
			if (mIsFrontCamera) {
				cameraFront();
			} else {
				cameraRear();
			}
			updateSurfaceView();
		} else {
			cameraOff();

			updateSurfaceView();
		}
	}


	public void onRedial(View v) {
		MtcCallDelegate.call(mNumber, mName, mPeerName, isVideo());
	}

	private static final int STATE_VIDEO = 1;
	private static final int STATE_AUDIO = 2;
	private static final int STATE_NO_ANSWER = 3;
	private static final int STATE_NETWORK_UNAVAILABLE = 4;
	private static final int STATE_OFFLINE = 5;
	private static final int STATE_TEMPORARILY_UNAVAILABLE = 6;
	private static final int STATE_BUSY = 7;
	private static final int STATE_DISCONNECTED = 8;

	private void setLayoutState(int state) {
		switch (state) {
			case STATE_VIDEO:
			case STATE_AUDIO:
				boolean isVideo = state == STATE_VIDEO;
				mViewMute.setVisibility(View.VISIBLE);
				mViewAudio.setVisibility(View.VISIBLE);
				mViewRedial.setVisibility(View.GONE);
				mViewEnd.setVisibility(View.VISIBLE);
				mViewCancel.setVisibility(View.GONE);
				if (isVideo) {

					if (!mIsCameraOff) {
						mViewCameraOff.setVisibility(View.VISIBLE);
						mViewSwitch.setVisibility(View.VISIBLE);
						mViewCameraOn.setVisibility(View.GONE);
					} else {
						mViewCameraOff.setVisibility(View.GONE);
						mViewSwitch.setVisibility(View.GONE);
						mViewCameraOn.setVisibility(View.VISIBLE);
					}

				} else {
					mViewCameraOff.setVisibility(View.GONE);
					mViewSwitch.setVisibility(View.GONE);
					mViewCameraOn.setVisibility(View.GONE);
				}

				break;
			case STATE_NO_ANSWER:
			case STATE_DISCONNECTED:
				mViewMute.setVisibility(View.GONE);
				mViewAudio.setVisibility(View.GONE);
				mViewCameraOff.setVisibility(View.GONE);
				mViewSwitch.setVisibility(View.GONE);
				mViewRedial.setVisibility(View.VISIBLE);
				mViewEnd.setVisibility(View.GONE);
				mViewCancel.setVisibility(View.VISIBLE);
				mViewCameraOn.setVisibility(View.GONE);
				mBtnRedial.setImageResource(MtcResource.getIdByName("drawable", "call_redial_video_state"));
				break;
			case STATE_NETWORK_UNAVAILABLE:
			case STATE_OFFLINE:
			case STATE_TEMPORARILY_UNAVAILABLE:
				mViewMute.setVisibility(View.GONE);
				mViewAudio.setVisibility(View.GONE);
				mViewCameraOff.setVisibility(View.GONE);
				mViewSwitch.setVisibility(View.GONE);
				mViewRedial.setVisibility(View.VISIBLE);
				mViewEnd.setVisibility(View.GONE);
				mViewCancel.setVisibility(View.VISIBLE);
				mViewCameraOn.setVisibility(View.GONE);
				break;
			case STATE_BUSY:
				mViewMute.setVisibility(View.GONE);
				mViewAudio.setVisibility(View.GONE);
				mViewCameraOff.setVisibility(View.GONE);
				mViewSwitch.setVisibility(View.GONE);
				mViewRedial.setVisibility(View.VISIBLE);
				mViewEnd.setVisibility(View.GONE);
				mViewCancel.setVisibility(View.VISIBLE);
				mViewCameraOn.setVisibility(View.GONE);
				mViewRedial.setVisibility(View.VISIBLE);
				break;
			default:
				break;
		}
	}

	@Override
	public void callHandleViewAnswerVoice(CallIncomingSlideView v) {
		answer(VIDEO_CURRENT);
	}


	@Override
	public void callHandleViewAnswerVideo(CallIncomingSlideView v) {
		//mImageAvatar.setVisibility(View.GONE);
		if (isVideo()) {
			if (!previewStarted()) {
				mtcCallDelegateStartPreview();
				createVideoViews();
			}
		}
		answer(VIDEO_CURRENT);
	}

	@Override
	public void callHandleViewAnswerCameraOff(CallIncomingSlideView v) {
		answer(VIDEO_CAMERA_OFF);
	}

	@Override
	public void callHandleViewDecline(CallIncomingSlideView v) {
		mSessState = CALL_STATE_DECLINING;
		MtcCall.Mtc_CallTerm(mSessId,
				MtcCallConstants.EN_MTC_CALL_TERM_STATUS_DECLINE, "");
		mtcCallDelegateTermed(mSessId,
				MtcCallConstants.EN_MTC_CALL_TERM_STATUS_DECLINE, "");
	}

	private void sendVideoPausedForCameraOff() {
		MtcCall.Mtc_CallVideoSetSend(mSessId,
				MtcCallConstants.EN_MTC_CALL_TRANSMISSION_CAMOFF);
	}

	private void sendVideoResumeForCameraOn() {
		MtcCall.Mtc_CallVideoSetSend(mSessId,
				MtcCallConstants.EN_MTC_CALL_TRANSMISSION_NORMAL);
	}

	private boolean setErrorText(String text) {

		if (TextUtils.equals(text, mTxtError.getText())) {
			return false;
		}
		SurfaceView remoteView = getRemoteView();
		if (!TextUtils.isEmpty(text)) {
			mTxtErrorContainer.setVisibility(View.VISIBLE);
			mTxtError.setText(text);
			setActivityFullScreen(true);
			if (remoteView != null) {
				String renderId = MtcCall.Mtc_CallGetName(mSessId);
				ZmfVideo.renderEffect(remoteView, renderId,
						ZmfVideo.RENDER_EFFECT_BLUR, null, null);
				ZmfVideo.renderFreeze(remoteView, renderId, true);
				mRemoteViewFreezed = true;
			}
		} else {
			mTxtErrorContainer.setVisibility(View.GONE);
			mTxtError.setText(null);
			setActivityFullScreen(!mOperationShown);
			if (remoteView != null) {
				String renderId = MtcCall.Mtc_CallGetName(mSessId);
				ZmfVideo.renderFreeze(remoteView, renderId, false);
				ZmfVideo.renderEffect(remoteView, renderId,
						ZmfVideo.RENDER_EFFECT_NONE, null, null);
				mRemoteViewFreezed = false;
			}
		}
		return true;
	}

	private void setErrorText() {
		setErrorText(true);
	}

	private void setErrorText(boolean alertIfNeeded) {
		if (mSessState < CALL_STATE_TALKING) {
			setErrorText(null);
			FloatWindowService.setState(mSessState, isVideo());
			return;
		}

		if (getNet() == MtcCliConstants.MTC_ANET_UNAVAILABLE) {
			if (setErrorText(getString(MtcResource.getIdByName("string",
					"Please_check_the_network_connection")))) {
				FloatWindowService.setState(CALL_STATE_TIMING, isVideo());
				if (alertIfNeeded) {
					ringAlert();
				}
			}
			if (!sHandler.hasMessages(MSG_CALL_DISCONNECTED)) {
				sHandler.sendEmptyMessageDelayed(MSG_CALL_DISCONNECTED, 30000);
			}
			return;
		}

		if (mReconnecting) {
			if (setErrorText(getString(MtcResource.getIdByName("string",
					"Reconnecting")))) {
				FloatWindowService.setState(CALL_STATE_TIMING, isVideo());
				if (alertIfNeeded) {
					ringAlert();
				}
			}
			return;
		}

		if (mPausedByCS) {
			if (setErrorText(getString(MtcResource.getIdByName("string",
					"Interrupted_by_regular_call_description")))) {
				FloatWindowService.setState(CALL_STATE_PAUSED, isVideo());
				if (alertIfNeeded) {
					ringAlert();
				}
			}
			return;
		}

		if (mPaused) {
			if (setErrorText(getString(MtcResource.getIdByName("string",
					"Call_paused")))) {
				FloatWindowService.setState(CALL_STATE_PAUSED, isVideo());
				if (alertIfNeeded) {
					ringAlert();
				}
			}
			return;
		}

		if (MtcCallDelegate.isInPhoneCall()) {
			setErrorText(getString(MtcResource.getIdByName("string",
					"Call_paused")));
			FloatWindowService.setState(CALL_STATE_PAUSED, isVideo());
			return;
		}

		int audioNetSta = MtcCall.Mtc_CallGetAudioNetSta(mSessId);
		if (audioNetSta <= MtcCallConstants.EN_MTC_NET_STATUS_DISCONNECTED) {
			if (!sHandler.hasMessages(MSG_CALL_DISCONNECTED)) {
				sHandler.sendEmptyMessageDelayed(MSG_CALL_DISCONNECTED, 30000);
			}
			mDisconnect = true;
		}

		if (isVideo()) {
			switch (mVideoReceiveStatus) {
				case MtcCallConstants.EN_MTC_CALL_TRANSMISSION_CAMOFF:
					setErrorText(getString(MtcResource.getIdByName("string",
							"Other_side_camera_off")));
					FloatWindowService.setState(CALL_STATE_TIMING, isVideo());
					return;
				case MtcCallConstants.EN_MTC_CALL_TRANSMISSION_PAUSE:
					setErrorText(getString(MtcResource.getIdByName("string",
							"Video_paused")));
					FloatWindowService.setState(CALL_STATE_TIMING, isVideo());
					return;
				default:
					break;
			}
		} else {
			if (audioNetSta <= MtcCallConstants.EN_MTC_NET_STATUS_DISCONNECTED) {
				setErrorText(getString(MtcResource.getIdByName("string",
						"Poor_connection")));
				FloatWindowService.setState(CALL_STATE_TIMING, isVideo());
				return;
			}
		}
		setErrorText(null);
		FloatWindowService.setState(mSessState, isVideo());
	}

	private void removeVideoViewsIfNeeded() {
		if (mRemovedLargeSurfaceView != null
				&& mRemovedLargeSurfaceView.getParent() == mViewMain) {
			mViewMain.removeView(mRemovedLargeSurfaceView);
			mRemovedLargeSurfaceView = null;
		}
		if (mRemovedSmallSurfaceView != null
				&& mRemovedSmallSurfaceView.getParent() == mViewMain) {
			mViewMain.removeView(mRemovedSmallSurfaceView);
			mRemovedSmallSurfaceView = null;
		}
	}

	private void term() {
		if (mViewIncoming != null) {
			mViewIncoming.destroy();
		}
		FloatWindowService.setState(CALL_STATE_ENDING, isVideo());
		if (mAlertDialog != null) {
			if (mAlertDialog.isShowing())
				mAlertDialog.dismiss();
			mAlertDialog = null;
		}
		if (mAnswerAlertDialog != null) {
			if (mAnswerAlertDialog.isShowing())
				mAnswerAlertDialog.dismiss();
			mAnswerAlertDialog = null;
		}
		if (mCameraErrorDialog != null) {
			if (mCameraErrorDialog.isShowing())
				mCameraErrorDialog.dismiss();
			mCameraErrorDialog = null;
		}
		if (mAlertPermissionDialog != null) {
			if (mAlertPermissionDialog.isShowing())
				mAlertPermissionDialog.dismiss();
			mAlertPermissionDialog = null;
		}
		if (mEndAndCallDialog != null) {
			if (mEndAndCallDialog.isShowing())
				mEndAndCallDialog.dismiss();
			mEndAndCallDialog = null;
		}
		if (mSnsMessageDialog != null) {
			if (mSnsMessageDialog.isShowing())
				mSnsMessageDialog.dismiss();
			mSnsMessageDialog = null;
		}

		if (mStatistics != null && mStatistics.isShow()) {
			mStatistics.hideStat();
		}

		ringStop();
		ringBackStop();
		removeNotification();

		if ((mSessState != CALL_STATE_INCOMING)
				&& (mSessState != CALL_STATE_DECLINING))
			setOperationShown(true);

		mSessState = CALL_STATE_NONE;
		mtcCallDelegateStopVideo(mSessId);

		mSessId = MtcConstants.INVALIDID;
		FloatWindowService.setSessId(mSessId);

		if (sHandler.hasMessages(MSG_CALL_DISCONNECTED)) {
			sHandler.removeMessages(MSG_CALL_DISCONNECTED);
		}
	}

	private void declineWithText(String text) {
		mSessState = CALL_STATE_DECLINING;
		MtcCall.Mtc_CallTerm(mSessId,
				MtcCallConstants.EN_MTC_CALL_TERM_STATUS_DECLINE, text);
		mtcCallDelegateTermed(mSessId,
				MtcCallConstants.EN_MTC_CALL_TERM_STATUS_DECLINE, "");
	}

	private boolean previewStarted() {
		return (getLocalView() != null)
				&& (getLocalView().getVisibility() == View.VISIBLE);
	}


	private void didResume() {
		mResumed = true;
		/*
		 * if (mSessState == CALL_STATE_INCOMING) { if (isVideo()) { if
		 * (!previewStarted()) { mtcCallDelegateStartPreview(); } } }
		 */
		if (isTalking()) {
			mAudioManager.requestAudioFocus(null,
					AudioManager.STREAM_VOICE_CALL,
					AudioManager.AUDIOFOCUS_GAIN);
		}
	}


	private void switchSurfaceView() {
		String capture = null;
		if (mIsFrontCamera) {
			capture = ZmfVideo.CaptureFront;
		} else {
			capture = ZmfVideo.CaptureBack;
		}

		int screenWidth = mViewMain.getWidth();
		int screenHeight = mViewMain.getHeight();
		SurfaceView newLocalView;
		SurfaceView newRemoteView;
		MtcNumber smallViewWidth = new MtcNumber();
		MtcNumber smallViewHeight = new MtcNumber();

		if (!mLocalViewInSmallSurfaceView) {
			newLocalView = mSmallSurfaceView;
			newRemoteView = mLargeSurfaceView;
			MtcCallExt.Mtc_CallGetVideoLocalSize(mSessId, smallViewHeight,
					smallViewWidth);
		} else {
			newLocalView = mLargeSurfaceView;
			newRemoteView = mSmallSurfaceView;
			MtcCallExt.Mtc_CallGetVideoRemoteSize(mSessId, smallViewHeight,
					smallViewWidth);
		}

		// resize small surface view
		//LogUtil.IShowLg(smallViewHeight.getValue()+"------"+smallViewWidth.getValue(),null);
		//ST_MTC_RECT rect = MtcVideo.calcSmallViewRect(smallViewWidth.getValue(), smallViewHeight.getValue(), screenWidth, screenHeight);
		ST_MTC_RECT rect = MtcVideo.calcSmallViewRect(this, PixlesUtils.px2dip(this,350), PixlesUtils.px2dip(this, 450), screenWidth, screenHeight);

		MtcVideo.setViewRect(mSmallSurfaceView, rect);


		ZmfVideo.renderAdd(newLocalView, capture, 0,
				ZmfVideo.RENDER_FULL_SCREEN);
		ZmfVideo.renderAdd(newRemoteView, MtcCall.Mtc_CallGetName(mSessId), 0,
				ZmfVideo.RENDER_AUTO);
		ZmfVideo.renderRemove(newLocalView, MtcCall.Mtc_CallGetName(mSessId));
		ZmfVideo.renderRemove(newRemoteView, capture);

		ZmfVideo.renderRotate(newLocalView, 0);
		ZmfVideo.renderRotate(newRemoteView, mRemoteRotationDegree);

		// update freeze state
		if (mRemoteViewFreezed) {
			ZmfVideo.renderFreeze(newLocalView, capture, false);
			ZmfVideo.renderEffect(newLocalView, capture, ZmfVideo.RENDER_EFFECT_NONE, null, null);
			ZmfVideo.renderFreeze(newRemoteView,
					MtcCall.Mtc_CallGetName(mSessId), true);
			ZmfVideo.renderEffect(newRemoteView,
					MtcCall.Mtc_CallGetName(mSessId),
					ZmfVideo.RENDER_EFFECT_BLUR, null, null);
		} else {
			ZmfVideo.renderFreeze(newLocalView, capture, false);
			ZmfVideo.renderEffect(newLocalView, capture,
					ZmfVideo.RENDER_EFFECT_NONE, null, null);
			ZmfVideo.renderFreeze(newRemoteView,
					MtcCall.Mtc_CallGetName(mSessId), false);
			ZmfVideo.renderEffect(newRemoteView,
					MtcCall.Mtc_CallGetName(mSessId),
					ZmfVideo.RENDER_EFFECT_NONE, null, null);
		}

		mLocalViewInSmallSurfaceView = !mLocalViewInSmallSurfaceView;
	}


	private SurfaceView getLocalView() {
		return mLocalViewInSmallSurfaceView ? mSmallSurfaceView
				: mLargeSurfaceView;
	}

	private SurfaceView getRemoteView() {
		return mLocalViewInSmallSurfaceView ? mLargeSurfaceView
				: mSmallSurfaceView;
	}

	@SuppressWarnings("unused")
	private void updateSurfaceView(int orientation) {
		if (mSmallSurfaceView == null) {
			return;
		}
		int screenWidth = mViewMain.getWidth();
		int screenHeight = mViewMain.getHeight();

		if (screenWidth == mSmallSurfaceView.getWidth()
				&& screenHeight == mSmallSurfaceView.getHeight()) {
			return;
		}
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mSmallSurfaceView
				.getLayoutParams();
		switch (orientation * 90) {
			case 0:
			case 180:
				lp.leftMargin = 10;
				lp.topMargin = 10;
				lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, -1);
				lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
				break;
			case 90:
				lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, -1);
				lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
				lp.leftMargin = 60;
				lp.topMargin = 60;
				break;
			case 270:
				lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, -1);
				lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
				lp.rightMargin = 60;
				lp.topMargin = 60;
				break;
			default:
				break;
		}
		mSmallSurfaceView.requestLayout();
	}

	private void updateSurfaceView() {
		if (!mLocalViewShrinked) {
			mImgBg.setVisibility(View.VISIBLE);
		}
		if (getRemoteView() == null || getLocalView() == null) {
			return;
		}
		if (mIsCameraOff) {
			if (mVideoReceiveStatus == MtcCallConstants.EN_MTC_CALL_TRANSMISSION_CAMOFF) {
				getRemoteView().setVisibility(View.INVISIBLE);
			} else {
				if (!mLocalViewInSmallSurfaceView) {
					switchSurfaceView();
				}
				getRemoteView().setVisibility(View.VISIBLE);
			}
			getLocalView().setVisibility(View.INVISIBLE);
		} else {
			if (mVideoReceiveStatus != MtcCallConstants.EN_MTC_CALL_TRANSMISSION_CAMOFF) {
				if (!mLocalViewInSmallSurfaceView) {
					switchSurfaceView();
				}
				getRemoteView().setVisibility(View.VISIBLE);
				mImgBg.setVisibility(View.GONE);
			} else {
				if (mLocalViewInSmallSurfaceView) {
					switchSurfaceView();
				}
				getRemoteView().setVisibility(View.INVISIBLE);
			}
			getLocalView().setVisibility(View.VISIBLE);
		}
	}

	private void updateErrorTextView(int orientation) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mTxtErrorContainer
				.getLayoutParams();
		switch (orientation * 90) {
			case 0:
			case 180:
				lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, -1);
				lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, -1);
				lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, -1);
				lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
				lp.height = LayoutParams.WRAP_CONTENT;
				lp.width = LayoutParams.MATCH_PARENT;
				break;
			case 90:
				lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, -1);
				lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, -1);
				lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
				lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
				lp.height = LayoutParams.MATCH_PARENT;
				lp.width = LayoutParams.WRAP_CONTENT;
				break;
			case 270:
				lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, -1);
				lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
				lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, -1);
				lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, -1);
				lp.height = LayoutParams.MATCH_PARENT;
				lp.width = LayoutParams.WRAP_CONTENT;
				break;
		}
		mTxtErrorContainer.setOrientation(orientation * 90, false);
	}

	private void updateButtons(int orientation) {
		Rotatable[] indicators = { mViewEnd, mBtnMute, mBtnAudio, mBtnSwitch,
				mBtnCameraOff, mBtnRedial, mBtnCancel, mBtnCameraOn };

		for (Rotatable indicator : indicators) {
			if (indicator != null) {
				indicator.setOrientation(orientation * 90, true);
			}
		}

		boolean visible = (orientation & 1) == 0;
		for (TextView tv : mBtnTxtList) {
			tv.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
		}
	}

	private void setViewEnabled(View view, boolean enabled) {
		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				setViewEnabled(viewGroup.getChildAt(i), enabled);
			}
		} else {
			view.setEnabled(enabled);
		}
	}

	private int getCallDuration() {
		if (mBaseTime <= 0) {
			return 0;
		}
		return (int) (SystemClock.elapsedRealtime() - mBaseTime);
	}

	private boolean isAudioNetStateOk() {
		int audioNetState = MtcCall.Mtc_CallGetAudioNetSta(mSessId);
		if (audioNetState >= MtcCallConstants.EN_MTC_NET_STATUS_DISCONNECTED) {
			return true;
		}
		return false;
	}

	public static boolean isOnTop() {
		ActivityManager am = (ActivityManager) MtcCallDelegate.sContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (topActivity.getClassName().equals(CallActivity.class.getName())) {
				return true;
			}
		}
		return false;
	}

	public void showAlertPermissionDialog() {
		if (mAlertPermissionDialog != null
				&& mAlertPermissionDialog.isShowing()) {
			return;
		}
		MiuiUtils.setAlertPermissionDialogShowed(this);
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(MtcResource.getIdByName("string",
				"Enable_display_pop_up_window"));
		b.setMessage(getString(MtcResource.getIdByName("string",
				"Enable_display_pop_up_window_description_format"), "JusCloud"));
		b.setPositiveButton(MtcResource.getIdByName("string", "Security"),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MiuiUtils.openMiuiPermissionActivity(CallActivity.this);
					}
				});
		b.setCancelable(true);
		mAlertPermissionDialog = b.create();
		mAlertPermissionDialog.setCanceledOnTouchOutside(false);
		mAlertPermissionDialog.show();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void hideSystemUI() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
							| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
							| View.SYSTEM_UI_FLAG_IMMERSIVE);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void showSystemUI() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}
	}

	private void setupOperationPadding() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			SystemBarTintManager manager = new SystemBarTintManager(this);
			int navigationBarHeight = manager.getConfig()
					.getNavigationBarHeight();
			findViewById(MtcResource.getIdByName("id", "call_operation"))
					.setPadding(0, 0, 0, navigationBarHeight);
		}
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void setupStatusBarColor() {
		Window window = this.getWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			WindowManager.LayoutParams winParams = window.getAttributes();
			final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			winParams.flags |= bits;
			window.setAttributes(winParams);
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintColor(Color.TRANSPARENT);
		}
	}

	private void setActivityFullScreen(boolean fullScreen) {
		Window window = this.getWindow();
		WindowManager.LayoutParams attrs = window.getAttributes();
		if (fullScreen) {
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			window.setAttributes(attrs);
		} else {
			attrs.flags &= ~(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			window.setAttributes(attrs);
		}
	}

	private int getNet() {
		int net = MtcCliConstants.MTC_ANET_UNAVAILABLE;
		ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnected()) {
			net = (ni.getType() << 8);
		}
		return net;
	}

	private void checkToUpload() {
		boolean enable = MtcCallDelegate
				.getEnabled(MtcCallDelegate.UPLOAD_LOG_KEY);
		if (!enable)
			return;

		if (mSessState == CALL_STATE_CONNECTING)
			upload("Reason: Connect to term");

		if (mDisconnect)
			upload("Reason: Peer disconnect");

	}

	private void upload(String memo) {
		File fileDir = getExternalCacheDir();
		String dir = null;
		if (fileDir != null) {
			dir = fileDir.getAbsolutePath();
		} else {
			dir = getCacheDir().getAbsolutePath();
		}
		dir += "/mtc/log/";
		fileDir = new File(dir);
		fileDir.mkdirs();

		String date = new SimpleDateFormat("yyyyMMdd_hhmm", Locale.US)
				.format(new Date());
		PackageManager pm = getPackageManager();
		String version = null;
		try {
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			version = Integer.toString(pi.versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (version == null) {
			version = "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(getPackageName());
		builder.append("_");
		builder.append(version);
		builder.append("_");
		builder.append(date);
		builder.append("_a.tgz");
		JSONObject json = new JSONObject();
		try {
			json.put(MtcAcvConstants.MtcParmAcvCommitArchiveName,
					builder.toString());
			json.put(MtcAcvConstants.MtcParmAcvCommitDeviceId,
					MtcCli.Mtc_CliGetDevId());
			json.put(MtcAcvConstants.MtcParmAcvCommitMemo, memo);
			json.put(MtcAcvConstants.MtcParmAcvCommitPaths,
					new JSONArray().put(dir));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		MtcAcv.Mtc_AcvCommitJ(0, json.toString());
	}

	public String getMeta(Context context, String metaName) {
		String meta = null;
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (appInfo != null && appInfo.metaData != null) {
				meta = appInfo.metaData.getString(metaName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			meta = null;
		}
		return meta;
	}

	static class Contact {
		public String mNumber;
		public String mName;
		public String mPeerName;

		Contact(String number, String name, String peerName) {
			mNumber = number;
			mName = name;
			mPeerName = peerName;
		}
	}

}
