package com.example.ishow.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ishow.Adapter.RankAdapter;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.UIActivity.PersonalCenterActivity;
import com.example.ishow.UIActivity.PracticeRankActivity;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MRME on 2016-04-19.
 */
public class ClazFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.rank_empty_content)
    TextView rankEmptyContent;
    ArrayList<UserEntry>  datas =new ArrayList<UserEntry>();
    RankAdapter adapter=null;

    private UserEntry studentInfo;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = getView(R.layout.fragment_rank_listview);
        ButterKnife.bind(this, rootView);
        listView.setOnItemClickListener(this);
        return rootView;
    }

    public void bindData2UI(int position, final Context context, boolean weekFlag) {
        this.context = context;
        studentInfo = SharePrefrence.getInstance().getStudentInfo(context.getApplicationContext());
        try {
            JSONObject ob = new JSONObject();
            ob.put("userid", studentInfo.getUserid());
            ob.put("show_type", position + "");
            ob.put("time_type", weekFlag ? iShowConfig.TIME_TYPE_WEEK : iShowConfig.TIME_TYPE_DAY);
            XHttpUtils.getInstace().getValue(iShowConfig.getUserTextPracticeRank, ob, new XHttpUtils.OnHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    setData2UI(context,result);
                    LogUtil.e("onSuccess" + result);
                }
                @Override
                public void onError(String errorResson) {
                    LogUtil.e(errorResson);
                   if (listView!=null){
                       listView.setVisibility(View.GONE);
                       rankEmptyContent.setVisibility(View.VISIBLE);
                       rankEmptyContent.setText(getString(R.string.request_server_error));
                   }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setData2UI(Context context, String result) {
        if (TextUtils.equals(result,"[]")||TextUtils.equals(result,null)) {
          if(listView!=null){
              listView.setVisibility(View.GONE);
              rankEmptyContent.setVisibility(View.VISIBLE);
              rankEmptyContent.setText(getString(R.string.request_server_error));
          }
        }else{
            try {
                JSONObject object = new JSONObject(result);
                if (object.getInt("code")==0){
                    if(listView!=null){
                        listView.setVisibility(View.GONE);
                        rankEmptyContent.setVisibility(View.VISIBLE);
                        rankEmptyContent.setText(object.getString("msg"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
              if (listView!=null){
                  listView.setVisibility(View.VISIBLE);
                  rankEmptyContent.setVisibility(View.GONE);
              }
            }

            Gson gson =new Gson();
            LogUtil.e(result);

            if (datas!=null){
                datas = gson.fromJson(result,new TypeToken<ArrayList<UserEntry>>(){}.getType());
                adapter =  new RankAdapter(context,datas,studentInfo);
                listView.setAdapter(adapter);
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("user",datas.get(position));
        Intent intent = new Intent(context, PersonalCenterActivity.class);
        intent.putExtras(bundle);
        ActivityCompat.startActivity((PracticeRankActivity)context,intent, ActivityOptionsCompat.makeSceneTransitionAnimation((PracticeRankActivity)context).toBundle());
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
