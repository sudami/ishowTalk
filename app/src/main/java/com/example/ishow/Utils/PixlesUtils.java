/**
 * 
 */
package com.example.ishow.Utils;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * @author MRME
 *
 */
public class PixlesUtils {


	/**
	 * 获取屏幕宽度
	 * @param mContext
	 * @return px
     */
	public static int getScreenWidthPixels(Context mContext) {

		WindowManager wm =(WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm =new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * 获取屏幕高度
	 * @param mContext
	 * @return  px
     */
	public static int getScreenHeightPixels(Context mContext) {

		WindowManager wm =(WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm =new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	/*
    * dp转化成px大小
    *
    */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
   /*
   *  px转化成dp大小
   *
   */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
}
