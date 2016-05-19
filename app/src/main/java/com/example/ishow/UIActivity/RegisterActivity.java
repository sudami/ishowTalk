package com.example.ishow.UIActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ishow.BaseComponent.BaseCompactActivity;
import com.example.ishow.R;
import com.example.ishow.UIView.MaterialDialog;
import com.example.ishow.UIView.MaterialEditext;
import com.example.ishow.UIView.PasswordEditText;
import com.example.ishow.Utils.SwitchAnimationUtil;
import com.example.ishow.Utils.ToastUtil;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MRME on 2016-04-11.
 */
public class RegisterActivity extends BaseCompactActivity implements View.OnClickListener {
    @Bind(R.id.course_title_name_center)
    TextView courseTitleNameCenter;
    @Bind(R.id.toolbar_edit)
    TextView toolbarEdit;
    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;
    @Bind(R.id.register)
    Button register;
    @Bind(R.id.phone)
    PasswordEditText username;
    @Bind(R.id.phoneInput)
    TextInputLayout phoneInput;
    @Bind(R.id.phone2)
    PasswordEditText phone2;
    @Bind(R.id.phoneInput2)
    TextInputLayout phoneInput2;
    @Bind(R.id.getcodeEditext)
    MaterialEditext getcodeEditext;
    @Bind(R.id.getCodeInput)
    TextInputLayout getCodeInput;
    @Bind(R.id.getcodetext)
    TextView getcodetext;
    @Bind(R.id.password)
    PasswordEditText password;
    @Bind(R.id.passInput)
    TextInputLayout passInput;
    @Bind(R.id.course_title_name)
    TextView courseTitleName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        bindView2UI();
        new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(), SwitchAnimationUtil.AnimationType.HORIZON_CROSS);
    }

    private void bindView2UI() {
        setToolbar(Toolbar, true);
        courseTitleName.setText(getString(R.string.title_zhuce));
        // toolbarEdit.setText(getString(R.string.title_denglu));
        username = (PasswordEditText) phoneInput.getEditText();
        phoneInput.setHint(getString(R.string.nickname));

        phone2 = (PasswordEditText) phoneInput2.getEditText();

        getcodeEditext = (MaterialEditext) getCodeInput.getEditText();

        password = (PasswordEditText) passInput.getEditText();

        register.setOnClickListener(this);
        getcodetext.setOnClickListener(this);
        toolbarEdit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //注册了
            case R.id.register:
                register();
                break;
            //获取验证码
            case R.id.getcodetext:
                getMsgCode(phone2, phoneInput2, getcodetext);
                break;
            //跳转到登陆界面8787
            case R.id.toolbar_edit:
                //取消掉这样的布局
                //startActivity(LoginActivity.class);
                break;
        }
    }

    /**
     * 开始判断登陆条件了
     */
    private void register() {
        if (TextUtils.equals(phone2.getText().toString(), "")) {
            phoneInput.setError(getString(R.string.error_phone));
            ToastUtil.makeSnack(phoneInput, getString(R.string.error_phone), this);
        } else if (TextUtils.equals(getcodeEditext.getText().toString(), "")) {
            getCodeInput.setError(getString(R.string.error_msgcode));
            ToastUtil.makeSnack(phoneInput, getString(R.string.error_msgcode), this);
        } else if (TextUtils.equals(password.getText().toString(), "")) {
            passInput.setError(getString(R.string.error_password));
            ToastUtil.makeSnack(phoneInput, getString(R.string.error_password), this);
        } else if (TextUtils.equals(username.getText().toString(), "")) {
            phoneInput2.setError(getString(R.string.error_nickname));
            ToastUtil.makeSnack(phoneInput, getString(R.string.error_nickname), this);
        } else {
            MaterialDialog.getInstance().showDloag(this, getString(R.string.registering));
            JSONObject mJSONObject = new JSONObject();
            try {
                hideSoftKeyBoard(phoneInput2);
                mJSONObject.put("username", username.getText().toString());
                mJSONObject.put("password", password.getText().toString());
                mJSONObject.put("phone", phone2.getText().toString());
                mJSONObject.put("code", getcodeEditext.getText().toString());
                XHttpUtils.getInstace().getValue(iShowConfig.register, mJSONObject, new XHttpUtils.OnHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        MaterialDialog.getInstance().cancelDialog();
                        JSONObject object = new JSONObject();
                        try {
                            if (object.getInt("code")==1)
                            {
                                startActivity(LoginActivity.class);
                                ToastUtil.showToast(RegisterActivity.this,object.getString("msg"));
                                ActivityCompat.finishAfterTransition(RegisterActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String errorResson) {
                        MaterialDialog.getInstance().cancelDialog();
                        ToastUtil.makeSnack(phoneInput, errorResson, RegisterActivity.this);
                        showSoftKeyBoard(phoneInput2);
                    }
                });
            } catch (JSONException e) {
                MaterialDialog.getInstance().cancelDialog();
                e.printStackTrace();
            }
        }
    }

/*    int senconds = 60;
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

    public void getMsgCode() {
        if (TextUtils.equals(phone2.getText().toString(), null) || phone2.getText().length() < 11) {
            phoneInput2.setError(getString(R.string.error_phone));
            return;
        } else {

            JSONObject mJSONObject = new JSONObject();
            try {
                MaterialDialog.getInstance().showDloag(RegisterActivity.this, getString(R.string.request_server));
                mJSONObject.put("phone", phone2.getText().toString());
                XHttpUtils.getInstace().getValue(iShowConfig.getmsgcode, mJSONObject, new XHttpUtils.OnHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        MaterialDialog.getInstance().cancelDialog();
                        handler.postDelayed(runnable, 1000);
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.getInt("status") == 1010)
                                phoneInput2.setError(getString(R.string.error_user_exit));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String errorResson) {
                        MaterialDialog.getInstance().cancelDialog();
                        ToastUtil.showToast(RegisterActivity.this, errorResson);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/

    @Override
    protected void onDestroy() {
        //handler.removeCallbacks(runnable);
        super.onDestroy();
    }
}
