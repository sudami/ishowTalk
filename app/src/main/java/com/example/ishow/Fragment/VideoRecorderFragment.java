package com.example.ishow.Fragment;

import android.content.DialogInterface;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
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
import com.example.ishow.UIView.UploadMediaPop;
import com.example.ishow.Utils.TimeUtil;
import com.example.ishow.Utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    private MediaRecorder recorder;//视频录制管理者
    private String videoOutPath;//录制的视频文件 路径

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


    boolean record = false;//是否正在录制
    @OnClick({R.id.fragment_media_top_close, R.id.fragment_media_top_switch, R.id.fragment_media_delete, R.id.fragment_media_recorder, R.id.fragment_media_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_media_top_close:
                ActivityCompat.finishAfterTransition(getActivity());
                break;
            case R.id.fragment_media_top_switch:
                switchCamera();
                break;
            case R.id.fragment_media_delete:
                fragmentMediaDelete();
                resetFragmentMediaTop();
                break;
            case R.id.fragment_media_recorder:
                if (!record) {
                    startRecord();
                    record = !record;
                } else {
                    stopRecord();
                    record = !record;
                }
                break;
            case R.id.fragment_media_save:
                fragmentMediaUpload();
                break;
        }
    }

    /***上传录制的文件*/
    private void fragmentMediaUpload() {
        /**如果正在录制视频 进步提示*/
        if (record) {
            ToastUtil.showToast(getActivity(), getString(R.string.shipinluzhi_stop_recod_first));
            return;
        }

        /**显示上传视频必填的信息对话框*/
      new UploadMediaPop().showMediaPop(getActivity(), String.valueOf(time/1000));
    }


    /***放弃录制的文件*/
    private void fragmentMediaDelete() {
        /**如果正在录制视频 进步提示*/
        if (record) {
            ToastUtil.showToast(getActivity(), getString(R.string.shipinluzhi_stop_recod_first));
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(getString(R.string.shipinluzhi_delete_file));
        dialog.setNegativeButton(getString(R.string.shipinluzhi_delete_Y), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(videoOutPath);
                if (file.exists())
                    file.delete();
                dialog.cancel();
            }
        });
        dialog.setPositiveButton(getString(R.string.shipinluzhi_delete_N), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setCancelable(false);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.create();
        dialog.show();
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
            recorder.setOrientationHint(90);
            recorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
            recorder.setVideoEncodingBitRate(1024 * 1024);
            //recorder.setVideoFrameRate(20);
            videoOutPath = Environment.getExternalStorageDirectory() + "/DCIM/" + "111.mp4";
            recorder.setOutputFile(videoOutPath);
            recorder.setVideoSize(480, 320);
            recorder.setOnErrorListener(this);
        }
        try {
            recorder.prepare();
            recorder.start();
            fragmentMediaRecorder.setImageResource(R.drawable.icon_zantingluzhi);
            recordSystemTime = System.currentTimeMillis();
            setMediaOperation(false);
            handler.postDelayed(runnable, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    long maxTime  = 5*60*1000;
    private long time;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            time = System.currentTimeMillis() - recordSystemTime;
            fragmentMediaTopTime.setText(TimeUtil.getMinandSeconds(time));
            fragmentMediaProgressBar.setProgress((int) ((time*1.0f/maxTime)*100));
            handler.postDelayed(runnable, 1000);
        }
    };

    private void setMediaOperation(boolean b) {
        fragmentMediaDelete.setEnabled(b);
        fragmentMediaDelete.setAlpha(b?1f:0.5f);
        fragmentMediaSave.setEnabled(b);
        fragmentMediaSave.setAlpha(b?1f:0.5f);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }
    };
    /**
     * 切换前后摄像头
     */
    int cameraFace = 0;//  默认后置摄像头
    private void switchCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras <= 1) return;
        if (cameraFace == 0) {
            cameraFace = 1;
            resetCamera();
            startPerview(mSurfaceHolder);
        } else if (cameraFace == 1) {
            cameraFace = 0;
            resetCamera();
            startPerview(mSurfaceHolder);
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
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                //camera.autoFocus(null);
            }
            List<Camera.Size> videoSizes = parameters.getSupportedVideoSizes();
            /**fragmentMediaSurfaceView 宽高 比率*/
            int surWidth = 1;
            int surHeight = 1;
            if (fragmentMediaSurfaceView!=null){
                 surWidth  = fragmentMediaSurfaceView.getMeasuredWidth();
                 surHeight = fragmentMediaSurfaceView.getMeasuredHeight();
            }
            float surRatio=1.0f;
            surRatio = surWidth*1.0f/surHeight;
            /**找出getSupportedVideoSizes中 与surfaceview  宽高比最接近的  不然会拉伸变形*/
            for (Camera.Size size :videoSizes) {
                if (Math.abs(size.width*1.0f/size.height - surRatio)<=0.25) {
                    parameters.setPreviewSize(size.width,size.height);
                    LogUtil.e(size.width+"--"+size.height);
                    break;
                }
            }
          //  camera.autoFocus(null);
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
        stopRealseCamera();
        startPerview(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopRealseCamera();
    }

    private void stopRealseCamera() {
        if (recorder!=null)
        {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder=null;
        }
        if (camera != null) {
            camera.stopPreview();//停止预览
            camera.release();      //释放资源
            camera = null;
        }
    }

    @Override
    public void onDestroyView() {
        stopRealseCamera();
        mSurfaceHolder.removeCallback(this);
        handler.removeCallbacks(runnable);
        mSurfaceHolder = null;
        fragmentMediaSurfaceView =null;
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {

    }

    @Override
    public void onResume() {
        MobclickAgent.onPageStart("录视频_resume");
        if (isPause) startPerview(mSurfaceHolder);
        super.onResume();
    }

    private boolean isPause =false;
    @Override
    public void onPause() {
        stopRealseCamera();
        isPause= !isPause;
        MobclickAgent.onPageStart("录视频_onPause");
        super.onPause();
    }
}
