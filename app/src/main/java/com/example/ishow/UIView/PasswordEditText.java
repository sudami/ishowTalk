package com.example.ishow.UIView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;

import com.example.ishow.R;
import com.example.ishow.Utils.PixlesUtils;

/**
 * Created by Shine on 09/02/16.
 */
public class PasswordEditText extends EditText {

    private final int EXTRA_TOUCH_AREA = 50;
    private Drawable mHideDrawable;
    private Drawable mShowDrawable;
    private boolean mPasswordVisible = false;
    private boolean touchDown;
    private boolean cleartext=false;
    private boolean mShowAsText;
    private String mHideTextString;
    private String mShowTextString;


    public PasswordEditText(Context context) {
        super(context);
    }

    public PasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }


    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, 0, 0);
    }

    @TargetApi(21)
    public PasswordEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setBackgroundColor(Color.TRANSPARENT);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.password_edit_text, defStyleAttr, defStyleRes);

        int hideDrawableResId = R.mipmap.in_anshul_hide_password;
        int showDrawableResId = R.mipmap.in_anshul_show_password;

        try {
            cleartext = a.getBoolean(R.styleable.password_edit_text_clear_text, false);
            mPasswordVisible = a.getBoolean(R.styleable.password_edit_text_password_visible, false);
            hideDrawableResId = a.getResourceId(R.styleable.password_edit_text_hide_drawable, hideDrawableResId);
            showDrawableResId = a.getResourceId(R.styleable.password_edit_text_show_drawable, showDrawableResId);
            mShowAsText = a.getBoolean(R.styleable.password_edit_text_show_as_text, false);
            if (mShowAsText) {
                mShowTextString = a.getString(R.styleable.password_edit_text_show_text);
                mHideTextString = a.getString(R.styleable.password_edit_text_hide_text);
            }
        } finally {
            a.recycle();
            mHideDrawable = ContextCompat.getDrawable(getContext(), hideDrawableResId);
            mShowDrawable = ContextCompat.getDrawable(getContext(), showDrawableResId);
            if (mShowAsText) {
                mHideTextString = TextUtils.isEmpty(mHideTextString) ? getContext().getString(R.string.hide_text) : mHideTextString;
                mShowTextString = TextUtils.isEmpty(mShowTextString) ? getContext().getString(R.string.show_text) : mShowTextString;
            }
        }

        mHideDrawable.setBounds(0, 0, mHideDrawable.getIntrinsicWidth(), mHideDrawable.getIntrinsicHeight());
        mShowDrawable.setBounds(0, 0, mShowDrawable.getIntrinsicWidth(), mShowDrawable.getIntrinsicHeight());
        if (!cleartext){
            if (mPasswordVisible) {
                showPassword();
            } else {
                hidePassword();
            }
        }
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
               if (cleartext) if (s.length()>0)  showPassword();else  setCompoundDrawables(null, null, null, null);
            }
        });
    }


    private void togglePasswordView() {
       if (!cleartext){
           if (mPasswordVisible) {
               hidePassword();
           } else {
               showPassword();
           }
       }else{
           //mShowDrawable.setAlpha(0);
           this.setText("");

       }
    }

    private void showPassword() {
        if (mShowAsText) {
            setCompoundDrawables(null, null, new TextDrawable(mHideTextString), null);
        } else {
            setCompoundDrawables(null, null, mHideDrawable, null);
        }
        setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        setSelection(getText().length());
        mPasswordVisible = true;
    }

    private void hidePassword() {
        if (mShowAsText) {
            setCompoundDrawables(null, null, new TextDrawable(mShowTextString), null);
        } else {
            setCompoundDrawables(null, null, mShowDrawable, null);
        }
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        setSelection(getText().length());
        mPasswordVisible = false;
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
       // canvas.drawLine(8,getHeight()-10,getWidth()-8,getHeight()-10, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int right = getRight();
        final int drawableSize = getCompoundPaddingRight();
        final int x = (int) event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (x + EXTRA_TOUCH_AREA >= right - drawableSize && x <= right + EXTRA_TOUCH_AREA) {
                    touchDown = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (x + EXTRA_TOUCH_AREA >= right - drawableSize && x <= right + EXTRA_TOUCH_AREA && touchDown) {
                    touchDown = false;
                    togglePasswordView();
                    return true;
                }
                touchDown = false;
                break;

        }
        return super.onTouchEvent(event);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable saveState = super.onSaveInstanceState();
        return new SavedState(saveState, mPasswordVisible);
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        SavedState savedState = (SavedState) state;

        mPasswordVisible = savedState.isPasswordVisible();
        if (mPasswordVisible) {
            showPassword();
        } else {
            hidePassword();
        }
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    private static class SavedState extends BaseSavedState {

        private boolean isPasswordVisible = false;

        public SavedState(Parcel source) {
            super(source);
            isPasswordVisible = source.readByte() != 0;
        }

        public SavedState(Parcelable superState, boolean passwordVisible) {
            super(superState);
            isPasswordVisible = passwordVisible;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte((byte) (isPasswordVisible == true ? 1 : 0));
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

        };

        public boolean isPasswordVisible() {
            return isPasswordVisible;
        }
    }

    private class TextDrawable extends Drawable {
        private String mText = "";
        private ColorStateList mPrefixTextColor;

        public TextDrawable(String text) {
            mText = text;
          //  mPrefixTextColor = getTextColors();
           // setBounds(0, 0, (int) getPaint().measureText(mText), (int) getTextSize());
        }

        @Override
        public void draw(Canvas canvas) {
            /*Paint paint = getPaint();

            paint.setColor(mPrefixTextColor.getColorForState(getDrawableState(), 0));
            int lineBaseline = getLineBounds(0, null);*/
          //  canvas.drawText(mText, 0, canvas.getClipBounds().top + lineBaseline, paint);
        }

        @Override
        public void setAlpha(int alpha) {/* Not supported */}

        @Override
        public void setColorFilter(ColorFilter colorFilter) {/* Not supported */}

        @Override
        public int getOpacity() {
            return 1;
        }
    }
}
