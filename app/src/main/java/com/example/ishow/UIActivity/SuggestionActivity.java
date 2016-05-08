package com.example.ishow.UIActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.BaseComponent.BaseCompactActivity;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.UIView.MaterialDialog;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.ToastUtil;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;

import org.json.JSONObject;

import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MRME on 2016-04-20.
 */
public class SuggestionActivity extends AppBaseCompatActivity {
    @Bind(R.id.course_title_name)
    TextView courseTitleName;
    @Bind(R.id.toolbar_edit)
    TextView toolbarEdit;
    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;
    @Bind(R.id.et_comment)
    EditText etComment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        ButterKnife.bind(this);
        setToolbar();
    }

    public void setToolbar(){
        courseTitleName.setText(getString(R.string.suugesstion));
        toolbarEdit.setText("提交");
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(SuggestionActivity.this);
            }
        });

    }
    MaterialDialog dialog=null;
    @OnClick(R.id.toolbar_edit)
    public void onClick() {
        if (etComment.getText().length()<=15){
            ToastUtil.makeSnack(etComment,"content less than 15 words!",this);
        }else {
            UserEntry studentInfo = SharePrefrence.getInstance().getStudentInfo(getApplicationContext());
            if (studentInfo==null){
                ToastUtil.makeSnack(etComment,"please login first and retry!",this);
                return ;
            }
            JSONObject mJSONObject = new JSONObject();
            try{
                dialog =new MaterialDialog();
                dialog.showDloag(this,getString(R.string.request_server));
                hideSoftKeyBoard(etComment);
                mJSONObject.put("username",studentInfo.getName());
                mJSONObject.put("content", etComment.getText().toString());
                XHttpUtils.getInstace().getValue(iShowConfig.suggestion, mJSONObject, new XHttpUtils.OnHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        dialog.cancelDialog();
                        ToastUtil.makeSnack(toolbarEdit,"提交成功",SuggestionActivity.this);
                        etComment.setText("");
                    }

                    @Override
                    public void onError(String errorResson) {
                        dialog.cancelDialog();
                        ToastUtil.makeSnack(etComment,errorResson,SuggestionActivity.this);
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }
}
