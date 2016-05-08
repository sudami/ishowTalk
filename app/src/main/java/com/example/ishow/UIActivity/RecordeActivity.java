package com.example.ishow.UIActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.ishow.Adapter.LocalCourseAdapter;
import com.example.ishow.Adapter.RecorderAdapter;
import com.example.ishow.Application.iShowTalkApplication;
import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Bean.CourseEntry;
import com.example.ishow.R;
import com.example.ishow.Utils.PixlesUtils;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.StorageUtils;
import com.example.ishow.Utils.ToastUtil;

import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by MRME on 2016-04-08.
 */
public class RecordeActivity extends AppBaseCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    /*@Bind(R.id.course_title_name)
    TextView courseTitleName;
    @Bind(R.id.Toolbar)
    android.support.v7.widget.Toolbar Toolbar;
    @Bind(R.id.listView)*/
    SwipeMenuListView mListView;
    private ArrayList<String> all = new ArrayList<String>();
    private RecorderAdapter adapter;
    View contentView;
    private Dialog dialog;
    private PopupWindow pop;
    private int position;//长按删除的position

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
      //  setAppBaseCompactActivity();
       // setToolbarneedForResult(getString(R.string.fans_title));
        super.onCreate(savedInstanceState);
        contentView =getView(R.layout.activity_recorder);
        setContentView(contentView);
        setAppBaseCompactActivity(contentView);
        setToolbar(true,getString(R.string.luyin_title));
        mListView = (SwipeMenuListView) contentView.findViewById(R.id.listView);
        //courseTitleName.setText(getString(R.string.luyin_title));
        mListView.setOnItemLongClickListener(this);
        Toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
        getLocalCourseDatas();
    }

    private void finishActivity() {
        /*Intent intent = new Intent();
        intent.putExtra("luyin",all.size()+"");
        setResult(2,intent);
        ActivityCompat.finishAfterTransition(RecordeActivity.this);*/
        SharePrefrence.getInstance().putAudioSize(getApplicationContext(),all.size()+"");
        ActivityCompat.finishAfterTransition(RecordeActivity.this);
    }

    /**
     * 获取 数据库中 已下载的可是信息 但该课时 不一定下载完全。。但会有完全下载应该有的文件个数
     */
    private void getLocalCourseDatas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String nameDirs = StorageUtils.getInstance().getPathNameDirs("recoder");
                File file = new File(nameDirs);
                if (file.exists())
                {
                    File[] files = file.listFiles(getFileNameFilter());
                    if (files.length==0){
                        showTipContent(getString(R.string.luyin_no_files));
                    }else{
                        for (File f:files) {
                            all.add(f.getPath());
                        }
                    }
                    handler.sendEmptyMessage(0);
                }else{
                    handler.sendEmptyMessage(2);
                }
            }
        }).start();
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    if (all != null)
                    setSwipListView();
                    break;
                case 1:
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                    break;
                case 2:
                    showTipContent(getString(R.string.no_storage_permission));
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void setSwipListView() {
        addContentView(mListView);
        adapter = new RecorderAdapter(this,  all);
        mListView.setAdapter(adapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
               /* SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);*/

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);
        mListView.setOpenInterpolator(new BounceInterpolator());
        mListView.setCloseInterpolator(new AnticipateOvershootInterpolator());
        mListView.setOnItemClickListener(this);
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        ToastUtil.showToast(RecordeActivity.this,"shanchu");
                        // delete
                        RecordeActivity.this.position=position;
                        deleteCourse();
                        break;
                    case 1:
                       /* ToastUtil.showToast(RecordeActivity.this,"shanchu");
                        // delete
                        RecordeActivity.this.position=position;
                        deleteCourse();*/
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    private void deleteCourse() {
        if (dialog==null){
            dialog= new Dialog(this);
            dialog.setContentView(R.layout.progressbar);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();
        String path = all.get(position);
        File file =new File(path);
        if (file.exists())
            file.delete();
        all.remove(position);
        adapter.notifyDataSetChanged();
        dialog.dismiss();
    }
    public FilenameFilter getFileNameFilter(){
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.contains(".amr"))
                    return true;
                return false;
            }
        };
        return filter;
    }
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        this.position = position;
        showDeletePopDialog(view);
        return false;
    }

    private void showDeletePopDialog(View view) {
        if (pop==null){
            View v = View.inflate(RecordeActivity.this, R.layout.long_press_delete,null);
            pop =new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            pop.setBackgroundDrawable(new BitmapDrawable());
            pop.setContentView(v);
            pop.setFocusable(true);
            v.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    deleteCourse();
                    pop.dismiss();
                   // ToastUtil.showToast(RecordeActivity.this,"shanchu");
                }
            });
            LogUtil.e(view.getWidth()/2+""+view.getHeight()+"--"+ pop.getWidth());
        }
        pop.showAsDropDown(view,view.getWidth()/2- PixlesUtils.dip2px(RecordeActivity.this,70)/2,-view.getHeight());

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String filePath = all.get(position);
        Intent it = new Intent(Intent.ACTION_VIEW);
        it.setDataAndType(Uri.parse("file://" +filePath), "audio/amr");
        startActivity(it);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishActivity();
    }
}
