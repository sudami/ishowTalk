package com.example.ishow.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishow.Bean.ShortCourseEntry;
import com.example.ishow.R;

import java.util.ArrayList;

/**
 * Created by MRME on 2016-04-14.
 */
public class MusciPlayAdapter extends BasicAdapter<ShortCourseEntry> {

    private int playIndex;

    public MusciPlayAdapter(Context context, ArrayList<ShortCourseEntry> datas, int playIndex) {
        super(context, datas);
        this.playIndex = playIndex;
    }

    @Override
    public View getConvertView(int position, View convertView, ViewGroup parent) {
        ShortCourseEntry entry =  arrayList.get(position);
        ViewHolder holder = ViewHolder.get(context,convertView,parent, R.layout.music_play_menu_item,position);
        TextView title =  holder.getView(R.id.menu_coursename);
        ImageView image =  holder.getView(R.id.menu_zhengzaibofang);

        image.setVisibility(playIndex==position?View.VISIBLE:View.GONE);
        title.setTextColor(playIndex==position?context.getResources().getColor(R.color.colorAccent): Color.BLACK);
        title.setText(entry.getTitle());
        return holder.getConvertView();
    }

    public void setisPalyingIndex(int index){

        this.playIndex = index;
        notifyDataSetChanged();
    }
}
