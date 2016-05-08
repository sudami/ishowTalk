package com.example.ishow.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.ishow.Fragment.ClazFragment;
import com.example.ishow.R;

import java.util.ArrayList;

/**
 * Created by MRME on 2016-04-19.
 */
public class FragmentTextRankAdapter extends FragmentPagerAdapter {

    private Context context;
    private ArrayList<Fragment> fragments;
    private boolean weekFlag;

    public FragmentTextRankAdapter(FragmentManager fm, Context context, ArrayList<Fragment> fragments) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return context.getResources().getString(R.string.rank_wenben_qaunguo);

            case 1:
                return context.getResources().getString(R.string.rank_wenben_school);
            case 2:
                return context.getResources().getString(R.string.rank_wenben_class);
        }
        return super.getPageTitle(position);
    }

    public void setWeekFlag(boolean WeekFlag){
        weekFlag = WeekFlag;
    }
}
