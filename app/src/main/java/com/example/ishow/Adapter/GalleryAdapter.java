package com.example.ishow.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishow.Bean.VideoEntry;
import com.example.ishow.R;
import com.example.ishow.Utils.StorageUtils;
import com.example.ishow.Utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by MRME on 2016-05-09.
 */
public class GalleryAdapter extends BasicAdapter<VideoEntry> {
    private Context context;
    private ArrayList<VideoEntry> arrayList;
    private HashMap<Integer,Boolean> checkList ;

    public GalleryAdapter(Context context, ArrayList<VideoEntry> arrayList) {
        super(context, arrayList);
        this.context = context;
        this.arrayList = arrayList;

        setCheckBoxState();
    }

    @Override
    public View getConvertView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context,convertView,parent, R.layout.galleryfragment_grid_item,position);
        CheckBox box = holder.getView(R.id.gallery_item_checkbox);
        ImageView  image = holder.getView(R.id.gallery_item_mediaavart);
        TextView size = holder.getView(R.id.gallery_media_size);
        final VideoEntry entry = arrayList.get(position);
        box.setChecked(checkList.get(position));
        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked =checkList.get(position);
                if (checkList.containsValue(true))
                {
                    if (checked) {
                        ((CheckBox) v).setChecked(!checked);
                        checkList.put(position,!checked);
                        i.onBoxChecked(false,position,entry.getDuration());
                    }
                    else{
                        ((CheckBox)v).setChecked(false);
                        ToastUtil.showToast(context,context.getString(R.string.only_one_video_canChoose));

                    }
                }else
                {
                    ((CheckBox) v).setChecked(!checked);
                    i.onBoxChecked(true,position,entry.getDuration());
                    checkList.put(position,!checked);
                }
            }
        });
        size.setText(StorageUtils.getInstance().getSdVideoSize(entry.getSize()));
        image.setImageBitmap(entry.getBitmap());
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(entry.getFilePath()), "video/mp4");
                context.startActivity(intent);
            }
        });

        return holder.getConvertView();
    }

    public onCheckBoxChecked i;
    public interface onCheckBoxChecked{
        public void onBoxChecked(boolean checked,int position,long druation);
    }
    public void addOnCheckBoxCheckedInterface(onCheckBoxChecked checked){
        this.i = checked;
    }

    public void notifyDataChange(){
        setCheckBoxState();
        notifyDataSetChanged();

    }
    public void setCheckBoxState()
    {
        if (checkList ==null)checkList =new HashMap<>();
        for (int i = 0; i <getCount() ; i++) {
            checkList.put(i,false);
        }
    }
}
