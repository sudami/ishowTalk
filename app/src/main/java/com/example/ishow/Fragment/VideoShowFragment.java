package com.example.ishow.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.ishow.Adapter.MediaAdapter;
import com.example.ishow.Bean.MediaEntry;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.extras.recyclerview.PullToRefreshRecyclerView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MRME on 2016-05-10.
 */
public class VideoShowFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2{
    @Bind(R.id.base_loading)
    LinearLayout baseLoading;
    @Bind(R.id.media_list)
    PullToRefreshRecyclerView mediaList;
    @Bind(R.id.media_empty)
    LinearLayout mediaEmpty;
    private RecyclerView recyclerView;
    private ArrayList<MediaEntry> list ;
    private MediaAdapter mediaAdapter;
    private Context context;
    private boolean isTest;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = getView(R.layout.fragment_media);
        ButterKnife.bind(this, rootView);
        /**设置refreshRecyclerView的一些属性*/
        recyclerView = mediaList.getRefreshableView();
        GridLayoutManager manager = new GridLayoutManager(context,2, GridLayoutManager.VERTICAL, false);
        manager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(manager);
        mediaList.setMode(PullToRefreshBase.Mode.BOTH);
        mediaList.setOnRefreshListener(this);
        return rootView;
    }



    /***
     *  true 口册视频 ---1   false娱乐视频 -----0
     * @param isTest 是否是口测视频
     */
    private int mediaOffset=0;
    public void getDataFromServer(Context context,boolean isTest){
        this.context = context;
        this.isTest = isTest;
        UserEntry studentInfo = SharePrefrence.getInstance().getStudentInfo(context);
        //{mediaBelong:15555043402,mediaOffset:0,isTest:0}
        JSONObject object = new JSONObject();
        try {
            object.put("mediaBelong",studentInfo.getMobile());
            object.put("mediaOffset",mediaOffset);
            object.put("isTest",isTest?1:0);
            XHttpUtils.getInstace().getValue(iShowConfig.getPersonVideo, object, new XHttpUtils.OnHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    mediaList.onRefreshComplete();
                    setUIbyData(result);
                    LogUtil.e(result);
                }

                @Override
                public void onError(String errorResson) {
                    LogUtil.e(errorResson);
                    mediaList.onRefreshComplete();
                    baseLoading.setVisibility(View.GONE);
                    mediaEmpty.setVisibility(View.VISIBLE);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setUIbyData(String result) {
        if (list==null) list = new ArrayList<>();
        if (mediaOffset==0)list.clear();
        try {
            JSONObject object = new JSONObject(result);
            if (object.getString("code")==1+"") baseLoading.setVisibility(View.GONE);
            if (object.has("msg"))
            {
                JSONArray msg = object.getJSONArray("msg");
                Gson gson = new Gson();
                list = gson.fromJson(msg.toString(),new TypeToken<ArrayList<MediaEntry>>(){}.getType());
                if (mediaAdapter==null)
                {
                    mediaAdapter =new MediaAdapter(getActivity(),list);
                    recyclerView.setAdapter(mediaAdapter);
                }else if (mediaOffset==0) mediaAdapter.refreshData(list);
                else mediaAdapter.notifyItemRangeInserted(mediaOffset,list.size()-1);
            }else {baseLoading.setVisibility(View.GONE); mediaEmpty.setVisibility(View.VISIBLE);}
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        MobclickAgent.onPageStart("首页_视频展示_resume");
        super.onResume();
    }

    @Override
    public void onPause() {
        MobclickAgent.onPageStart("首页_视频展示-本校本班_onPause");
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mediaOffset =0;
        getDataFromServer(context,isTest);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        if (list!=null)mediaOffset = list.size();
        getDataFromServer(context,isTest);
    }
}
