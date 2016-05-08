package com.example.ishow.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishow.Application.iShowTalkApplication;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.Utils.TimeUtil;
import com.example.ishow.iShowConfig.iShowConfig;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by MRME on 2016-04-20.
 */
public class PracticeHistoryAdater extends BasicAdapter<UserEntry> {
    private Context context;
    private ArrayList<UserEntry> datas;

    public PracticeHistoryAdater(Context context, ArrayList<UserEntry> datas) {
        super(context, datas);
        this.context = context;
        this.datas = datas;
    }

    @Override
    public View getConvertView(int position, View convertView, ViewGroup parent) {
        UserEntry entry = datas.get(position);
        ViewHolder holder =ViewHolder.get(context,convertView,parent, R.layout.activity_practice_history_list_item,position);
        ImageView avart =holder.getView(R.id.practice_item_head);
        TextView name = holder.getView(R.id.practice_item_name);
        TextView school =holder.getView(R.id.practice_item_secondName);
        TextView time = holder.getView(R.id.practice_item_time);
        if (TextUtils.equals(entry.getImg(), iShowConfig.morentouxiang))
            avart.setImageResource(R.mipmap.ic_launcher_moren);
       else  x.image().bind(avart,entry.getImg(), iShowTalkApplication.getInstance().getIgetImageOptions(context,45,8));
        if(entry.getName().contains("?")){
            name.setText("iShow student");
        }else name.setText(entry.getName());
       // name.setText(entry.getName());
      //  school.setText(entry.getCampus());
        if(entry.getCampus().contains("?")){
            school.setText("iShowSchool");
        }else school.setText(entry.getCampus());
        time.setText(entry.getTimelong());
        return holder.getConvertView();
    }
}
