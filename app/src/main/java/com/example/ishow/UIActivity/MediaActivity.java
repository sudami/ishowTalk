package com.example.ishow.UIActivity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.R;
import com.handmark.pulltorefresh.library.extras.recyclerview.PullToRefreshRecyclerView;

import org.xutils.common.util.LogUtil;

import java.io.File;

import butterknife.OnClick;

/**
 * Created by MRME on 2016-05-05.
 */
public class MediaActivity extends AppBaseCompatActivity {
    View rootView;
    PullToRefreshRecyclerView refreshRecyclerView;
    LinearLayout emptyLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getView(R.layout.activity_media);
        setContentView(rootView);

        bindView2UI();

    }

    private void bindView2UI() {
        setAppBaseCompactActivity(rootView);
        setToolbar(true,getString(R.string.shipinluzhi_title));
        refreshRecyclerView = (PullToRefreshRecyclerView) rootView.findViewById(R.id.media_list);
        emptyLayout = (LinearLayout) rootView.findViewById(R.id.media_empty);
        recyclerView = refreshRecyclerView.getRefreshableView();
        GridLayoutManager manager = new GridLayoutManager(this,GridLayoutManager.DEFAULT_SPAN_COUNT,GridLayoutManager.VERTICAL,false);
        manager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(manager);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String videoPath = cursor.getString(1);//获取文件路径
        File file = new File(videoPath);
        LogUtil.e("存在" + file.exists());
    }

}
