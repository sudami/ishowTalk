package com.example.ishow.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Bean.MusicEntry;
import com.example.ishow.R;
import com.example.ishow.UIView.MaterialDialog;
import com.example.ishow.Utils.ChatManager;
import com.example.ishow.Utils.Interface.RequestPermissionInterface;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.StorageUtils;
import com.example.ishow.Utils.ToastUtil;
import com.max.demo.MixManager;

import org.tecunhuman.AndroidJNI;
import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import de.greenrobot.event.EventBus;

/**
 * Created by MRME on 2016-04-13.
 */
public class MusicPlayService extends Service implements MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {


    private ArrayList<String> mPaths;
    public MediaPlayer player;
    private int musicIndex = -1;//记录 现在正在播放音乐下标  是否是同一个课时 会在startPlayPause中判断
    private int currentPosition = -1;//记录 现在正在播放的时间
    private int courseId = -1;
    public MusicEntry musicEntry;
    private AudioManager mAudioMgr;
    private AudioManager.OnAudioFocusChangeListener listener;

    //*********************以下做 音频转换 的 变量
    private float tempo = 0.5f;
    private float pitch = 0;
    private float rate = 0;
    private long lastSysTime;//当前课时上一次 记录时间的时间点


    @Override
    public void onCreate() {
        super.onCreate();

        if (musicEntry == null)
            musicEntry = new MusicEntry();

        if (listener == null)
            listener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        if (player != null)
                            if (currentPosition > 0)
                                player.seekTo(currentPosition);
                        //有音频焦点 可以播放
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        releasePlay();
                        //要做释放操作了
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        if (player != null)
                            if (!player.isPlaying()) {
                                player.pause();
                                //被暂停了 也应该重新初始化一下时间
                                lastSysTime = System.currentTimeMillis();
                                currentPosition = player.getCurrentPosition();
                            }
                        //很快恢复
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        //低音播放
                    }
                }
            };

        requestAudioFocus();
    }


    public boolean requestAudioFocus() {
        if (mAudioMgr == null) {
            mAudioMgr = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            int focus = mAudioMgr.requestAudioFocus(listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (focus == AudioManager.AUDIOFOCUS_GAIN)
                return true;
        }
        return false;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new playService();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        int playMode = SharePrefrence.getInstance().getPlayMode(getApplicationContext());
        if (playMode == 0) startNewMusic(musicIndex);
        else if (playMode == 1) nextPlay();
        else if (playMode == 2) randomPlay();
    }

    private void randomPlay() {
        Random random = new Random();
        musicIndex = random.nextInt(mPaths.size());
        startNewMusic(musicIndex);
    }

    public class playService extends Binder {
        public MusicPlayService getInstance() {
            return MusicPlayService.this;
        }
    }

    public void startPlay(Context context, final String courseDIR, int courseId) {
        //防止 推出activity再次进来 重新播放的尴尬
        if (this.courseId == courseId)
            return;
        this.courseId = courseId;
        //如果是两个不同的课时 则应该重新初始化一下时间
        lastSysTime = System.currentTimeMillis();
        startCheck(context, courseDIR);
    }

    /**
     * 开启播放 传课时 音频路径 去文件夹下 找所有MP3文件 装载到列表中
     *
     * @param courseDIR
     * @param context
     */
    public void startCheck(Context context, final String courseDIR) {
        //播放之前 检查 权限
        ((AppBaseCompatActivity) context).checkPermissonForStrorage(new RequestPermissionInterface() {
            @Override
            public void onPermissionRequestResult(boolean result,boolean first) {
                if (result) {
                    //曲线回调 之后检查音频目录是否存在
                    File file = new File(courseDIR);
                    if (!file.exists()) {
                        ToastUtil.showToast(getBaseContext(), getString(R.string.course_paths_isNUll_toPlay));
                    } else {
                        // 播放之前的 权限检查 文件检查 以后 走大这一步

                        //还要进一步检索 音频文件是否存在

                        //用于装载 所有音频 的 列表
                        if (mPaths == null) mPaths = new ArrayList<>();
                        mPaths.clear();
                        //检索 courseDIR该目录下 所有MP3文件
                        File[] files = file.listFiles(getFileNameFilter());
                        if (files != null) {
                            for (File f : files) {
                                mPaths.add(f.getPath().toString());
                            }
                            //  走到这才是 开始一首新的 播放  默认播放第一首
                            startNewMusic(0);
                        } else {
                            ToastUtil.showToast(getBaseContext(), getString(R.string.course_paths_isNUll_toPlay));
                        }
                    }
                } else {
                    ToastUtil.showToast(getBaseContext(), getString(R.string.no_storage_permission));
                }
            }
        });
    }

    public void startNewMusic(int musicIndex) {
        if (handler!=null)
            handler.removeCallbacks(runnable);

        if (player != null)
            if (player.isPlaying())
                if (this.musicIndex == musicIndex)
                    return;

        this.musicIndex = musicIndex;
        if (player == null) {
            player = new MediaPlayer();
        }
        try {
            if (player.isPlaying())
                player.pause();
            player.reset();
            if (mPaths==null){ ToastUtil.showToast(getBaseContext(),"没有可播放的音频文件,请清除后重新下载.");
            return;}
            if(mPaths.size()==0){ ToastUtil.showToast(getBaseContext(),"没有可播放的音频文件,请清除后重新下载.");
                return;}
            String path =mPaths.get(musicIndex);
            File file = new File(path);
            if (!file.exists()){
                ToastUtil.showToast(getBaseContext(),"没有可播放的音频文件,请清除后重新下载.");
                return;
            }

            player.setDataSource(path);
            player.setOnPreparedListener(this);
            player.setOnErrorListener(this);
            player.setOnSeekCompleteListener(this);
            player.setOnCompletionListener(this);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //定位到某一个时间点
    public void onPlaySeekTo(float percent) {
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
            }
            musicEntry.setState(5);
            EventBus.getDefault().post(musicEntry);
            player.seekTo(0);
        } else {
            startNewMusic(musicIndex);
        }
    }

    //MusicPlayActivity 调用此方法 开始或者暂停播放
    public void pauseOrPlay() {
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
                musicEntry.setState(2);
                EventBus.getDefault().post(musicEntry);
                handler.removeCallbacks(runnable);
                currentPosition = player.getCurrentPosition();
            } else {
                musicEntry.setState(1);
                //被暂停后开始 也应该重新初始化一下时间
                lastSysTime = System.currentTimeMillis();
                player.start();
                handler.sendEmptyMessage(0);
                EventBus.getDefault().post(musicEntry);
            }
        } else {
            startNewMusic(musicIndex);
        }
    }

    public void pauseByseekbar() {
        if (player != null)
            if (player.isPlaying()) {
                handler.removeCallbacks(runnable);
                //被暂停了 也应该重新初始化一下时间
                lastSysTime = System.currentTimeMillis();
                player.pause();
            }
    }

    public boolean startByseekbar(float percent) {
        if (player != null) {
            player.seekTo((int) (player.getDuration() * percent));
            handler.sendEmptyMessage(0);
            if (!player.isPlaying())
                player.pause();
            player.start();
            return true;
        }
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        // mp.start();
        //  seekOrPrepareComplete(mp);

    }

    public void nextPlay() {
        if (mPaths==null){ ToastUtil.showToast(getBaseContext(),"没有可播放的音频文件,请清除后重新下载.");
            return;}
        if (musicIndex <= mPaths.size() - 2) {
            musicIndex++;
            if (player != null) {
                if (player.isPlaying()) {
                    player.pause();
                    player.stop();
                }
            }startNewMusic(musicIndex);

        }
    }

    public void prePlay() {
        if (musicIndex >= 1) {
            musicIndex--;
            if (player != null)
                if (player.isPlaying()) {
                    player.pause();
                    player.stop();
                }
            startNewMusic(musicIndex);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //记录当前课时练习时间
            updateCurrentCoursePracticeTime();
            if(msg.what==-1){
                resetPlayerStartPlayFile(msg);
            }else{
                if (player != null) {
                    if (player.isPlaying()) {
                        seekOrPrepareComplete(player);
                        handler.postDelayed(runnable, 1000);
                    } else {
                        handler.removeCallbacks(runnable);
                    }
                } else {
                    handler.removeCallbacks(runnable);
                }
            }
        }
    };


    ChatManager manager=null;
    //记录当前课时练习时间
    private void updateCurrentCoursePracticeTime() {
        long time = (System.currentTimeMillis() - lastSysTime);

        if (time<5000) return ;
        //更新当前课时练习时间hanlder回调上一次 记录的时间
        // 然后保存数据库 小于5000  就放行
        if (time>=5000) lastSysTime =System.currentTimeMillis();
        if (manager==null)manager = new ChatManager();
        manager.saveCoursePracticeEntry(courseId, (int) (time/1000));


    }

    private void resetPlayerStartPlayFile(Message msg) {
        dialog.cancelDialog();
        String ratePath = (String) msg.obj;
        if (!new File(ratePath).exists()){
            return;
        }
        if (player!=null){
            if (player.isPlaying())
                player.pause();
            player.reset();
            try {
                player.setDataSource(ratePath);
                player.prepare();
                player.start();
                //EventBus.getDefault().post(true);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }
    };

    @Override
    public void onPrepared(MediaPlayer mp) {
        handler.postDelayed(runnable, 500);
        //player=mp;
        // mp.start();
    }

    private void seekOrPrepareComplete(MediaPlayer mp) {
        //LogUtil.e(progressA+"-seekOrPrepareComplete-"+progressB);
        if (progressA != -1 && progressB != -1) {
            if (progressA > progressB ? mp.getCurrentPosition() >= progressA : mp.getCurrentPosition() >= progressB)
                startAbRepeat(progressA, progressB);
        }
        musicEntry.setState(1);
        musicEntry.setTotalTime(caculateMusicTime2String(mp.getDuration()));
        musicEntry.setAnimationTime(mp.getDuration() - mp.getCurrentPosition());
        musicEntry.setHasTime(caculateMusicTime2String(mp.getCurrentPosition()));
        musicEntry.setProgress((float) mp.getCurrentPosition() / (float) mp.getDuration());
        EventBus.getDefault().post(musicEntry);
    }


    /**
     * 开始AB复读
     *
     * @param progressA
     * @param progressB
     */
    int progressA = -1;
    int progressB = -1;

    public boolean startAbRepeat(int p1, int p2) {

        LogUtil.e(progressA+"-startAbRepeat-"+progressB);
        if (player != null) {
            //如果ab复读时间间隔小于2s则 不予复读
            if (progressB==-1&&progressA==-1){
                this.progressA = (int) ((float)p1/100*player.getDuration());
                this.progressB = (int) ((float)p2/100*player.getDuration());
                if ((float) Math.abs(progressA - progressB) < 1000) {
                    cancelAbRepeat();
                    return false;
                }
            }
            if (player.isPlaying())
                player.pause();
            handler.removeCallbacks(runnable);
            player.seekTo(progressA < progressB ? progressA : progressB);
            player.start();
            handler.sendEmptyMessage(0);
            return true;
        }
        return false;
    }

    //取消ab复读
    public void cancelAbRepeat() {
        progressA = progressB = -1;
    }
    public int getCurPlayIndex(){
        return musicIndex;
    }

    private String caculateMusicTime2String(int time) {
        int seconds = (time + 500) / 1000;//除以1000转换为秒
        if (seconds > 0 && seconds < 10) return "00:0" + seconds;
        else if (seconds >= 10 && seconds < 60) return "00:" + seconds;
        else if (seconds >= 60) {
            int min = seconds / 60;
            int sec = min % 60;
            if (sec > 10) {
                return "0" + min + ":" + sec;
            } else {
                return "0" + min + ": 0" + sec;
            }
        }
        return "00:00";
    }
    MixManager mixManager;
    MaterialDialog dialog;
    boolean hasLoadLib = false;
    public void changedMP3Rate(Context c,int index){
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
                musicEntry.setState(2);
                EventBus.getDefault().post(musicEntry);
                handler.removeCallbacks(runnable);
                currentPosition = player.getCurrentPosition();
            }
        }
        final String curPlayPath = mPaths.get(musicIndex);
        final File wavFile =  StorageUtils.getInstance().CheckFileExist(curPlayPath);
        final String wavPath = wavFile.getAbsolutePath();

        if (mixManager==null)
            mixManager= new MixManager();

        if (dialog==null)
            dialog =new MaterialDialog();
            dialog.showDloag(c,getString(R.string.first_too_long));
        final float speed =getRateSpeed(index);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!wavFile.exists()){
                    try {
                        wavFile.createNewFile();
                        mixManager.TransMp3ToWav(curPlayPath, wavFile.getAbsolutePath(),20000, 1, 255);
                        LogUtil.e("mp3转换成 wav");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String start = wavPath.substring(0, wavPath.lastIndexOf("/") + 1);
                String name = wavPath.substring(wavPath.lastIndexOf("/") + 1,
                        wavPath.lastIndexOf("."));
                String outPath  = start + speed + name+".mp3";
                File file = new File(outPath);
                if (!file.exists()) {
                    if (!hasLoadLib){
                        System.loadLibrary("soundtouch");
                        System.loadLibrary("soundstretch");
                        hasLoadLib=!hasLoadLib;
                    }
                   // AndroidJNI.soundStretch.process(wavPath + ".wav", outPath, mySpeed, 0, 0);
                    AndroidJNI.soundStretch.process(wavPath, outPath, speed, pitch,rate);
                }
                Message msg =handler.obtainMessage();
                msg.what=-1;
                msg.obj = outPath;
                handler.sendMessage(msg);
                LogUtil.e("outpath"+outPath);
            }
        }).start();
    }
    private float getRateSpeed(int speed){
        switch (speed){
            case 0:
                return 30f;
            case 1:
                return 1f;
            case 2:
                return -30f;
            case 3:
                return -45f;
        }
        return 1f;
    }
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtil.e("onError" + what + "--" + extra);
        musicEntry.setState(6);
        EventBus.getDefault().post(musicEntry);
        releasePlay();
        startNewMusic(musicIndex);
        return false;
    }

    public FilenameFilter getFileNameFilter() {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.contains(".mp3"))
                    return true;
                return false;
            }
        };
        return filter;
    }

    @Override
    public void onDestroy() {
        stopSelf();
        releasePlay();
        super.onDestroy();
    }

    private void releasePlay() {
        if (player != null)
            if (player.isPlaying()) {
                player.pause();
                player.release();
                player = null;
            }
        if (mAudioMgr != null) {
            mAudioMgr.abandonAudioFocus(listener);
            listener = null;
        }
    }

    public boolean isPlaying(){
        if (player!=null)
            return player.isPlaying();
        return false;
    }

    public void startPlayByShouye(Context context,String dir,int courseId){
        if (mPaths==null){
            startPlay(context,dir,courseId);
        }else startNewMusic(musicIndex);
    }
}
