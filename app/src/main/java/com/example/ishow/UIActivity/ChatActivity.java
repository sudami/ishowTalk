package com.example.ishow.UIActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishow.Adapter.ChatAdapter;
import com.example.ishow.BaseComponent.CallBaseCompactActivity;
import com.example.ishow.Bean.MsgEntry;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.RecycleViewAnimator.SlideInOutBottomItemAnimator;
import com.example.ishow.Service.JustalkStateCheckReciver;
import com.example.ishow.UIView.MaterialDialog;
import com.example.ishow.Utils.ChatManager;
import com.example.ishow.Utils.Interface.TextUtil;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.ToastUtil;
import com.example.ishow.iShowConfig.iShowConfig;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.extras.recyclerview.PullToRefreshRecyclerView;
import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcBuddy;
import com.justalk.cloud.lemon.MtcConstants;
import com.justalk.cloud.lemon.MtcIm;
import com.justalk.cloud.lemon.MtcImConstants;
import com.justalk.cloud.lemon.MtcUser;
import com.justalk.cloud.lemon.MtcUserConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MRME on 2016-04-25.
 */
public class ChatActivity extends CallBaseCompactActivity implements JustalkStateCheckReciver.JustalkStateCheckListner{
    @Bind(R.id.course_title_name)
    TextView courseTitleName;
    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;
    @Bind(R.id.chat_listview)
    PullToRefreshRecyclerView chatListview;
    @Bind(R.id.chat_editext)
    EditText chatEditext;
    @Bind(R.id.caht_send)
    Button cahtSend;
    @Bind(R.id.toolbar_search)
    ImageView toolbarSearch;
    @Bind(R.id.AppBarLayout)
    android.support.design.widget.AppBarLayout AppBarLayout;
    private RecyclerView recyclerView;

