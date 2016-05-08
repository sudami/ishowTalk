package com.example.ishow.UIActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.ishow.Adapter.FragmentTextRankAdapter;
import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Fragment.ClazFragment;
import com.example.ishow.R;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MRME on 2016-04-19.
 */
public class PracticeRankActivity extends AppBaseCompatActivity {
    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;
    boolean weekFlag = false;
    @Bind(R.id.course_title_name)
    TextView courseTitleName;
    @Bind(R.id.toolbar_edit)
    TextView toolbarEdit;
    @Bind(R.id.practice_tablayout)
    TabLayout practiceTablayout;
    @Bind(R.id.practice_pager)
    ViewPager practicePager;
    @Bind(R.id.AppBarLayout)
    android.support.design.widget.AppBarLayout AppBarLayout;
    private FragmentTextRankAdapter adpter;
    private ClazFragment fClaz;
    private ClazFragment fSchool;
    private ClazFragment fCountry;
    private ArrayList<Fragment> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practicerank);
        ButterKnife.bind(this);
        bindView2UI();
    }

    private void bindView2UI() {
        fClaz = new ClazFragment();
        fSchool = new ClazFragment();
        fCountry = new ClazFragment();
        list.add(fClaz);
        list.add(fSchool);
        list.add(fCountry);

        courseTitleName.setText(getString(R.string.rank_wenben_rank));
        toolbarEdit.setText(getString(R.string.rank_wenben_week));
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AppBarLayout.setBackgroundColor(getResources().getColor(R.color.rank_header_content_color));
        Toolbar.setBackgroundColor(getResources().getColor(R.color.rank_header_content_color));
        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(PracticeRankActivity.this);
            }
        });
        practiceTablayout.setTabMode(TabLayout.MODE_FIXED);
        practiceTablayout.addTab(practiceTablayout.newTab().setText(getString(R.string.rank_wenben_qaunguo)));
        practiceTablayout.addTab(practiceTablayout.newTab().setText(getString(R.string.rank_wenben_school)));
        practiceTablayout.addTab(practiceTablayout.newTab().setText(getString(R.string.rank_wenben_class)));
        practicePager.setOffscreenPageLimit(3);
        adpter = new FragmentTextRankAdapter(getSupportFragmentManager(), this, list);
        practicePager.setAdapter(adpter);
        practicePager.setCurrentItem(0);
        practiceTablayout.setupWithViewPager(practicePager);
        practiceTablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LogUtil.e(tab.getPosition() + "onTabSelected");
                practicePager.setCurrentItem(tab.getPosition());
                PracticeRankActivity.this.onTabSelected(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                practicePager.setCurrentItem(tab.getPosition());
                LogUtil.e(tab.getPosition() + "onTabReselected");
            }
        });
        onTabSelected(0);
    }

    private void onTabSelected(int position) {
        Fragment fClaz = list.get(position);
        ((ClazFragment) fClaz).bindData2UI(position, this, weekFlag);
    }

    @OnClick(R.id.toolbar_edit)
    public void onClick() {

        weekFlag = !weekFlag;
        adpter.setWeekFlag(weekFlag);
        toolbarEdit.setText(weekFlag ? getString(R.string.rank_wenben_today) : getString(R.string.rank_wenben_week));
        practicePager.setCurrentItem(0);
        Fragment fClaz = list.get(0);
        ((ClazFragment) fClaz).bindData2UI(0, this, weekFlag);
    }
}
