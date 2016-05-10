package com.example.ishow.UIActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Fragment.GalleryFragment;
import com.example.ishow.Fragment.VideoRecorderFragment;
import com.example.ishow.R;
import com.example.ishow.UIView.MediaRecorderBottomTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by MRME on 2016-05-05.
 */
public class MediaRecordActivity extends AppBaseCompatActivity {

    @Bind(R.id.media_recorder_shipin)
    MediaRecorderBottomTextView mediaRecorderShipin;
    @Bind(R.id.media_recorder_gallery)
    MediaRecorderBottomTextView mediaRecorderGallery;

    VideoRecorderFragment record;
    GalleryFragment gallery;

    FragmentManager fm;
    FragmentTransaction ft;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediarecorder);
        ButterKnife.bind(this);
        fm = getSupportFragmentManager();
        showHideFragment(R.id.media_recorder_shipin);
    }

    @OnClick({R.id.media_recorder_shipin, R.id.media_recorder_gallery})
    public void onClick(View view) {
        showHideFragment(view.getId());
    }

    private void showHideFragment(int id) {
        ft = fm.beginTransaction();
        if (record!=null)
            ft.hide(record);
        if (gallery!=null)
            ft.hide(gallery);
        switch (id)
        {
            case R.id.media_recorder_shipin:
                mediaRecorderShipin.setBottomLineVisibity(true);
                mediaRecorderGallery.setBottomLineVisibity(false);
                if (record==null)
                {
                    record=new VideoRecorderFragment();
                    ft.add(R.id.media_recorder_container,record).show(record);

                }else ft.show(record);
                break;
            case R.id.media_recorder_gallery:
                mediaRecorderShipin.setBottomLineVisibity(false);
                mediaRecorderGallery.setBottomLineVisibity(true);
                if (gallery==null)
                {
                    gallery=new GalleryFragment();
                    ft.add(R.id.media_recorder_container,gallery).show(gallery);

                }else ft.show(record);
                break;
        }
        ft.commit();

    }
}
