package com.example.ishow.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.example.ishow.Adapter.MediaAdapter;
import com.example.ishow.BaseComponent.DividerGridItemDecoration;
import com.example.ishow.BaseComponent.SpaceItemDecoration;
import com.example.ishow.Bean.MediaEntry;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.UIActivity.MediaPlayActivity;
import com.example.ishow.Utils.PixlesUtils;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.extras.recyclerview.PullToRefreshRecyclerView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MRME on 2016-05-10.
 */
public class VideoShowFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, AdapterView.OnItemClickListener {
    @Bind(R.id.base_loading)
    LinearLayout baseLoading;
    @Bind(R.id.media_list)
    PullToRefreshGridView mediaList;
    @Bind(R.id.media_empty)
    LinearLayout mediaEmpty;
    private ArrayList<MediaEntry> list ;
    private MediaAdapter mediaAdapter;
    private Context context;
    private int isPrivate;
    private int isTest;
    private int mediaType;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = getView(R.layout.fragment_media);
        ButterKnife.bind(this, rootView);
        /**设置refreshRecyclerView的一些属性*/
        mediaList.setMode(PullToRefreshBase.Mode.BOTH);
        mediaList.setOnRefreshListener(this);
        mediaList.setOnItemClickListener(this);
        return rootView;
    }



    /***
     *  isPrivate ==1  私人  isTest =0  口测  1  娱乐
     * @param isTest 是否是口测视频
     */
    private int mediaOffset=0;
    public void getDataFromServer(Context context,int isTest,int isPrivate,int mediaType){
        this.context = context;
        this.isPrivate = isPrivate;
        this.isTest = isTest;
        this.mediaType = mediaType;
        UserEntry studentInfo = SharePrefrence.getInstance().getStudentInfo(context);
        //{mediaBelong:15555043402,mediaOffset:0,isTest:0}
        JSONObject object = new JSONObject();
        try {
            String url = (isPrivate==0?iShowConfig.getCommentVideoByType:iShowConfig.getPersonVideo);
           if (isPrivate==1)
           {
               object.put("mediaBelong",studentInfo.getMobile());
               object.put("mediaOffset",mediaOffset);
               object.put("isTest",isTest==1?1:0);
           }else
           {
               object.put("cmmonMediaType",mediaType);
           }

            XHttpUtils.getInstace().getValue(url ,object, new XHttpUtils.OnHttpCallBack() {
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
                gson.fromJson(msg.toString(),new TypeToken<List<MediaEntry>>(){}.getType());
                list.addAll((Collection<? extends MediaEntry>) gson.fromJson(msg.toString(),new TypeToken<ArrayList<MediaEntry>>(){}.getType()));
                if (mediaAdapter==null)
                {
                    mediaAdapter =new MediaAdapter(getActivity(),list);
                    mediaList.setAdapter(mediaAdapter);
                }else mediaAdapter.notifyDataSetChanged();
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
        getDataFromServer(context,isPrivate,isTest,mediaType);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        if (list!=null)mediaOffset = list.size();
        getDataFromServer(context,isPrivate,isTest,mediaType);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MediaEntry entry = list.get(position);
        Intent intent = new Intent(getActivity(), MediaPlayActivity.class);
        intent.putExtra("mediaTotal",entry.getMediaLength());
        intent.putExtra("mediaTitle",entry.getMediaTitle());
        intent.putExtra("videoUrl",entry.getMediaVideoUrlNew());
        intent.putExtra("imageUrl",entry.getFirstImgUrl());
        intent.putExtra("mp4Url",entry.getMediaVideoUrl());
        intent.putExtra("videoId",entry.getId());
        ActivityCompat.startActivity(getActivity(),intent, ActivityOptionsCompat.makeCustomAnimation(getActivity(),R.anim.slide_in_from_bottom,R.anim.slide_out_to_bottom).toBundle());
    }
}
