package com.example.ishow.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.example.ishow.R;
import com.example.ishow.spinnerwheel.adapters.AbstractWheelTextAdapter;

public class PlaySpeedAdapter extends AbstractWheelTextAdapter {

    public PlaySpeedAdapter(Context context) {
        super(context, R.layout.wheel_playspeed_item, NO_RESOURCE);
        setItemTextResource(R.id.playspeed_name);
    }

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        View view = super.getItem(index, cachedView, parent);
        return view;
    }
    
    @Override
    public int getItemsCount() {
        return 4;
    }
    
    @Override
    protected CharSequence getItemText(int index) {
        if (index==0){
            return "x"+1.5;
        }else  if (index==1){
            return "x"+1;
        }else  if (index==2){
            return "x"+0.5;
        }else  if (index==3){
            return " x"+0.3;
        }else return "x"+1.0;
    }
}
