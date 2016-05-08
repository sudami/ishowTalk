package com.example.ishow.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ishow.R;

import butterknife.ButterKnife;


/**
 * Created by MRME on 2016-03-28.
 */
public abstract class BaseFragment extends Fragment {

    public LayoutInflater inflater;
    public View rootView =null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 获取XML对应的 View 对象
     * @param layoutId
     * @return
     */
    public View getView(int layoutId){
        this.inflater = LayoutInflater.from(getActivity());
        View rootView = inflater.inflate(layoutId,null);
//        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
