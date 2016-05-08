/**
 * 
 */
package com.example.ishow.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ishow.Application.iShowTalkApplication;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.Utils.Interface.TextUtil;
import com.example.ishow.iShowConfig.iShowConfig;

import org.xutils.x;

import java.util.List;

/**
 * @author MRME
 *
 */
public class PracticeAdapter extends BaseAdapter {

	

	private Context context;
	private List<UserEntry> datas;


	public PracticeAdapter(Context context, List<UserEntry> datas) {
		this.context = context;
		this.datas = datas;
		
	}

	
	@Override
	public int getCount() {
		
		return datas.size();
	}

	
	@Override
	public Object getItem(int position) {
		
		return datas.get(position);
	}

	
	@Override
	public long getItemId(int position) {
		
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			holder= new ViewHolder();
			convertView = View.inflate(context, R.layout.activity_select_members_grid_item, null);
			holder.head = (ImageView) convertView.findViewById(R.id.grid_head);
			holder.name = (TextView) convertView.findViewById(R.id.grid_name);
			holder.onlionState = (ImageView) convertView.findViewById(R.id.grid_state);
			holder.school = (TextView) convertView.findViewById(R.id.grid_name2);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		UserEntry recent =datas.get(position);
		holder.name.setText(TextUtil.splitCharOfText(recent.getName(),true));
		holder.school.setText(TextUtil.splitCharOfText(recent.getCampus(),false));
		if (TextUtils.equals(recent.getImg(), iShowConfig.morentouxiang))
			holder.head.setImageResource(R.mipmap.ic_launcher_moren);
		else x.image().bind(holder.head,recent.getImg(), iShowTalkApplication.getInstance().getIgetImageOptions(context,60,5));
		if(TextUtils.equals(recent.getIsOnline(), "1")){
			holder.onlionState.setImageResource(R.drawable.flag_status_online);
		}else if(TextUtils.equals(recent.getIsOnline(), "2")){
			holder.onlionState.setImageResource(R.drawable.flag_status_busy);
		}else{
			holder.onlionState.setImageResource(R.drawable.flag_status_offline);
		}
		//holder.onlionState.setImageResource(recent.isOnlion()?R.drawable.flag_status_online:R.drawable.flag_status_busy);
		return convertView;
	}
	
	class ViewHolder{
		ImageView head;
		TextView name;
		ImageView onlionState;
		TextView school;
	}

}
