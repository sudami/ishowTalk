package com.example.ishow.UIActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ishow.BaseComponent.BaseCompactActivity;
import com.example.ishow.R;
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
public class ForgetPasswordActivity extends BaseCompactActivity {
    @Bind(R.id.course_title_name)
    TextView courseTitleName;
    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;
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
    @Bind(R.id.forget_password)
    PasswordEditText passwordagain;
    @Bind(R.id.passInputagain)
    TextInputLayout passInputagain;
    @Bind(R.id.forgetpassword)
    Button ok;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        ButterKnife.bind(this);
       new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(), SwitchAnimationUtil.AnimationType.HORIZON_CROSS);
        bindView2UI();
    }

    private void bindView2UI() {
        courseTitleName.setText(getString(R.string.title_forget_password));
        setToolbar(Toolbar,true);
        phone2 = (PasswordEditText) phoneInput2.getEditText();
        getcodeEditext = (MaterialEditext) getCodeInput.getEditText();
        password = (PasswordEditText) passInput.getEditText();
        passwordagain = (PasswordEditText) passInputagain.getEditText();
        getcodetext.setOnClickListener(this);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.getcodetext:
                getMsgCode(phone2,phoneInput2,getcodetext);
                break;
            case R.id.forgetpassword:
                resetPassword();
                break;
        }
    }

    private void resetPassword() {
        hideSoftKeyBoard(passInput);
        if (TextUtils.equals(phone2.getText(),"")){
            phoneInput2.setError(getString(R.string.error_phone));
            ToastUtil.makeSnack(phoneInput2,getString(R.string.error_phone),this);
        }else if (TextUtils.equals(getcodeEditext.getText(),"")){
            getCodeInput.setError(getString(R.string.error_msgcode));
            ToastUtil.makeSnack(getCodeInput,getString(R.string.error_msgcode),this);
        }else if (TextUtils.equals(password.getText(),"")){
            passInput.setError(getString(R.string.error_password));
            ToastUtil.makeSnack(passInput,getString(R.string.error_password),this);
        }else if (TextUtils.equals(passwordagain.getText(),"")||!TextUtils.equals(passwordagain.getText().toString(),password.getText().toString())){
            passInputagain.setError(getString(R.string.error_twice_pass_notequal));
            ToastUtil.makeSnack(passInputagain,getString(R.string.error_twice_pass_notequal),this);;
        }else{
            JSONObject mJSONObject = new JSONObject();
            try {
                mJSONObject.put("phone", phone2.getText().toString());
                mJSONObject.put("password", password.getText().toString());
                mJSONObject.put("code", getcodeEditext.getText().toString());
                XHttpUtils.getInstace().getValue(iShowConfig.forgetpassword, mJSONObject, new XHttpUtils.OnHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object =new JSONObject(result);
                            //密码充值成功
                            if (object.getInt("code")==1) {
                                ToastUtil.makeSnack(phoneInput2,getString(R.string.mima_reset_ok),ForgetPasswordActivity.this);
                            }
                            //验证码错误
                            else if(object.getInt("code")==0) {
                                ToastUtil.makeSnack(getCodeInput,getString(R.string.error_msgcode),ForgetPasswordActivity.this);
                                getCodeInput.setError(getString(R.string.error_msgcode));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(String errorResson) {
                        ToastUtil.makeSnack(phoneInput2,errorResson,ForgetPasswordActivity.this);
                        showSoftKeyBoard(passInput);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
