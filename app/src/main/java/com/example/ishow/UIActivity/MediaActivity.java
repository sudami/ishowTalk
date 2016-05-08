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

    private Button gallery, captrue, cancel;
    private Dialog dialog;
    private void showPopDialog() {
        if (dialog==null){
            View view = getLayoutInflater().inflate(R.layout.photo_choose_dialog, null);
            gallery = (Button) view.findViewById(R.id.media_gallery);
            captrue = (Button) view.findViewById(R.id.media_captrue);
            cancel = (Button) view.findViewById(R.id.media_cancel);
            dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
            dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = dialog.getWindow();
            // 设置显示动画
            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = getWindowManager().getDefaultDisplay().getHeight();
            // 以下这两句是为了保证按钮可以水平满屏
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            // 设置显示位置
            dialog.onWindowAttributesChanged(wl);
            // 设置点击外围解散
            dialog.setCanceledOnTouchOutside(true);
        }
        dialog.show();
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

    @OnClick({R.id.media_gallery, R.id.media_captrue, R.id.media_cancel})
    public void onClick(View view) {
        Intent intent1 = null;
        switch (view.getId()) {
            case R.id.media_gallery:
                intent1 = new Intent();
                intent1.setAction(Intent.ACTION_PICK);
                intent1.setType("video/*");
                startActivityForResult(intent1,1002);
                dialog.cancel();
                break;
            case R.id.media_captrue:
                intent1 = new Intent(this,MediaRecordActivity.class);
                startActivityForResult(intent1,1003);
                dialog.cancel();
                break;
            case R.id.media_cancel:
                dialog.cancel();
                break;
        }
    }
}
