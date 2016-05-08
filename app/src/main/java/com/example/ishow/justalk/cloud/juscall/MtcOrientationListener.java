package com.example.ishow.justalk.cloud.juscall;

import java.lang.ref.WeakReference;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;

public class MtcOrientationListener extends OrientationEventListener implements 
        MtcSettingsSystemObserver.Callback {

    public int mOrientation;
    
    public interface Callback {
        void mtcOrientationChanged(int orientation, int previousOrientation);
    }
    
    public MtcOrientationListener(Context context, Handler handler) {
        super(context);
        mContext = context;
        mAccelerometerRotationObserver = new MtcSettingsSystemObserver(handler);
        mAccelerometerRotationObserver.setCallback(this);
    }

    public void setCallback(Callback callback) {
        mCallback = (callback == null) ? null : new WeakReference<Callback>(callback);
    }
    
    public Callback getCallback() {
        return (mCallback == null) ? null : mCallback.get();
    }
    
    public void enable() {
        mOrientation = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        ContentResolver contentResolver = mContext.getContentResolver();
        try {
            mAccelerometerRotation = Settings.System.getInt(contentResolver,
                    Settings.System.ACCELEROMETER_ROTATION);
        } catch (SettingNotFoundException e) {
            mAccelerometerRotation = 1;
            e.printStackTrace();
        }
        mAccelerometerRotationObserver.register(contentResolver, Settings.System.ACCELEROMETER_ROTATION);
        super.enable();
    }
    
    public void disable() {
        super.disable();
        mAccelerometerRotationObserver.unregister(mContext.getContentResolver());
    }
    
    @Override
    public void onOrientationChanged(int orientation) {
        if (mAccelerometerRotation == 0) return;

        Callback callback = getCallback();
        if (callback == null) return;
        
        int newOrientation = Surface.ROTATION_0;
        if (orientation >= 45 && orientation < 135)
            newOrientation = Surface.ROTATION_90;
        else if (orientation >= 135 && orientation < 225)
            newOrientation = Surface.ROTATION_180;
        else if (orientation >= 225 && orientation < 315)
            newOrientation = Surface.ROTATION_270;
        
        if (mOrientationChanging) {
            if (newOrientation != mOrientationToChange) {
                sHandler.removeMessages(ORIENTATION_CHANGED);
                sHandler.removeMessages(ORIENTATION_CHANG);
                mOrientationToChange = newOrientation;
                Message msg = Message.obtain(sHandler);
                msg.what = ORIENTATION_CHANG;
                msg.obj = this;
                msg.arg1 = newOrientation;
                sHandler.sendMessageDelayed(msg, 500);
            }
        } else {
            setOrientation(newOrientation);
        }
    }

    @Override
    public void mtcSettingsSystemChanged(MtcSettingsSystemObserver observer) {
        try {
            mAccelerometerRotation = Settings.System.getInt(
                    mContext.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);
        } catch (SettingNotFoundException e) {
            mAccelerometerRotation = 1;
            e.printStackTrace();
        }
    }
    
    private void setOrientation(int orientation) {
        if (mOrientation != orientation) {
            int previousOrientation = mOrientation;
            mOrientation = orientation;
            Callback callback = getCallback();
            if (callback != null) {
                callback.mtcOrientationChanged(orientation, previousOrientation);
            }
            mOrientationChanging = true;
            mOrientationToChange = orientation;
            Message msg = Message.obtain(sHandler);
            msg.what = ORIENTATION_CHANGED;
            msg.obj = this;
            sHandler.sendMessageDelayed(msg, 500);
        } else {
            mOrientationChanging = false;
        }
    }
    
    private void orientationChanged() {
        mOrientationChanging = false;
    }
    
    private Context mContext;
    private WeakReference<Callback> mCallback;
    private MtcSettingsSystemObserver mAccelerometerRotationObserver;
    private int mAccelerometerRotation;
    private boolean mOrientationChanging = false;
    private int mOrientationToChange;
    
    private static final int ORIENTATION_CHANG = 1;
    private static final int ORIENTATION_CHANGED = 2;
    
    private static Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ORIENTATION_CHANG: {
                    MtcOrientationListener listener = (MtcOrientationListener)msg.obj;
                    listener.setOrientation(msg.arg1);
                    break;
                }
                case ORIENTATION_CHANGED: {
                    MtcOrientationListener listener = (MtcOrientationListener)msg.obj;
                    listener.orientationChanged();
                    break;
                }
            }
        }
    };
}
