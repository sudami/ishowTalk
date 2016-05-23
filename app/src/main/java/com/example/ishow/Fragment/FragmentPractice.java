package com.example.ishow.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ishow.Bean.CourseEntry;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.UIActivity.SelectMembersActivity;
import com.example.ishow.UIView.MaterialDialog;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.ToastUtil;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;
import com.example.ishow.justalk.cloud.juscall.MtcCallDelegate;
import com.justalk.cloud.lemon.MtcMdm;
import com.justalk.cloud.lemon.MtcMediaConstants;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FragmentPractice extends BaseFragment implements
        OnClickListener, OnItemClickListener {


   // ImageView practiceGroup;//群组对练
    public QuickAdapater adpater;
    public ArrayList<CourseEntry> list = new ArrayList<CourseEntry>();
    public boolean shouldAll = false;

    public static int selectPosition = 0;
    public MaterialDialog dialog = null;
    Button btn;
    @Bind(R.id.kecheng)
    TextView kecheng;
    @Bind(R.id.primary)
    TextView primary;
    @Bind(R.id.middle)
    TextView middle;
    @Bind(R.id.quick_high)
    TextView quickHigh;
    @Bind(R.id.chuzhonggao)
    LinearLayout chuzhonggao;
    @Bind(R.id.quick_high2)
    TextView high2;
    @Bind(R.id.quick_video)
    TextView quickVideo;
    @Bind(R.id.quick_video2)
    TextView video2;
    @Bind(R.id.gaojiyingshi)
    LinearLayout gaojiyingshi;
    @Bind(R.id.keshi)
    TextView keshi;
    @Bind(R.id.quick_item_title)
    TextView quickItemTitle;
    @Bind(R.id.quick_item_name)
    TextView quickItemName;
    @Bind(R.id.quick_item)
    LinearLayout quickItem;
    @Bind(R.id.quick_grid)
    GridView quickGrid;
    @Bind(R.id.quick_kuaisu)
    ImageView quickKuaisu;
    @Bind(R.id.quick_group)
    ImageView quickGroup;
    @Bind(R.id.quick_layout)
    LinearLayout quickLayout;

    Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = getView(R.layout.fragment_practice);
        ButterKnife.bind(this, rootView);
        mContext = getActivity();
        setOnClickListner();
        getPerCourseList(37);
        primary.setTextColor(Color.WHITE);
        primary.setBackgroundResource(R.drawable.quic_griditem_selector_left);
        return rootView;
    }

    private void setOnClickListner() {
        primary.setOnClickListener(this);
        middle.setOnClickListener(this);
        quickHigh.setOnClickListener(this);
        high2.setOnClickListener(this);
        quickVideo.setOnClickListener(this);
        video2.setOnClickListener(this);
        quickKuaisu.setOnClickListener(this);
        quickGrid.setOnItemClickListener(this);
        quickGroup.setOnClickListener(this);
    }

    // 37 38 39 47 48 79 �� �� �� ��2.0 Ӱ�� Ӱ��2.0
    private void getPerCourseList(int courseId) {

        try {
            showDialog();
            JSONObject j = new JSONObject();
            j.put("kecheng", courseId);
            XHttpUtils.getInstace().getValue(iShowConfig.QUICK_PRACTICE, j, new XHttpUtils.OnHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    setData2UIByResult(result);
                }

                @Override
                public void onError(String errorResson) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setData2UIByResult(String result) {
        if (result != "") {
            try {
                list.clear();
                JSONArray array = new JSONArray(result);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    CourseEntry entry = new CourseEntry();
                    entry.setTitle(object.getString("title"));
                    entry.setContent("第 " + (i + 1) + "课");
                    entry.setId(Integer.parseInt(object.getString("id")));
                    list.add(entry);
                    dialog.cancelDialog();
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

            if (adpater == null) {
                adpater = new QuickAdapater();
                quickGrid.setAdapter(adpater);


            } else {
                adpater.notifyDataSetChanged();
            }
        } else {
            ToastUtil.makeSnack(quickGrid, "暂时没有可学习的课程", getActivity());
        }
    }

    public int shouldShowCurouse = 15;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class QuickAdapater extends BaseAdapter {

        class ViewHolder {
            public TextView name;
            public TextView title;
            public LinearLayout layout;
            public LinearLayout layoutItem;
        }

        @Override
        public int getCount() {

            if (!shouldAll) {
                return shouldShowCurouse;
            }
            return list.size();
        }

        @Override
        public Object getItem(int position) {

            return list.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getActivity(),
                        R.layout.fragment_banner_grid_item, null);
                holder.title = (TextView) convertView
                        .findViewById(R.id.quick_item_title);
                holder.name = (TextView) convertView
                        .findViewById(R.id.quick_item_name);
                holder.layout = (LinearLayout) convertView
                        .findViewById(R.id.quick_item_more);
                holder.layoutItem = (LinearLayout) convertView
                        .findViewById(R.id.quick_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            if (position == selectPosition) {
                holder.name.setTextColor(Color.WHITE);
                holder.title.setTextColor(Color.WHITE);
                holder.layoutItem.setBackgroundResource(R.drawable.quick_griditem_selector);
            } else {
                holder.name.setTextColor(ContextCompat.getColor(mContext,R.color.secondary_text_color));
                holder.title.setTextColor(ContextCompat.getColor(mContext,R.color.first_text_color));
                holder.layoutItem.setBackgroundResource(R.drawable.quick_practice_bg);
            }

            if (!shouldAll) {
                if (position != shouldShowCurouse - 1) {
                    CourseEntry recent = list.get(position);
                    holder.name.setText(recent.getTitle());
                    holder.title.setText(recent.getContent());
                } else {
                    holder.layoutItem.setVisibility(View.GONE);
                    holder.layout.setVisibility(View.VISIBLE);
                }
            } else {
                CourseEntry recent = list.get(position);
                holder.name.setText(recent.getTitle());
                holder.title.setText(recent.getContent());
                holder.layoutItem.setVisibility(View.VISIBLE);
                holder.layout.setVisibility(View.GONE);
            }

            return convertView;
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.quick_kuaisu:
                startQuickPractice();
                break;
            case R.id.primary:
                shouldAll = false;
                getPerCourseList(37);
                resetCourseState();
                ((TextView) v).setTextColor(Color.WHITE);
                v.setBackgroundResource(R.drawable.quic_griditem_selector_left);
                break;
            case R.id.middle:
                shouldAll = false;
                getPerCourseList(38);
                resetCourseState();
                ((TextView) v).setTextColor(Color.WHITE);
                v.setBackgroundResource(R.drawable.quick_griditem_selector_rectangle);
                break;

            case R.id.quick_high:
                shouldAll = false;
                getPerCourseList(39);
                resetCourseState();
                ((TextView) v).setTextColor(Color.WHITE);
                v.setBackgroundResource(R.drawable.quick_griditem_selector_right);
                break;
            case R.id.quick_high2:
                getPerCourseList(47);
                resetCourseState();
                ((TextView) v).setTextColor(Color.WHITE);
                v.setBackgroundResource(R.drawable.quic_griditem_selector_left);
                break;
            case R.id.quick_video:
                shouldAll = false;
                getPerCourseList(48);
                resetCourseState();
                ((TextView) v).setTextColor(Color.WHITE);
                v.setBackgroundResource(R.drawable.quick_griditem_selector_rectangle);
                break;
            case R.id.quick_video2:
                ToastUtil.makeSnack(v, "该课程暂时未开放", mContext);
                break;

            //群组对练
            case R.id.quick_group:

                if (SharePrefrence.getInstance().getStudentInfo(mContext) == null) {
                    ToastUtil.makeSnack(quickGrid, getString(R.string.history_NO_login), mContext);
                    break;
                }
                quickGroup.setBackgroundResource(R.drawable.icon_practice_group);
                Intent intent = new Intent(mContext, SelectMembersActivity.class);
                intent.putExtra("courseId", list.get(selectPosition).getId()+"");
                startActivity(intent);
                break;
        }
    }

    private void startQuickPractice() {
        if (SharePrefrence.getInstance().getStudentInfo(mContext) == null) {
            ToastUtil.makeSnack(quickGrid, getString(R.string.history_NO_login), mContext);
            return;
        }
            try {
                showDialog();
                JSONObject object = new JSONObject();
                object.put("CourseId", list.get(selectPosition).getId());
                XHttpUtils.getInstace().getValue(iShowConfig.QUICK_PRACTICE_PERSONINFO, object, new XHttpUtils.OnHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        if (result != null) {
                            try {
                                JSONObject object = new JSONObject(result);
                                UserEntry entry = new UserEntry();
                                entry.setUserid(object
                                        .getString("id"));
                                entry.setImg(object
                                        .getString("img"));
                                entry.setName(object
                                        .getString("name"));
                                entry.setMobile(object
                                        .getString("mobile"));
                                dialog.cancelDialog();

                                startCallactivity(entry);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtil.makeSnack(quickGrid, "暂时没有与你相符的学员!", mContext);
                        }
                    }
                    @Override
                    public void onError(String errorResson) {
                        ToastUtil.makeSnack(quickGrid, errorResson, mContext);
                        LogUtil.e(errorResson);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    private void showDialog() {
        if (dialog == null) {
            dialog = new MaterialDialog();
        }
        dialog.showDloag(mContext, getString(R.string.request_server));
    }

    private void startCallactivity(UserEntry recentMsg) {
        MtcMdm.Mtc_MdmAnSetBitrateMode(MtcMediaConstants.EN_MTC_AN_HIGH);//recentMsg.getMobile()
        MtcCallDelegate.call(recentMsg.getMobile(), makeIhsowUserRankCallJosnObject(),
                makeIshowRecentCallJosnObject(recentMsg), true);
    }
    private String makeIhsowUserRankCallJosnObject() {
        UserEntry localRank = SharePrefrence.getInstance().getStudentInfo(mContext);
        JSONObject local = new JSONObject();
        try {
            local.put("username", localRank.getName());
            local.put("userimg", localRank.getImg());
            local.put("userphone", localRank.getMobile());
            local.put("userid", localRank.getUserid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return local.toString();
    }

    private String makeIshowRecentCallJosnObject(UserEntry recentMsg) {
        JSONObject local = new JSONObject();
        try {
            local.put("username", recentMsg.getName());
            local.put("userimg", recentMsg.getImg());
            local.put("userphone", recentMsg.getMobile());
            local.put("userid", recentMsg.getUserid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return local.toString();
    }

    private void resetCourseState() {
        primary.setBackgroundColor(Color.TRANSPARENT);
        middle.setBackgroundColor(Color.TRANSPARENT);
        quickHigh.setBackgroundColor(Color.TRANSPARENT);
        high2.setBackgroundColor(Color.TRANSPARENT);
        quickVideo.setBackgroundColor(Color.TRANSPARENT);
        video2.setBackgroundColor(Color.TRANSPARENT);
        primary.setTextColor(Color.BLACK);
        middle.setTextColor(Color.BLACK);
        quickHigh.setTextColor(Color.BLACK);
        high2.setTextColor(Color.BLACK);
        quickVideo.setTextColor(Color.BLACK);
        video2.setTextColor(Color.BLACK);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {


        selectPosition = position;
        quickGrid.smoothScrollToPosition(position);
        adpater.notifyDataSetChanged();
        quickKuaisu.setBackgroundResource(R.drawable.icon_practice_suiji_white);
        quickGroup.setBackgroundResource(R.drawable.icon_practice_group_white);
        if (!shouldAll && position == shouldShowCurouse - 1) {
            shouldAll = true;
            adpater.notifyDataSetChanged();
            quickGrid.smoothScrollToPosition(shouldShowCurouse - 1);
        }
    }

    @Override
    public void onResume() {
        MobclickAgent.onPageStart("首页_对练_resume");
        super.onResume();
    }

    @Override
    public void onPause() {
        MobclickAgent.onPageStart("首页_对练_onPause");
        super.onPause();
    }
}
