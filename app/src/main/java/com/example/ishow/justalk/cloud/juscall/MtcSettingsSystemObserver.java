package com.example.ishow.justalk.cloud.juscall;

import java.lang.ref.WeakReference;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

public class MtcSettingsSystemObserver extends ContentObserver {

    public interface Callback {
        void mtcSettingsSystemChanged(MtcSettingsSystemObserver observer);
    }
    
    public String mName;
    
    public MtcSettingsSystemObserver(Handler handler) {
        super(handler);
        // TODO Auto-generated constructor stub
    }

    public void setCallback(Callback callback) {
        mCallback = (callback == null) ? null : new WeakReference<Callback>(callback);
    }
    
    public Callback getCallback() {
        return (mCallback == null) ? null : mCallback.get();
    }
    
    public void register(ContentResolver contentResolver, String name) {
        Uri uri = Settings.System.getUriFor(name);
        contentResolver.registerContentObserver(uri, false, this);
        mName = name;
    }
    
    public void unregister(ContentResolver contentResolver) {
        contentResolver.unregisterContentObserver(this);
    }
    
    public void onChange(boolean selfChange) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.mtcSettingsSystemChanged(this);
        }
    }
    
    private WeakReference<Callback> mCallback;
}
