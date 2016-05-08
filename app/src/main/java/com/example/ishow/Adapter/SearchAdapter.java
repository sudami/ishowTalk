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
import com.example.ishow.Utils.Interface.TextUtil;
import com.example.ishow.iShowConfig.iShowConfig;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by MRME on 2016-04-26.
 */
public class SearchAdapter extends BasicAdapter<UserEntry> {

    private Context context;
    private ArrayList<UserEntry> arrayList;

    public SearchAdapter(Context context, ArrayList<UserEntry> arrayList) {
        super(context, arrayList);
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public View getConvertView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context,convertView,parent, R.layout.activity_search_list_item,position);
        ImageView avart = holder.getView(R.id.ran_avart);
        TextView  name = holder.getView(R.id.ran_name);
        TextView school = holder.getView(R.id.ran_from);
        TextView guanzhu = holder.getView(R.id.ran_time);
        UserEntry entry = arrayList.get(position);
        if (TextUtils.equals(entry.getImg(), iShowConfig.morentouxiang))
            avart.setImageResource(R.mipmap.ic_launcher_moren);
        else x.image().bind(avart,entry.getImg(), iShowTalkApplication.getInstance().getIgetImageOptions(context,45,5));
        name.setText(TextUtil.splitCharOfText(entry.getName(),true));
        school.setText(TextUtil.splitCharOfText(entry.getCampus(),false));

        if (!entry.is_focus()){ guanzhu.setText("");guanzhu.setBackgroundResource(R.mipmap.icon_guanzhu);}
        else {
            guanzhu.setBackgroundResource(R.drawable.translate_drawable);
            guanzhu.setText("已关注");
        }
        guanzhu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fansinterface!=null){
                    fansinterface.add2Fan(position);
                }
            }
        });
        return holder.getConvertView();
    }

    SearchAdapterAdd2Fansinterface fansinterface;
    public interface  SearchAdapterAdd2Fansinterface{
        public void add2Fan(int position);
    }
    public void setOnSearchAdapterAdd2Fansinterface(SearchAdapterAdd2Fansinterface fansinterface1){
        this.fansinterface=fansinterface1;
    }

    public void updateItem(int position,boolean ok){
        UserEntry entry = arrayList.get(position);
        entry.setIs_focus(ok);
        arrayList.set(position,entry);
        notifyDataSetChanged();
    }
}
