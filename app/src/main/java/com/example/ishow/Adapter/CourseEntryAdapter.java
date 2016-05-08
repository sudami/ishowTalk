package com.example.ishow.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Bean.CourseEntry;
import com.example.ishow.R;
import com.example.ishow.UIView.CircleProgressBar;
import com.example.ishow.Utils.Interface.RequestPermissionInterface;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.ToastUtil;

import org.xutils.common.util.LogUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by MRME on 2016-03-31.
 */
public class CourseEntryAdapter extends RecyclerView.Adapter<CourseEntryAdapter.ViewHolder>{

    private Context context;
    private LinkedList<CourseEntry> datas;


    public CourseEntryAdapter(Context context, LinkedList<CourseEntry> datas){

        this.context = context;
        this.datas = datas;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.activity_course_item,null);
        return new ViewHolder(view);
    }

    public interface  RecycleViewOnclickListner {
        public void onRecycleViewOnclickListner(int posotion);
        public void onRecycleViewItemclickListner(CourseEntry entry,String claz);
    }
    public RecycleViewOnclickListner LISTNER;
    public void setOnRecycleViewOnclickListner(RecycleViewOnclickListner listner){
        this.LISTNER = listner;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CourseEntry entry = datas.get(position);
        LogUtil.e(entry.getCourseState()+"----"+position);
        holder.courseState.setTag(position);
        holder.courseText.setTag(position);
        holder.progressBar.setTag(position);

        holder.courseContent.setText(entry.getTitle());
        holder.courseTitle.setText("第"+(position+1)+"课");
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseEntry ce =datas.get(position);
                if (ce.getCourseState()==3)
                  LISTNER.onRecycleViewItemclickListner(ce,"第"+(position+1)+"课");
                else  ToastUtil.showToast(context,context.getResources().getString(R.string.please_download_first));
            }
        });
        holder.courseState.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((AppBaseCompatActivity)context).checkPermissonForStrorage(new RequestPermissionInterface() {
                    @Override
                    public void onPermissionRequestResult(boolean result,boolean first) {
                        if (result){
                           if (LISTNER!=null){
                               LISTNER.onRecycleViewOnclickListner(position);}
                        }else{
                            ToastUtil.showToast(context,context.getResources().getString(R.string.no_storage_permission));
                        }
                    }
                });
            }
        });
       // LogUtil.e(entry.getId()+"--progressBar-"+holder.progressBar.getTag()+"-courseText-"+holder.courseText.getTag()+"--courseState-"+holder.courseState.getTag());
        switch (entry.getCourseState()){
            //没下载
            case 0:
                /*if (position== holder.progressBar.getTag()){
                    holder.progressBar.setVisibility(View.GONE);
                    holder.courseText.setVisibility(View.GONE);
                    holder.courseState.setImageResource(R.drawable.iconfont_xiazai);
                }*/
                holder.progressBar.setVisibility(View.GONE);
                holder.courseText.setVisibility(View.GONE);
                holder.courseState.setImageResource(R.drawable.iconfont_xiazai);
                break;
            //正在下载
            case 1:
                /*if (position==  holder.progressBar.getTag()){
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.progressBar.setProgress(entry.getProgressbar());
                    holder.courseState.setVisibility(View.VISIBLE);
                    holder.courseText.setVisibility(View.GONE);
                    holder.courseState.setImageResource(R.drawable.iconfont_zantingxiazai_pressed);
                }*/
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.progressBar.setProgress(entry.getProgressbar());
                holder.courseState.setVisibility(View.VISIBLE);
                holder.courseText.setVisibility(View.GONE);
                holder.courseState.setImageResource(R.drawable.iconfont_zantingxiazai_pressed);
                break;
            //暂停下载
            case 2:
               /* if (position==  holder.progressBar.getTag()){
                    holder.progressBar.setProgress(entry.getProgressbar());
                    holder.progressBar.setVisibility(View.VISIBLE);
                    holder.courseText.setVisibility(View.GONE);
                    holder.courseState.setVisibility(View.VISIBLE);
                    holder.courseState.setImageResource(R.drawable.iconfont_bofang);
                }*/
                holder.progressBar.setProgress(entry.getProgressbar());
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.courseText.setVisibility(View.GONE);
                holder.courseState.setVisibility(View.VISIBLE);
                holder.courseState.setImageResource(R.drawable.iconfont_bofang);
                break;
            //下载完成
            case 3:
               /* if (position==  holder.progressBar.getTag()){
                    holder.progressBar.setVisibility(View.GONE);
                    holder.courseState.setVisibility(View.GONE);
                    holder.courseText.setVisibility(View.GONE);
                }*/
                holder.progressBar.setVisibility(View.GONE);
                holder.courseState.setVisibility(View.GONE);
                holder.courseText.setVisibility(View.GONE);
                break;
            //点击下载还没加入队列
            case 4:
                /*if (position==  holder.progressBar.getTag()){
                    holder.progressBar.setVisibility(View.GONE);
                    holder.courseState.setVisibility(View.GONE);
                    holder.courseText.setVisibility(View.VISIBLE);
                }*/
                holder.progressBar.setVisibility(View.GONE);
                holder.courseState.setVisibility(View.GONE);
                holder.courseText.setVisibility(View.VISIBLE);
                break;
            //可能刚加入队列
            case 5:
               /* if (position==  holder.progressBar.getTag()){
                    holder.progressBar.setVisibility(View.GONE);
                    holder.courseState.setVisibility(View.GONE);
                    holder.courseText.setText("队列中...");
                    holder.courseText.setVisibility(View.VISIBLE);
                }*/
                holder.progressBar.setVisibility(View.GONE);
                holder.courseState.setVisibility(View.GONE);
                holder.courseText.setText("队列中...");
                holder.courseText.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (datas!=null)
            return datas.size();
        return 0;
    }

    public void addCourseEntry(CourseEntry entry){
        this.datas.add(entry);
        notifyItemInserted(this.datas.size()-1);
    }

    public void changeCourseEntry(CourseEntry entry,int index){
        datas.set(index,entry);
        notifyItemChanged(index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView courseTitle;
        public TextView courseContent;
        public ImageButton courseState;
        public CircleProgressBar progressBar;
        public TextView courseText;
        public CardView cardview;
        public ViewHolder(View itemView) {
            super(itemView);
            cardview= (CardView) itemView.findViewById(R.id.cardview);
            courseText = (TextView) itemView.findViewById(R.id.course_item_state_text);
            courseTitle = (TextView) itemView.findViewById(R.id.course_item_name);
            courseContent = (TextView) itemView.findViewById(R.id.course_item_content);
            courseState = (ImageButton) itemView.findViewById(R.id.course_item_down_state);
            progressBar = (CircleProgressBar) itemView.findViewById(R.id.CircleProgressBar);
        }
    }
}
