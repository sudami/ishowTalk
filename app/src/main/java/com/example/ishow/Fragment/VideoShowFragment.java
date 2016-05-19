package com.example.ishow.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by MRME on 2016-05-10.
 */
public class VideoShowFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
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
}
