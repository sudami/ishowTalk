package com.example.ishow.UIActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.ishow.Adapter.PlaySpeedAdapter;
import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Bean.CourseEntry;
import com.example.ishow.Bean.MusicEntry;
import com.example.ishow.Bean.ShortCourseEntry;
import com.example.ishow.R;
import com.example.ishow.Service.MusicPlayService;
import com.example.ishow.UIView.CircleImageView;
import com.example.ishow.UIView.MaterialPopMenu;
import com.example.ishow.UIView.MaterialRecodDialog;
import com.example.ishow.UIView.MaterialSeekBar;
import com.example.ishow.Utils.Interface.MaterialPopInterface;
import com.example.ishow.Utils.Interface.RequestPermissionInterface;
import com.example.ishow.Utils.Interface.materialSeekbarAbClickListner;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.SoundMeter;
import com.example.ishow.Utils.ToastUtil;
import com.example.ishow.spinnerwheel.AbstractWheel;
import com.example.ishow.spinnerwheel.OnWheelScrollListener;
import com.example.ishow.spinnerwheel.WheelVerticalView;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by MRME on 2016-04-12.
 */
public class MusicPlayActivity extends AppBaseCompatActivity implements SeekBar.OnSeekBarChangeListener, materialSeekbarAbClickListner, View.OnTouchListener, AudioManager.OnAudioFocusChangeListener {
    @Bind(R.id.music_play_background)
    ImageView musicPlayBackground;
    @Bind(R.id.course_title_name)
    TextView courseTitleName;
    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;
    @Bind(R.id.music_play_avart)
    CircleImageView musicPlayAvart;
    @Bind(R.id.speed_number)
    WheelVerticalView speedNumber;
    @Bind(R.id.music_play_AB)
    ImageView musicPlayAB;
    @Bind(R.id.music_play_menu)
    ImageView musicPlayMenu;
    @Bind(R.id.music_play_curTime)
    TextView musicPlayCurTime;
    @Bind(R.id.MaterialSeekBar)
    MaterialSeekBar musicPlaySeekbar;
    @Bind(R.id.music_play_totalTime)
    TextView musicPlayTotalTime;
    @Bind(R.id.music_play_single_random)
    ImageView musicPlaySingleRandom;
    @Bind(R.id.music_play_recoder)
    ImageButton musicPlayRecoder;
    @Bind(R.id.music_play_play_pre)
    ImageView musicPlayPlayPre;
    @Bind(R.id.music_play_play_btn)
    ImageView musicPlayPlayBtn;
    @Bind(R.id.music_play_play_next)
    ImageView musicPlayPlayNext;

