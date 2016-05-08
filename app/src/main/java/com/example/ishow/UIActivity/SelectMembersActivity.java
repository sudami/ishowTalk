package com.example.ishow.UIActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.example.ishow.BaseComponent.BaseCompactActivity;
import com.example.ishow.Fragment.SelectMembersFragment;
import com.example.ishow.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MRME on 2016-04-22.
 */
public class SelectMembersActivity extends BaseCompactActivity {

    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;
    @Bind(R.id.course_title_name)
    TextView courseTitleName;
    @Bind(R.id.RadioGroup)
    android.widget.RadioGroup RadioGroup;
    private SelectMembersFragment practiceFragment;
    private SelectMembersFragment collectionFragment;
    private SelectMembersFragment practiceGrade;
    private String courseId;

    private void showTab(int i) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        hideAllFragment(ft);
        switch (i) {
            case 0:
                if (practiceFragment != null) {
                    ft.show(practiceFragment);
                } else {
                    practiceFragment = new SelectMembersFragment();
                    ft.add(R.id.newFrameLayout, practiceFragment).show(practiceFragment);
                    practiceFragment.getClazStudent(this,false, true, courseId);
                }
                break;
            case 1:
                if (collectionFragment != null) {
                    ft.show(collectionFragment);
                } else {
                    collectionFragment = new SelectMembersFragment();
                    ft.add(R.id.newFrameLayout, collectionFragment).show(collectionFragment);
                    collectionFragment.getClazStudent(this,false, false, courseId);
                }
                break;
            case 2:
                if (practiceGrade != null) {
                    ft.show(practiceGrade);
                } else {
                    practiceGrade = new SelectMembersFragment();
                    ft.add(R.id.newFrameLayout, practiceGrade).show(practiceGrade);
                    practiceGrade.getClazStudent(this,true, true, courseId);
                }
                break;

            default:
                break;
        }
        ft.commit();
    }

    private void hideAllFragment(FragmentTransaction ft) {
        if (practiceFragment != null) {
            ft.hide(practiceFragment);
        }
        if (collectionFragment != null) {
            ft.hide(collectionFragment);
        }
        if (practiceGrade != null) {
            ft.hide(practiceGrade);
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectmembers);
        ButterKnife.bind(this);
        courseId = getIntent().getStringExtra("courseId");
        showTab(0);
        courseTitleName.setText(getString(R.string.choose_sudent));
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ActivityCompat.startActivity(SelectMembersActivity.this,new Intent(SelectMembersActivity.this, LoginActivity.class),null);
                ActivityCompat.finishAfterTransition(SelectMembersActivity.this);
            }
        });
        RadioGroup.setVisibility(View.VISIBLE);

    }

    @OnClick({R.id.newAll, R.id.newGrade, R.id.newFans})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newAll:
                showTab(0);
                break;
            case R.id.newGrade:
                showTab(1);
                break;
            case R.id.newFans:
                showTab(2);
                break;
        }
    }
}