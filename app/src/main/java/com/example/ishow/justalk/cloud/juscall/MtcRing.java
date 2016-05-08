package com.example.ishow.justalk.cloud.juscall;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.justalk.cloud.lemon.MtcProf;
import com.justalk.cloud.lemon.MtcProfDb;

public class MtcRing {
    
    public MtcRing() {
    }

    public void vibrate(Context c) {
        if (mVibrator == null)
            mVibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
        mVibrator.vibrate(VIBRATE_PATTERN, 0);
    }
    
    public void ring(Context c) {
        AudioManager audioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
        int mode = audioManager.getRingerMode();
        switch (mode) {
            case AudioManager.RINGER_MODE_SILENT:
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                if (mVibrator == null)
                    mVibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
                mVibrator.vibrate(VIBRATE_PATTERN, 0);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                if (mVibrator == null)
                    mVibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrateWhenRinging()) {
                    mVibrator.vibrate(VIBRATE_PATTERN, 0);
                }

                audioManager.requestAudioFocus(null, AudioManager.STREAM_RING, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                audioManager.setMode(AudioManager.MODE_NORMAL);
                if (mMediaPlayer == null) {
                    mMediaPlayer = new MediaPlayer();
                } else {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.stop();
                    }
                    mMediaPlayer.reset();
                }
                mMediaPlayer.setLooping(true);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);

                AssetFileDescriptor afd = null;
                try {
                    String ringtone = getRingtone(c);
                    if (TextUtils.isEmpty(ringtone)) {
                    	ringtone = c.getResources().getString(MtcResource.getIdByName("string", "ringtone_spring_ding_dong"));
                    }
                    if (Uri.parse(ringtone).isRelative()) {
                        String rawFileName = ringtone.substring(0, ringtone.lastIndexOf("."));
                        Resources res = c.getResources();
                        int id = res.getIdentifier(rawFileName, "raw", c.getPackageName());
                        afd = res.openRawResourceFd(id);
                        mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        afd.close();
                        afd = null;
                    } else {
                        mMediaPlayer.setDataSource(c, Uri.parse(ringtone));
                    }
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (afd != null) {
                    try {
                        afd.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void play(Context c, String ringtone, boolean looping, long timeout, int streamType) {
        if (timeout > 0) {
            sHandler.sendMessageDelayed(sHandler.obtainMessage(0, this), timeout);
        }
        
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        } else {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
        }
        mMediaPlayer.setLooping(looping);
        mMediaPlayer.setAudioStreamType(streamType);
        AudioManager audioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(null, streamType, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        AssetFileDescriptor afd = null;
        try {
            Uri uri = Uri.parse(ringtone);
            if (uri.isRelative()) {
                String rawFileName = ringtone.substring(0, ringtone.lastIndexOf("."));
                Resources res = c.getResources();
                int id = res.getIdentifier(rawFileName, "raw", c.getPackageName());
                afd = res.openRawResourceFd(id);
                mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                afd = null;
            } else {
                mMediaPlayer.setDataSource(c, uri);
            }
            
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (afd != null) {
            try {
                afd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void stop() {
        sHandler.removeMessages(0, this);
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mVibrator != null) {
            mVibrator.cancel();
            mVibrator = null;
        }
    }
    
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }
    
    private MediaPlayer mMediaPlayer;
    private Vibrator mVibrator;
    private static final long[] VIBRATE_PATTERN = {1000, 1000};
    
    private static final Handler sHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            MtcRing ring = (MtcRing) message.obj;
            ring.stop();
        }
    };
    
    public static boolean vibrateWhenRinging() {
        String vibrate = MtcProfDb.Mtc_ProfDbGetExtParm(VIBRATE_WHEN_RINGING);
        return TextUtils.isEmpty(vibrate);
    }
    
    public static void setVibrateWhenRinging(boolean vibrate) {
        MtcProfDb.Mtc_ProfDbSetExtParm(VIBRATE_WHEN_RINGING, vibrate ? "" : "0");
        MtcProf.Mtc_ProfSaveProvision();
    }
    
    public static String getRingtone(Context context) {
        String ringtone = MtcProfDb.Mtc_ProfDbGetExtParm(RINGTONE);
        if (TextUtils.isEmpty(ringtone))
            return null;
        if (!ringtoneExists(context, ringtone)) {
            setRingtone("", RINGTONE_TYPE_DEFAULT, "");
            return null;
        }
        return ringtone;
    }
    
    public static int getRingtoneType() {
        String extParm = MtcProfDb.Mtc_ProfDbGetExtParm(RINGTONE_TYPE);
        if (TextUtils.isEmpty(extParm)) {
            return RINGTONE_TYPE_DEFAULT;
        }
        return Integer.valueOf(extParm);
    }
    
    public static void setRingtone(String ringtone, int type, String title) {
        MtcProfDb.Mtc_ProfDbSetExtParm(RINGTONE, ringtone);
        MtcProfDb.Mtc_ProfDbSetExtParm(RINGTONE_TYPE, String.valueOf(type));
        MtcProfDb.Mtc_ProfDbSetExtParm(RINGTONE_TITLE, title);
        MtcProf.Mtc_ProfSaveProvision();
    }
    
    public static void setRingtone(Uri uri, int type, String title) {
        setRingtone(uri.toString(), type, title);
    }
    
    public static String getRingtoneTitle() {
        return MtcProfDb.Mtc_ProfDbGetExtParm(RINGTONE_TITLE);
    }
    
    private static boolean ringtoneExists(Context context, String ringtone) {
        boolean ret = false;
        Uri uri = Uri.parse(ringtone);
        if (uri.isRelative()) {
        	String[] list = context.getResources().getStringArray(MtcResource.getIdByName("array", "ringtone_files"));
            for (String assetFile : list) {
                if (TextUtils.equals(ringtone, assetFile)) {
                    ret = true;
                    break;
                }
            }
        } else if ("file".equals(uri.getScheme())) {
            File file = new File(URI.create(ringtone));
            ret = file.exists();
        } else {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(Uri.parse(ringtone), new String[] { BaseColumns._ID }, null, null, null);
                if (cursor != null) {
                    ret = cursor.getCount() > 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
    
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return ret;
    }
    
    public static final int RINGTONE_TYPE_DEFAULT = 0;
    public static final int RINGTONE_TYPE_SYSTEM = RINGTONE_TYPE_DEFAULT + 1;
    public static final int RINGTONE_TYPE_CUSTOM = RINGTONE_TYPE_SYSTEM + 1;
    
    private static final String RINGTONE = "Ringtone";
    private static final String RINGTONE_TYPE = "RingtoneType";
    private static final String RINGTONE_TITLE = "RingtoneTitle";
    private static final String VIBRATE_WHEN_RINGING = "VibrateWhenRinging";
}
