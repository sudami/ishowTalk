package com.example.ishow.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.example.ishow.Fragment.FragmentCourse;
import com.example.ishow.Fragment.FragmentMe;
import com.example.ishow.Fragment.FragmentMsgList;
import com.example.ishow.Fragment.FragmentPractice;

import java.util.ArrayList;

/**
 * Created by MRME on 2016-03-28.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private FragmentManager fm;
    private ArrayList<Fragment> fragments;

    public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fm = fm;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
       if (fragments==null){
           return null;

       }else{
           Fragment f = fragments.get(position);
           if (f==null){
               switch (position){
                   case 0:
                       f= new FragmentCourse();
                       break;
                   case 1:
                       f= new FragmentPractice();
                       break;
                   case 2:
                       f= new FragmentMsgList();
                       break;
                   case 3:
                       f= new FragmentMe();
                       break;

               }

           }
           return f;
       }
    }

    @Override
    public int getCount() {
        if (fragments!=null)
            return fragments.size();
        return 0;
    }



   /* @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment f=  (Fragment)super.instantiateItem(container, position);

        fm.beginTransaction().add(container.getId(),f).commit();
        return f;
    }*/


}
