package com.example.ishow.UIActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ishow.Adapter.SearchAdapter;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.UIView.MaterialDialog;
import com.example.ishow.Utils.Interface.TextUtil;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.ToastUtil;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MRME on 2016-04-26.
 */
public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchAdapter.SearchAdapterAdd2Fansinterface {
    @Bind(R.id.course_title_name)
    TextView courseTitleName;
    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;
    @Bind(R.id.search_editext)
    EditText searchEditext;
    @Bind(R.id.search_text)
    TextView searchText;
    @Bind(R.id.listView)
    ListView listView;
    UserEntry studentInfo;
    private ArrayList<UserEntry> list;
    private SearchAdapter searchAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        bindView2UI();
        studentInfo = SharePrefrence.getInstance().getStudentInfo(getApplicationContext());
        listView.setOnItemClickListener(this);
    }

    private void bindView2UI() {
        courseTitleName.setText(getString(R.string.search_freinds));
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(SearchActivity.this);
            }
        });
    }

    MaterialDialog dialog;
    @OnClick(R.id.search_text)
    public void onClick() {
       /* if (searchEditext.getText().length()<=10);
        {
            ToastUtil.makeSnack(searchEditext,getString(R.string.search_no_keyowrods),this);
            return ;
        }*/
        try {
            if (dialog==null)
                dialog = new MaterialDialog();
            dialog.showDloag(this,getString(R.string.request_server));
            JSONObject object = new JSONObject();
            object.put("search",searchEditext.getText().toString().trim());
            object.put("uid",studentInfo.getUserid()==null?studentInfo.getId():studentInfo.getUserid());
            XHttpUtils.getInstace().getValue(iShowConfig.searchByKeyWords, object, new XHttpUtils.OnHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    dialog.cancelDialog();
                    setData2UI(result);
                }

                @Override
                public void onError(String errorResson) {
                    dialog.cancelDialog();
                    ToastUtil.makeSnack(searchEditext,errorResson,SearchActivity.this);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setData2UI(String result) {
        if (result==null||result=="[]")
            return;
        if (list==null)
            list = new ArrayList<>();
        if (list!=null)list.clear();
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                UserEntry entry = new UserEntry();
                entry.setUserid(object.getString("uid"));
                entry.setImg(object.getString("userhead"));
                entry.setName(object.getString("username"));
                entry.setMobile(object.getString("mobile"));
                entry.setCampus(object.getString("school"));
               if (object.has("isFollow")){
                   entry.setIs_focus(object.getString("isFollow")=="1"?true:false);
               }else entry.setIs_focus(true);
                list.add(entry);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (searchAdapter==null)
        {
            searchAdapter = new SearchAdapter(this,list);
            listView.setAdapter(searchAdapter);
            searchAdapter.setOnSearchAdapterAdd2Fansinterface(this);
        }else searchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("user",list.get(position));
        Intent intent = new Intent(this,PersonalCenterActivity.class);
        intent.putExtras(bundle);
        ActivityCompat.startActivity(this,intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void add2Fan(final int position) {
        UserEntry entry = list.get(position);
        if (entry.is_focus())
            return;
        final JSONObject o1 = new JSONObject();
        try {
            dialog =new MaterialDialog();
            dialog.showDloag(this,getString(R.string.request_server));
            o1.put("masterID", entry.getUserid());
            o1.put("is_del", "n");
            o1.put("fanID", studentInfo.getUserid()==null?studentInfo.getId():studentInfo.getUserid());
            XHttpUtils.getInstace().getValue(iShowConfig.Follow, o1, new XHttpUtils.OnHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    dialog.cancelDialog();
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getInt("code")==1) {
                            searchAdapter.updateItem(position,true);
                        }else  ToastUtil.makeSnack(listView,object.getString("msg"),SearchActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String errorResson) {
                    dialog.cancelDialog();
                    ToastUtil.makeSnack(listView,errorResson,SearchActivity.this);
                }
            });
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }
}
