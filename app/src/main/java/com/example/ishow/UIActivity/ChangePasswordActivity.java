package com.example.ishow.UIActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ishow.BaseComponent.BaseCompactActivity;
import com.example.ishow.Bean.UserEntry;
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

import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MRME on 2016-04-12.
 */
public class ChangePasswordActivity extends BaseCompactActivity {
    @Bind(R.id.course_title_name)
    TextView courseTitleName;
    @Bind(R.id.oldpassword)
    PasswordEditText oldpassword;
    @Bind(R.id.oldpassInput)
    TextInputLayout oldpassInput;
    @Bind(R.id.password)
    PasswordEditText password;
    @Bind(R.id.passInput)
    TextInputLayout passInput;
    @Bind(R.id.forget_password)
    PasswordEditText Passwordagain;
    @Bind(R.id.passInputagain)
    TextInputLayout passInputagain;
    @Bind(R.id.change_password)
    Button okbutton;
    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        ButterKnife.bind(this);

        bindView2UI();
        new SwitchAnimationUtil().startAnimation(getWindow().getDecorView(), SwitchAnimationUtil.AnimationType.HORIZON_CROSS);
    }

    private void bindView2UI() {

        courseTitleName.setText(getString(R.string.xiugai_mima));
        oldpassword = (PasswordEditText) oldpassInput.getEditText();
        password = (PasswordEditText) passInput.getEditText();
        Passwordagain = (PasswordEditText) passInputagain.getEditText();
        okbutton.setOnClickListener(this);
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(ChangePasswordActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        hideSoftKeyBoard(passInputagain);
        UserEntry studentInfo = SharePrefrence.getInstance().getStudentInfo(getApplicationContext());
        if (studentInfo!=null)
            if (studentInfo.getUserid()=="")
                return;
        if (v.getId() == R.id.change_password) {
            if (TextUtils.equals(oldpassword.getText().toString(), "")) {
                oldpassInput.setError(getString(R.string.error_password));
                ToastUtil.makeSnack(oldpassInput, getString(R.string.error_password),this);
            } else if (TextUtils.equals(password.getText().toString(), "")) {
                passInput.setError(getString(R.string.error_password));
                ToastUtil.makeSnack(passInput, getString(R.string.error_password), this);
            } else if (TextUtils.equals(password.getText().toString(), "") || !TextUtils.equals(password.getText().toString(), Passwordagain.getText().toString())) {
                passInputagain.setError(getString(R.string.error_twice_pass_notequal));
                ToastUtil.makeSnack(passInput, getString(R.string.error_twice_pass_notequal), this);
            } else {
                JSONObject mJSONObject = new JSONObject();
                try {
                    MaterialDialog.getInstance().showDloag(this,getString(R.string.request_server));
                    mJSONObject.put("phone", studentInfo.getMobile());
                    mJSONObject.put("password", oldpassword.getText().toString());
                    mJSONObject.put("newspassword", password.getText().toString());
                    XHttpUtils.getInstace().getValue(iShowConfig.changgePassword, mJSONObject, new XHttpUtils.OnHttpCallBack() {
                        @Override
                        public void onSuccess(String result) {
                            MaterialDialog.getInstance().cancelDialog();
                            try {
                                JSONObject object =new JSONObject(result);
                                if (object.getInt("code")==0){
                                    ToastUtil.makeSnack(passInput, object.getString("msg"), ChangePasswordActivity.this);
                                    oldpassInput.setError(object.getString("msg"));
                                }else if (object.getInt("code")==1)ToastUtil.showToast(ChangePasswordActivity.this,object.getString("msg"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String errorResson) {
                            MaterialDialog.getInstance().cancelDialog();
                            ToastUtil.makeSnack(passInput, errorResson, ChangePasswordActivity.this);
                            showSoftKeyBoard(passInputagain);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
