package com.example.ishow.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishow.Application.iShowTalkApplication;
import com.example.ishow.Bean.Conversation;
import com.example.ishow.Bean.MsgEntry;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.Utils.Interface.TextUtil;
import com.example.ishow.Utils.TimeUtil;
import com.example.ishow.iShowConfig.iShowConfig;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by MRME on 2016-04-26.
 */
public class ConversationAdapter extends BasicAdapter<Conversation> {
    private Context context;
    private ArrayList<Conversation> arrayList;
    private UserEntry studentInfo;

    public ConversationAdapter(Context context, ArrayList<Conversation> arrayList, UserEntry studentInfo) {
        super(context, arrayList);
        this.context = context;
        this.arrayList = arrayList;
        this.studentInfo = studentInfo;
    }

    @Override
    public View getConvertView(int position, View convertView, ViewGroup parent) {
        position = getCount()-1-position;
        Conversation msgEntry = arrayList.get(position);

        ViewHolder holder =ViewHolder.get(context,convertView,parent, R.layout.fragment_msg_list_item,position);
        ImageView avart = holder.getView(R.id.practice_item_head);
        TextView  name = holder.getView(R.id.practice_item_name);
        TextView content = holder.getView(R.id.practice_item_secondName);
        TextView time = holder.getView(R.id.time);
        TextView unread = holder.getView(R.id.unread);
       // boolean equals = TextUtils.equals(msgEntry.getFromUserid(), studentInfo.getUserid());
        if (TextUtils.equals(msgEntry.getFromImg(), iShowConfig.morentouxiang))
            avart.setImageResource(R.mipmap.ic_launcher_moren);
        else x.image().bind(avart,msgEntry.getFromImg(), iShowTalkApplication.getInstance().getIgetImageOptions(context,45,5));
       // name.setText(msgEntry.getFromNick());
        name.setText(TextUtil.splitCharOfText(msgEntry.getFromNick(),true));
        content.setText(msgEntry.getTextMsg());
        if (msgEntry.getUnreadCount()>0) {
            unread.setVisibility(View.VISIBLE);
            unread.setText(msgEntry.getUnreadCount()+"");
        }else unread.setVisibility(View.GONE);
        time.setText(TimeUtil.getDescriptionTimeFromTimestamp(msgEntry.getMsgTime()));
        return holder.getConvertView();
    }
}
