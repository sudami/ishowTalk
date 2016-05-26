package com.example.ishow.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishow.Application.iShowTalkApplication;
import com.example.ishow.Bean.MediaComment;
import com.example.ishow.R;
import com.example.ishow.Utils.TimeUtil;
import com.example.ishow.iShowConfig.iShowConfig;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by MRS on 2016/5/25.
 */
public class MediaComAdapter extends BasicAdapter<MediaComment.MsgBean.CommentsBean> {
    public MediaComAdapter(Context context, ArrayList<MediaComment.MsgBean.CommentsBean> arrayList) {
        super(context, arrayList);
    }

    @Override
    public View getConvertView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = ViewHolder.get(context,convertView,parent, R.layout.activity_mediaplay_list_item,position);
        ImageView imageView = holder.getView(R.id.media_auth_avart);
        TextView name = holder.getView(R.id.media_auth_name);
        TextView time = holder.getView(R.id.media_auth_upload);
        TextView pinglun = holder.getView(R.id.media_auth_time);
        TextView pinglunCount = holder.getView(R.id.media_auth_pinglun_count);

        MediaComment.MsgBean.CommentsBean CommentsBean = arrayList.get(position);
        if (TextUtils.equals(CommentsBean.getUserImg(),iShowConfig.morentouxiang))
            imageView.setImageResource(R.mipmap.ic_launcher_moren);
        else x.image().bind(imageView,CommentsBean.getUserImg(), iShowTalkApplication.getInstance().getIgetImageOptions(context,20,5));
        name.setText(CommentsBean.getUserNick());
        time.setText(TimeUtil.getDescriptionTimeFromTimestamp(Long.parseLong(CommentsBean.getCommentTime())/1000));
        pinglun.setText(CommentsBean.getMediaComment());
        if (position==0){
            pinglunCount.setText(getCount()+"条评论");
            pinglunCount.setVisibility(View.VISIBLE);
        }else  pinglunCount.setVisibility(View.GONE);
        return holder.getConvertView();
    }
}
