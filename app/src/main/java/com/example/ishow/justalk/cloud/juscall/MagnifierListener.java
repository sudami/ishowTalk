package com.example.ishow.justalk.cloud.juscall;

import java.lang.ref.WeakReference;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcCall;
import com.justalk.cloud.lemon.MtcCallConstants;
import com.justalk.cloud.lemon.MtcConstants;
import com.justalk.cloud.zmf.ZmfVideo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.GestureDetector.OnGestureListener;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

public class MagnifierListener implements OnTouchListener, OnGestureListener,
		OnLongClickListener {

	public interface Callback {
		void tapGesture();
	}

	public class MODE {
		public final static int NONE = 1;
		public final static int DRAG = 2;
		public final static int ZOOM = 3;
	}

	private int mSessId;
	private String mRenderId;
	private int mode;
	private float start_x;
	private float start_y;
	private float beforeLenght;
	private boolean sRtpReply = false;
	private SurfaceView mMagnView;

	private LocalBroadcastManager broadcastManager;
	private BroadcastReceiver mMangifiedImageReceiver;

	private WeakReference<Callback> mCallback;
	private long startTimeStamp = 0;
	private GestureDetector detector = new GestureDetector(this);

	public MagnifierListener(Context context, int sessId, SurfaceView view) {
		// TODO Auto-generated constructor stub
		this.mSessId = sessId;
		mMagnView = view;
		mRenderId = MtcCall.Mtc_CallGetName(mSessId);
		broadcastManager = LocalBroadcastManager.getInstance(context);
		mMangifiedImageReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int timeStamp = MtcConstants.INVALIDID;
				try {
					String info = intent.getStringExtra(MtcApi.EXTRA_INFO);
					JSONObject json = (JSONObject) new JSONTokener(info)
							.nextValue();
					timeStamp = json
							.getInt(MtcCallConstants.MtcCallImageTimeStampKey);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}

				receivedMangifiedImage(timeStamp);
			}
		};
		broadcastManager.registerReceiver(mMangifiedImageReceiver,
						new IntentFilter(MtcCallConstants.MtcCallReceivedMangifiedImageNotification));
	}

	public void unregisterReceiver() {
		broadcastManager.unregisterReceiver(mMangifiedImageReceiver);
		mMangifiedImageReceiver = null;
	}
	
	public void setCallback(Callback callback) {
		mCallback = (callback == null) ? null : new WeakReference<Callback>(
				callback);
	}

	public Callback getCallback() {
		return (mCallback == null) ? null : mCallback.get();
	}

	private void receivedMangifiedImage(int timeStamp) {
		if (sRtpReply == false) {
			ZmfVideo.renderMatch(mMagnView, mRenderId,
					ZmfVideo.RENDER_MATCH_TIMESTAMP,
					String.format("{\"timeStamp\":%d}", timeStamp), null);
			sRtpReply = true;
		}
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		onTouchDown(e);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			startTimeStamp = System.currentTimeMillis();
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			onPointerDown(event);
			break;

		case MotionEvent.ACTION_MOVE:
			onTouchMove(event);
			break;
		case MotionEvent.ACTION_UP:
			long interval = System.currentTimeMillis() - startTimeStamp;
			if (interval < 200) {
				Callback callback = getCallback();
				if (callback != null) {
					callback.tapGesture();
				}
			}
			if (mode == MODE.DRAG)
				ZmfVideo.renderEffect(mMagnView, mRenderId,
						ZmfVideo.RENDER_EFFECT_NONE, null, null);
			mode = MODE.NONE;
			break;

		case MotionEvent.ACTION_POINTER_UP:
			if (mode == MODE.ZOOM && sRtpReply == true) {
				float afterLenght = getDistance(event);
				float scale_temp = afterLenght / beforeLenght;
				float centerX = (event.getX(1) + event.getX(0)) / 2.0f
						/ mMagnView.getWidth();
				float centerY = (event.getY(1) + event.getY(0)) / 2.0f
						/ mMagnView.getHeight();

				float location[] = new float[5];
				location[0] = start_x;
				location[1] = start_y;
				location[2] = centerX - start_x;
				location[3] = centerY - start_y;
				location[4] = scale_temp;
				ZmfVideo.renderGetLocation(mMagnView, mRenderId, location);
				centerX = location[0];
				centerY = location[1];
				float offsetX = location[2];
				float offsetY = location[3];
				MtcCall.Mtc_CallMangify(mSessId, centerX, centerY, scale_temp,
						offsetX, offsetY);
			}
			sRtpReply = false;
			mode = MODE.NONE;
			break;
		}
		detector.onTouchEvent(event);
		return false;
	}

	private void onTouchDown(MotionEvent event) {
		mode = MODE.DRAG;
		float centerX = event.getX() / mMagnView.getWidth();
		float centerY = event.getY() / mMagnView.getHeight();
		float location[] = new float[5];
		location[0] = start_x;
		location[1] = start_y;
		location[2] = 0.0f;
		location[3] = 0.0f;
		location[4] = 2.0f;
		ZmfVideo.renderGetLocation(mMagnView, mRenderId, location);
		centerX = location[0];
		centerY = location[1];
		ZmfVideo.renderEffect(
				mMagnView,
				mRenderId,
				ZmfVideo.RENDER_EFFECT_MAGNIFIER,
				String.format(
						"{\"x\":%f,\"y\":%f,\"dx\":%f,\"dy\":%f,\"zoom\":%f,\"radius\":%f}",
						centerX, centerY, 0.0, 0.0, 2.0, 0.3), null);
	}

	private void onPointerDown(MotionEvent event) {
		if (event.getPointerCount() == 2) {
			mode = MODE.ZOOM;
			start_x = (event.getX(1) + event.getX(0)) / 2.0f
					/ mMagnView.getWidth();
			start_y = (event.getY(1) + event.getY(0)) / 2.0f
					/ mMagnView.getHeight();
			beforeLenght = getDistance(event);
		}
	}

	private int getDistance(MotionEvent event) {
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	private void onTouchMove(MotionEvent event) {
		if (mode == MODE.DRAG) {
			float centerX = event.getX() / mMagnView.getWidth();
			float centerY = event.getY() / mMagnView.getHeight();
			float location[] = new float[5];
			location[0] = centerX;
			location[1] = centerY;
			location[2] = 0.0f;
			location[3] = 0.0f;
			location[4] = 2.0f;
			ZmfVideo.renderGetLocation(mMagnView, mRenderId, location);
			centerX = location[0];
			centerY = location[1];
			ZmfVideo.renderEffect(
					mMagnView,
					mRenderId,
					ZmfVideo.RENDER_EFFECT_MAGNIFIER,
					String.format(
							"{\"x\":%f,\"y\":%f,\"dx\":%f,\"dy\":%f,\"zoom\":%f,\"radius\":%f}",
							centerX, centerY, 0.0, 0.0, 2.0, 0.3), null);
		} else if (mode == MODE.ZOOM && sRtpReply == true) {
			if (event.getPointerCount() != 2)
				return;
			float afterLenght = getDistance(event);
			float scale_temp = afterLenght / beforeLenght;
			float centerX = (event.getX(1) + event.getX(0)) / 2.0f
					/ mMagnView.getWidth();
			float centerY = (event.getY(1) + event.getY(0)) / 2.0f
					/ mMagnView.getHeight();
			float location[] = new float[5];
			location[0] = start_x;
			location[1] = start_y;
			location[2] = centerX - start_x;
			location[3] = centerY - start_y;
			location[4] = scale_temp;
			ZmfVideo.renderGetLocation(mMagnView, mRenderId, location);
			centerX = location[0];
			centerY = location[1];
			float offsetX = location[2];
			float offsetY = location[3];
			ZmfVideo.renderEffect(
					mMagnView,
					mRenderId,
					ZmfVideo.RENDER_EFFECT_MAGNIFIER,
					String.format(
							"{\"x\":%f,\"y\":%f,\"dx\":%f,\"dy\":%f,\"zoom\":%f,\"radius\":%f}",
							centerX, centerY, offsetX, offsetY, scale_temp, 2.0), null);
		}
	}
}
