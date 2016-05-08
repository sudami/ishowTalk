package com.example.ishow.justalk.cloud.juscall;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.media.AudioManager;

public class MtcBluetoothHelper extends MtcBluetooth {

	protected Context mContext;
	
    public interface Callback {
        void mtcBluetoothChanged();
    }

    public ArrayList<String> mNameList = new ArrayList<String>();
    public ArrayList<String> mAddressList = new ArrayList<String>();
    
    private boolean mSpeakerOn = false;

    public MtcBluetoothHelper(Context context) {
        super(context);
        this.mContext = context;
    }

    public void setCallback(Callback callback) {
        mCallback = (callback == null) ? null : new WeakReference<Callback>(callback);
    }

    public Callback getCallback() {
        return (mCallback == null) ? null : mCallback.get();
    }

    public boolean unlink(boolean speakerOn) {
        boolean ret = super.unlink();
        AudioManager audioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(speakerOn);
        mSpeakerOn = speakerOn;
        return ret;
    }

    @Override
    public void onScoAudioDisconnected() {
        AudioManager audioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(mSpeakerOn);
    }

    @Override
    public void onScoAudioConnected() {
        mSpeakerOn = false;
    }

    @Override
    public void onHeadsetDisconnected(final String address) {
        int index = mAddressList.indexOf(address);
        mAddressList.remove(index);
        mNameList.remove(index);
        Callback callback = getCallback();
        if (callback == null) {
            return;
        }
        callback.mtcBluetoothChanged();
    }

    @Override
    public void onHeadsetConnected(final String address, final String name) {
        mAddressList.add(address);
        mNameList.add(name);
        Callback callback = getCallback();
        if (callback == null) {
            return;
        }
        callback.mtcBluetoothChanged();
    }

    public int getCount() {
        return mNameList.size();
    }

    private WeakReference<Callback> mCallback;
}
