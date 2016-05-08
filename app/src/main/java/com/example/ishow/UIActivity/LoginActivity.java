package com.example.ishow.UIActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ishow.BaseComponent.BaseCompactActivity;
import com.example.ishow.MainActivity;
import com.example.ishow.R;
import com.example.ishow.UIView.MaterialDialog;
import com.example.ishow.UIView.PasswordEditText;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.SwitchAnimationUtil;
import com.example.ishow.Utils.ToastUtil;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MRME on 2016-04-11.
 */
public class LoginActivity extends BaseCompactActivity implements View.OnClickListener {

    @Bind(R.id.course_title_name_center)
    TextView courseTitleNameCenter;
    @Bind(R.id.toolbar_edit)
    TextView toolbarEdit;
    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;
    @Bind(R.id.phone)
    PasswordEditText phone;
    @Bind(R.id.phoneInput)
    TextInputLayout phoneInput;
    @Bind(R.id.password)
    PasswordEditText password;
    @Bind(R.id.passInput)
    TextInputLayout passwordInput;
    @Bind(R.id.forget_password)
    TextView forgetPassword;
    @Bind(R.id.login)
    Button login;

    // public String moRenUrl="http://7xlm33.com1.z0.glb.clouddn.com/fuduji_morentouxiang.jpg";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        bindView2UI();
        new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(), SwitchAnimationUtil.AnimationType.HORIZON_CROSS);
        if (getIntent().hasExtra("isExitAppByUser")){
            Intent exitApp = new Intent(iShowConfig.ExitAPPBroadCastReciver);
            sendBroadcast(exitApp);
        }
    }

    private void bindView2UI() {
        //setToolbar(Toolbar, false);
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(LoginActivity.this);
                //startActivity(PersonalCenterActivity.class);
            }
        });
        courseTitleNameCenter.setText(getString(R.string.title_denglu));
        toolbarEdit.setText(getString(R.string.title_zhuce));
        phoneInput.setHint("手机号");
        passwordInput.setHint("密码");
        phone = (PasswordEditText) phoneInput.getEditText();
        password = (PasswordEditText) passwordInput.getEditText();
        login.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        toolbarEdit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                if (TextUtils.equals(phone.getText(), "")) {
                    phoneInput.setError(getString(R.string.error_phone));
                    ToastUtil.makeSnack(v, getString(R.string.error_phone), this);
                } else if (TextUtils.equals(password.getText(), "")) {
                    passwordInput.setError(getString(R.string.error_password));
                    ToastUtil.makeSnack(v, getString(R.string.error_password), this);
                } else Login();
                break;
            case R.id.toolbar_edit:
                startActivity(RegisterActivity.class);
                break;
            case R.id.forget_password:
                startActivity(ForgetPasswordActivity.class);
                break;
        }
    }

    private void Login() {
        MaterialDialog.getInstance().showDloag(this, getString(R.string.logining));
        hideSoftKeyBoard(phone);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", phone.getText().toString());
            jsonObject.put("password", password.getText().toString());
            XHttpUtils.getInstace().getValue(iShowConfig.login, jsonObject, new XHttpUtils.OnHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject mJSONObject = new JSONObject(result);
                        String message = mJSONObject.getString("msg");
                        //ToastUtil.showToast(LoginActivity.this, message);
                        if (mJSONObject.getInt("code") == 1){
                            //登陆成功后 学院信息保存
                            ToastUtil.showToast(LoginActivity.this, message);
                            SharePrefrence.getInstance().putStudentInfo(getApplicationContext(), mJSONObject.getJSONObject("data").toString());
                            SharePrefrence.getInstance().putStudentNameAndPassword(getApplicationContext(),phone.getText().toString(),password.getText().toString());
                            startActivity(MainActivity.class);
                           // LogUtil.e("---"+SharePrefrence.getInstance().getStudentInfo(getApplicationContext()));
                        }else if (mJSONObject.getInt("code") == 0){
                            ToastUtil.makeSnack(passwordInput,mJSONObject.getString("msg"),LoginActivity.this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        MaterialDialog.getInstance().cancelDialog();
                        ToastUtil.makeSnack(phone, e.getMessage(), LoginActivity.this);
                    }
                    MaterialDialog.getInstance().cancelDialog();
                }

                @Override
                public void onError(String errorResson) {
                    ToastUtil.makeSnack(phone, errorResson, LoginActivity.this);
                    MaterialDialog.getInstance().cancelDialog();
                    showSoftKeyBoard(phone);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
