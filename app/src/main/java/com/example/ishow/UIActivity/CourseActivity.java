package com.example.ishow.UIActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.ishow.Adapter.CourseEntryAdapter;
import com.example.ishow.Application.iShowTalkApplication;
import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.BaseComponent.DividerItemDecoration;
import com.example.ishow.Bean.CourseEntry;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.R;
import com.example.ishow.Service.MusicDownLoadService;
import com.example.ishow.Utils.JsonUtils;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.StorageUtils;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.extras.recyclerview.PullToRefreshRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by MRME on 2016-03-29.
 */
public class CourseActivity extends AppBaseCompatActivity implements CourseEntryAdapter.RecycleViewOnclickListner {
    PullToRefreshRecyclerView refreshListView;
    private String title;
    private String classId;
    private RecyclerView recyclerView;

    private LinkedList<CourseEntry> datas = new LinkedList<>();
    private List<String> musicPath = null;
    private CourseEntryAdapter adapter;
    private MusicDownLoadService downLoadService;
    private CourseEntry needDownloadEntry;
    private View contentView;
    private UserEntry studentnfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentView =getView(R.layout.activity_course);
        refreshListView = (PullToRefreshRecyclerView) contentView.findViewById(R.id.refresh_list_view);
        setContentView(contentView);
        title = getIntent().getStringExtra("courseName");
        classId = getIntent().getStringExtra("courseId");
        setAppBaseCompactActivity(contentView);
        setToolbar(true,title);
        EventBus.getDefault().register(this);
        Intent intent = new Intent(this, MusicDownLoadService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        setRecycleView();

    }

    private void setRecycleView() {
        refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        recyclerView = refreshListView.getRefreshableView();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // recyclerView.setItemAnimator(new SlideInOutTopItemAnimator(recyclerView));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
        refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                getClassIdCousrse();
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    LogUtil.e(firstVisibleItemPosition+"onScrollStateChanged"+lastVisibleItemPosition);
                    if(adapter!=null)adapter.notifyItemRangeChanged(firstVisibleItemPosition, lastVisibleItemPosition - firstVisibleItemPosition);

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        studentnfo= SharePrefrence.getInstance().getStudentInfo(getApplicationContext());
    }

