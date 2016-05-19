package com.example.ishow.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.example.ishow.Adapter.PracticeAdapter;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.UIActivity.PersonalCenterActivity;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.SwitchAnimationUtil;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectMembersFragment extends Fragment {

    @Bind(R.id.PullToRefreshGridView)
    com.handmark.pulltorefresh.library.PullToRefreshGridView PullToRefreshGridView;
    private View view;
    private Context mContext;
    private PracticeAdapter adapter;
    List<UserEntry> list =new ArrayList<UserEntry>();
    private boolean getFans;
    private boolean country;
    private String courseId;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_select_members, null);
        ButterKnife.bind(this, view);
        PullToRefreshGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(mContext, PersonalCenterActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", list.get(position));
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        PullToRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                getClazStudent(mContext, getFans, country, courseId);
            }
        });
        new SwitchAnimationUtil().startAnimation(PullToRefreshGridView, SwitchAnimationUtil.AnimationType.HORIZON_CROSS);
        return view;
    }

    public void getClazStudent(Context mContext,boolean getFans,boolean country,String courseId) {
        this.mContext = mContext;
        this.getFans = getFans;
        this.country = country;
        this.courseId = courseId;


        JSONObject object = new JSONObject();
        UserEntry rank = SharePrefrence.getInstance().getStudentInfo(mContext);
        try {

            if (!getFans){
                if (country){
                    object.put("classFlag", country);
                }
                object.put("uid", rank.getUserid());
                object.put("courseID", courseId);
            }else{
                object.put("uid", rank.getUserid());
            }
            String url = getFans?iShowConfig.getFans:iShowConfig.getAvaiableStudent;
           // PullToRefreshGridView.setRefreshing(true);
            LogUtil.e(url);
            XHttpUtils.getInstace().getValue(url, object, new XHttpUtils.OnHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    PullToRefreshGridView.onRefreshComplete();
                    OnHttpSuccess(result);
                }

                @Override
                public void onError(String errorResson) {
                    PullToRefreshGridView.onRefreshComplete();
                }
            });
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }



    public void OnHttpSuccess(String result) {
        if(result==null){
            return ;
        }
        if (list != null) {
            list.clear();
        }
        try {
            JSONArray array =new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                UserEntry recent = new UserEntry();

                recent.setUserid(object.getString("uid"));
                recent.setImg(object.getString("userHead"));
                recent.setMobile(object.getString("userName"));
                recent.setName(object.getString("userNick"));
                recent.setCampus(object.getString( "school"));
                String state ="1";
                if (object.has("isOnline")) {
                    state = object.getString("isOnline");
                }
                if(TextUtils.equals(state, "1")){
                    recent.setIsOnline("1");}
                else if(TextUtils.equals(state, "2")){
                    recent.setIsOnline("2");
                }else{
                    recent.setIsOnline("3");
                }
                list.add(recent);
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        if (adapter == null) {
            adapter = new PracticeAdapter(mContext, list);
            PullToRefreshGridView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }


    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        MobclickAgent.onPageStart("首页_人员选择-本校本班_resume");
        super.onResume();
    }

    @Override
    public void onPause() {
        MobclickAgent.onPageStart("首页_人员选择-本校本班_onPause");
        super.onPause();
    }
}
