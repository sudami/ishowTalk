package com.example.ishow.justalk.cloud.juscall;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.justalk.cloud.lemon.MtcCall;
import com.justalk.cloud.lemon.MtcMdm;

public class Statistics extends FrameLayout implements OnClickListener, Runnable {

    private static final int COLOR_MAIN_BACKGROUND = 0x80ffffff;
    private static final int COLOR_HEAD_BACKGROUND = Color.argb(128, 153, 153, 153);
    private static final int COLOR_HEAD_SELECTED_BACKGROUND = Color.argb(128, 204, 204, 204);

    static int sSessionId;
    private LinearLayout m_StatMain;
    private TextView m_StatText;
    private TextView m_StatHeadVoice;
    private TextView m_StatHeadVideo;
    private TextView m_StatHeadMPT;
    private TextView m_StatHeadMdm;
    private boolean m_IsShow = false;

    private static final int STAT_MSG_TYPE_VOICE = 1;
    private static final int STAT_MSG_TYPE_VIDEO = 2;
    private static final int STAT_MSG_TYPE_MPT = 3;
    private static final int STAT_MSG_TYPE_MDM = 4;

    private int m_MsgType = STAT_MSG_TYPE_VOICE;
    private static String mStrNotRunning;

    private static final int DELAY_MS = 1000;
    private static Handler sHandler = new Handler() {
        public void handleMessage(Message msg) {
            TextView tv = (TextView) msg.obj;
            switch (msg.what) {
                case STAT_MSG_TYPE_VOICE:
                    tv.setText(getVoiceStat());
                    break;
                case STAT_MSG_TYPE_VIDEO:
                    tv.setText(getVideoStat());
                    break;
                case STAT_MSG_TYPE_MPT:
                    tv.setText(getMPTStat());
                    break;
                case STAT_MSG_TYPE_MDM:
                    tv.setText(getMdmStat());
                    break;
                default:
                    break;
            }
        }
    };

    public Statistics(Context context) {
        super(context);
    }

    public Statistics(Context context, int sessionId) {
        super(context);
        sSessionId = sessionId;
        m_StatMain = new LinearLayout(context);
        m_StatMain.setOrientation(LinearLayout.VERTICAL);
        m_StatMain.setBackgroundColor(COLOR_MAIN_BACKGROUND);
        final float scale = getResources().getDisplayMetrics().density;
        int px = (int) (23 * scale + 0.5f);
        LayoutParams framelayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
                Gravity.BOTTOM);
        // status bar height
        framelayoutParams.topMargin = px;
        // head
        m_StatHeadVoice = new TextView(context);
        m_StatHeadVoice.setBackgroundColor(COLOR_HEAD_BACKGROUND);
        m_StatHeadVoice.setGravity(Gravity.CENTER);
        m_StatHeadVoice.setText("Voice");
        m_StatHeadVoice.setTextColor(Color.BLACK);
        m_StatHeadVoice.setTextSize(16);
        m_StatHeadVideo = new TextView(context);
        m_StatHeadVideo.setBackgroundColor(COLOR_HEAD_BACKGROUND);
        m_StatHeadVideo.setGravity(Gravity.CENTER);
        m_StatHeadVideo.setText("Video");
        m_StatHeadVideo.setTextColor(Color.BLACK);
        m_StatHeadVideo.setTextSize(16);
        m_StatHeadMPT = new TextView(context);
        m_StatHeadMPT.setBackgroundColor(COLOR_HEAD_BACKGROUND);
        m_StatHeadMPT.setGravity(Gravity.CENTER);
        m_StatHeadMPT.setText("NAT");
        m_StatHeadMPT.setTextColor(Color.BLACK);
        m_StatHeadMPT.setTextSize(16);
        m_StatHeadMdm = new TextView(context);
        m_StatHeadMdm.setBackgroundColor(COLOR_HEAD_BACKGROUND);
        m_StatHeadMdm.setGravity(Gravity.CENTER);
        m_StatHeadMdm.setText("Mdm");
        m_StatHeadMdm.setTextColor(Color.BLACK);
        m_StatHeadMdm.setTextSize(16);