    private ServiceConnection musicServiceConnection = null;
    private MusicPlayService musicPlayService;
    private CourseEntry entry;
    private String title;
    private int courseId;
    private ArrayList<ShortCourseEntry> courseList;
    private String dir;
    private ShortCourseEntry shortCourse;
    private long playMode;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplay);

        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        bindView2UI();
    }

    private void bindView2UI() {
        startService();
        title = getIntent().getStringExtra("title");
        entry = getIntent().getParcelableExtra("courseEntry");
        boolean fromFragmentCourse = getIntent().hasExtra("fromFragmentCourse");
        dir = entry.getDirPath();
        courseId = Integer.valueOf(dir.substring(dir.lastIndexOf("/") + 1));
        //********************将正在播放的音乐对象保存起来************************************************///
        SharePrefrence.getInstance().putPlayingCourseIdAndDir(this,title,fromFragmentCourse?entry.getContent():entry.getTitle(),courseId,dir);
        courseList = SharePrefrence.getInstance().getCourseList(getApplicationContext(), courseId);
        musicPlaySeekbar.setABImageView(musicPlayAB);
        musicPlayAB.setImageResource(R.mipmap.ab);
        musicPlayAvart.setImageResource(R.mipmap.bg);
        //******************************设置WheelVerticalView速度选择器*********************************//
        speedNumber.setVisibleItems(4);
        speedNumber.setViewAdapter(new PlaySpeedAdapter(this));
        speedNumber.setCurrentItem(1);
        speedNumber.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {

            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
                wheel.getCurrentItem();
                if (musicPlayService!=null){
                    musicPlayService.changedMP3Rate(MusicPlayActivity.this, wheel.getCurrentItem());
                }
            }
        });

        //*****************************设置Toolbar透明 **************************************************//
        setSupportActionBar(Toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar.setBackgroundColor(Color.TRANSPARENT);
        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(MusicPlayActivity.this);
            }
        });
        if (courseList!=null){
            if(courseList.size()>0){
                shortCourse = courseList.get(0);
                courseTitleName.setText(this.title + "-" + shortCourse.getTitle());
            }
        }else  courseTitleName.setText(this.title);
        //*************************设置音频播放模式******************************************************//
        playMode = SharePrefrence.getInstance().getPlayMode(getApplicationContext());
        setPlayerMode();
        musicPlaySeekbar.setOnSeekBarChangeListener(this);
        musicPlaySeekbar.addOnMaterialSeekbarAbClickListner(this);
        musicPlayRecoder.setOnTouchListener(this);
        musicPlayRecoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("11111");
            }
        });
    }

    private void setPlayerMode() {
        //
        if (playMode == 0) musicPlaySingleRandom.setImageResource(R.mipmap.iconfont_danquxunhuan);
        else if (playMode == 1) musicPlaySingleRandom.setImageResource(R.mipmap.icon_shunxubofang);
        else if (playMode == 2) musicPlaySingleRandom.setImageResource(R.mipmap.icon_suijibofang);
        else musicPlaySingleRandom.setImageResource(R.mipmap.iconfont_danquxunhuan);
    }

    @OnClick({R.id.music_play_single_random, R.id.music_play_recoder, R.id.music_play_play_pre, R.id.music_play_play_btn, R.id.music_play_play_next, R.id.music_play_menu})
    public void onClick(View view) {
        switch (view.getId()) {
            //单曲循环  随机 顺序
            case R.id.music_play_single_random:
                if (playMode == 0) {
                    playMode++;
                    SharePrefrence.getInstance().putPlayMode(getApplicationContext(), 1);
                } else if (playMode == 1) {
                    playMode++;
                    SharePrefrence.getInstance().putPlayMode(getApplicationContext(), 2);
                } else if (playMode == 2) {
                    playMode = 0;
                    SharePrefrence.getInstance().putPlayMode(getApplicationContext(), 0);
                }
                setPlayerMode();
                break;
            //录音
            case R.id.music_play_recoder:
                cancelAvartAnimation();
                break;
            //菜单
            case R.id.music_play_menu:
                showPopMenu();
                break;
            //前一曲
            case R.id.music_play_play_pre:
                if (musicPlayService != null) {
                    cancelAvartAnimation();
                    musicPlayService.prePlay();
                }
                speedNumber.setCurrentItem(1);
                break;
            //播放 暂停
            case R.id.music_play_play_btn:
                // startService();
                if (musicPlayService != null) {
                    cancelAvartAnimation();
                    musicPlayService.pausePlay();
                }
                break;
            //后一曲
            case R.id.music_play_play_next:
                if (musicPlayService != null) {
                    musicPlayService.nextPlay();
                    cancelAvartAnimation();
                }
                speedNumber.setCurrentItem(1);
                break;
        }
    }

    private void showPopMenu() {
        MaterialPopMenu popMenu = new MaterialPopMenu(getApplicationContext(), courseList);
        musicPlayMenu.setImageResource(R.drawable.iconfont_liebiao_red);
        popMenu.showMenu(musicPlayMenu, title, musicPlayService.getCurPlayIndex());
        popMenu.setOnPopMenuItemClickListener(new MaterialPopInterface() {
            @Override
            public void onPopMenuItemClick(String popTile, int position) {
                courseTitleName.setText(title + "-" + popTile);
                musicPlayService.startNewMusic(position);
            }
        });
        //  musicPlayService.changedMP3Rate(3);
    }

    //音频播放 暂停
    private void startService() {
        if (musicPlayService == null) {
            newMusicServiceConnection();
            Intent intent = new Intent(this, MusicPlayService.class);
            startService(intent);
            bindService(intent, musicServiceConnection, BIND_AUTO_CREATE);
        }
    }

    private void newMusicServiceConnection() {
        musicServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicPlayService = ((MusicPlayService.playService) service).getInstance();
                musicPlayService.startPlay(MusicPlayActivity.this, dir, courseId);

            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicPlayService = null;
                musicServiceConnection = null;
            }
        };
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void musicPlayStae(MusicEntry state) {
        switch (state.getState()) {
            //缓冲
            case 0:
                break;
            //播放
            case 1:
                //musicPlayCurTime.sett
                musicPlayPlayBtn.setImageResource(R.mipmap.icon_zanting);
                musicPlayCurTime.setText(state.getHasTime());
                musicPlayTotalTime.setText(state.getTotalTime());
                musicPlaySeekbar.setProgress((int) (state.getProgress() * 100));
                startAvartAnimation(state.getAnimationTime());
                break;
            //暂停
            case 2:
                cancelAvartAnimation();
                musicPlayPlayBtn.setImageResource(R.mipmap.icon_bofang);
                break;
            //恢复
            case 3:
                break;
            // seekto
            case 4:

                break;
            //error
            case 5:
                cancelAvartAnimation();
                break;
        }
    }

    private void cancelAvartAnimation() {
        musicPlayAvart.clearAnimation();
    }

    private void startAvartAnimation(int animationTime) {
        Animation animation = musicPlayAvart.getAnimation();
        if (animation == null) {
            animation = AnimationUtils.loadAnimation(this, R.anim.music_play_avart_roateanim);
            animation.setRepeatMode(Animation.REVERSE);
            animation.setFillAfter(false);
            animation.setDuration(animationTime);
            musicPlayAvart.startAnimation(animation);
        }
    }

    @Override
    protected void onDestroy() {
        if (musicPlayService != null)
            unbindService(musicServiceConnection);
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
        if (dialog != null)
            dialog.cancelDialog();
        if (manager!=null)
            manager.abandonAudioFocus(this);
        super.onDestroy();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (musicPlayService != null) {
            musicPlayService.pauseByseekbar();
            cancelAvartAnimation();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        LogUtil.e(seekBar.getProgress() + "----");
        if (musicPlayService != null) {
            boolean iStart = musicPlayService.startByseekbar((float) seekBar.getProgress() / seekBar.getMax());
            if (!iStart) musicPlaySeekbar.setProgress(0);
        }
    }

    int progressA = 0;

    @Override
    public void drawTextA(int progress) {
        this.progressA = progress;
    }

    int progressB = 0;

    @Override
    public void drawTextB(int progress) {
        this.progressB = progress;

        if (musicPlayService != null) {
            boolean abRepeat = musicPlayService.startAbRepeat(progressA, progressB);
            LogUtil.e("--drawTextB--"+abRepeat);
            if (!abRepeat) {
                musicPlaySeekbar.startClearCanvas();
                cancelAvartAnimation();
            }
        }
    }


    @Override
    public void clear() {
        progressA = 0;
        progressB = 0;
        if (musicPlayService!=null)
            musicPlayService.cancelAbRepeat();
    }

    SoundMeter soundMeter;
    MaterialRecodDialog dialog;
    long startRecordeTime;
    int[] location = new int[2];
    String timeTips;
    AudioManager manager;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            double amp = soundMeter.getAmplitude();
            int l = (int) ((System.currentTimeMillis() - startRecordeTime) / 1000);
            LogUtil.e(l+"----"+System.currentTimeMillis()+"---"+startRecordeTime);
            if (l < 50) {
                timeTips = l + "s";
                dialog.updateView(timeTips, amp);
                handler.postDelayed(runnable, 300);
            }
            else if (l >= 50 && l < 60) {
                timeTips = String.format(getResources().getString(R.string.time_left), (60 - l));
                dialog.updateView(timeTips, amp);
                handler.postDelayed(runnable, 300);
            } else {
                cancelRecorder();
            }
            LogUtil.e(amp + "handleMessage");

        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            handler.sendEmptyMessage(0);
        }
    };


    boolean isOntouch =false;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isOntouch =true;
                initRecord(v);
                break;
            case MotionEvent.ACTION_MOVE:
                //判断是否在范围内
               /* if (event.getX()<location[0] && event.getY() < +location[1]) {
                  //  LogUtil.e("是");
                } else cancelRecorder();*/
                break;
            case MotionEvent.ACTION_UP:
                cancelRecorder();

                break;
        }
        LogUtil.e(event.getX()+"--"+event.getY()+"--"+location[0]+"--"+location[1]);
        return true;
    }

    private void cancelRecorder() {
        isOntouch =false;
        handler.removeCallbacks(runnable);
        if (soundMeter!=null) soundMeter.stop();
        soundMeter =null;
        if (dialog!=null)
            dialog.showRecordeCompleteUI(courseTitleName.getText().toString());

    }

    private void initRecord(final View v) {
        checkPermissonForRecord(new RequestPermissionInterface() {
            @Override
            public void onPermissionRequestResult(boolean result,boolean first) {
                if (result){
                    if (isOntouch){
                        manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                        manager.requestAudioFocus(MusicPlayActivity.this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
                        if (dialog == null) dialog = new MaterialRecodDialog();
                        if (soundMeter == null) soundMeter = new SoundMeter();
                        dialog.showDialog(MusicPlayActivity.this);
                        boolean start = soundMeter.start(courseTitleName.getText().toString()+".amr");
                        if (!start) {
                            dialog.cancelDialog();
                            ToastUtil.makeSnack(v, getString(R.string.no_storage_permission), MusicPlayActivity.this);
                        } else {
                            //soundMeter.start();
                            startRecordeTime = System.currentTimeMillis();
                            handler.postDelayed(runnable, 300);
                        }
                    }
                }else{
                    if (!result)ToastUtil.makeSnack(v, getString(R.string.no_record_permission), MusicPlayActivity.this);
                }
            }
        });
    }

    /* manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                           manager.requestAudioFocus(MusicPlayActivity.this,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
                           if (dialog == null) dialog = new MaterialRecodDialog();
                           if (soundMeter == null) soundMeter = new SoundMeter();
                           dialog.showDialog(MusicPlayActivity.this);
                           boolean start = soundMeter.start(courseTitleName.getText().toString()+".amr");
                           if (!start) {
                               dialog.cancelDialog();
                               ToastUtil.makeSnack(v, getString(R.string.no_storage_permission), MusicPlayActivity.this);
                           } else {
                               //soundMeter.start();
                               startRecordeTime = System.currentTimeMillis();
                               handler.postDelayed(runnable, 300);
                           }
                       }*/
    @Override
    public void onAudioFocusChange(int focusChange) {

    }
}
