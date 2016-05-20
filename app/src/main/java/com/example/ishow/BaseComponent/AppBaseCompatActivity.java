package com.example.ishow.BaseComponent;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ishow.R;
import com.example.ishow.UIActivity.LocalCourseActivity;
import com.example.ishow.Utils.Interface.RequestPermissionInterface;
import com.example.ishow.Utils.SystemBarTintManager;
import com.example.ishow.iShowConfig.iShowConfig;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;


/**
 * Created by MRME on 2016-03-21.
 */
public class AppBaseCompatActivity extends AppCompatActivity implements View.OnClickListener {

    public RequestPermissionInterface permissionInterface;
   // @Bind(R.id.course_title_name)

    //@Bind(R.id.Toolbar)
    public android.support.v7.widget.Toolbar Toolbar;
  //  @Bind(R.id.base_loading)
    private LinearLayout baseLoading;

   // @Bind(R.id.base_error)
    private LinearLayout baseError;
  //  @Bind(R.id.container)
    public FrameLayout container;

    public LayoutInflater inflater;
    private SystemBarTintManager mTintManager;
    private TextView tverror;
    public TextView courseTitleName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //setStatusBar(0);
        super.onCreate(savedInstanceState);
    }

    public void setAppBaseCompactActivity(View contentView) {
        //setContentView(R.layout.framlayout_container);
        courseTitleName = (TextView)contentView. findViewById(R.id.course_title_name);
        Toolbar = (android.support.v7.widget.Toolbar)contentView. findViewById(R.id.Toolbar);
        baseLoading = (LinearLayout) contentView.findViewById(R.id.base_loading);
        container = (FrameLayout) contentView.findViewById(R.id.container);
        baseError= (LinearLayout) contentView.findViewById(R.id.base_error);
        tverror = (TextView) contentView.findViewById(R.id.base_error_text);

       // ButterKnife.bind(this);

       // setToolbar(true, "iShow");
        // StatusBarUtils.setColor(this,R.color.colorAccent);
        //new SystemBarTintManager(this);

    }

    public void setStatusBar(int colorId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT&&Build.VERSION.SDK_INT <23) {
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (false) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
            mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setNavigationBarTintEnabled(true);
            if (colorId==0 )
              mTintManager.setTintColor(getResources().getColor(R.color.colorPrimary));
            else if (colorId==1)  mTintManager.setTintColor(getResources().getColor(R.color.rank_header_content_color));

        }
    }

    /**
     * 设置actionbar
     */
    public void setToolbar(boolean enable, String title) {
        courseTitleName.setText(title);
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(AppBaseCompatActivity.this);
            }
        });
    }

    public View getView(int layoutId) {
        inflater = LayoutInflater.from(this);
        return inflater.inflate(layoutId, null);
    }

    public void addContentView(View contentView) {
        container.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
    }

    public void showTipContent(String reason) {
        baseLoading.setVisibility(View.GONE);
        baseError.setVisibility(View.VISIBLE);
        tverror.setText(reason);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        ActivityCompat.finishAfterTransition(this);

    }

    @Override
    public void onClick(View v) {
       // startActivity(new Intent(this, LocalCourseActivity.class));
        // ActivityCompat.finishAfterTransition(this);
    }

    /**
     * 判断当前系统版本 是否在API23以上
     *
     * @return
     */
    public boolean isAndroidLoppin() {
        if (Build.VERSION.SDK_INT >= 23) {
            return true;
        }
        return false;
    }
    public void hideSoftKeyBoard(View login){
        InputMethodManager imm =(InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(login.getWindowToken(), 0);
    }
    public void showSoftKeyBoard(View phone){
        InputMethodManager imm =(InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(phone, 0);
    }
    /**
     * 获取 sd卡 权限
     *
     * @param permission
     */
    public void checkPermissonForStrorage(RequestPermissionInterface permission) {
        if(!isAndroidLoppin()){
            permissionInterface.onPermissionRequestResult(true,false);
            return;
        }
        if (permission != null) {
            permissionInterface = permission;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.STORAGE) != PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission_group.STORAGE},
                    iShowConfig.Manifest_permission_EXTERNAL_STORAGE
            );
        } else {
            permissionInterface.onPermissionRequestResult(true,false);
        }
    }
    public void checkPermissonForRecord(RequestPermissionInterface permission) {
        if(!isAndroidLoppin()){
            permissionInterface.onPermissionRequestResult(true,false);
            return;
        }
        if (permission != null) {
            permissionInterface = permission;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    iShowConfig.Manifest_permission_RECORD_AUDIO
            );
        } else {
            permissionInterface.onPermissionRequestResult(true,false);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void checkPermissonForCamera(RequestPermissionInterface permission){
        if(!isAndroidLoppin()){
            permissionInterface.onPermissionRequestResult(true,false);
            return;
        }
        if (permission != null) {
            permissionInterface = permission;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission_group.CAMERA) != PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission_group.CAMERA,},
                    iShowConfig.Manifest_permission_CAMERA
            );
        } else {
            permissionInterface.onPermissionRequestResult(true,false);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermissionsResult(permissions[0], grantResults[0]);
    }

    private void onRequestPermissionsResult(String permission, int grantResult) {
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            if (permissionInterface != null)
                permissionInterface.onPermissionRequestResult(true,true);
        } else {
            if (permissionInterface != null)
                permissionInterface.onPermissionRequestResult(false,true);
            Toast.makeText(this, permission + "权限被禁用", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onResume() {

        MobclickAgent.onResume(this);
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

