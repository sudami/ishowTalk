package com.example.ishow.UIActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.R;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MRME on 2016-04-20.
 */
public class AboutUsActivity extends AppBaseCompatActivity {
    View view =null;
    TextView tv_about_ishowcontent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        view = getView(R.layout.activity_us);
        tv_about_ishowcontent = (TextView) view.findViewById(R.id.tv_about_ishowcontent);
        setContentView(view);
        setAppBaseCompactActivity(view);
        setToolbar(true,getString(R.string.about_us));

        JSONObject mJSONObject = new JSONObject();
        try {
            mJSONObject.put("id", "43");
            XHttpUtils.getInstace().getValue(iShowConfig.aboutUS, mJSONObject, new XHttpUtils.OnHttpCallBack() {
                @Override
                public void onSuccess(String result) {
                    setDataByResult(result);
                }
                @Override
                public void onError(String errorResson) {
                    showTipContent(errorResson);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDataByResult(String result) {
        addContentView(view);
        try {
            JSONObject mJsonObject = new JSONObject(result);
            String content = mJsonObject.getString("content");
            tv_about_ishowcontent.setText(Html.fromHtml(content));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
