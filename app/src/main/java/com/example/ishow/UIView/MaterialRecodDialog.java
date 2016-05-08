package com.example.ishow.UIView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ishow.R;
import com.example.ishow.Utils.PixlesUtils;
import com.example.ishow.Utils.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Handler;

import butterknife.Bind;

/**
 * Created by MRME on 2016-04-11.
 */
public class MaterialRecodDialog implements View.OnClickListener, MediaPlayer.OnCompletionListener, DialogInterface.OnDismissListener, MediaPlayer.OnPreparedListener {
    TextView dialogTips;
    Dialog dialog;
    TextView recodeTime;
    LinearLayout layout;
    TextView cancel;
    TextView ok;
    TextView split;
    TextView splitV;
    TextView falseTime;
    ImageView microPhone;
    ImageView play;
    CircleProgressBar bar;
    private String MP3name;
    private Context context;


    public void showDialog(Context context) {
        this.context = context;

        if (dialog == null) {
            View v = View.inflate(context, R.layout.music_play_recoder, null);
            dialog = new Dialog(context, R.style.dialog);
            dialog.setContentView(v, new ViewGroup.LayoutParams(PixlesUtils.dip2px(context, 210), PixlesUtils.dip2px(context, 210)));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            Window mWindow = dialog.getWindow();

            WindowManager.LayoutParams lp = mWindow.getAttributes();
            lp.x = 0;
            lp.y = -PixlesUtils.dip2px(context, 30);
            dialogTips = (TextView) v.findViewById(R.id.music_play_recoder_tips);
            cancel = (TextView) v.findViewById(R.id.music_play_recoder_cancel);
            ok = (TextView) v.findViewById(R.id.music_play_recoder_ok);
            split = (TextView) v.findViewById(R.id.music_play_recoder_split);
            splitV = (TextView) v.findViewById(R.id.split_v);
            microPhone = (ImageView) v.findViewById(R.id.music_play_recoder_image);
            recodeTime = (TextView) v.findViewById(R.id.music_play_recoder_time);
            layout = (LinearLayout) v.findViewById(R.id.music_play_recoder_layout);
            play = (ImageView) v.findViewById(R.id.music_play_recoder_play);
            bar = (CircleProgressBar) v.findViewById(R.id.progrssbar);
            falseTime = (TextView) v.findViewById(R.id.music_play_recoder_time_false);
            ok.setOnClickListener(this);
            cancel.setOnClickListener(this);
            play.setOnClickListener(this);
            dialog.setOnDismissListener(this);
        }
        play.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.INVISIBLE);
        falseTime.setVisibility(View.GONE);
        microPhone.setVisibility(View.VISIBLE);
        recodeTime.setText("0s");
        dialogTips.setVisibility(View.VISIBLE);
        split.setVisibility(View.GONE);
        splitV.setVisibility(View.GONE);
        ok.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);
        dialogTips.setText("松手取消");
        dialog.show();
    }

    public void cancelDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

    public void updateView(String time, double amp) {

        recodeTime.setText(time);
        updateRecordDialog((int) amp / 2);
    }

    public void showRecordeCompleteUI(String MP3name) {
        this.MP3name = MP3name;
        split.setVisibility(View.VISIBLE);
        dialogTips.setVisibility(View.GONE);
        splitV.setVisibility(View.VISIBLE);
        ok.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
        play.setVisibility(View.VISIBLE);
        bar.setVisibility(View.VISIBLE);
        falseTime.setVisibility(View.VISIBLE);
        microPhone.setVisibility(View.INVISIBLE);
        recodeTime.setText("点击播放");
        play.setImageResource(R.mipmap.icon_record_bofang);
    }

    private void updateRecordDialog(int amp) {
        switch (amp) {
            case 0:
            case 1:
                microPhone.setImageResource(R.mipmap.luyin1);
                break;
            case 2:
            case 3:
                microPhone.setImageResource(R.mipmap.luyin2);
                break;

            case 4:
            case 5:
            case 6:
                microPhone.setImageResource(R.mipmap.luyin3);
                break;

            case 7:
            case 8:
                microPhone.setImageResource(R.mipmap.luyin4);
                break;
            default:
                microPhone.setImageResource(R.mipmap.luyin4);
        }
    }


    boolean playing = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_play_recoder_play:
                //dialog.dismiss();
                if (playing) {
                    releaseMediaplayer();
                } else {
                    playRecorder();
                }
                playing =!playing;
                break;
            case R.id.music_play_recoder_ok:
                dialog.dismiss();
                break;
            case R.id.music_play_recoder_cancel:
                dialog.dismiss();
                String dirs = StorageUtils.getInstance().getPathNameDirs("recoder");
                File file = new File(dirs, MP3name + ".amr");
                if (file.exists())
                    file.delete();

                break;
        }
    }

    public void releaseMediaplayer() {
        handler.removeCallbacks(runnable);
        play.setImageResource(R.mipmap.icon_record_bofang);
        recodeTime.setText("点击播放");
        bar.setProgress(0);
        falseTime.setText("0s");
        if (player != null) {
            if (player.isPlaying())
                player.pause();
            player.stop();
            player.release();
            player = null;

        }
    }

    MediaPlayer player;

    private void playRecorder() {
        String dirs = StorageUtils.getInstance().getPathNameDirs("recoder");
        File file = new File(dirs, MP3name + ".amr");
        if (!file.exists())
            return;
        play.setImageResource(R.mipmap.icon_tingzhi);
        if (player == null) {
            player = new MediaPlayer();
            player.reset();
            try {
                player.setDataSource(context, Uri.fromFile(file));
                player.setOnPreparedListener(this);
                player.prepare();
                player.start();
                player.setOnCompletionListener(this);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        play.setEnabled(true);
        releaseMediaplayer();
        playing = !playing;
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
           // dialogTips.setText(context.getString(R.string.record_palying));
            bar.setProgress((int) (((float) player.getCurrentPosition() / (float) player.getDuration()) * 100));
            int time = (int) ((float) player.getCurrentPosition() / 1000);
            falseTime.setText(time + "s");
            recodeTime.setText(context.getString(R.string.record_palying));
            handler.postDelayed(runnable, 300);
        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);
        }
    };

    @Override
    public void onDismiss(DialogInterface dialog) {
        releaseMediaplayer();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        handler.sendEmptyMessage(0);
    }
}
