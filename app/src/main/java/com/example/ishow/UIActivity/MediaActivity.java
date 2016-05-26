package com.example.ishow.UIActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Fragment.VideoShowFragment;
import com.example.ishow.R;
import com.example.ishow.Utils.Interface.RequestPermissionInterface;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MRME on 2016-05-05.
 */
public class MediaActivity extends AppBaseCompatActivity {


    @Bind(R.id.Video_suggestion)
    RadioButton VideoSuggestion;
    @Bind(R.id.Video_new)
    RadioButton VideoNew;
    @Bind(R.id.Video_hot)
    RadioButton VideoHot;
    @Bind(R.id.course_title_name)
    TextView courseTitleName;
    @Bind(R.id.toolbar_search)
    ImageView toolbarSearch;
    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;
    @Bind(R.id.RadioGroup)
    RadioGroup VideoRadioGroup;
    private View rootView;
    VideoShowFragment kouce;
    VideoShowFragment yule;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getView(R.layout.activity_media);
        setContentView(rootView);
        ButterKnife.bind(this);
        fm = getSupportFragmentManager();
        showHideFragment(R.id.Video_suggestion);
        bindView2UI();


    }

    private void showHideFragment(int id) {
        ft = fm.beginTransaction();
        if (kouce != null)
            ft.hide(kouce);
        if (yule != null)
            ft.hide(yule);
        switch (id) {
            case R.id.Video_suggestion:
                if (kouce == null) {
                    kouce = new VideoShowFragment();
                    ft.add(R.id.media_container, kouce).show(kouce);
                    kouce.getDataFromServer(this,true,false,0);
                } else ft.show(kouce);

                break;
            case R.id.Video_hot:
                if (yule == null) {
                    yule = new VideoShowFragment();
                    ft.add(R.id.media_container, yule).show(yule);
                    yule.getDataFromServer(this,false,false,0);
                } else ft.show(yule);
                break;
        }
        ft.commit();
    }

    private void bindView2UI() {
        /**设置标题栏 返回按钮 标题  右侧上传视频图标*/
        setAppBaseCompactActivity(rootView);
        setToolbar(true, getString(R.string.shipinluzhi_title));
        toolbarSearch.setVisibility(View.VISIBLE);
        toolbarSearch.setImageResource(R.drawable.icon_shangchuanluzhi);
        VideoHot.setText("娱乐视频");
        VideoSuggestion.setText("口测视频");
        VideoNew.setVisibility(View.GONE);
        VideoRadioGroup.setVisibility(View.VISIBLE);
    }


    public void startActivityForMediaRecoder() {
        /**6.0权限检测*/
        checkPermissonForCamera(new RequestPermissionInterface() {
            @Override
            public void onPermissionRequestResult(boolean result, boolean first) {
                if (result)
                    ActivityCompat.startActivity(MediaActivity.this, new Intent(MediaActivity.this, MediaRecordActivity.class), ActivityOptionsCompat.makeCustomAnimation(MediaActivity.this, android.R.anim.slide_in_left, android.R.anim.slide_out_right).toBundle());
            }
        });
    }

    @OnClick({R.id.Video_suggestion, R.id.Video_hot, R.id.toolbar_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Video_suggestion:
                showHideFragment(view.getId());
                break;
            case R.id.Video_hot:
                showHideFragment(view.getId());
                break;
            case R.id.toolbar_search:
                startActivityForMediaRecoder();
                break;
        }
    }
}
