package com.example.ishow.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ishow.Bean.CourseEntry;
import com.example.ishow.R;

import java.util.ArrayList;

/**
 * Created by MRME on 2016-04-08.
 */
public class LocalCourseAdapter extends BasicAdapter<CourseEntry>{


    public LocalCourseAdapter(Context context, ArrayList<CourseEntry> arrayList) {
        super(context, arrayList);

    }
    @Override
    public View getConvertView(int position, View convertView, ViewGroup parent) {
        CourseEntry entry =  arrayList.get(position);
        ViewHolder holder = ViewHolder.get(context,convertView,parent, R.layout.activity_course_local_item,position);
        TextView name =  holder.getView(R.id.course_local_item_name);
        TextView title =  holder.getView(R.id.course_local_item_title);
        TextView size =  holder.getView(R.id.course_local_item_musicSize);
        name.setText(entry.getContent());
        title.setText(entry.getTitle());
        size.setText(entry.getBaseCourseMaxDownloadSize()+"");
        return holder.getConvertView();
    }
}
