package com.example.ishow.UIView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.ishow.Utils.PixlesUtils;

/**
 * Created by MRME on 2016-05-05.
 */
public class MediaRecorderBottomTextView extends TextView {

    private Context context;

    public MediaRecorderBottomTextView(Context context) {
        super(context);
        init(context);

    }

    public MediaRecorderBottomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MediaRecorderBottomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("NewApi")
    public MediaRecorderBottomTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {

        this.context = context;
    }

    Paint paint=null;
    boolean clear;
    @Override
    protected void onDraw(Canvas canvas) {
       if (!clear)
       {
           if (paint==null)
           {
               paint = new Paint(Paint.ANTI_ALIAS_FLAG);
               paint.setColor(Color.parseColor("#ff3a2f"));
               paint.setStyle(Paint.Style.FILL);
               paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
               paint.setStrokeWidth(PixlesUtils.dip2px(context,3));
           }
           canvas.drawLine(PixlesUtils.dip2px(context,10),getMeasuredHeight()-PixlesUtils.dip2px(context,3),getMeasuredWidth()-PixlesUtils.dip2px(context,10),getMeasuredHeight()-PixlesUtils.dip2px(context,3),paint);
       }
        else
       {
           if (paint==null)
           {
               paint = new Paint(Paint.ANTI_ALIAS_FLAG);
               paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
               canvas.drawPaint(paint);
           }
       }


        super.onDraw(canvas);
    }


    public void setBottomLineVisibity(boolean enable){
        clear =enable;
        invalidate();
    }

}
