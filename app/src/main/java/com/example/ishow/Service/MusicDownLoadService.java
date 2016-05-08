package com.example.ishow.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.ishow.Bean.CourseEntry;
import com.example.ishow.R;
import com.example.ishow.Utils.JsonUtils;
import com.example.ishow.Utils.SharePrefrence;
import com.example.ishow.Utils.ToastUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import org.xutils.common.util.LogUtil;

import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by MRME on 2016-04-05.
 */
public class MusicDownLoadService extends Service {
    public FileDownloadListener queueTarget = null;
    LinkedList<CourseEntry> courseEntryList = null;
    private CourseEntry entryPost;

    @Override
    public void onCreate() {

        if (courseEntryList == null)
            courseEntryList = new LinkedList<>();
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MusicDownLoadServiceBind();
    }

    public class MusicDownLoadServiceBind extends Binder {
        public MusicDownLoadService getInstace() {
            return MusicDownLoadService.this;
        }
    }

    public FileDownloadListener initalizerFileDownloadListener() {
        queueTarget = new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                //此时已经加入到 队列中  把任务加入到BaseDownloadTask  以便 暂停 继续等操作
                // LogUtil.e(FileDownloader.getImpl().getStatus(task.getDownloadId()) + "--当前进度pending");
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                // LogUtil.e("connected" + task.getDownloadId() + "----------" + task.getTag());

            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                 // LogUtil.e("progress" + task.getDownloadId());
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
               // LogUtil.e("blockComplete" + task.getDownloadId() + "----------" + task.getTag());
                synchronized (courseEntryList) {
                    //LogUtil.e("blockComplete是否旧文件" + task.isReusedOldFile());
                    if (!task.isReusedOldFile())
                        getPostCourseEntry(task);
                }
            }


            @Override
            protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {

            }

            @Override
            protected void completed(BaseDownloadTask task) {

            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                LogUtil.e("paused我被暂停了" + task.getDownloadId() + "----------" + task.getTag());
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                //ToastUtil.showToast(getBaseContext(), e.getMessage() + "error" + task.getTag());
            }

            @Override
            protected void warn(BaseDownloadTask task) {

            }
        };
        return queueTarget;
    }

    private void getPostCourseEntry(BaseDownloadTask task) {
        for (int i = 0; i < courseEntryList.size(); i++) {
            entryPost = courseEntryList.get(i);
            if (entryPost.getId() == (Integer) task.getTag()) {
                int hasDownSize = entryPost.getBaseCourseHasDownloadSize() + 1;
                int progressbar = hasDownSize * 100 / entryPost.getBaseCourseMaxDownloadSize();
                entryPost.setBaseCourseHasDownloadSize(hasDownSize);
                entryPost.setCourseState(progressbar >= 100 ? 3 : 1);
                entryPost.setProgressbar(progressbar);
                courseEntryList.set(i, entryPost);
                EventBus.getDefault().post(entryPost);
              //  LogUtil.e(task.getTag()+"---当前进度"+progressbar);
               // SharePrefrence.getInstance().putCoursehasDownloadSize(getBaseContext(),(Integer)task.getTag(),hasDownSize);
                if (progressbar >= 100) {courseEntryList.remove(i);}
            }
        }
    }

    //开始一个队列  如果是暂停后再下载的那就是自动断点续传
    public void startDownloadQueue(CourseEntry entry,List<String> urlPath) {

        startNewDownloadQueue(entry,urlPath);

       // LogUtil.e(entry.getProgressbar() + "--我被开始了--" + entry.getId());
    }

    private void startNewDownloadQueue(CourseEntry needDownloadEntry,List<String> urlPath) {
       synchronized (courseEntryList){
           needDownloadEntry.setFileDownloadListener(initalizerFileDownloadListener());
           courseEntryList.add(needDownloadEntry);
           if (urlPath==null)
            {   SharePrefrence.getInstance().putCourseJsonDownloadPath(getBaseContext(),needDownloadEntry.getId(),"",0);
                ToastUtil.showToast(getBaseContext(),getString(R.string.course_paths_isNUll));
                needDownloadEntry.setCourseState(0);
                EventBus.getDefault().post(needDownloadEntry);
               return ;
            }
           for (String url : urlPath) {
               FileDownloader.getImpl().create(url).setPath(needDownloadEntry.getDirPath() + "/" + url.substring(url.lastIndexOf("/"))).setTag(needDownloadEntry.getId()).setCallbackProgressTimes(0).setListener(needDownloadEntry.getFileDownloadListener()).ready();
           }
           //SharePrefrence.getInstance().putCourseJsonDownloadPath(getBaseContext(),needDownloadEntry.getId(),"",needDownloadEntry.getBaseCourseMaxDownloadSize());
           FileDownloader.enableAvoidDropFrame();
           FileDownloader.getImpl().start(queueTarget, true);
           needDownloadEntry.setCourseState(5);
           EventBus.getDefault().post(needDownloadEntry);
       }
    }

    public void pauseDownloadQueue(int courseId) {
       synchronized (courseEntryList){
           for (CourseEntry entry : courseEntryList) {
               if (entry.getId() == courseId) {
                   FileDownloader.getImpl().pause(entry.getFileDownloadListener());
                   courseEntryList.remove(entry);
                   break;
               }
           }
       }
    }

    public void resumeDownloadQeueu(Context contxt,CourseEntry course) {
       synchronized (this){
           for (int i = 0; i < courseEntryList.size(); i++) {
               CourseEntry c = courseEntryList.get(i);
               if (c.getId() == course.getId()) {
                  // LogUtil.e("我被恢复了---" + c.getId());
                   startNewDownloadQueue(courseEntryList.get(i), SharePrefrence.getInstance().getCourseJsonDownloadPath(contxt,course.getId()));

                   break;
               }else if (i>=courseEntryList.size()-1){
                   SharePrefrence.getInstance().putCoursehasDownloadSize(contxt,course.getId(),0);
                   course.setProgressbar(0);
                   course.setCourseState(0);
                   EventBus.getDefault().post(course);
               }

           }
       }
    }

    public CourseEntry getCourseEntryStatu(CourseEntry entry) {
        if (courseEntryList.size() == 0) return entry;
        else {
            for (CourseEntry entry1 : courseEntryList) {
                if (entry.getId() == entry1.getId())
                    return entry1;
                break;
            }
        }
        return entry;
    }
}
