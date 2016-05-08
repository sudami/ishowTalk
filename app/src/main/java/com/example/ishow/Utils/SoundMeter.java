package com.example.ishow.Utils;

import java.io.File;
import java.io.IOException;

import android.bluetooth.le.ScanRecord;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;

import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Utils.Interface.RequestPermissionInterface;

import org.xutils.common.util.LogUtil;

public class SoundMeter implements MediaRecorder.OnErrorListener {
    static final private double EMA_FILTER = 0.6;
    private MediaRecorder mRecorder = null;
    private double mEMA = 0.0;

    public boolean start( String name) {
        if (mRecorder==null){
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);//设置音频源
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);//设置输出格式
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//设置音频编辑
            mRecorder.setOnErrorListener(this);
            mRecorder.setOutputFile(StorageUtils.getInstance().getPathNameDirs("recoder") + "/" + name);
        }
        try {
            mRecorder.prepare();
            mRecorder.start();
            mEMA = 0.0;
            return true;
        } catch (IllegalStateException e) {
            LogUtil.e(e.getMessage());
            return false;
            //  resetRecorder();
        } catch (IOException e) {
            //  resetRecorder();
            //LogUtil.e(e.getMessage());;
            return false;
        }
    }
    public void stop() {
        if (mRecorder != null) {
            mRecorder.setOnErrorListener(null);
            mRecorder.setOnInfoListener(null);
            //mRecorder.setPreviewDisplay(null);
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void pause() {
        if (mRecorder != null) {
            mRecorder.stop();
        }
    }

    public void start() {
        if (mRecorder != null) {
            mRecorder.start();
        }
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude() / 2700.0);
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {

    }
}