        LinearLayout ll_stat_head = new LinearLayout(context);
        ll_stat_head.setOrientation(LinearLayout.HORIZONTAL);
        ll_stat_head.setBackgroundColor(COLOR_HEAD_BACKGROUND);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, (int) (30 * scale + 0.5));
        params.weight = 1;
        ll_stat_head.addView(m_StatHeadVoice, params);
        ll_stat_head.addView(m_StatHeadVideo, params);
        ll_stat_head.addView(m_StatHeadMPT, params);
        ll_stat_head.addView(m_StatHeadMdm, params);
        params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        m_StatMain.addView(ll_stat_head, params);
        // content
        m_StatText = new TextView(context);
        m_StatText.setTextColor(Color.BLACK);
        m_StatText.setTextSize(14);
        m_StatText.setTypeface(Typeface.MONOSPACE);
        ScrollView sv_stat = new ScrollView(context);
        px = (int) (5 * scale + 0.5);
        sv_stat.setPadding(px, px, px, px);
        sv_stat.addView(m_StatText);
        params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        m_StatMain.addView(sv_stat, params);

        addView(m_StatMain, framelayoutParams);

        sv_stat.setFillViewport(true);
        m_StatText.setOnClickListener(this);
        m_StatHeadVoice.setOnClickListener(this);
        m_StatHeadVideo.setOnClickListener(this);
        m_StatHeadMPT.setOnClickListener(this);
        m_StatHeadMdm.setOnClickListener(this);

        ll_stat_head.setSoundEffectsEnabled(false);
        sv_stat.setSoundEffectsEnabled(false);
        m_StatMain.setSoundEffectsEnabled(false);
        m_StatText.setSoundEffectsEnabled(false);
        m_StatHeadVoice.setSoundEffectsEnabled(false);
        m_StatHeadVideo.setSoundEffectsEnabled(false);
        m_StatHeadMPT.setSoundEffectsEnabled(false);
        m_StatHeadMdm.setSoundEffectsEnabled(false);

        mStrNotRunning = context.getResources().getString(MtcResource.getIdByName("string", "Not_running"));
    }

    public void showStat(boolean isVideo) {
        if (m_StatMain.getVisibility() != View.VISIBLE) {
            m_StatMain.setVisibility(View.VISIBLE);
        }
        if (sHandler != null)
            sHandler.postDelayed(this, DELAY_MS);

        if (isVideo) {
            showStatVideo();;
        } else {
            showStatVoice();
        }
        m_IsShow = true;
    }

    public void hideStat() {
        if (m_StatMain.getVisibility() != View.GONE) {
            m_StatMain.setVisibility(View.GONE);
        }
        if (sHandler != null)
            sHandler.removeCallbacks(this);

        m_IsShow = false;
    }

    public boolean isShow() {
        return m_IsShow;
    }

    private void showStatVoice() {
        m_StatText.setText(getVoiceStat());
        m_StatHeadVoice.setBackgroundColor(COLOR_HEAD_SELECTED_BACKGROUND);
        m_StatHeadVideo.setBackgroundColor(COLOR_HEAD_BACKGROUND);
        m_StatHeadMPT.setBackgroundColor(COLOR_HEAD_BACKGROUND);
        m_StatHeadMdm.setBackgroundColor(COLOR_HEAD_BACKGROUND);

        m_MsgType = STAT_MSG_TYPE_VOICE;
    }

    private void showStatVideo() {
        m_StatText.setText(getVideoStat());
        m_StatHeadVoice.setBackgroundColor(COLOR_HEAD_BACKGROUND);
        m_StatHeadVideo.setBackgroundColor(COLOR_HEAD_SELECTED_BACKGROUND);
        m_StatHeadMPT.setBackgroundColor(COLOR_HEAD_BACKGROUND);
        m_StatHeadMdm.setBackgroundColor(COLOR_HEAD_BACKGROUND);

        m_MsgType = STAT_MSG_TYPE_VIDEO;
    }

    private void showStatMpt() {
        m_StatText.setText(getMPTStat());
        m_StatHeadVoice.setBackgroundColor(COLOR_HEAD_BACKGROUND);
        m_StatHeadVideo.setBackgroundColor(COLOR_HEAD_BACKGROUND);
        m_StatHeadMPT.setBackgroundColor(COLOR_HEAD_SELECTED_BACKGROUND);
        m_StatHeadMdm.setBackgroundColor(COLOR_HEAD_BACKGROUND);

        m_MsgType = STAT_MSG_TYPE_MPT;
    }

    private void showStatMdm() {
        m_StatText.setText(getMdmStat());
        m_StatHeadVoice.setBackgroundColor(COLOR_HEAD_BACKGROUND);
        m_StatHeadVideo.setBackgroundColor(COLOR_HEAD_BACKGROUND);
        m_StatHeadMPT.setBackgroundColor(COLOR_HEAD_BACKGROUND);
        m_StatHeadMdm.setBackgroundColor(COLOR_HEAD_SELECTED_BACKGROUND);

        m_MsgType = STAT_MSG_TYPE_MDM;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(m_StatHeadVoice)) {
            showStatVoice();
        } else if (v.equals(m_StatHeadVideo)) {
            showStatVideo();
        } else if (v.equals(m_StatHeadMPT)) {
            showStatMpt();
        } else if (v.equals(m_StatHeadMdm)) {
            showStatMdm();
        } else {
            hideStat();
        }
    }

    private static String getVoiceStat() {
        String s = MtcCall.Mtc_CallGetAudioStat(sSessionId);
        if (TextUtils.isEmpty(s))
            s = mStrNotRunning;
        return s;
    }

    private static String getVideoStat() {
        String s = MtcCall.Mtc_CallGetVideoStat(sSessionId);
        if (TextUtils.isEmpty(s))
            s = mStrNotRunning;
        return s;
    }

    private static String getMPTStat() {
        String s = MtcCall.Mtc_CallGetMptStat(sSessionId);
        if (TextUtils.isEmpty(s))
            s = mStrNotRunning;
        return s;
    }

    public static String getMdmStat() {
        StringBuilder buff = new StringBuilder();
        buff.append("Mode:\t").append(getAudioManagerModeString()).append("\n");
        buff.append("Input:\t").append(MtcMdm.Mtc_MdmGetAndroidAudioInputDevice()).append("\n");
        buff.append("Output:\t").append(MtcMdm.Mtc_MdmGetAndroidAudioOutputDevice()).append("\n");
        buff.append("OS Aec:\t").append(MtcMdm.Mtc_MdmGetOsAec()).append("\n");
        buff.append("OS Agc:\t").append(MtcMdm.Mtc_MdmGetOsAgc()).append("\n");
        buff.append("Mmp:\t").append(MtcMdm.Mtc_MdmGetMmpVersion()).append("\n");
        return buff.toString();
    }

    private static String getAudioManagerModeString() {
        String modeString = "MODE_UNKNOWN";
        switch (MtcMdm.Mtc_MdmGetAndroidAudioMode()) {
            case AudioManager.MODE_CURRENT:
                modeString = "MODE_CURRENT";
                break;
            case AudioManager.MODE_IN_CALL:
                modeString = "MODE_IN_CALL";
                break;
            case AudioManager.MODE_IN_COMMUNICATION:
                modeString = "MODE_IN_COMMUNICATION";
                break;
            case AudioManager.MODE_NORMAL:
                modeString = "MODE_NORMAL";
                break;
            case AudioManager.MODE_INVALID:
                modeString = "MODE_INVALID";
                break;
            case AudioManager.MODE_RINGTONE:
                modeString = "MODE_RINGTONE";
                break;
        }
        return modeString;
    }

    @Override
    public void run() {
        Message msg = new Message();
        msg.what = m_MsgType;
        msg.obj = m_StatText;
        sHandler.sendMessage(msg);
        if (isShow())
            sHandler.postDelayed(this, DELAY_MS);
    }
}