    private JustalkStateCheckReciver stateCheckReciver;
    private LocalBroadcastManager broadcastManager;
    private UserEntry studentInfo;
    private UserEntry chatEntry;
    private ChatManager manager;
    private List<MsgEntry> list  =new ArrayList<>();
    private ChatAdapter chatAdapter;
    private MaterialDialog dialog;
    private boolean isChating;
    View view ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (getIntent().hasExtra("isChating")) {
            isChating = true;
        }
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_chat,null);
        setContentView(view);
        ButterKnife.bind(this);
       if (isChating)
           view.setBackgroundColor(Color.TRANSPARENT);
       else view.setBackgroundColor(Color.WHITE);
        chatEntry = getIntent().getExtras().getParcelable("userEntry");
        studentInfo = SharePrefrence.getInstance().getStudentInfo(getApplicationContext());

        setRecycleView();
        registerBroadCast();


        getChatMsgFromDb();
        iShowConfig.talingUid = chatEntry.getUserid();
        iShowConfig.chatISOpen = true;
    }


    private void registerBroadCast() {
        stateCheckReciver = new JustalkStateCheckReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(1000);
        intentFilter.addAction("MtcBuddyQueryLoginInfoOkNotification");
        intentFilter.addAction("MtcBuddyQueryLoginInfoDidFailNotification");
        intentFilter.addAction(MtcImConstants.MtcImSendDidFailNotification);
        intentFilter.addAction(MtcImConstants.MtcImSendOkNotification);
        intentFilter.addAction(MtcImConstants.MtcImTextDidReceiveNotification);
        stateCheckReciver.setOnJustalkStateCheckListner(this);
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(stateCheckReciver, intentFilter);

    }

    private void setRecycleView() {
        toolbarSearch.setImageResource(R.drawable.video_chat);
        toolbarSearch.setVisibility(isChating ? View.GONE : View.VISIBLE);
        courseTitleName.setText(chatEntry==null?"ishow":chatEntry.getName());
        courseTitleName.setVisibility(isChating ? View.GONE : View.VISIBLE);
        if (isChating) {
            AppBarLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            Toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(ChatActivity.this);
            }
        });

        chatListview.setMode(isChating ? PullToRefreshBase.Mode.DISABLED : PullToRefreshBase.Mode.PULL_FROM_START);
        recyclerView = chatListview.getRefreshableView();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new SlideInOutBottomItemAnimator(recyclerView));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        chatListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                // getClassIdCousrse();
            }
        });
        chatListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                chatListview.setRefreshing(true);
                getChatMsgFromDb();
            }
        });
    }

    private void getChatMsgFromDb() {

        if (manager == null) manager = new ChatManager();
        if (list == null) list = new ArrayList<>();
        List<MsgEntry> orLoadMore = manager.getChat15MessagesOrLoadMore(isChating,studentInfo.getUserid()==null?studentInfo.getId():studentInfo.getUserid(), chatEntry.getUserid()==null?chatEntry.getId():chatEntry.getUserid(), list.size());
        if (orLoadMore != null)
            list.addAll(orLoadMore);
        if (chatAdapter == null) {
            chatAdapter = new ChatAdapter(this, list, studentInfo,isChating);
            recyclerView.setAdapter(chatAdapter);
            recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
        } else chatAdapter.notifyDataSetChanged();
        chatListview.onRefreshComplete();
    }

    @OnClick({R.id.caht_send, R.id.toolbar_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.caht_send:
                if (chatEditext.getText().toString().trim() == "") {
                    ToastUtil.makeSnack(chatEditext, "content size less than 10", this);
                    return;
                }
                long msgTime = new Date().getTime();

                MsgEntry entry = new MsgEntry();

                entry.setFromUserid(studentInfo.getUserid()==null?studentInfo.getId():studentInfo.getUserid());
                entry.setFromNick(studentInfo.getName());
                entry.setFromMobile(studentInfo.getMobile());
                entry.setFromImg(studentInfo.getImg());

                //消息接收方消息体
                entry.setToUserid(chatEntry.getUserid());
                entry.setToNick(chatEntry.getName());
                entry.setToMobile(chatEntry.getMobile());
                entry.setToImg(chatEntry.getImg());

                entry.setState(0);
                entry.setTextMsg(chatEditext.getText().toString().trim());
                entry.setMsgTime(msgTime);
                entry.setRead(true);
                if (chatAdapter == null) {
                    chatAdapter = new ChatAdapter(this, list, studentInfo,isChating);
                    recyclerView.setAdapter(chatAdapter);
                    recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                }
                int listposition = chatAdapter.addMsgEntry(entry);
                entry.setListPosition(listposition);

                if (manager==null)manager = new ChatManager();
                int dbId = manager.saveTextMessage(entry, studentInfo);
               // LogUtil.e(listposition + "listposition-dbId" + dbId);
                try {
                    JSONObject object = new JSONObject();
                    object.put("msg", chatEditext.getText().toString().trim());
                    object.put("msgTime", msgTime);
                    object.put("fromId", studentInfo.getUserid()==null?studentInfo.getId():studentInfo.getUserid());
                    object.put("fromImg", studentInfo.getImg());
                    object.put("fromPhone", studentInfo.getMobile());
                    object.put("fromName", studentInfo.getName());
                    String userUri = MtcUser.Mtc_UserFormUri(MtcUserConstants.EN_MTC_USER_ID_USERNAME, "13429655539");//13429655539
                    int info = MtcIm.Mtc_ImSendText(dbId, userUri, object.toString(), null);
                    if (info == MtcConstants.ZOK) {
                    } else if (info == MtcConstants.ZFAILED) {
                        entry.setState(2);
                        manager.updateMsgState(false, dbId);
                        chatAdapter.updateMsgEntry(listposition, false);
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
                chatEditext.setText("");
                recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                break;
            case R.id.toolbar_search:
                dialog = new MaterialDialog();
                dialog.showDloag(this, getString(R.string.request_server));
                if (MtcBuddy.Mtc_BuddyQueryLoginInfo(0L, MtcUser.Mtc_UserFormUri(MtcUser.EN_MTC_USER_ID_USERNAME, chatEntry.getMobile()), 3) != MtcConstants.ZOK) {
                    ToastUtil.makeSnack(view, getString(R.string.history_NO_login), this);
                    dialog.cancelDialog();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        broadcastManager.unregisterReceiver(stateCheckReciver);
        broadcastManager = null;
        hideSoftKeyBoard(chatEditext);
        iShowConfig.talingUid = "";
        iShowConfig.chatISOpen = false;
        //LogUtil.e("iShowConfig.talingUid" + iShowConfig.talingUid);

        super.onDestroy();
    }

    public void hideSoftKeyBoard(View login) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(login.getWindowToken(), 0);
    }

    @Override
    public void onCheckJustalkStateNotification(boolean state) {
        dialog.cancelDialog();
        dialog.cancelDialog();
        if (state) {
            startCallactivity(chatEntry);
        } else ToastUtil.makeSnack(toolbarSearch, getString(R.string.history_NO_login_op), this);
    }

    @Override
    public void onTextSendFaild(Intent intent) {

        if (manager == null) manager = new ChatManager();
        int cookie = intent.getIntExtra(MtcApi.EXTRA_COOKIE, -1);
        int listPosition = manager.updateMsgState(false, cookie);
        chatAdapter.updateMsgEntry(listPosition, false);
    }

    @Override
    public void onTextSendOK(Intent intent) {

        int cookie = intent.getIntExtra(MtcApi.EXTRA_COOKIE, -1);
        if (manager == null) manager = new ChatManager();
        int listPosition = manager.updateMsgState(true, cookie);
        chatAdapter.updateMsgEntry(listPosition, true);

    }

    @Override
    public void onTextRecevier(MsgEntry msg) {
        msg.setToUserid(studentInfo.getUserid()==null?studentInfo.getId():studentInfo.getUserid());
        if (TextUtils.equals(msg.getFromUserid(),iShowConfig.talingUid))
            chatAdapter.addMsgEntry(msg);
        if (manager==null)manager=new ChatManager();
        manager.saveTextMessage(msg,studentInfo);
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }
}
