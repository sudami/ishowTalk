package com.example.ishow.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishow.Application.iShowTalkApplication;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.iShowConfig.iShowConfig;

import org.xutils.x;

import java.util.ArrayList;

import butterknife.Bind;

/**
 * Created by MRME on 2016-04-20.
 */
public class FansAdapter extends BasicAdapter<UserEntry> {
    private Context context;
    private ArrayList<UserEntry> datas;

    public FansAdapter(Context context, ArrayList<UserEntry> arrayList) {
        super(context, arrayList);
        this.context = context;
        datas = arrayList;
    }

    @Override
    public View getConvertView(int position, View convertView, ViewGroup parent) {
        UserEntry entry = datas.get(position);
        ViewHolder holder = ViewHolder.get(context, convertView, parent, R.layout.activity_fans_list_item, position);
        TextView ranName =holder.getView(R.id.ran_name);
        TextView ranFrom =holder.getView(R.id.ran_from);
        TextView ranTime =holder.getView(R.id.ran_time);
        ImageView ranAvart =holder.getView(R.id.ran_avart);
        if (TextUtils.equals(entry.getImg(), iShowConfig.morentouxiang))
            ranAvart.setImageResource(R.mipmap.ic_launcher_moren);
        else x.image().bind(ranAvart,entry.getImg(), iShowTalkApplication.getInstance().getIgetImageOptions(context,45,8));
        if (entry.getName().contains("?"))ranName.setText("iShow student");else ranName.setText(entry.getName());
        if (entry.getCampus().contains("?"))ranName.setText("iShowSchool");else ranFrom.setText(entry.getCampus());
        if (entry.getIsOnline()=="1"){ranTime.setText("在线");ranTime.setTextColor(Color.parseColor("#4dc160"));}
        else if (entry.getIsOnline()=="2"){ranTime.setText("忙碌");ranTime.setTextColor(Color.parseColor("#d13c4a"));}
        else {ranTime.setText("离线");ranTime.setTextColor(Color.parseColor("#e6e6e6"));}
        return holder.getConvertView();
    }
}
