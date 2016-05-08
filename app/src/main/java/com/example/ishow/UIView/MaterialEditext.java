package com.example.ishow.UIView;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ViewParent;
import android.widget.EditText;

import com.example.ishow.R;
import com.example.ishow.Utils.PixlesUtils;

/**
 * Created by MRME on 2016-04-11.
 */
public class MaterialEditext extends EditText {
    public MaterialEditext(Context context) {
        super(context);
        init(context);
    }

    public MaterialEditext(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MaterialEditext(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaterialEditext(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.TRANSPARENT);
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ViewParent parent = getParent();
                if (parent!=null)
                    if (parent instanceof TextInputLayout)
                        ((TextInputLayout) parent).setErrorEnabled(false);
                        //((TextInputLayout) parent).setError("");
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //if (cleartext) if (s.length()>0)  showPassword();else  setCompoundDrawables(null, null, null, null);
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeWidth(2);
        if(this.isFocused() == true)

            paint.setColor(getResources().getColor(R.color.green));

        else

            paint.setColor(getResources().getColor(R.color.colorPrimary));

        canvas.drawLine(PixlesUtils.dip2px(getContext(),10),getHeight()-PixlesUtils.dip2px(getContext(),3),getWidth()-PixlesUtils.dip2px(getContext(),10),getHeight()- PixlesUtils.dip2px(getContext(),3), paint);
    }
}
