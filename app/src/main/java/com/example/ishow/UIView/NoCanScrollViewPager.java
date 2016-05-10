package com.example.ishow.UIView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by MRME on 2016-05-10.
 */
public class NoCanScrollViewPager extends ViewPager {
    public NoCanScrollViewPager(Context context) {
        super(context);
    }

    public NoCanScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void scrollTo(int x, int y) {

    }
}
