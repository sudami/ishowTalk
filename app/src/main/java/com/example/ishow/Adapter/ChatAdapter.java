package com.example.ishow.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishow.Application.iShowTalkApplication;
import com.example.ishow.Bean.MsgEntry;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.UIView.MaterialProgressBar;
import com.example.ishow.Utils.TimeUtil;

import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by MRME on 2016-04-25.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private boolean isChating;
    private LinkedList<MsgEntry> datas = new LinkedList<MsgEntry>();
    private UserEntry studentInfo;
    private LayoutInflater inflater;
    private long lastMsgTime;
    public ChatAdapter(Context context, List<MsgEntry> datas, UserEntry studentInfo, boolean isChating) {
        this.context = context;
        this.isChating = isChating;
        this.datas.addAll(datas);
        this.studentInfo = studentInfo;
        this.inflater = LayoutInflater.from(context);
        lastMsgTime = new Date().getTime();
    }
    @Override
    public int getItemViewType(int position) {
        position = getItemCount()-position-1;
        MsgEntry entry = datas.get(position);
        if (TextUtils.equals(entry.getFromUserid(),studentInfo.getUserid()==null?studentInfo.getId():studentInfo.getUserid())){
            return 0;
        }
        else
        {
            return 1;
        }

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==0)
        {
            View itemView = inflater.inflate(R.layout.activity_chat_list_item_right, null);
            return new ChatAdapterHolderR(itemView);
        }else{
            View itemView = inflater.inflate(R.layout.activity_chat_list_item_left, null);
            return new ChatAdapterHolderL(itemView);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        position = getItemCount()-position-1;
        MsgEntry entry = datas.get(position);

        lastMsgTime = entry.getMsgTime();
        if (holder instanceof  ChatAdapterHolderR){
            ((ChatAdapterHolderR)holder).chatTime.setVisibility(entry.getMsgTime()-lastMsgTime>=1000*60? View.VISIBLE: View.GONE);
            if (((ChatAdapterHolderR)holder).chatTime.getVisibility()== View.VISIBLE)((ChatAdapterHolderR)holder).chatTime.setText(TimeUtil.getDescriptionTimeFromTimestamp(entry.getMsgTime()));
            ((ChatAdapterHolderR)holder).chatAvatar.setVisibility(isChating? View.GONE: View.VISIBLE);
            if (!isChating)x.image().bind(((ChatAdapterHolderR)holder).chatAvatar,entry.getFromImg(), iShowTalkApplication.getInstance().getIgetImageOptions(context,45,5));
            ((ChatAdapterHolderR)holder).chatMsg.setText(entry.getTextMsg());
            if (entry.getState()== 0)
            {
                ((ChatAdapterHolderR)holder).chatPb.setVisibility(View.VISIBLE);
                ((ChatAdapterHolderR)holder).chatSendFail.setVisibility(View.GONE);
            }else if (entry.getState()== 2)
            {
                ((ChatAdapterHolderR)holder).chatPb.setVisibility(View.GONE);
                ((ChatAdapterHolderR)holder).chatSendFail.setVisibility(View.VISIBLE);
            }else {
                ((ChatAdapterHolderR)holder).chatPb.setVisibility(View.GONE);
                ((ChatAdapterHolderR)holder).chatSendFail.setVisibility(View.GONE);
            }


        }else {
            ((ChatAdapterHolderL)holder).chatTime.setVisibility(entry.getMsgTime()-lastMsgTime>=1000*60? View.VISIBLE: View.GONE);
            ((ChatAdapterHolderL)holder).chatAvatar.setVisibility(isChating? View.GONE: View.VISIBLE);
            if (((ChatAdapterHolderL)holder).chatTime.getVisibility()== View.VISIBLE)((ChatAdapterHolderR)holder).chatTime.setText(TimeUtil.getDescriptionTimeFromTimestamp(entry.getMsgTime()));
            if (!isChating) x.image().bind(((ChatAdapterHolderL)holder).chatAvatar,entry.getFromImg(), iShowTalkApplication.getInstance().getIgetImageOptions(context,45,5));
            ((ChatAdapterHolderL)holder).chatMsg.setText(entry.getTextMsg());
            ((ChatAdapterHolderL)holder).chatPb.setVisibility(View.GONE);
            ((ChatAdapterHolderL)holder).chatSendFail.setVisibility(View.GONE);

        }
    }


    @Override
    public int getItemCount() {
        if (datas!=null)return datas.size();
        return 0;
    }

    public class ChatAdapterHolderR extends RecyclerView.ViewHolder {

       public TextView chatTime;
        public ImageView chatAvatar;
        public TextView chatMsg;
        public  MaterialProgressBar chatPb;
        public  ImageView chatSendFail;


        public ChatAdapterHolderR(View itemView){
            super(itemView);
            chatTime = (TextView) itemView.findViewById(R.id.chat_send_time);
            chatAvatar = (ImageView) itemView.findViewById(R.id.chat_send_avatar);
            chatMsg = (TextView) itemView.findViewById(R.id.chat_send_msg);
            chatPb = (MaterialProgressBar) itemView.findViewById(R.id.chat_send_pb);
            chatSendFail = (ImageView) itemView.findViewById(R.id.chat_send_fail);
        }
    }

    public class ChatAdapterHolderL extends RecyclerView.ViewHolder {

        public TextView chatTime;
        public  ImageView chatAvatar;
        public TextView chatMsg;
        public MaterialProgressBar chatPb;
        public ImageView chatSendFail;


        public ChatAdapterHolderL(View itemView){
            super(itemView);
            chatTime = (TextView) itemView.findViewById(R.id.chat_recive_time);
            chatAvatar = (ImageView) itemView.findViewById(R.id.chat_recive_avatar);
            chatMsg = (TextView) itemView.findViewById(R.id.chat_recive_msg);
            chatPb = (MaterialProgressBar) itemView.findViewById(R.id.chat_recive_pb);
            chatSendFail = (ImageView) itemView.findViewById(R.id.chat_recive_fail);
        }
    }

    public int addMsgEntry(MsgEntry entry) {
        if (this.datas == null) datas = new LinkedList<>();
        this.datas.add(0,entry);
        notifyItemInserted(this.datas.size() - 1);
        return 0 ;
    }

    public void updateMsgEntry(int position, boolean sendOk) {
        if (position==-1)position=0;
        MsgEntry entry = this.datas.remove(position);
        entry.setState(sendOk ? 1 : 2);
        this.datas.add(position,entry);
        //this.datas.set(position, entry);
        notifyItemChanged(getItemCount()-position-1);
    }
}