    /**
     * 网络获取课程信息
     */
    private void getClassIdCousrse() {
        refreshListView.setRefreshing(true);
        JSONObject mJSONObject = new JSONObject();
        try {
            mJSONObject.put("cid", classId);
            mJSONObject.put("mobile", studentnfo.getMobile());
            mJSONObject.put("pageSize", 1000);
            mJSONObject.put("page", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        XHttpUtils.getInstace().getValue(iShowConfig.getCourseById, mJSONObject, new XHttpUtils.OnHttpCallBack() {
            @Override
            public void onSuccess(String result) {
                refreshListView.onRefreshComplete();
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getInt("code") == 1) {
                        //gson.fromJson(object.getJSONArray("data").toString(),new TypeToken<CourseEntry>(){}.getType());
                        SharePrefrence.getInstance().putCourseJson(CourseActivity.this, "15555043403", title, object.getJSONArray("data").toString());
                        datas = JsonUtils.getCourseEntryFromJson(object.getJSONArray("data").toString());
                        addContentView(datas);
                    } else {
                        showTipContent(object.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String errorResson) {
                LogUtil.e("errorResson" + errorResson);
                refreshListView.onRefreshComplete();
                showTipContent(errorResson);

            }
        });
    }


    private void addContentView(LinkedList<CourseEntry> datas) {

        if (adapter == null) {
            addContentView(contentView);
            adapter = new CourseEntryAdapter(this, datas);
            recyclerView.setAdapter(adapter);
            adapter.setOnRecycleViewOnclickListner(this);
        }else{
            adapter.notifyItemRangeChanged(0,datas.size()-1);
        }
       // container.setVisibility(View.GONE);
       // refreshListView.setVisibility(View.VISIBLE);
    }


    //根据 课程id 获取其对应的 所有短音频地址
    @Override
    public void onRecycleViewOnclickListner(final int posotion) {
        needDownloadEntry = datas.get(posotion);

        switch (needDownloadEntry.getCourseState()) {
            //没下载
            case 0:
                needDownloadEntry.setCourseState(4);
                needDownloadEntry.setProgressbar(1);
                startDownloadCourseMusicPathByCourseId(needDownloadEntry, posotion);
                break;
            //正在下载
            case 1:
                needDownloadEntry.setCourseState(2);
                downLoadService.pauseDownloadQueue(needDownloadEntry.getId());
                break;
            //暂停下载
            case 2:
                needDownloadEntry.setCourseState(4);
                startDownloadCourseMusicPathByCourseId(needDownloadEntry, posotion);
                // downLoadService.resumeDownloadQeueu(this,needDownloadEntry);
                break;
        }
        adapter.changeCourseEntry(needDownloadEntry, posotion);
    }

    @Override
    public void onRecycleViewItemclickListner(CourseEntry entry,String claz) {
        Intent intent =new Intent(this,MusicPlayActivity.class);
        intent.putExtra("title",title+"-"+claz);
        Bundle bundle =new Bundle();
        bundle.putParcelable("courseEntry",entry);
        intent.putExtras(bundle);
        ActivityCompat.startActivity(this,intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    }

    //开始从服务器拉取 课时 下载地址的信息
    private void startDownloadCourseMusicPathByCourseId(final CourseEntry entry, final int posotion) {
        //开始下载一个课时 音频之前。先从本地拿到 缓存的 本课时下载地址 否则再网络获取
        List<String> downloadPath = SharePrefrence.getInstance().getCourseJsonDownloadPath(this, entry.getId());
        if (downloadPath == null||downloadPath.size()==0) {
            try {
                JSONObject object = new JSONObject();
                object.put("id", entry.getId() + "");
                XHttpUtils.getInstace().getValue(iShowConfig.getAllMusicPathById, object, new XHttpUtils.OnHttpCallBack() {
                    @Override
                    public void onSuccess(final String result) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                startDownloadService(result, entry, posotion);
                            }
                        }).start();
                    }

                    @Override
                    public void onError(String errorResson) {
                        LogUtil.e("onError" + errorResson);
                        entry.setCourseState(0);
                        adapter.changeCourseEntry(entry, posotion);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //SharePrefrence.getInstance().putCourseJsonDownloadPath(CourseActivity.this, entry.getId(), result, musicPath.size());
            /*entry.setBaseCourseMaxDownloadSize(musicPath.size());
            entry.setContent(title + String.format(getString(R.string.class_course_title), posotion + 1));
            entry.setDirPath(StorageUtils.getInstance().getPathNameDirs(title + "/" + entry.getId()));
            //将下载的课程保存到数据库 方便下载管理功能 取数据
            try {
                x.getDb(iShowTalkApplication.getInstance().initDbConfig()).save(entry);
            } catch (DbException e) {
                e.printStackTrace();
            }*/


            entry.setBaseCourseMaxDownloadSize(downloadPath.size());
            entry.setDirPath(StorageUtils.getInstance().getPathNameDirs(title + "/" + entry.getId()));
            //开始一个下载队列
            downLoadService.startDownloadQueue(entry, downloadPath);
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downLoadService = ((MusicDownLoadService.MusicDownLoadServiceBind) service).getInstace();
            String courseJson = SharePrefrence.getInstance().getCourseJson(CourseActivity.this, "15555043403", title);
            if (courseJson == null || courseJson == "") {
                //从服务器获取 课程信息
                getClassIdCousrse();
            } else {
                //从本地shareprefrence中获取该课程的一些 json信息
                getClassIdCourseFromShareprefrence(courseJson);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            downLoadService = null;
            serviceConnection = null;
        }
    };

    //从本地shareprefrence中获取该课程的一些 json信息
    private void getClassIdCourseFromShareprefrence(final String courseJson) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                datas = JsonUtils.getCourseEntryFromLocalJson(CourseActivity.this, courseJson, title, downLoadService);
                handler.sendEmptyMessage(0);
            }
        }).start();

    }

    //加载完本地课程信息 以及检查每个课时状态后 更新界面
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            addContentView(datas);
        }
    };

    //开始一个课时任务的下载
    private void startDownloadService(String result, final CourseEntry entry, int posotion) {
        if (musicPath == null)
            musicPath = new ArrayList<>();
        musicPath.clear();
        musicPath = JsonUtils.getMusicPathByCourseId(result);
        if (musicPath == null||musicPath.size()==0) {
            //Toast.makeText(CourseActivity.this, getString(R.string.no_course_to_download), Toast.LENGTH_LONG).show();
            entry.setCourseState(0);
            adapter.changeCourseEntry(entry, posotion);

            return;
        }
        //把获取到课时 下载地址 json保存起来
        SharePrefrence.getInstance().putCourseJsonDownloadPath(CourseActivity.this, entry.getId(), result, musicPath.size());
        entry.setBaseCourseMaxDownloadSize(musicPath.size());
        entry.setContent(title + String.format(getString(R.string.class_course_title), posotion + 1));
        entry.setDirPath(StorageUtils.getInstance().getPathNameDirs(title + "/" + entry.getId()));
        //将下载的课程保存到数据库 方便下载管理功能 取数据
        try {
            x.getDb(iShowTalkApplication.getInstance().initDbConfig()).save(entry);
        } catch (DbException e) {
            e.printStackTrace();
        }
        //开始一个下载队列
        downLoadService.startDownloadQueue(entry, musicPath);
    }

    //接受 后台传过来的更新数据
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(CourseEntry entry) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        int lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        for (int i = firstVisibleItemPosition; i < lastVisibleItemPosition; i++) {
            CourseEntry entry1 = datas.get(i);
            if (entry1.getId() == entry.getId()) {
                entry1.setProgressbar(entry.getProgressbar());
                entry1.setCourseState(entry.getCourseState());
                adapter.changeCourseEntry(entry1, i);
                break;
            }
        }


    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (downLoadService != null)
            unbindService(serviceConnection);
        super.onDestroy();
    }
}
