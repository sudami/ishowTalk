package com.example.ishow.UIActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.ishow.R;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestBasicVideo extends Activity implements MediaRecorder.OnErrorListener, SurfaceHolder.Callback, Camera.AutoFocusCallback {



    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Button buttonStart;
    private Button buttonStop;
    private MediaRecorder recorder;
    private Camera camera;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.luxiang);

        mSurfaceView = (SurfaceView) findViewById(R.id.videoView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera!=null)
                    camera.autoFocus(null);
            }
        });

        buttonStart=(Button)findViewById(R.id.start);
        buttonStop=(Button)findViewById(R.id.stop);
        File defaultDir = Environment.getExternalStorageDirectory();
        String path = defaultDir.getAbsolutePath()+File.separator;//创建文件夹存放视频

        buttonStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder();
            }
        });

        buttonStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder=null;
            }
        });

       /* Intent intent = new Intent();
        intent.setType("video*//*");//选择视频
        intent.setAction(Intent.ACTION_PICK);//使用Intent.ACTION_GET_CONTENT这个Action //取得相片后返回本画面
        startActivityForResult(intent, 1000);*/
    }



    public void recorder() {

        if (recorder==null){
            recorder = new MediaRecorder();
            camera.unlock();
           // camera.stopPreview();
            recorder.setCamera(camera);
            recorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
           // recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            //设置文件输出格式
           // recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            //设置视频编码方式
        //    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
            recorder.setVideoEncodingBitRate(1024 * 1024);
            //recorder.setVideoFrameRate(20);
            String videoOutPath = Environment.getExternalStorageDirectory() + "/DCIM/" + "111.mp4";
            recorder.setOutputFile(videoOutPath);
            recorder.setVideoSize(320,240);
            recorder.setOnErrorListener(this);
        }
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera==null)camera= Camera.open();
        try {
            camera.setPreviewDisplay(mSurfaceHolder);
            camera.setDisplayOrientation(90);
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            camera.autoFocus(null);
            camera.setParameters(parameters);
            camera.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
    private void setCameraPamaters() {
        final int width = mSurfaceView.getMeasuredWidth();
        final int height = mSurfaceView.getMeasuredHeight();
        float hw= (float)width/(float)height;

        final List<Camera.Size> mSupportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
        for(Camera.Size str: mSupportedPreviewSizes)
        {
            float w = Math.abs((float)str.width/(float)width);
            float h = Math.abs((float)str.height/(float)height);
            float wh2 = w/h;

            LogUtil.e(hw+"--"+wh2+"--");
            if (Math.abs(wh2-hw)<=0.35){
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(str.width, str.height);
                camera.setParameters(parameters);
           }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
         if(camera != null)
        {
            camera.stopPreview();   //停止预览
            camera.release();      //释放资源
            camera = null;
        }
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String videoPath = cursor.getString(1);//获取文件路径
        File file = new File(videoPath);
        LogUtil.e("存在"+file.exists()+videoPath);
    }
}