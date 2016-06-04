package com.example.ishow.UIView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.ishow.R;
import com.example.ishow.Utils.PixlesUtils;

import org.xutils.common.util.LogUtil;

/**
 * Created by MRME on 2016-05-05.
 */
public class MediaRecorderBottomTextView extends TextView {

    private Context context;

    public MediaRecorderBottomTextView(Context context) {
        super(context);
        init(null,context);

    }

    public MediaRecorderBottomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,context);
    }

    public MediaRecorderBottomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    @SuppressLint("NewApi")
    public MediaRecorderBottomTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs,context);
    }

    int color;
    int clearColor;
    float lineHeight =3;
    private void init(AttributeSet attrs, Context context) {
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.BottomTextView);
        color = ta.getColor(R.styleable.BottomTextView_bottomlinecolor, 0);
        clearColor = ta.getColor(R.styleable.BottomTextView_bottomlinecolor_clear, 0);
        lineHeight = ta.getDimension(R.styleable.BottomTextView_bottomlinecolor_lineHeight, 1);
        if (color==0)color = Color.parseColor("#ff3a2f");
        if (clearColor==0)clearColor = Color.parseColor("#2b2d32");
        ta.recycle();

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
           }
           paint.setStyle(Paint.Style.FILL_AND_STROKE);

           paint.setColor(color);
          // paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
           paint.setStrokeWidth(PixlesUtils.dip2px(context,lineHeight));
           canvas.drawRect(PixlesUtils.dip2px(context,10),getMeasuredHeight()-PixlesUtils.dip2px(context,3),getMeasuredWidth()-PixlesUtils.dip2px(context,10),getMeasuredHeight()-PixlesUtils.dip2px(context,3),paint);
       }
        else
       {
           if (paint!=null)
           {
               paint.setColor(clearColor);
               paint.setStyle(Paint.Style.FILL);
               canvas.drawPaint(paint);
           }
       }
        super.onDraw(canvas);
    }


    public void setBottomLineVisibity(boolean enable){
        clear =enable;
        invalidate();
    }
    public void setChecked(boolean enable){
        setTextColor(enable?getResources().getColor(R.color.colorPrimary):Color.parseColor("#333333"));
    }
}
