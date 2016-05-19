package com.example.ishow.BaseComponent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.ishow.R;
import com.example.ishow.UIActivity.LoginActivity;
import com.example.ishow.UIView.MaterialDialog;
import com.example.ishow.UIView.MaterialEditext;
import com.example.ishow.UIView.PasswordEditText;
import com.example.ishow.Utils.SwitchAnimationUtil;
import com.example.ishow.Utils.SystemBarTintManager;
import com.example.ishow.Utils.ToastUtil;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MRME on 2016-04-11.
 */
public abstract class BaseCompactActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView getcodetext;
    private SystemBarTintManager mTintManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //setStatusBar(0);
        super.onCreate(savedInstanceState);
      //
    }

    public void setToolbar(Toolbar toolbar,boolean enable){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.startActivity(BaseCompactActivity.this,new Intent(BaseCompactActivity.this, LoginActivity.class),null);
                ActivityCompat.finishAfterTransition(BaseCompactActivity.this);
            }
        });

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
    public void hideSoftKeyBoard(View login){
        InputMethodManager imm =(InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(login.getWindowToken(), 0);
    }
    public void showSoftKeyBoard(View phone){
        InputMethodManager imm =(InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(phone, 0);
    }

    public void startActivity(Class targClaz){
      //  ActivityOptionsCompat.makeSceneTransitionAnimation()
       // ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, shareEmlemt, "transitionName");
        ActivityCompat.startActivity(this,new Intent(this,targClaz),null);
        ActivityCompat.finishAfterTransition(this);
       // startActivity();
    }

    @Override
    public void onClick(View v) {

    }



    //*****************************************封装了  获取验证码的 功能*****************************************//
    int senconds = 60;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            senconds--;
            getcodetext.setText(String.format(getString(R.string.seconds_to_retry), senconds));
            if (senconds > 0) {
                getcodetext.setEnabled(false);
                handler.postDelayed(runnable, 1000);
            } else {
                getcodetext.setEnabled(true);
                getcodetext.setText(getString(R.string.getmsgcode));
                handler.removeCallbacks(runnable);
            }
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }
    };
    public void getMsgCode(final PasswordEditText phone2, final TextInputLayout phoneInput2, TextView getcodetext){
        //隐藏掉键盘
        hideSoftKeyBoard(phone2);

        this.getcodetext = getcodetext;
        if (TextUtils.equals(phone2.getText().toString(), null) || phone2.getText().length() < 11) {
            phoneInput2.setError(getString(R.string.error_phone));
            ToastUtil.makeSnack(phone2,getString(R.string.error_phone),BaseCompactActivity.this);
           // ToastUtil.showToast(BaseCompactActivity.this,getString(R.string.error_phone));
            return;
        } else {
            JSONObject mJSONObject = new JSONObject();
            try {
                MaterialDialog.getInstance().showDloag(this, getString(R.string.request_server));
                mJSONObject.put("phone", phone2.getText().toString());
                mJSONObject.put("forpwd", 1);
                XHttpUtils.getInstace().getValue(iShowConfig.getmsgcode, mJSONObject, new XHttpUtils.OnHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        MaterialDialog.getInstance().cancelDialog();
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.getInt("status") == 1010)
                            {
                                phoneInput2.setError(getString(R.string.error_user_exit));
                                ToastUtil.makeSnack(phone2,getString(R.string.error_user_exit),BaseCompactActivity.this);
                                //200验证码 下发成功
                            }else if (object.getInt("status") == 200){
                                handler.postDelayed(runnable, 1000);
                                ToastUtil.makeSnack(phone2,getString(R.string.msg_send_OK),BaseCompactActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String errorResson) {
                        MaterialDialog.getInstance().cancelDialog();
                        ToastUtil.makeSnack(phone2,errorResson,BaseCompactActivity.this);
                       // ToastUtil.showToast(BaseCompactActivity.this, errorResson);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //*****************************************封装了  获取验证码的 功能*****************************************//
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
