package com.example.ishow.UIActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.R;

import butterknife.ButterKnife;


/**
 * Created by MRME on 2016-05-05.
 */
public class MediaRecordActivity extends AppBaseCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediarecorder);
        ButterKnife.bind(this);
    }
}
