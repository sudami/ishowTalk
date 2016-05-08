package com.example.ishow.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishow.Bean.MsgEntry;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.UIView.MaterialProgressBar;

import java.util.ArrayList;

/**
 * Created by MRME on 2016-04-27.
 */
public class ChatBasicAdapter extends BasicAdapter<MsgEntry> {
    private Context context;
    private ArrayList<MsgEntry> arrayList;
    private UserEntry studentInfo;

    public ChatBasicAdapter(Context context, ArrayList<MsgEntry> arrayList, UserEntry studentInfo) {
        super(context, arrayList);
        this.context = context;
        this.arrayList = arrayList;
        this.studentInfo = studentInfo;
    }

    @Override
    public int getItemViewType(int position) {
        MsgEntry  entry = arrayList.get(position);
        if (studentInfo.getUserid()==entry.getFromUserid())
            return 0;
        else return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getConvertView(int position, View convertView, ViewGroup parent) {
        ChatAdapterHolder holder=null;

        if (getItemViewType(position)==0){
            if (convertView==null)
            {
                convertView = View.inflate(context,R.layout.activity_chat_list_item_right,null);
            }
        }
        return null;
    }

    public class ChatAdapterHolder {

        public TextView chatTime;
        public ImageView chatAvatar;
        public TextView chatMsg;
        public MaterialProgressBar chatPb;
        public ImageView chatSendFail;


        public ChatAdapterHolder(View itemView){
            chatTime = (TextView) itemView.findViewById(R.id.chat_recive_time);
            chatAvatar = (ImageView) itemView.findViewById(R.id.chat_recive_avatar);
            chatMsg = (TextView) itemView.findViewById(R.id.chat_recive_msg);
            chatPb = (MaterialProgressBar) itemView.findViewById(R.id.chat_recive_pb);
            chatSendFail = (ImageView) itemView.findViewById(R.id.chat_recive_fail);
        }
    }
}
