package com.example.ishow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ishow.Application.iShowTalkApplication;
import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Bean.MsgEntry;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.Fragment.FragmentCourse;
import com.example.ishow.Fragment.FragmentMe;
import com.example.ishow.Fragment.FragmentMsgList;
import com.example.ishow.Fragment.FragmentPractice;
import com.example.ishow.Fragment.GalleryFragment;
import com.example.ishow.Fragment.VideoRecorderFragment;
import com.example.ishow.Service.JustalkStateCheckReciver;
import com.example.ishow.UIActivity.SearchActivity;
import com.example.ishow.Utils.ChatManager;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.ToastUtil;
import com.example.ishow.Utils.UpdaterManager;
import com.example.ishow.iShowConfig.iShowConfig;
import com.example.ishow.mipush.MiPush;
import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcCli;
import com.justalk.cloud.lemon.MtcCliConstants;
import com.justalk.cloud.lemon.MtcConstants;
import com.justalk.cloud.lemon.MtcIm;
import com.justalk.cloud.lemon.MtcImConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppBaseCompatActivity implements JustalkStateCheckReciver.JustalkStateCheckListner {
    FragmentCourse fragmentCourse;
    FragmentPractice fragmentPractice;
    FragmentMsgList fragmentMsgList;
    FragmentMe fragmentMe;
    //  @Bind(R.id.main_radiobutton_course)
    RadioButton mainRadiobuttonCourse;
    // @Bind(R.id.main_radiobutton_practice)
    RadioButton mainRadiobuttonPractice;
    // @Bind(R.id.main_radiobutton_msg)
    RadioButton mainRadiobuttonMsg;
    android.support.v7.widget.Toolbar Toolbar;
    @Bind(R.id.main_radiobutton_me)
    RadioButton mainRadiobuttonMe;
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    private TextView msg_unread_count;
    View contentView;

    ImageView toolbarSearch;
    private FragmentTransaction ft;
    BroadcastReceiver loginReciver;
    BroadcastReceiver NetworkReceiver;
    private LocalBroadcastManager broadcastManager;
    private ChatManager dbManager;
    private JustalkStateCheckReciver stateCheckReciver;
    private BroadcastReceiver ExitAppReciver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        contentView = getView(R.layout.activity_main);
        setContentView(contentView);
        ButterKnife.bind(this);
        findViewById();
        //bindFragment();
        ReigistBroadcast();
        new UpdaterManager(this);

    }


    private void findViewById() {

        Toolbar = (android.support.v7.widget.Toolbar) contentView.findViewById(R.id.Toolbar);
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("iShow");
        mainRadiobuttonCourse = (RadioButton) contentView.findViewById(R.id.main_radiobutton_course);
        mainRadiobuttonPractice = (RadioButton) contentView.findViewById(R.id.main_radiobutton_practice);
        mainRadiobuttonMsg = (RadioButton) contentView.findViewById(R.id.main_radiobutton_msg);
        mainRadiobuttonMe = (RadioButton) contentView.findViewById(R.id.main_radiobutton_me);
        radioGroup = (RadioGroup) contentView.findViewById(R.id.radioGroup);

        toolbarSearch = (ImageView) contentView.findViewById(R.id.toolbar_search);
        toolbarSearch.setVisibility(View.VISIBLE);
        msg_unread_count = (TextView) contentView.findViewById(R.id.msg_unread_count);
        showHideFragment(R.id.main_radiobutton_course);
        mainRadiobuttonCourse.setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //showHideFragment(checkedId);
            }
        });
    }


    private void showHideFragment(int i) {
        mainRadiobuttonCourse.setChecked(false);
        mainRadiobuttonPractice.setChecked(false);
        mainRadiobuttonMsg.setChecked(false);
        mainRadiobuttonMe.setChecked(false);
        ft = getSupportFragmentManager().beginTransaction();
        hideFragment();
        switch (i) {
            case R.id.main_radiobutton_course:
                if (fragmentCourse == null) {
                    fragmentCourse = new FragmentCourse();
                    ft.add(R.id.main_viewPager, fragmentCourse).show(fragmentCourse);
                } else ft.show(fragmentCourse);
                toolbarSearch.setVisibility(View.VISIBLE);

                break;
            case R.id.main_radiobutton_practice:
                if (fragmentPractice == null) {
                    fragmentPractice = new FragmentPractice();
                    ft.add(R.id.main_viewPager, fragmentPractice).show(fragmentPractice);
                } else ft.show(fragmentPractice);
                toolbarSearch.setVisibility(View.VISIBLE);
                // mainRadiobuttonPractice.setChecked(true);
                break;
            case R.id.main_radiobutton_msg:
                if (fragmentMsgList == null) {
                    fragmentMsgList = new FragmentMsgList();
                    ft.add(R.id.main_viewPager, fragmentMsgList).show(fragmentMsgList);
                } else ft.show(fragmentMsgList);
                // mainRadiobuttonMsg.setChecked(true);
                break;
            case R.id.main_radiobutton_me:
                if (fragmentMe == null) {
                    fragmentMe = new FragmentMe();
                    ft.add(R.id.main_viewPager, fragmentMe).show(fragmentMe);
                } else ft.show(fragmentMe);
                toolbarSearch.setVisibility(View.GONE);
                break;
        }
        ft.commit();
    }

    private void hideFragment() {
        if (fragmentCourse != null)
            ft.hide(fragmentCourse);
        if (fragmentPractice != null)
            ft.hide(fragmentPractice);
        if (fragmentMsgList != null)
            ft.hide(fragmentMsgList);
        if (fragmentMe != null)
            ft.hide(fragmentMe);
    }

    public void login() {
        String username = SharePrefrence.getInstance().getStudentName(getApplicationContext());
        String password = SharePrefrence.getInstance().getStudentPassword(getApplicationContext());
        String server = "sudp:ae.justalkcloud.com:9851";
        // sudp:ae.justalkcloud.com:9851
        // String server =
        //sp.getString("server","sudp:ae.justalkcloud.com:9851");
        // sp.getString("server","sudp:dev.ae.justalkcloud.com:9851");
        //String server = sp.getString("server", "sudp:ae.justalkcloud.com:9851");
        if (TextUtils.equals(username, "")) {
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put(MtcApi.KEY_SERVER_ADDRESS, server);
            json.put(MtcApi.KEY_PASSWORD, password);
            if (MtcApi.login(username, json) == MtcConstants.ZOK) {

            } else if (MtcApi.login(username, json) == MtcConstants.ZFAILED) {

            } else if (MtcApi.login(username, json) == MtcConstants.INVALIDID) {
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void ReigistBroadcast() {
        stateCheckReciver = new JustalkStateCheckReciver();
        stateCheckReciver.setOnJustalkStateCheckListner(this);
        //登陆justalk成后的回调
        loginReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                iShowTalkApplication.getInstance().startMiPush();
                MtcIm.Mtc_ImRefresh();
                ToastUtil.showToast(MainActivity.this, "login sucess");
            }
        };
        //监听到网络变化后开始登陆
        NetworkReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) {
                    return;
                }
                if (TextUtils.equals(intent.getAction(),
                        ConnectivityManager.CONNECTIVITY_ACTION)) {
                    if (MtcCli.Mtc_CliGetState() != MtcCliConstants.EN_MTC_CLI_STATE_LOGINED) {
                        login();
                    }
                }
            }
        };
        ExitAppReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) {
                    return;
                }
                if (TextUtils.equals(intent.getAction(), iShowConfig.ExitAPPBroadCastReciver)) {
                    MainActivity.this.finish();
                }
            }
        };
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter ExitAppReciverFilter = new IntentFilter();
        ExitAppReciverFilter.addAction(iShowConfig.ExitAPPBroadCastReciver);
        registerReceiver(ExitAppReciver, ExitAppReciverFilter);

        registerReceiver(NetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        broadcastManager.registerReceiver(loginReciver, new IntentFilter(MtcApi.MtcLoginOkNotification));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MtcImConstants.MtcImSendDidFailNotification);
        intentFilter.addAction(MtcImConstants.MtcImSendOkNotification);
        intentFilter.addAction(MtcImConstants.MtcImTextDidReceiveNotification);
        broadcastManager.registerReceiver(stateCheckReciver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        MtcApi.logout();
        MiPush.stop(getApplicationContext());
        unregisterReceiver(ExitAppReciver);
        unregisterReceiver(NetworkReceiver);
        broadcastManager.unregisterReceiver(loginReciver);
        broadcastManager.unregisterReceiver(stateCheckReciver);
        NetworkReceiver = null;
        loginReciver = null;
        ExitAppReciver = null;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        // testInfo();
        getUnreadCount();
        super.onResume();
    }

    private void getUnreadCount() {
        if (dbManager == null) dbManager = new ChatManager();
        int unreadCount = dbManager.getConversationUnreadCount();
        msg_unread_count.setVisibility(unreadCount == 0 ? View.GONE : View.VISIBLE);
        msg_unread_count.setText(unreadCount + "");
    }

    @Override
    public void onCheckJustalkStateNotification(boolean state) {

    }

    @Override
    public void onTextSendFaild(Intent intent) {
        updateDB(intent, false);
    }

    private void updateDB(Intent intent, boolean ok) {
        if (iShowConfig.talingUid != "")
            return;
        if (dbManager == null) dbManager = new ChatManager();
        int cookie = intent.getIntExtra(MtcApi.EXTRA_COOKIE, -1);
        dbManager.updateMsgState(ok, cookie);
    }

    @Override
    public void onTextSendOK(Intent intent) {
        updateDB(intent, true);
    }

    @Override
    public void onTextRecevier(MsgEntry msg) {
        if (studentInfo == null) studentInfo = SharePrefrence.getInstance().getStudentInfo(this);
        msg.setToUserid(studentInfo.getUserid());
        if (!iShowConfig.chatISOpen) {
            if (dbManager == null) dbManager = new ChatManager();
            dbManager.saveTextMessage(msg, studentInfo);
            if (!msg.isRead()) {
                msg_unread_count.setVisibility(View.VISIBLE);
                msg_unread_count.setText((Integer.parseInt(msg_unread_count.getText().toString()) + 1) + "");
            }
        }
        if (fragmentMsgList != null)
            if (!fragmentMsgList.isHidden())
                fragmentMsgList.getConversationListFromDb();

    }

    UserEntry studentInfo;

    private void testInfo() {

        if (studentInfo == null) studentInfo = SharePrefrence.getInstance().getStudentInfo(this);
        JSONObject object = new JSONObject();
        try {
            object.put("msg", "1000000");
            object.put("msgTime", new Date().getTime());
            object.put("fromId", "10086");
            object.put("fromImg", "http://123.jpg");
            object.put("fromPhone", "100867");
            object.put("fromName", "测试消息Name");

            MsgEntry entry = new MsgEntry();

            entry.setFromUserid("10086");
            entry.setFromNick("测试消息Name");
            entry.setFromMobile("100867");
            entry.setFromImg("http://123.jpg");

            //消息接收方消息体
            entry.setToUserid(studentInfo.getUserid());
            entry.setToNick(studentInfo.getName());
            entry.setToMobile(studentInfo.getMobile());
            entry.setToImg(studentInfo.getImg());

            entry.setState(1);
            entry.setTextMsg("测试消息10086");
            entry.setMsgTime(new Date().getTime());
            entry.setRead(false);
            if (dbManager == null) dbManager = new ChatManager();
            dbManager.saveTextMessage(entry, studentInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.main_radiobutton_course, R.id.main_radiobutton_practice, R.id.main_radiobutton_msg, R.id.main_radiobutton_me, R.id.toolbar_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_radiobutton_course:
                showHideFragment(R.id.main_radiobutton_course);
                mainRadiobuttonCourse.setChecked(true);
                break;
            case R.id.main_radiobutton_practice:
                showHideFragment(R.id.main_radiobutton_practice);
                mainRadiobuttonPractice.setChecked(true);
                break;
            case R.id.main_radiobutton_msg:
                showHideFragment(R.id.main_radiobutton_msg);
                mainRadiobuttonMsg.setChecked(true);
                break;
            case R.id.main_radiobutton_me:
                showHideFragment(R.id.main_radiobutton_me);
                mainRadiobuttonMe.setChecked(true);
                break;
            case R.id.toolbar_search:
                Intent intent = new Intent(this, SearchActivity.class);
                ActivityCompat.startActivity(this, intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (fragmentMe!=null)
                fragmentMe.onActivityResult(requestCode,  resultCode,  data);
    }
}
