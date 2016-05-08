package com.example.ishow.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ishow.Application.iShowTalkApplication;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.MainActivity;
import com.example.ishow.R;
import com.example.ishow.UIActivity.FansActivity;
import com.example.ishow.UIActivity.LocalCourseActivity;
import com.example.ishow.UIActivity.PersonalCenterActivity;
import com.example.ishow.UIActivity.PracticeHistoryActivity;
import com.example.ishow.UIActivity.PracticeRankActivity;
import com.example.ishow.UIActivity.RecordeActivity;
import com.example.ishow.UIActivity.SettingActivity;
import com.example.ishow.UIActivity.TestBasicVideo;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.TimeUtil;

import org.xutils.common.util.LogUtil;
import org.xutils.x;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MRME on 2016-03-28.
 */
public class FragmentMe extends BaseFragment {
    @Bind(R.id.self_avart)
    ImageView selfAvart;
    @Bind(R.id.self_name)
    TextView selfName;
    @Bind(R.id.self_school)
    TextView selfSchool;
    @Bind(R.id.self_TimeStudy)
    TextView selfTimeStudy;
    @Bind(R.id.wenbenLayout)
    RelativeLayout wenbenLayout;
    @Bind(R.id.shipinLayout)
    RelativeLayout shipinLayout;
    @Bind(R.id.jiluLayout)
    RelativeLayout jiluLayout;
    @Bind(R.id.xiazaiLayout)
    RelativeLayout xiazaiLayout;
    @Bind(R.id.shezhiLayout)
    RelativeLayout shezhiLayout;
    @Bind(R.id.fans_layout)
    LinearLayout fansLayout;
    @Bind(R.id.luyin_layout)
    LinearLayout luyinLayout;
    MainActivity context;
    @Bind(R.id.self_guanzhu)
    TextView selfGuanzhu;
    @Bind(R.id.self_luyin)
    TextView selfLuyin;
    @Bind(R.id.self_layout)
    RelativeLayout selfLayout;
    private UserEntry studentInfo;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = getView(R.layout.fragment_me);
        ButterKnife.bind(this, rootView);

        bindData2UI();
        return rootView;
    }

    private void bindData2UI() {
        context = (MainActivity)getActivity();
        studentInfo = SharePrefrence.getInstance().getStudentInfo(context.getApplicationContext());
        if (studentInfo != null) {
            x.image().bind(selfAvart, studentInfo.getImg(), iShowTalkApplication.getInstance().getIgetImageOptions(context, 80, 8));
            selfName.setText(studentInfo.getName());
            selfSchool.setText(getString(R.string.personal_from) + studentInfo.getCampus());
            selfTimeStudy.setText(studentInfo.getTimelong() != null ? TimeUtil.getHourAndMin(Long.parseLong(studentInfo.getTimelong())) : "0 Min");
            selfGuanzhu.setText(studentInfo.getFans()+"");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.self_layout,R.id.wenbenLayout, R.id.shipinLayout, R.id.jiluLayout, R.id.xiazaiLayout, R.id.shezhiLayout, R.id.fans_layout, R.id.luyin_layout})
    public void onClick(View view) {
        ActivityOptionsCompat optionsCompat =null;
        Intent intent =null;
        switch (view.getId()) {
            case R.id.self_layout:
                Bundle bundle = new Bundle();
                bundle.putParcelable("user",studentInfo);
                intent = new Intent(context, PersonalCenterActivity.class);
                intent.putExtras(bundle);
                ActivityCompat.startActivityForResult(context,intent,101 , ActivityOptionsCompat.makeSceneTransitionAnimation(context).toBundle());
                break;
            case R.id.wenbenLayout:
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, rootView.findViewById(R.id.icon_wenbenduilian_text), "transitionName");
                intent = new Intent(context, PracticeRankActivity.class);
                ActivityCompat.startActivity(context,intent, optionsCompat.toBundle());
                break;
            case R.id.shipinLayout:
                //*********************************************************视频录制界面*************************************************//
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, rootView.findViewById(R.id.icon_shipinduilian_text), "transitionName");
                intent = new Intent(context, PracticeRankActivity.class);
                ActivityCompat.startActivity(context,intent, optionsCompat.toBundle());
                break;
            case R.id.jiluLayout:
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, rootView.findViewById(R.id.icon_duilianjilu_text), "transitionName");
                intent = new Intent(context, PracticeHistoryActivity.class);
                ActivityCompat.startActivity(context,intent, optionsCompat.toBundle());
                break;
            case R.id.xiazaiLayout:
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, rootView.findViewById(R.id.icon_xiazai_text), "transitionName");
                intent = new Intent(context, LocalCourseActivity.class);
                ActivityCompat.startActivity(context,intent, optionsCompat.toBundle());
                break;
            case R.id.shezhiLayout:
               /* intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                startActivityForResult(intent, 1000);*/
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, rootView.findViewById(R.id.icon_shezhi_text), "transitionName");
                intent = new Intent(context, TestBasicVideo.class);
                ActivityCompat.startActivity(context,intent, optionsCompat.toBundle());
                break;
            case R.id.fans_layout:
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, rootView.findViewById(R.id.fans_layout), "transitionName");
                intent = new Intent(context, FansActivity.class);
                ActivityCompat.startActivityForResult(context,intent,1, optionsCompat.toBundle());
                break;
            case R.id.luyin_layout:
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(context, rootView.findViewById(R.id.luyin_layout), "transitionName");
                intent = new Intent(context, RecordeActivity.class);
                ActivityCompat.startActivityForResult(context,intent,2, optionsCompat.toBundle());
                break;
        }
    }
    @Override
    public void onResume() {
        selfGuanzhu.setText(SharePrefrence.getInstance().getFansSize(getActivity()));
        selfLuyin.setText(SharePrefrence.getInstance().getAudioSize(getActivity()));
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101)
            bindData2UI();
        /*Uri uriVideo = data.getData();
        Cursor cursor=context.getContentResolver().query(uriVideo, null, null, null, null);
        if (cursor.moveToNext()) {
                                         *//* _data：文件的绝对路径 ，_display_name：文件名 *//*
            String strVideoPath = cursor.getString(cursor.getColumnIndex("_data"))+cursor.getString(cursor.getColumnIndex("_display_name"));
            Toast.makeText(context, strVideoPath, Toast.LENGTH_SHORT).show();
        }*/
    }
}
