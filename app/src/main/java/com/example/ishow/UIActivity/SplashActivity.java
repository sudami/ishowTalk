package com.example.ishow.UIActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.ishow.Bean.UserEntry;
import com.example.ishow.MainActivity;
import com.example.ishow.R;
import com.example.ishow.Utils.NetworkConnection;
import com.example.ishow.Utils.PixlesUtils;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.StorageUtils;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;

/**
 * Created by MRME on 2016-04-27.
 */
public class SplashActivity extends AppCompatActivity{

    private Callback.Cancelable cancelable;
    ImageView imageView=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(imageView);
        imageView.setImageResource(R.mipmap.splash);
       /* Window window = this.getWindow();
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setAttributes(attrs);*/

        LogUtil.e(imageView.getWidth()+"--"+imageView.getHeight());
        if (NetworkConnection.isNetworkConnected(this)){
            JSONObject object = new JSONObject();
            try {
                object.put("type","1");
                 cancelable = XHttpUtils.getInstace().getValue(iShowConfig.getFlashImage, object, new XHttpUtils.OnHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        setDataWithResult(result);
                    }

                    @Override
                    public void onError(String errorResson) {
                        handler.sendEmptyMessageDelayed(0,2000);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else handler.sendEmptyMessageDelayed(0,2000);


    }

    private void setDataWithResult(String result) {
        try {
            JSONObject object = new JSONObject(result);
            if (object.has("code"))
                if (object.getInt("code")==0)
                    handler.sendEmptyMessageDelayed(0,2000);
            if (object.has("msg")){
                JSONArray array =object.getJSONArray("msg");
                String url = array.getJSONObject(0).getString("img");
                final String urlName = url.substring(url.lastIndexOf("/")+1);
                String splashFilePath = StorageUtils.getInstance().getSplash(urlName);
                if (splashFilePath!=null){
                    ImageLoader.getInstance().displayImage("file://"+splashFilePath,imageView);
                    handler.sendEmptyMessageDelayed(0,2000);
                }else{
                    ImageLoader.getInstance().displayImage(url,imageView,new SimpleImageLoadingListener(){
                        @Override
                        public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    StorageUtils.getInstance().saveBitmapCheckStroagePermision("splash",urlName,getApplicationContext(),loadedImage);
                                    handler.sendEmptyMessageDelayed(0,2000);
                                }
                            }).start();
                            super.onLoadingComplete(imageUri, view, loadedImage);
                        }
                    });
                }
            }
        } catch (JSONException e) {
            handler.sendEmptyMessageDelayed(0,2000);
            e.printStackTrace();
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UserEntry studentInfo = SharePrefrence.getInstance().getStudentInfo(getApplicationContext());
            Intent intent=null;
            if (studentInfo==null){
                intent=new Intent(SplashActivity.this,LoginActivity.class);
            }else intent=new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            SplashActivity.this.finish();
           // startActivity(intent);
          //  ActivityCompat.finishAfterTransition();
        }
    };

    @Override
    protected void onDestroy() {
        if (cancelable!=null)
            cancelable.cancel();
        super.onDestroy();
    }
}
