package com.example.ishow.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by MRME on 2016-03-28.
 */
public abstract class BasicAdapter<T> extends BaseAdapter {
    public Context context;
    public ArrayList<T> arrayList;

    public BasicAdapter(Context context, ArrayList<T> arrayList){

        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        if (arrayList!=null){
            LogUtil.e("arrayList"+arrayList.size());
            return arrayList.size();
        }
        return 0;
    }

    @Override
    public T getItem(int position) {
        if(arrayList!=null)
            return arrayList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return  getConvertView( position,  convertView, parent);
    }

    public  abstract View getConvertView(int position, View convertView, ViewGroup parent) ;
}
