package com.example.ishow.UIActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.ishow.Adapter.FansAdapter;
import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.UIView.MaterialDialog;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.ToastUtil;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by MRME on 2016-04-20.
 */
public class FansActivity extends AppBaseCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    SwipeMenuListView mListView;
    private ArrayList<UserEntry> datas = new ArrayList<UserEntry>();
    View contentView;
    private int position;//长按删除的position
    private FansAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        contentView =getView(R.layout.activity_course_local);
        setContentView(contentView);
        mListView = (SwipeMenuListView) contentView.findViewById(R.id.listView);
        setAppBaseCompactActivity(contentView);
        mListView.setOnItemLongClickListener(this);
        setToolbar(true,getString(R.string.fans_title));
        bindData2UI();
    }

    private void finishactivity() {
        /*Intent intent = new Intent();
        intent.putExtra("fans",datas.size()+"");
        setResult(1,intent);
        finish();*/
        SharePrefrence.getInstance().putFansSize(getApplicationContext(),datas.size()+"");
        ActivityCompat.finishAfterTransition(this);
       // ActivityCompat.finishAfterTransition(FansActivity.this);
    }

    private void bindData2UI() {
        UserEntry studentInfo = SharePrefrence.getInstance().getStudentInfo(getApplicationContext());
        if (studentInfo==null){
            showTipContent(getString(R.string.history_NO_login));
            return ;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("uid", studentInfo.getUserid());
            XHttpUtils.getInstace().getValue(iShowConfig.getFans, object, new XHttpUtils.OnHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                   setData2UI(result);
                }

                @Override
                public void onError(String errorResson) {
                    showTipContent(errorResson);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setData2UI(String result) {
        LogUtil.e(result);
        try {
            JSONArray array =new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {

                UserEntry entry = new UserEntry();
                JSONObject object = array.getJSONObject(i);
                entry.setUserid(object.getString("uid"));
                entry.setName(object.getString("userNick"));
                entry.setMobile(object.getString("userName"));
                entry.setImg(object.getString("userHead"));
                entry.setCampus(object.getString("school"));
                entry.setIsOnline(object.getString("isOnline"));
                datas.add(entry);
            }
            setSwipListView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //addContentView(contentView);
    }
    private void setSwipListView() {
        addContentView(mListView);
        adapter = new FansAdapter(this,datas);
        mListView.setAdapter(adapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                //deleteItem.setTitle("取关");
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);
        mListView.setOpenInterpolator(new BounceInterpolator());
        mListView.setCloseInterpolator(new AnticipateOvershootInterpolator());
        mListView.setOnItemClickListener(this);
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        FansActivity.this.position=position;
                        cancelFans();
                        break;
                    case 1:
                        FansActivity.this.position=position;
                        cancelFans();
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    MaterialDialog dialog;
    private void cancelFans() {
        JSONObject object = new JSONObject();
        UserEntry studentInfo = SharePrefrence.getInstance().getStudentInfo(getApplicationContext());
        if (studentInfo==null){
            ToastUtil.makeSnack(mListView,getString(R.string.history_NO_login),this);
            return ;
        }
        UserEntry entry = datas.get(position);
        try {
            dialog= new MaterialDialog();
            dialog.showDloag(this,getString(R.string.request_server));
            object.put("masterID", entry.getUserid());
            object.put("is_del", "y");
            object.put("fanID", studentInfo.getUserid()==null?studentInfo.getId():studentInfo.getUserid());
            XHttpUtils.getInstace().getValue(iShowConfig.Follow, object, new XHttpUtils.OnHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    dialog.cancelDialog();
                    setData2UIByResult(result);
                }

                @Override
                public void onError(String errorResson) {
                    dialog.cancelDialog();
                    ToastUtil.makeSnack(mListView,errorResson,FansActivity.this);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setData2UIByResult(String result) {
        try {
            JSONObject object =new JSONObject(result);
            if (object.getInt("code")==1){
                datas.remove(position);
                adapter.notifyDataSetChanged();
            }else{
                ToastUtil.makeSnack(mListView,object.getString("msg"),FansActivity.this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user",datas.get(position));
        Intent intent = new Intent(this, PersonalCenterActivity.class);
        intent.putExtras(bundle);
        ActivityCompat.startActivity(this,intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        finishactivity();
        super.onBackPressed();

    }
}
