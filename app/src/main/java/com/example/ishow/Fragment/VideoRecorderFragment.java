package com.example.ishow.Fragment;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.R;
import com.example.ishow.Utils.TimeUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MRME on 2016-05-09.
 */
public class VideoRecorderFragment extends BaseFragment implements SurfaceHolder.Callback, MediaRecorder.OnErrorListener {
    @Bind(R.id.fragment_media_top_time)
    TextView fragmentMediaTopTime;
    @Bind(R.id.fragment_media_SurfaceView)
    SurfaceView fragmentMediaSurfaceView;
    @Bind(R.id.fragment_media_delete)
    ImageView fragmentMediaDelete;
    @Bind(R.id.fragment_media_recorder)
    ImageView fragmentMediaRecorder;
    @Bind(R.id.fragment_media_save)
    ImageView fragmentMediaSave;
    @Bind(R.id.fragment_media_ProgressBar)
    ProgressBar fragmentMediaProgressBar;

    private SurfaceHolder mSurfaceHolder;
    private Camera camera;
    private MediaRecorder recorder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = getView(R.layout.fragment_mediarecorder);
        ButterKnife.bind(this, rootView);

        setData2UI();
        return rootView;

    }

    private void setData2UI() {
        //***********************************设置surfaceview的  一些属性 并且设置点击可以自动对焦*********************//
        mSurfaceHolder = fragmentMediaSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        fragmentMediaSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera != null)
                    camera.autoFocus(null);
            }
        });
        //***************************************OVER**********************************************************//

    }


    boolean record = true;

    @OnClick({R.id.fragment_media_top_close, R.id.fragment_media_top_switch, R.id.fragment_media_delete, R.id.fragment_media_recorder, R.id.fragment_media_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_media_top_close:
                ActivityCompat.finishAffinity(((AppBaseCompatActivity) context));
                break;
            case R.id.fragment_media_top_switch:
                switchCamera();
                break;
            case R.id.fragment_media_delete:
                resetFragmentMediaTop();
                break;
            case R.id.fragment_media_recorder:
                if (record) {
                    startRecord();
                    record = !record;
                } else {
                    stopRecord();
                    record = !record;
                }
                break;
            case R.id.fragment_media_save:
                resetFragmentMediaTop();
                break;
        }
    }

    private void resetFragmentMediaTop() {
        fragmentMediaProgressBar.setProgress(2);
        fragmentMediaTopTime.setText(TimeUtil.getMinandSeconds(0));
    }

    private void stopRecord() {
        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;
        handler.removeCallbacks(runnable);
        fragmentMediaDelete.setEnabled(true);
        fragmentMediaDelete.setAlpha(1.0f);
        fragmentMediaSave.setEnabled(true);
        fragmentMediaSave.setAlpha(1.0f);
        fragmentMediaRecorder.setImageResource(R.drawable.icon_kaishiluzhi);

    }

    long recordSystemTime = 0;

    private void startRecord() {
        if (recorder == null) {
            recorder = new MediaRecorder();
            recorder.reset();
            if (camera != null) {
                camera.unlock();
                recorder.setCamera(camera);
            }
            recorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
            recorder.setVideoEncodingBitRate(1024 * 1024*10);
            //recorder.setVideoFrameRate(20);
            String videoOutPath = Environment.getExternalStorageDirectory() + "/DCIM/" + "111.mp4";
            recorder.setOutputFile(videoOutPath);
            recorder.setVideoSize(320, 240);
            recorder.setOnErrorListener(this);
        }
        try {
            recorder.prepare();
            recorder.start();
            fragmentMediaRecorder.setImageResource(R.drawable.icon_zantingluzhi);
            recordSystemTime = System.currentTimeMillis();
            handler.postDelayed(runnable, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    long maxTime  = 5*60*1000;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            long time = System.currentTimeMillis() - recordSystemTime;
            fragmentMediaTopTime.setText(TimeUtil.getMinandSeconds(time));
            fragmentMediaProgressBar.setProgress((int) (time/maxTime));
            handler.postDelayed(runnable, 1000);
            if (time>=2000)
            {
                fragmentMediaDelete.setVisibility(View.VISIBLE);
                fragmentMediaSave.setVisibility(View.VISIBLE);
                fragmentMediaDelete.setEnabled(false);
                fragmentMediaDelete.setAlpha(0.5f);
                fragmentMediaSave.setEnabled(false);
                fragmentMediaSave.setAlpha(0.5f);
            }
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }
    };
    /**
     * 切换前后摄像头
     */
    int cameraFace = Camera.CameraInfo.CAMERA_FACING_BACK;

    private void switchCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras <= 1) return;
        Camera.CameraInfo cameraInfo = null;
        for (int i = 0; i < numberOfCameras; i++) {
            if (cameraFace == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    cameraFace = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    resetCamera();
                    startPerview(mSurfaceHolder);
                }
            } else if (cameraFace == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    cameraFace = Camera.CameraInfo.CAMERA_FACING_BACK;
                    resetCamera();
                    startPerview(mSurfaceHolder);
                }
            }
        }

    }

    private void resetCamera() {
        camera.stopPreview();//停掉原来摄像头的预览
        camera.release();//释放资源
        camera = null;//取消原来摄像头
        //  camera = Camera.open(i);//打开当前选中的摄像头
    }

    private void startPerview(SurfaceHolder holder) {
        if (camera == null)
            camera = Camera.open(cameraFace);
        try {
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            camera.autoFocus(null);
            camera.setParameters(parameters);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPerview(holder);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopRealseCamera();
    }

    private void stopRealseCamera() {
        if (camera != null) {
            camera.stopPreview();   //停止预览
            camera.release();      //释放资源
            camera = null;
            mSurfaceHolder.removeCallback(this);
            mSurfaceHolder = null;
            fragmentMediaSurfaceView = null;
        }
    }

    @Override
    public void onDestroyView() {
        stopRealseCamera();
        handler.removeCallbacks(runnable);
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {

    }

    @Override
    public void onResume() {
        MobclickAgent.onPageStart("录视频_resume");
        super.onResume();
    }

    @Override
    public void onPause() {
        MobclickAgent.onPageStart("录视频_onPause");
        super.onPause();
    }
}
