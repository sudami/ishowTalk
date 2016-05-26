package com.example.ishow.ViewFlow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.ishow.Bean.UserEntry;
import com.example.ishow.Bean.ViewFlowEntry;
import com.example.ishow.R;
import com.example.ishow.UIActivity.BannerActivity;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.StorageUtils;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by MRME on 2016-03-28.
 */
public class ExtendViewFlow extends  ViewFlow {
    ExtendViewFlowAdapter adapter;
    List<ViewFlowEntry> Entrys ;
    public ExtendViewFlow(Context context) {
        this(context,0);

    }

    public ExtendViewFlow(Context context, int sideBuffer) {
        this(context,null);
    }

    public ExtendViewFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initData(final Context context, final ExtendViewFlow extendViewFlow, final CircleFlowIndicator viewflowIndicator) {

        JSONObject object =new JSONObject();
        UserEntry studentInfo = SharePrefrence.getInstance().getStudentInfo(context.getApplicationContext());
        try {
            object.put("mobile",studentInfo.getMobile());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        XHttpUtils.getInstace().getValue(iShowConfig.getBanner,object,new XHttpUtils.OnHttpCallBack(){

            @Override
            public void onSuccess(String result) {
                Entrys = new ArrayList<ViewFlowEntry>();
                Gson gson =new Gson();
                Entrys = gson.fromJson(result,new TypeToken<List<ViewFlowEntry>>(){}.getType());
                setAdapter(context, extendViewFlow,viewflowIndicator,true);
            }

            @Override
            public void onError(String errorResson) {
                Entrys = new ArrayList<ViewFlowEntry>();
                Entrys = StorageUtils.getInstance().getBannerCachePath(context,"banner");
                setAdapter(context, extendViewFlow,viewflowIndicator,false);
            }
        });
    }

    private void setAdapter(Context context, ExtendViewFlow extendViewFlow,CircleFlowIndicator viewflowIndicator,Boolean net) {
        adapter = new ExtendViewFlowAdapter(context,Entrys,net);
        extendViewFlow.setAdapter(adapter);
        extendViewFlow.setmSideBuffer(Entrys==null?1:Entrys.size());
        extendViewFlow.setFlowIndicator(viewflowIndicator);
        extendViewFlow.setTimeSpan(3000);
        extendViewFlow.setSelection(3000);
        extendViewFlow.startAutoFlowTimer();
    }

    class  ExtendViewFlowAdapter extends BaseAdapter{

        private Context context;
        private List<ViewFlowEntry> list;
        private Boolean net;

        public  ExtendViewFlowAdapter(Context context, List<ViewFlowEntry> list,Boolean net){

            this.context = context;
            this.list = list;
            this.net = net;
        }
        @Override
        public int getCount() {
           return Integer.MAX_VALUE;
        }

        @Override
        public Object getItem(int position) {
            if (list!=null) return list.get(position);
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewPagerHolder holder=null;
            if (convertView==null){
                holder = new ViewPagerHolder();
                convertView = View.inflate(context, R.layout.fragment_banner_image,null);
                holder.image = (ImageView) convertView.findViewById(R.id.fragment_course_banner_image);
                convertView.setTag(holder);
            }else{
                holder = (ViewPagerHolder) convertView.getTag();
            }
           // x.image().bind(holder.image,list.get( position % list.size()).getImg(), iShowTalkApplication.getInstance().getIgetBannerOptions(context, PixlesUtils.getScreenWidthPixels(context),200));
           if (net){
               ImageLoader.getInstance().displayImage(list.get( position % list.size()).getImg(),holder.image,new SimpleImageLoadingListener(){
                   @Override
                   public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                       if (position<list.size()){

                           StorageUtils.getInstance().saveBitmapCheckStroagePermision("banner",list.get( position % list.size()).getPic_id(),context,loadedImage);
                       }

                       super.onLoadingComplete(imageUri, view, loadedImage);
                   }
               });
           }else{

              if (list!=null)
              {
                  if (list.size()>0)
                      holder.image.setImageBitmap(StorageUtils.getInstance().getBannerBitmap(list.get( position % list.size()).getTitle()));
                    // else holder.image.setImageResource(R.mipmap.banner);
              }//else   holder.image.setImageResource(R.mipmap.banner);
               //ImageLoader.getInstance().displayImage(list.get( position % list.size()).getTitle(),holder.image);
           }
          if (list!=null){
              if (list.size()>0){
                  holder.image.setOnClickListener(new OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          Intent intent = new Intent(context, BannerActivity.class);
                          intent.putExtra("center_image_link",list.get( position % list.size()).getLink());
                          LogUtil.e("dizhi"+list.get( position % list.size()).getLink());
                          context.startActivity(intent);
                      }
                  });
              }
          }
            return convertView;
        }

        class ViewPagerHolder{
            ImageView image;
        }
    }
}
