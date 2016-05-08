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
 * Created by MRME on 2016-04-19.
 */
public class RankAdapter extends BasicAdapter<UserEntry> {
    private Context context;
    private ArrayList<UserEntry> arrayList;
    private UserEntry studentInfo;

    public RankAdapter(Context context, ArrayList<UserEntry> arrayList, UserEntry studentInfo) {
        super(context, arrayList);
        this.context = context;
        this.arrayList = arrayList;
        this.studentInfo = studentInfo;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        else
            return 1;
    }

    @Override
    public View getConvertView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        UserEntry entry = arrayList.get(position);
        if (getItemViewType(position) == 0) {
            holder = ViewHolder.get(context, convertView, parent, R.layout.fragment_rank_list_header, position);
        } else {
            holder = ViewHolder.get(context, convertView, parent, R.layout.fragment_rank_list_item, position);
        }
        if (getItemViewType(position) == 0) {
            ImageView image = holder.getView(R.id.rank_item_header_avart);
            TextView name = holder.getView(R.id.rank_item_header_name);
            TextView time = holder.getView(R.id.rank_item_header_time);
            ImageView selficon = holder.getView(R.id.rank_item_mingci_img);
            TextView selfText = holder.getView(R.id.rank_item_mingci_text);
            TextView selfname = holder.getView(R.id.ran_name);
            TextView selffrom = holder.getView(R.id.ran_from);
            TextView selfTime = holder.getView(R.id.ran_time);
            ImageView selfavart = holder.getView(R.id.ran_avart);
            if (TextUtils.equals(entry.getImg(), iShowConfig.morentouxiang))
                image.setImageResource(R.mipmap.ic_launcher_moren);
            else x.image().bind(image, entry.getImg(), iShowTalkApplication.getInstance().getIgetImageOptions(context, 45, 8));
           // name.setText(entry.getName());
            if(entry.getName().contains("?")){
                name.setText("iShow student");
            }else name.setText(entry.getName());
            time.setText(TimeUtil.getHourAndMinText(Long.parseLong(entry.getTimelong())));
            if (studentInfo != null) ;
            {
                selficon.setVisibility(View.GONE);
                selfText.setVisibility(View.VISIBLE);
                selfText.setText(studentInfo.getPaiming());
                if (studentInfo.getTimelong()!=null)
                selfTime.setText(TimeUtil.getHourAndMinText(Long.parseLong(studentInfo.getTimelong())));
                x.image().bind(selfavart, studentInfo.getImg(), iShowTalkApplication.getInstance().getIgetImageOptions(context, 45, 8));
                if(studentInfo.getName().contains("?")){
                    selfname.setText("iShow student");
                }else selfname.setText(studentInfo.getName());

              //  selffrom.setText(studentInfo.getCampus());
                if(entry.getCampus().contains("?")){
                    selffrom.setText("iShowSchool");
                }else selffrom.setText(entry.getCampus());
            }

        } else {
            ImageView icon = holder.getView(R.id.rank_item_mingci_img);
            TextView iconText = holder.getView(R.id.rank_item_mingci_text);
            TextView name = holder.getView(R.id.ran_name);
            TextView from = holder.getView(R.id.ran_from);
            TextView time = holder.getView(R.id.ran_time);
            ImageView avart = holder.getView(R.id.ran_avart);
            if (position == 1) {
                icon.setImageResource(R.mipmap.iconfont_dierming);
            } else if (position == 2)
                icon.setImageResource(R.mipmap.iconfont_disanming);
            else {
                icon.setVisibility(View.GONE);
                iconText.setVisibility(View.VISIBLE);
                iconText.setText((position + 1) + "");
            }
            time.setText(TimeUtil.getHourAndMinText(Integer.parseInt(entry.getTimelong())));
            if (TextUtils.equals(entry.getImg(), iShowConfig.morentouxiang))
                avart.setImageResource(R.mipmap.ic_launcher_moren);
           else  x.image().bind(avart, entry.getImg(), iShowTalkApplication.getInstance().getIgetImageOptions(context, 45, 8));
            if(entry.getName().contains("?")){
                name.setText("iShow student");
            }else name.setText(entry.getName());
           // name.setText(entry.getName());
          //  from.setText(entry.getCampus());
            if(entry.getCampus().contains("?")){
                from.setText("iShowSchool");
            }else from.setText(entry.getCampus());
        }
        return holder.getConvertView();
    }
}
