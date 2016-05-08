package com.example.ishow.UIActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ishow.Application.iShowTalkApplication;
import com.example.ishow.BaseComponent.CallBaseCompactActivity;
import com.example.ishow.Bean.MsgEntry;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.Service.JustalkStateCheckReciver;
import com.example.ishow.UIView.MaterialDialog;
import com.example.ishow.UIView.PullScrollView;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.TimeUtil;
import com.example.ishow.Utils.ToastUtil;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;
import com.example.ishow.justalk.cloud.juscall.MtcCallDelegate;
import com.google.gson.Gson;
import com.justalk.cloud.lemon.MtcBuddy;
import com.justalk.cloud.lemon.MtcConstants;
import com.justalk.cloud.lemon.MtcMdm;
import com.justalk.cloud.lemon.MtcMediaConstants;
import com.justalk.cloud.lemon.MtcUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;
import org.xutils.x;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MRME on 2016-04-18.
 */
public class PersonalCenterActivity extends CallBaseCompactActivity implements JustalkStateCheckReciver.JustalkStateCheckListner, View.OnTouchListener {
    @Bind(R.id.background_img)
    ImageView backgroundImg;
    @Bind(R.id.scroll_view)
    PullScrollView scrollView;
    @Bind(R.id.course_title_name)
    TextView courseTitleName;
    @Bind(R.id.toolbar_edit)
    TextView toolbarEdit;
    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;
    @Bind(R.id.person_avart)
    ImageView personAvart;
    @Bind(R.id.person_name)
    TextView personName;
    @Bind(R.id.person_state)
    ImageView personState;
    @Bind(R.id.person_sign)
    TextView personSign;
    @Bind(R.id.school)
    TextView school;
    @Bind(R.id.real_school)
    TextView realSchool;
    @Bind(R.id.claz)
    TextView claz;
    @Bind(R.id.country_rank)
    TextView countryRank;
    @Bind(R.id.today_rank)
    TextView todayRank;
    @Bind(R.id.week_rank)
    TextView weekRank;
    @Bind(R.id.total_rank)
    TextView totalRank;
    @Bind(R.id.msg)
    ImageView msg;
    @Bind(R.id.call)
    ImageView call;
    private UserEntry selfInfo;
    private UserEntry IntentStudentInfo;
    private String uid;
    private JustalkStateCheckReciver stateCheckReciver;
    private LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        ButterKnife.bind(this);
        IntentStudentInfo = getIntent().getExtras().getParcelable("user");
        uid = IntentStudentInfo.getUserid();
        bindView2UI();
        registerReceiver();
    }

    private void registerReceiver() {
        stateCheckReciver = new JustalkStateCheckReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(100);
        intentFilter.addAction("MtcBuddyQueryLoginInfoOkNotification");
        intentFilter.addAction("MtcBuddyQueryLoginInfoDidFailNotification");

        stateCheckReciver.setOnJustalkStateCheckListner(this);
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(stateCheckReciver, intentFilter);
    }

    private void bindView2UI() {
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        courseTitleName.setText(getString(R.string.personal_center));
        toolbarEdit.setText(getString(R.string.personal_right));
        Toolbar.setBackgroundColor(Color.TRANSPARENT);
        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivityResult();
                //ActivityCompat.finishAfterTransition(PersonalCenterActivity.this);
            }
        });
        scrollView.setHeader(backgroundImg);
        scrollView.setOnTouchListener(this);
        //***************************这里应该 有一部 接收 userentry的 方法 getintent;
        getPersonInfoByUID();
    }

    MaterialDialog dialog;

    private void getPersonInfoByUID() {
        selfInfo = SharePrefrence.getInstance().getStudentInfo(getApplicationContext());
        if (TextUtils.equals(uid, selfInfo.getUserid())) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
            params.bottomMargin = 0;
            scrollView.setLayoutParams(params);
        }
        dialog = new MaterialDialog();
        dialog.showDloag(this, getString(R.string.request_server));
        JSONObject object = new JSONObject();
        try {
            object.put("userid", uid);
            object.put("myid", selfInfo.getUserid()==null?selfInfo.getId():selfInfo.getUserid());
            XHttpUtils.getInstace().getValue(iShowConfig.getCommitePersonInfo, object, new XHttpUtils.OnHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    LogUtil.e("onSuccess" + result);
                    dialog.cancelDialog();
                    if (result != null) {
                        Gson gson = new Gson();
                        IntentStudentInfo = gson.fromJson(result, UserEntry.class);
                        setStudentInfoByResult(IntentStudentInfo);
                    }
                }

                @Override
                public void onError(String errorResson) {
                    LogUtil.e("onError" + errorResson);
                    ToastUtil.makeSnack(msg, errorResson, PersonalCenterActivity.this);
                    dialog.cancelDialog();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void setStudentInfoByResult(UserEntry studentInfo) {
        selfInfo = SharePrefrence.getInstance().getStudentInfo(getApplicationContext());
        boolean equals = TextUtils.equals(uid, selfInfo.getUserid());
        if (selfInfo != null) {
            msg.setVisibility(TextUtils.equals(uid, selfInfo.getUserid()) ? View.GONE : View.VISIBLE);
            call.setVisibility(TextUtils.equals(uid, selfInfo.getUserid()) ? View.GONE : View.VISIBLE);
            personState.setVisibility(TextUtils.equals(uid, selfInfo.getUserid()) ? View.GONE : View.VISIBLE);
            toolbarEdit.setVisibility(TextUtils.equals(uid, selfInfo.getUserid()) ? View.VISIBLE : View.GONE);
            //  personState.setVisibility(TextUtils.equals(uid, selfInfo.getUserid()) ? View.GONE : View.VISIBLE);
            personState.setImageResource(TextUtils.equals(uid, selfInfo.getUserid()) ? R.drawable.iocn_yiguanzhu : R.mipmap.icon_guanzhu);
        }
        if (studentInfo != null) {
            if (TextUtils.equals(iShowConfig.morentouxiang,studentInfo.getImg()))
                personAvart.setImageResource(R.mipmap.ic_launcher_moren);
            else x.image().bind(personAvart, equals ? selfInfo.getImg() : studentInfo.getImg(), iShowTalkApplication.getInstance().getIgetImageOptions(this, 80, 8));
            personName.setText(equals ? selfInfo.getName() : studentInfo.getName());
            personSign.setText(equals ? selfInfo.getSign() : studentInfo.getSign());
            personState.setImageResource(studentInfo.is_focus() ? R.drawable.iocn_yiguanzhu : R.mipmap.icon_guanzhu);
            school.setText(equals ? selfInfo.getCampus() : studentInfo.getCampus());
            realSchool.setText(equals ? selfInfo.getSchool() : studentInfo.getSchool());
            claz.setText(equals ? selfInfo.getGrade() : studentInfo.getGrade());
            if (studentInfo.getTimelong() != null&&studentInfo.getTodaytime() != "")
                totalRank.setText(TimeUtil.getHourAndMinText(Long.parseLong(equals ? selfInfo.getTimelong() : studentInfo.getTimelong())));
            if (studentInfo.getTodaytime() != ""&&studentInfo.getTodaytime() != null)
                todayRank.setText(TimeUtil.getHourAndMinText(Long.parseLong(equals ? selfInfo.getTodaytime() : studentInfo.getTodaytime())));
            if (studentInfo.getWeektime() != ""&&studentInfo.getTodaytime() != null)
                weekRank.setText(TimeUtil.getHourAndMinText(Long.parseLong(equals ? selfInfo.getWeektime() : studentInfo.getWeektime())));
            countryRank.setText(equals ? selfInfo.getPaiming() : studentInfo.getPaiming());
        }
    }


    @OnClick({R.id.toolbar_edit, R.id.person_state, R.id.msg, R.id.call})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_edit:
                ActivityCompat.startActivityForResult(this, new Intent(this, PersonalInfoActivity.class), 100,ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
                break;
            case R.id.person_state:
                add2Fans();
                break;
            case R.id.msg:
                Bundle bundle = new Bundle();
                bundle.putParcelable("userEntry", IntentStudentInfo);
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtras(bundle);
                ActivityCompat.startActivity(this, intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
                break;
            case R.id.call:
                dialog = new MaterialDialog();
                dialog.showDloag(this, getString(R.string.request_server));
                if (MtcBuddy.Mtc_BuddyQueryLoginInfo(0L, MtcUser.Mtc_UserFormUri(MtcUser.EN_MTC_USER_ID_USERNAME, IntentStudentInfo.getMobile()), 3) != MtcConstants.ZOK) {
                    ToastUtil.makeSnack(call, getString(R.string.history_NO_login), this);
                    dialog.cancelDialog();
                }
                break;
        }
    }

    private void add2Fans() {
        if (IntentStudentInfo.is_focus())
            return;
        final JSONObject o1 = new JSONObject();
        try {
            dialog = new MaterialDialog();
            dialog.showDloag(this, getString(R.string.request_server));
            o1.put("masterID", uid);
            o1.put("is_del", "n");
            o1.put("fanID", selfInfo.getUserid()==null?selfInfo.getId(): selfInfo.getId());
            XHttpUtils.getInstace().getValue(iShowConfig.Follow, o1, new XHttpUtils.OnHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    LogUtil.e(result);
                    dialog.cancelDialog();
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getInt("code")==1){

                            personState.setImageResource(R.drawable.iocn_yiguanzhu);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(String errorResson) {
                    dialog.cancelDialog();
                    ToastUtil.makeSnack(personState, errorResson, PersonalCenterActivity.this);
                }
            });
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onCheckJustalkStateNotification(boolean state) {
        dialog.cancelDialog();
        if (state) {
            startCallactivity(IntentStudentInfo);
        } else ToastUtil.makeSnack(call, getString(R.string.history_NO_login_op), this);
    }

    @Override
    public void onTextSendFaild(Intent In) {

    }

    @Override
    public void onTextSendOK(Intent In) {

    }

    @Override
    public void onTextRecevier(MsgEntry In) {

    }




    int[] location = new int[2];
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        personState.getLocationInWindow(location);
        LogUtil.e(location[0]+"--"+location[1]+"-"+personState.getWidth()+"-"+personState.getHeight());
       if (event.getAction()==MotionEvent.ACTION_UP){
           if (event.getX()>location[0]&&event.getX()<location[0]+personState.getWidth()||event.getY()>location[1]&&event.getY()<location[1]+personState.getHeight())
               add2Fans();
       }
        return false;
    }

    @Override
    protected void onDestroy() {
        broadcastManager.unregisterReceiver(stateCheckReciver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finishActivityResult();

        super.onBackPressed();
    }
    private void finishActivityResult() {
        Intent intent =new Intent();
        setResult(101,intent);
        ActivityCompat.finishAfterTransition(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100)
            setStudentInfoByResult(IntentStudentInfo);
    }
}
