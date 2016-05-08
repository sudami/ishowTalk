package com.example.ishow.UIView;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.ishow.R;
import com.example.ishow.Utils.Interface.materialSeekbarAbClickListner;

/**
 * Created by MRME on 2016-04-12.
 */
public class MaterialSeekBar extends SeekBar implements View.OnClickListener {

    public String textA = "A";

    public String textB = "B";

    public String textC = "I";

    private float textCH;
    private float textAW;

    private materialSeekbarAbClickListner listner;

    public MaterialSeekBar(Context context) {
        super(context);
        init(context);
    }


    public MaterialSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MaterialSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mPaintA = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintA.setStyle(Paint.Style.STROKE);
        mPaintA.setColor(getResources().getColor(R.color.colorAccent));
        mPaintA.setTextSize(getResources().getDimension(R.dimen.material_seekbar_textsize));
        mPaintA.setTypeface(Typeface.DEFAULT_BOLD);
        Paint.FontMetrics fm = mPaintA.getFontMetrics();
        textAW = mPaintA.measureText(textA);
        textCH = (float) (Math.ceil(fm.descent - fm.top) + 2);

    }

    private Paint mPaintA;

    private ImageView image;


    private boolean isClear=false;

    public void addOnMaterialSeekbarAbClickListner(materialSeekbarAbClickListner l){
        this.listner =l;
    }
    @Override
    protected synchronized void onDraw(Canvas canvas) {

        super.onDraw(canvas);
       //mPaintA.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        // 获取height ：
      if (!isClear){
          if (positionX_A!=0){
              canvas.drawText(textA, positionX_A, positonY_A, mPaintA);
              canvas.drawText(textC, positionX_A+textAW/4, positonY_A/2+textCH/4, mPaintA);
          }

          if(positionX_B!=0){
              canvas.drawText(textB, positionX_B, positonY_B, mPaintA);
              canvas.drawText(textC, positionX_B+textAW/4, positonY_B/2+textCH/4, mPaintA);
          }
      }else{

          clearCanvas(canvas);

      }

    }



    public void clearCanvas(Canvas canvas) {
        mPaintA.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawText("", 0, 0, mPaintA);
        positionX_A=0;
        positionX_B=0;
        isClear=!isClear;
    }

    public void startClearCanvas() {
        isClear=!isClear;
        invalidate();
        image.setImageResource(R.mipmap.ab);
        clickCount = 0;
    }

    float  positionX_A = 0;
    float  positonY_A = 0;
    public void startDrawA() {
        mPaintA.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        positionX_A = (float)getProgress()/(float)getMax()*getWidth();
        positonY_A  =getHeight();
        invalidate();
    }

    float positionX_B = 0;
    float positonY_B = 0;
    public void startDrawB() {
        mPaintA.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        positionX_B = (float)getProgress()/(float)getMax()*getWidth();
        positonY_B  =getHeight();
        invalidate();
    }

    //默认控制器点击次数为0
    int clickCount = 0;

    //设置 AB段的 控制器
    public void setABImageView(ImageView image) {
        this.image = image;
        image.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (clickCount == 0) {
            ((ImageView) v).setImageResource(R.mipmap.a_48);
            clickCount++;
            startDrawA();
            if (listner!=null)listner.drawTextA(getProgress());
        } else if (clickCount == 1) {
            ((ImageView) v).setImageResource(R.mipmap.b_88);
            startDrawB();
            clickCount++;
            if (listner!=null)listner.drawTextB(getProgress());
        } else if (clickCount == 2) {
            startClearCanvas();
            if (listner!=null)listner.clear();
        }
    }
}
