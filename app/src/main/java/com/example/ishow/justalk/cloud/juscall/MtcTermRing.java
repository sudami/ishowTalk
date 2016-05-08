package com.example.ishow.justalk.cloud.juscall;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;

public class MtcTermRing implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    public MtcTermRing() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }
    
    public void release()  {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnCompletionListener(null);
            mMediaPlayer.setOnErrorListener(null);
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mCompletion = null;
        sHandler.removeCallbacks(mRepeat);
        mRepeat = null;
    }
    
    public void start(Context c, int resid)    {
        start(c, resid, 1, 0, null);
    }
    
    public void start(Context c, int resid, int times){
        start(c, resid, times, 0, null);
    }
    
    public void start(Context c, int resid, int times, int interval, Runnable completion) {
        mMediaPlayer.reset();
        setDataSource(c, resid);
        mTimes = times;
        mInterval = interval;
        mCompletion = completion;
        if (mTimes < 0 && mInterval <= 0) {
            mMediaPlayer.setLooping(true);
        }
        try {
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void stop()  {
    	if (mMediaPlayer != null) {
    		mMediaPlayer.stop();
            mMediaPlayer.reset();
    	}
        mCompletion = null;
        if (mRepeat != null) {
        	sHandler.removeCallbacks(mRepeat);
        }
    }
    
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        complete();
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mTimes < 0 || --mTimes > 0) {
            if (mInterval >= 0) {
                if (mRepeat == null) {
                    mRepeat = new Runnable() {
                        @Override
                        public void run() {
                            mMediaPlayer.start();
                        }
                    };
                }
                sHandler.postDelayed(mRepeat, mInterval);
            }
        } else {
            complete();
        }
    }
    
    private static Handler sHandler = new Handler();
    
    private MediaPlayer mMediaPlayer;
    private int mTimes;
    private long mInterval;
    private Runnable mCompletion;
    private Runnable mRepeat;
    
    private void complete() {
        if (mCompletion != null) {
            mCompletion.run();
        }
    }
    
    private void setDataSource(Context c, int resid)   {
        AssetFileDescriptor afd = c.getResources().openRawResourceFd(resid);
        if (afd == null) return;
        try {
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
