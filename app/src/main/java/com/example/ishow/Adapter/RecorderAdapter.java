package com.example.ishow.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ishow.R;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by MRME on 2016-04-20.
 */
public class RecorderAdapter extends BasicAdapter<String> {
    private Context context;
    private ArrayList<String> arrayList;

    public RecorderAdapter(Context context, ArrayList<String> arrayList) {
        super(context, arrayList);
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public View getConvertView(int position, View convertView, ViewGroup parent) {
        String path =arrayList.get(position);
        RecorderAdapterHolder holder=null;
        if(convertView==null){
            convertView = View.inflate(context,R.layout.activity_yinpin_list_item,null);
            LogUtil.e("getConvertView"+convertView.getMeasuredHeight());
            //convertView.setLayoutParams(new ViewGroup.LayoutParams(PixlesUtils.getScreenWidthPixels(context),PixlesUtils.dip2px(context,140)));
            holder = new RecorderAdapterHolder();
            holder.filename = (TextView) convertView.findViewById(R.id.course_yinpin_item_name);
            convertView.setTag(holder);
        }else holder = (RecorderAdapterHolder) convertView.getTag();

        holder.filename.setText(path.substring(path.lastIndexOf("/")+1,path.lastIndexOf(".")));

        return convertView;
    }
    class RecorderAdapterHolder{
      public  TextView  filename;
    }
}
