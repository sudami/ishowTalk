package com.example.ishow.BaseComponent;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.ishow.Bean.UserEntry;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.justalk.cloud.juscall.MtcCallDelegate;
import com.justalk.cloud.lemon.MtcMdm;
import com.justalk.cloud.lemon.MtcMediaConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MRME on 2016-04-26.
 */
public class CallBaseCompactActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void startCallactivity(UserEntry recentMsg) {
        MtcMdm.Mtc_MdmAnSetBitrateMode(MtcMediaConstants.EN_MTC_AN_HIGH);//recentMsg.getMobile()
        MtcCallDelegate.call(recentMsg.getMobile(), makeIhsowUserRankCallJosnObject(),
                makeIshowRecentCallJosnObject(recentMsg), true);
    }

    public String makeIhsowUserRankCallJosnObject() {
        UserEntry localRank = SharePrefrence.getInstance().getStudentInfo(getApplicationContext());
        JSONObject local = new JSONObject();
        try {
            local.put("username", localRank.getName());
            local.put("userimg", localRank.getImg());
            local.put("userphone", localRank.getMobile());
            local.put("userid", localRank.getUserid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return local.toString();
    }

    public String makeIshowRecentCallJosnObject(UserEntry recentMsg) {
        JSONObject local = new JSONObject();
        try {
            local.put("username", recentMsg.getName());
            local.put("userimg", recentMsg.getImg());
            local.put("userphone", recentMsg.getMobile());
            local.put("userid", recentMsg.getUserid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return local.toString();
    }
}
