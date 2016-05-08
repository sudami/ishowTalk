package com.example.ishow.Fragment;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.ishow.Bean.CourseEntry;
import com.example.ishow.MainActivity;
import com.example.ishow.R;
import com.example.ishow.Service.MusicPlayService;
import com.example.ishow.Service.StartServiceReceiver;
import com.example.ishow.UIActivity.MusicPlayActivity;
import com.example.ishow.UIView.NoScrollGridView;
import com.example.ishow.Utils.Interface.TextUtil;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.ViewFlow.CircleFlowIndicator;
import com.example.ishow.ViewFlow.ExtendViewFlow;
import com.example.ishow.iShowConfig.iShowConfig;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MRME on 2016-03-28.
 */
public class FragmentCourse extends BaseFragment {


    @Bind(R.id.fragment_course_music_controller)
    RelativeLayout fragmentCourseMusicController;
    @Bind(R.id.viewflow_banner)
    ExtendViewFlow viewflowBanner;
    @Bind(R.id.viewflowindic_banner)
    CircleFlowIndicator viewflowIndicator;
    @Bind(R.id.fragment_course_NoScrollGridView)
    NoScrollGridView NoScrollGridView;
    @Bind(R.id.scrollView)
    ScrollView scrollView;

    public Context context;
    @Bind(R.id.shoye_playing)
    ImageView shoyePlaying;
    @Bind(R.id.shoye_text)
    TextView shoyeText;
    @Bind(R.id.shouye_bofang)
    ImageView shouyeBofang;
    @Bind(R.id.shouye_next)
    ImageView shouyeNext;
    @Bind(R.id.shoye_enter)
    ImageView shoyeEnter;
    private ServiceConnection musicServiceConnection;
    private MusicPlayService musicPlayService;
    private CourseEntry lastPlayedCourseEntry;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = getView(R.layout.fragment_course);
        context = getActivity();
        ButterKnife.bind(this, rootView);
        NoScrollGridView.initData(context);
        viewflowBanner.initData(context, viewflowBanner, viewflowIndicator);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        }, 1);

        Intent intent = new Intent(context, MusicPlayService.class);
        context.startService(intent);
        context.startService(intent);
        context.bindService(intent, newMusicServiceConnection(), Context.BIND_AUTO_CREATE);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.unbindService(musicServiceConnection);
        ButterKnife.unbind(this);
    }
    private ServiceConnection newMusicServiceConnection() {
        musicServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicPlayService = ((MusicPlayService.playService) service).getInstance();
                /*int courseId = SharePrefrence.getInstance().getPlayingCourseId(context);
                String courseDir = SharePrefrence.getInstance().getPlayingCourseDir(context);
                String text = SharePrefrence.getInstance().getPlayingCourseText(context);
                if (!TextUtils.equals(text,"")&&!TextUtils.equals(courseId+"",0+"")&&!TextUtils.equals(courseDir,"")){
                    fragmentCourseMusicController.setVisibility(View.VISIBLE);
                }else  fragmentCourseMusicController.setVisibility(View.GONE);*/
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicPlayService = null;
                musicServiceConnection = null;
            }
        };
        return musicServiceConnection;
    }

    @OnClick({R.id.shoye_playing, R.id.shouye_bofang, R.id.shouye_next, R.id.fragment_course_music_controller})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.shoye_playing:

                break;
            case R.id.shouye_bofang:
                if (musicPlayService!=null)
                {
                    if (musicPlayService.isPlaying()){
                        musicPlayService.pausePlay();
                        shouyeBofang.setImageResource(R.mipmap.shouye_play);
                    }else {

                        musicPlayService.startPlayByShouye(context,lastPlayedCourseEntry.getDirPath(),lastPlayedCourseEntry.getId());
                        shouyeBofang.setImageResource(R.mipmap.shouye_pause);
                    }
                }
                break;
            case R.id.shouye_next:
                if (musicPlayService!=null) {
                    musicPlayService.nextPlay();
                    shouyeBofang.setImageResource(R.mipmap.shouye_pause);
                }
                break;
            case R.id.fragment_course_music_controller:
                if (lastPlayedCourseEntry!=null)
                {
                    Intent intent = new Intent(context,MusicPlayActivity.class);
                    intent.putExtra("title",lastPlayedCourseEntry.getTitle());
                    intent.putExtra("fromFragmentCourse",true);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("courseEntry",lastPlayedCourseEntry);
                    intent.putExtras(bundle);
                    ActivityCompat.startActivity((MainActivity)context,intent, ActivityOptionsCompat.makeSceneTransitionAnimation(((MainActivity)context)).toBundle());
                }
                break;
        }
    }

    @Override
    public void onResume() {
        lastPlayedCourseEntry = SharePrefrence.getInstance().getLastPlayedCourseEntry(context);
        fragmentCourseMusicController.setVisibility(lastPlayedCourseEntry.getId()!=0?View.VISIBLE:View.GONE);
        if (lastPlayedCourseEntry.getId()!=0)
        {
            shoyeText.setText(lastPlayedCourseEntry.getTitle()+"-"+lastPlayedCourseEntry.getContent());
            if (musicPlayService!=null)
            {
                if (musicPlayService.isPlaying()){
                      shouyeBofang.setImageResource(R.mipmap.shouye_pause);
                }else shouyeBofang.setImageResource(R.mipmap.shouye_play);
            }else  shouyeBofang.setImageResource(R.mipmap.shouye_play);
        }
        super.onResume();
    }
}
