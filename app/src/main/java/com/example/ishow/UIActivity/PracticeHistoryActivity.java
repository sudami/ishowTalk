package com.example.ishow.UIActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ishow.Adapter.PracticeHistoryAdater;
import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by MRME on 2016-04-20.
 */
public class PracticeHistoryActivity extends AppBaseCompatActivity implements AdapterView.OnItemClickListener {
    View contentView = null;
    com.handmark.pulltorefresh.library.PullToRefreshListView PullToRefreshListView;
    PracticeHistoryAdater adater;
    private int pager=1;
    private ArrayList<UserEntry> datas =new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView = getView(R.layout.activity_practice_history_list);
        setContentView(contentView);
        setAppBaseCompactActivity(contentView);
        setToolbar(true,getString(R.string.history_title));
        PullToRefreshListView = (com.handmark.pulltorefresh.library.PullToRefreshListView) contentView.findViewById(R.id.PullToRefreshListView);
        PullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        PullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pager=1;
                PullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                bindData2UI();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pager++;
                bindData2UI();
            }
        });
        PullToRefreshListView.setOnItemClickListener(this);
        bindData2UI();
    }

    private void bindData2UI() {
        UserEntry studentInfo = SharePrefrence.getInstance().getStudentInfo(getApplicationContext());
        if (studentInfo==null){
            showTipContent(getString(R.string.history_NO_login));
            return ;
        }
        JSONObject object =new JSONObject();
        try {
            object.put("mobile", studentInfo.getMobile());
            object.put("pager", pager);
            XHttpUtils.getInstace().getValue(iShowConfig.getPracticeHistory, object, new XHttpUtils.OnHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    PullToRefreshListView.setRefreshing(false);
                    setData2UI(result);
                }
                @Override
                public void onError(String errorResson) {
                    PullToRefreshListView.setRefreshing(false);
                    showTipContent(errorResson);
                }
            });
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    private void setData2UI(String result) {
        try {
            JSONObject object =new JSONObject(result);
            if (object.getInt("AllTotal")==0){showTipContent(getString(R.string.NO_history));}
            else{
                JSONArray array =object.getJSONArray("List");
                for (int i=0;i<array.length();i++){
                    UserEntry entry = new UserEntry();
                    JSONObject ob = array.getJSONObject(i);
                    entry.setUserid(ob.getString("ReceiverUid"));
                    entry.setTimelong(ob.getString("Callendtime"));//Timelong这里用来装载Callendtime
                    entry.setName(ob.getString("ReceiverName"));
                    entry.setMobile(ob.getString("Mobile"));
                    entry.setImg(ob.getString("ReceiverHeadPicture"));
                    entry.setCampus(ob.getString("ReceiverSchool"));
                    datas.add(entry);
                }

                if (adater ==null){
                    addContentView(contentView);
                    adater = new PracticeHistoryAdater(PracticeHistoryActivity.this,datas);
                    PullToRefreshListView.setAdapter(adater);
                }else {
                    adater.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showTipContent(e.getMessage());
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user",datas.get(position));
        Intent intent = new Intent(this, PersonalCenterActivity.class);
        intent.putExtras(bundle);
        ActivityCompat.startActivity(this,intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    }
}
