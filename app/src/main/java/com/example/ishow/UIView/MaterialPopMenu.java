package com.example.ishow.UIView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.ishow.Adapter.MusciPlayAdapter;
import com.example.ishow.Bean.ShortCourseEntry;
import com.example.ishow.R;
import com.example.ishow.Utils.Interface.MaterialPopInterface;
import com.example.ishow.Utils.PixlesUtils;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MRME on 2016-04-14.
 */
public class MaterialPopMenu implements AdapterView.OnItemClickListener {

    private Context context;
    private ArrayList<ShortCourseEntry> datas;
    private LayoutInflater inflater;
    private PopupWindow pop;
    public TextView Title;
    private ListView listView;
    private MusciPlayAdapter adapter;
    private View popView;

    public MaterialPopMenu(Context context, ArrayList<ShortCourseEntry>  datas){

        this.context = context;
        this.datas = datas;
        inflater=LayoutInflater.from(context);

    }
    public void showMenu(final ImageView parent, String popTitle, int playIndex){
       if (pop==null){
           popView =inflater.inflate(R.layout.activity_music_play_popmenu,null);
           pop = new PopupWindow(popView, PixlesUtils.dip2px(context,200), PixlesUtils.dip2px(context,240));
           pop.setContentView(popView);
           pop.setBackgroundDrawable(new BitmapDrawable());
           //pop.setAnimationStyle(R.style.PopupAnimation);
           pop.setFocusable(true);
           pop.setOutsideTouchable(true);
           Title = (TextView) popView.findViewById(R.id.menu_title);
           listView = (ListView) popView.findViewById(R.id.menu_listview);

           if (adapter==null){
               adapter =new MusciPlayAdapter(context,datas,playIndex);
           }else adapter.setisPalyingIndex(playIndex);
           listView.setAdapter(adapter);
           listView.setOnItemClickListener(this);
           Title.setText(popTitle+"("+datas.size()+")节");
       }
        int[] position =new int[2];
        parent.getLocationOnScreen(position);
        LogUtil.e(position[0]+"----"+position[1]+"-"+parent.getWidth()+"--"+parent.getHeight());

        pop.showAsDropDown(parent,-PixlesUtils.dip2px(context,50),parent.getHeight());
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                parent.setImageResource(R.drawable.iconfont_liebiao);
            }
        });
        LogUtil.e(pop.getWidth()+"*-"+pop.getHeight());
    }

    public void hidePop(){
        pop.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        adapter.setisPalyingIndex(position);
        hidePop();
        if (popInterface!=null)popInterface.onPopMenuItemClick(adapter.getItem(position).getTitle(),position);
    }
    MaterialPopInterface popInterface;
    public void setOnPopMenuItemClickListener(MaterialPopInterface listener){
        this.popInterface =listener;
    }

}
