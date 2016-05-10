package com.example.ishow.Fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ishow.Adapter.GalleryAdapter;
import com.example.ishow.Adapter.VideoParentAdapter;
import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Bean.VideoEntry;
import com.example.ishow.R;
import com.example.ishow.Utils.PixlesUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by MRME on 2016-05-08.
 */
public class GalleryFragment extends BaseFragment implements AdapterView.OnItemClickListener, GalleryAdapter.onCheckBoxChecked {

    Context context;
    ArrayList<VideoEntry> list;
    ArrayList<String> buckets;
    ArrayList<VideoEntry> datas;

    @Bind(R.id.fragment_gallery_top_dir)
    TextView fragmentGalleryTopDir;
    @Bind(R.id.fragment_gallery_top_jiantou)
    ImageView fragmentGalleryTopJiantou;
    @Bind(R.id.fragment_gallery_top_upload)
    TextView fragmentGalleryTopUpload;
    @Bind(R.id.PullToRefreshRecyclerView)
    PullToRefreshGridView pullToRefreshGridView;

    VideoParentAdapter adapter = null;
    GalleryAdapter galleryAdapter = null;
    @Bind(R.id.fragment_gallery_top_layout)
    RelativeLayout fragmentGalleryTopLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = getView(R.layout.fragment_videoparent);
        ButterKnife.bind(this, rootView);
        context = getActivity();
        //  loadVideosDirs();
        pullToRefreshGridView.setMode(PullToRefreshBase.Mode.DISABLED);
        loadAllVideoes();
        return rootView;


    }

    private void loadAllVideoes() {
        if (datas != null) {
            Message msg = handler.obtainMessage();
            msg.what = 1;
            handler.sendMessage(msg);
            return;
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver resolver = context.getContentResolver();
                Cursor query = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);
                while (query.moveToNext()) {
                    int mediaId = query.getInt(query.getColumnIndex(MediaStore.Video.Media._ID));
                    String filePath = query.getString(query.getColumnIndex(MediaStore.Video.Media.DATA));
                    long duration = query.getLong(query.getColumnIndex(MediaStore.Video.Media.DURATION));
                    long size = query.getLong(query.getColumnIndex(MediaStore.Video.Media.SIZE));
                    BitmapFactory.Options options = null;
                    Bitmap bitmap = getBitmapOptions(resolver, mediaId, options);


                    // Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(resolver, mediaId, MediaStore.Images.Thumbnails.MINI_KIND, null);
                    VideoEntry entry = new VideoEntry();
                    entry.setMediaId(mediaId);
                    entry.setFilePath(filePath);
                    entry.setDuration(duration);
                    entry.setSize(size);
                    entry.setBitmap(bitmap);
                    LogUtil.e(entry.toString());
                    if (datas == null) datas = new ArrayList<>();
                    datas.add(entry);
                }
                query.close();
                Message msg = handler.obtainMessage();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        });
        thread.start();
    }

    private Bitmap getBitmapOptions(ContentResolver resolver, int mediaId, BitmapFactory.Options options) {

        return ThumbnailUtils.extractThumbnail( MediaStore.Video.Thumbnails.getThumbnail(resolver, mediaId, MediaStore.Images.Thumbnails.MINI_KIND,
                options), PixlesUtils.dip2px(context,50), PixlesUtils.dip2px(context,50), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
    }

    String[] mediaColumns = new String[]{
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME
    };


    Thread thread;

    private void loadVideosDirs() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver resolver = context.getContentResolver();
                Cursor query = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns,  MediaStore.Video.Media.SIZE+"<?", new String[]{100*1024*1024+""}, MediaStore.Video.VideoColumns.DATE_TAKEN);
                if (query.getCount() > 0)
                    if (datas == null) datas = new ArrayList<>();
                    else datas.clear();
                while (query.moveToNext()) {

                    int mediaId = query.getInt(query.getColumnIndex(MediaStore.Video.Media._ID));
                    String filePath = query.getString(query.getColumnIndex(MediaStore.Video.Media.DATA));
                    // String ymimeTpe = query.getString(query.getColumnIndex(MediaStore.Video.Media.MIME_TYPE));
                    long duration = query.getLong(query.getColumnIndex(MediaStore.Video.Media.DURATION));
                    String bucketName = query.getString(query.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                    if (buckets == null) buckets = new ArrayList<>();
                    if (!buckets.contains(bucketName))
                        buckets.add(bucketName);
                    else continue;
                    File file = new File(filePath.substring(0, filePath.lastIndexOf("/")));
                    File[] files = null;
                    if (file.exists()) {
                        files = file.listFiles(new FileFilter() {
                            @Override
                            public boolean accept(File pathname) {
                                if (pathname.getName().endsWith(".mp4")
                                        ||pathname.getName().endsWith(".3gp")
                                        ||pathname.getName().endsWith(".avi")
                                        ||pathname.getName().endsWith(".m3u8")
                                        ||pathname.getName().endsWith(".wav"))
                                    return true;
                                return false;
                            }
                        });
                    }
                    if (files == null)
                        continue;
                    Bitmap bitmap = getBitmapOptions(resolver, mediaId, null);
                    VideoEntry entry = new VideoEntry();
                    entry.setMediaId(mediaId);
                    entry.setFilePath(filePath);
                    //entry.setYmimeTpe(ymimeTpe);
                    entry.setDuration(duration);
                    entry.setBucketName(bucketName);
                    entry.setFileCount(files.length);
                    entry.setBitmap(bitmap);
                    LogUtil.e(entry.toString());
                    if (list == null) list = new ArrayList<>();
                    list.add(entry);
                }

                query.close();
                Message msg = handler.obtainMessage();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        });
        thread.start();
    }

    private ListView videoListview;
    private PopupWindow popupWindow;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    setVideoList();
                    break;
                case 1:
                    if (galleryAdapter == null) {
                        galleryAdapter = new GalleryAdapter(context, datas);
                        pullToRefreshGridView.setAdapter(galleryAdapter);
                        galleryAdapter.addOnCheckBoxCheckedInterface(GalleryFragment.this);
                    } else galleryAdapter.notifyDataChange();
                    break;
            }
        }
    };
    Animation animation = null;

    private void setVideoList() {
        //**************************************箭头指示器 旋转180******************************//
       //  startRoatAnimation();

        if (adapter == null) {
            adapter = new VideoParentAdapter(context, list);
        } else adapter.notifyDataSetChanged();
        if (videoListview == null) {
            videoListview = new ListView(context);
            // videoListview.setDivider(getResources().getDrawable(R.drawable.divider_listview));
            videoListview.setOnItemClickListener(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(PixlesUtils.getScreenWidthPixels(context), ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = PixlesUtils.dip2px(context, 10);
            videoListview.setLayoutParams(params);
            videoListview.setBackgroundColor(getResources().getColor(android.R.color.white));
            if (adapter == null)
                adapter = new VideoParentAdapter(context, list);
            videoListview.setAdapter(adapter);
        }
        if (popupWindow == null) {
            popupWindow = new PopupWindow(PixlesUtils.getScreenWidthPixels(context), ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            //  popupWindow.setAnimationStyle(R.style.PopupAnimation);
            popupWindow.setContentView(videoListview);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);

        }
        popupWindow.showAsDropDown(fragmentGalleryTopLayout);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (thread != null) {
            thread.isAlive();
            thread.interrupt();
        }

    }

    @OnClick({R.id.fragment_gallery_top_close, R.id.fragment_gallery_top_layout, R.id.fragment_gallery_top_upload})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_gallery_top_close:
                ActivityCompat.finishAfterTransition((AppBaseCompatActivity) context);
                break;
            case R.id.fragment_gallery_top_layout:
                startRoatAnimation();
                loadVideosDirs();
                break;
            case R.id.fragment_gallery_top_upload:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startRoatAnimation();
        loadSingleDirVideoes(list.get(position).getBucketName());
        popupWindow.dismiss();
        fragmentGalleryTopDir.setText(list.get(position).getBucketName());
    }

    /**
     * 获取单个文件夹下所有视频
     *
     * @param bucketName 目录名
     */
    private void loadSingleDirVideoes(final String bucketName) {

        ContentResolver resolver = context.getContentResolver();
        Cursor query = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, MediaStore.Video.VideoColumns.DATE_TAKEN);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver resolver = context.getContentResolver();
                Cursor query = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, MediaStore.Video.Media.BUCKET_DISPLAY_NAME + "=?", new String[]{bucketName}, null);
                while (query.moveToNext()) {
                    int mediaId = query.getInt(query.getColumnIndex(MediaStore.Video.Media._ID));
                    String filePath = query.getString(query.getColumnIndex(MediaStore.Video.Media.DATA));
                    long duration = query.getLong(query.getColumnIndex(MediaStore.Video.Media.DURATION));
                    long size = query.getLong(query.getColumnIndex(MediaStore.Video.Media.SIZE));
                    Bitmap bitmap =getBitmapOptions(resolver, mediaId,null);
                    VideoEntry entry = new VideoEntry();
                    entry.setMediaId(mediaId);
                    entry.setFilePath(filePath);
                    entry.setDuration(duration);
                    entry.setSize(size);
                    entry.setBitmap(bitmap);
                    LogUtil.e(entry.toString());
                    datas.add(entry);
                }
                query.close();
                Message msg = handler.obtainMessage();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        });
        thread.start();
    }

    /**
     * 箭头 做动画处理
     */
    private void startRoatAnimation() {
        if (fragmentGalleryTopJiantou.getAnimation()!=null)
            fragmentGalleryTopJiantou.clearAnimation();
        if (animation == null) {
            animation = AnimationUtils.loadAnimation(context,R.anim.roatanimation);
            animation.setDuration(500);
            animation.setFillEnabled(true);
            animation.setFillAfter(true);
            animation.setFillBefore(false);
        }
        fragmentGalleryTopJiantou.startAnimation(animation);
    }

    @Override
    public void onBoxChecked(boolean checked, int position) {
        fragmentGalleryTopUpload.setEnabled(checked);
        fragmentGalleryTopUpload.setAlpha(checked?1.0f:0.5f);
    }
}
