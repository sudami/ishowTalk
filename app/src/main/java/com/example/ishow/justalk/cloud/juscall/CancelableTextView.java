package com.example.ishow.justalk.cloud.juscall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class CancelableTextView extends TextView {

    private DrawableRightListener mRightListener ;

    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;

    public CancelableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CancelableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CancelableTextView(Context context) {
        super(context);
    }

    public void setDrawableRightListener(DrawableRightListener listener) {
        this.mRightListener = listener;
    }

    public interface DrawableRightListener {
        void onDrawableRightClick(View view) ;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mRightListener != null) {
                    Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT] ;
                    if (drawableRight != null) {
                        if (event.getX() > getWidth() - getPaddingRight() - drawableRight.getIntrinsicWidth()) {
                            mRightListener.onDrawableRightClick(this);
                            return true;
                        }
                    }
                }
                break;
        }

        return super.onTouchEvent(event);
    }

}
