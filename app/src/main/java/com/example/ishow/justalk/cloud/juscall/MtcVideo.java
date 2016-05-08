package com.example.ishow.justalk.cloud.juscall;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.justalk.cloud.lemon.MtcMediaConstants;
import com.justalk.cloud.lemon.ST_MTC_RECT;

import java.util.Map;

public class MtcVideo {
    
    public static int sMode = MtcMediaConstants.EN_MTC_DISPLAY_FULL_CONTENT;
    
    public static ST_MTC_RECT calcSmallViewRect(Context c,int localWidth, int localHeight,
    		int screenWidth, int screenHeight) {
        int height = 0, width = 0;
        if (localWidth > 0 && localHeight > 0) {
            /* height = (int) Math.sqrt(screenWidth * screenHeight * localHeight / localWidth / 24); */
            /* not work in Nexus 5 and Meizu MX 3 */
        	float scale = MtcCallDelegate.getSmallViewSize();
            height = (int) Math.sqrt(screenWidth * screenHeight * scale * localHeight / localWidth);
            if (Build.MODEL.equals("GT-I9000")) {
                int remainder = height % 16;
                if (remainder <= 8) {
                    height -= remainder;
                } else {
                    height += 16 - remainder;
                }
            }
            width = height * localWidth / localHeight;
        }
        int x = 0, y = 0;
        Map<String, String> map = MtcCallDelegate.getSmallViewLocate();
        if (map != null) {
        	try {
    			float xValue = Float.valueOf(map.get(MtcCallDelegate.SMALL_VIEW_X)).floatValue();
    			float yValue = Float.valueOf(map.get(MtcCallDelegate.SMALL_VIEW_Y)).floatValue();
    			int localX = (int) (screenWidth * xValue);
    			int localY = (int) (screenHeight * yValue);
    			if (localX < screenWidth - width) 
    				x = localX;
    			else
    				x = screenWidth - width;
    			
    			if (localY < screenHeight - height) 
    				y = localY;
    			else
    				y = screenHeight - height;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				x = 10;
				y = 10;
			}
			
		} else {
			x = 10;
			y = 10;
		}
        ST_MTC_RECT localRect = new ST_MTC_RECT();
        localRect.setIX(x);
        localRect.setIY(y);
       /* localRect.setIWidth(width);
        localRect.setIHeight(height);*/
        localRect.setIWidth(350);
        localRect.setIHeight(470);
        return localRect;
    }
    
    public static ST_MTC_RECT getViewRect(View v) {
        ST_MTC_RECT rect = new ST_MTC_RECT();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v.getLayoutParams();
        rect.setIX(params.leftMargin);
        rect.setIY(params.topMargin);
        rect.setIWidth(params.width);
        rect.setIHeight(params.height);
        return rect;
    }

    public static void setViewRect(View v, ST_MTC_RECT rect) {
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(rect.getIWidth(), rect.getIHeight());
        rlp.leftMargin = rect.getIX();
        rlp.topMargin = rect.getIY();

        v.setLayoutParams(rlp);
    }
    
    public static class OnTouchMoveListener implements OnTouchListener {

        int offsetX = 0;
        int offsetY = 0;
        long startTimeStamp = 0;
        
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            final int action = event.getAction();
            final int x = (int) event.getRawX();
            final int y = (int) event.getRawY();
            int width = v.getWidth();
            int height = v.getHeight();
            View mainView = (View) v.getParent();
            int screenWidth = mainView.getWidth();
            int screenHeight = mainView.getHeight();
            ViewGroup.LayoutParams p = v.getLayoutParams();
            if (!(p instanceof RelativeLayout.LayoutParams))
                return false;
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) p;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    startTimeStamp = System.currentTimeMillis();
                    offsetX = rlp.leftMargin - x;
                    offsetY = rlp.topMargin - y;
                    break;
                case MotionEvent.ACTION_MOVE: 
                    int iX = (int) (offsetX + x);
                    int iY = (int) (offsetY + y);
                    if (iX < 0) 
                    	iX = 0;
                    else if (iX > screenWidth - width)
                    	iX = screenWidth - width;
                    
                    if (iY < 0) 
						iY = 0;
                    else if (iY > screenHeight - height)
                    	iY = screenHeight - height;
                    rlp.leftMargin = iX;
                    rlp.topMargin = iY;
                    v.setLayoutParams(rlp);
                    break;
                case MotionEvent.ACTION_UP:
                    long interval = System.currentTimeMillis() - startTimeStamp;
                    if (interval < 200
                            && Math.abs(rlp.leftMargin - x - offsetX) < 5
                            && Math.abs(rlp.topMargin - y - offsetY) < 5) {
                        v.performClick();
                    }
                    int iX2 = (int) (offsetX + x);
                    int iY2 = (int) (offsetY + y);
                    if (iX2 < 0) 
                    	iX2 = 0;
                    else if (iX2 > screenWidth - width)
                    	iX2 = screenWidth - width;
                    
                    if (iY2 < 0) 
						iY2 = 0;
                    else if (iY2 > screenHeight - height)
                    	iY2 = screenHeight - height;
                    
                    rlp.leftMargin = iX2;
                    rlp.topMargin = iY2;
                    v.setLayoutParams(rlp);
                    offsetX = 0;
                    offsetY = 0;
                    break;
            }
            return true;
        }
    }
}
