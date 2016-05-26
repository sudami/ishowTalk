package com.example.ishow.UIActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.UIView.MaterialDialog;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.StorageUtils;
import com.example.ishow.Utils.UpdaterManager;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.HttpMethod;

import java.io.File;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MRME on 2016-04-20.
 */
public class SettingActivity extends AppBaseCompatActivity {
    @Bind(R.id.course_title_name)
    TextView courseTitleName;
    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;
    @Bind(R.id.aboutUs)
    TextView aboutUs;
    @Bind(R.id.changePw)
    TextView changePw;
    @Bind(R.id.suggestion)
    TextView suggestion;
    @Bind(R.id.clearCache)
    TextView clearCache;
    @Bind(R.id.checkNew)
    TextView checkNew;
    @Bind(R.id.logout)
    Button logout;
    MaterialDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(SettingActivity.this);
            }
        });
        courseTitleName.setText(getString(R.string.shezhi_title));

    }

    @OnClick({R.id.aboutUs, R.id.changePw, R.id.suggestion, R.id.clearCache, R.id.checkNew, R.id.logout})
    public void onClick(View view) {
        ActivityOptionsCompat optionsCompat = null;
        Intent intent = null;
        switch (view.getId()) {
            case R.id.aboutUs:
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, findViewById(R.id.aboutUs), "transitionName");
                intent = new Intent(this, AboutUsActivity.class);
                ActivityCompat.startActivity(this, intent, optionsCompat.toBundle());
                break;
            case R.id.changePw:
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, findViewById(R.id.changePw), "transitionName");
                intent = new Intent(this, ChangePasswordActivity.class);
                ActivityCompat.startActivity(this, intent, optionsCompat.toBundle());
                break;
            case R.id.suggestion:
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, findViewById(R.id.suggestion), "transitionName");
                intent = new Intent(this, SuggestionActivity.class);
                ActivityCompat.startActivity(this, intent, optionsCompat.toBundle());
                break;
            case R.id.clearCache:
                FileDelete();
                break;
            case R.id.checkNew:
                new UpdaterManager(this).checkUpdate(true);
                break;
            case R.id.logout:
                UserEntry studentInfo = SharePrefrence.getInstance().getStudentInfo(getApplicationContext());
                if (studentInfo == null) {
                    optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
                    intent = new Intent(this, LoginActivity.class);
                    ActivityCompat.finishAfterTransition(this);
                    ActivityCompat.startActivity(this, intent, optionsCompat.toBundle());
                } else {
                    JSONObject object = new JSONObject();
                    try {
                        dialog = new MaterialDialog();
                        dialog.showDloag(this, getString(R.string.request_server));
                        object.put("uid", studentInfo.getUserid());
                        object.put("status", "3");
                        XHttpUtils.getInstace().getValue(iShowConfig.exitapp, object, new XHttpUtils.OnHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                dialog.cancelDialog();
                                SharePrefrence.getInstance().putStudentInfo(SettingActivity.this,null);
                                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(SettingActivity.this);
                                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                               // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("isExitAppByUser",true);
                                ActivityCompat.startActivity(SettingActivity.this, intent, activityOptionsCompat.toBundle());
                                ActivityCompat.finishAfterTransition(SettingActivity.this);
                            }

                            @Override
                            public void onError(String errorResson) {
                                dialog.cancelDialog();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    private void FileDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setMessage(getString(R.string.files_delete_yes_no));
        builder.setPositiveButton(getString(R.string.files_delete_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startFileDelete();
            }
        });
        builder.setNegativeButton(getString(R.string.files_delete_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (dialog!=null)
                dialog.cancelDialog();
        }
    };
    private void startFileDelete() {
        dialog = new MaterialDialog();
        dialog.showDloag(this, getString(R.string.files_deleting));
        dialog.setCancelable(false);
        final String rootNameDirs = StorageUtils.getInstance().getRootNameDirs();

        new Thread(new Runnable() {
            @Override
            public void run() {
                File file =new File(rootNameDirs);
                checkFileIsDirectory(file);
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void checkFileIsDirectory(File file) {
        if (file.exists())
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f:files) {
                    checkFileIsDirectory(f);
                }
            }else{
                file.delete();
            }
    }
}
