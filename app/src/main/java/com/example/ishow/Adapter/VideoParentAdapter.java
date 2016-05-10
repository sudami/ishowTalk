package com.example.ishow.Adapter;

import android.content.Context;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishow.Application.iShowTalkApplication;
import com.example.ishow.Bean.VideoEntry;
import com.example.ishow.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by MRME on 2016-05-08.
 */
public class VideoParentAdapter extends BasicAdapter<VideoEntry> {
    private Context context;
    private ArrayList<VideoEntry> arrayList;

    public VideoParentAdapter(Context context, ArrayList<VideoEntry> arrayList) {
        super(context, arrayList);
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public View getConvertView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context,convertView,parent, R.layout.fragment_videoparent_list_item,position);

        ImageView avart = holder.getView(R.id.video_item_avart);
        TextView bucket = holder.getView(R.id.video_item_dir);
        TextView count = holder.getView(R.id.video_item_dirCount);

        VideoEntry entry = arrayList.get(position);
        avart.setImageBitmap(entry.getBitmap());
        //ImageLoader.getInstance().displayImage("file://"+entry.getThumPath(),avart);
        bucket.setText(entry.getBucketName());
        count.setText(entry.getFileCount()+"个视频");

        return holder.getConvertView();
    }
}
