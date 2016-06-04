package com.example.ishow.Utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.example.ishow.BaseComponent.AppBaseCompatActivity;
import com.example.ishow.Bean.CourseEntry;
import com.example.ishow.Bean.ViewFlowEntry;
import com.example.ishow.Utils.Interface.RequestPermissionInterface;

import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MRME on 2016-03-29.
 */
public class StorageUtils {
    public static StorageUtils instance = null;

    public static StorageUtils getInstance() {
        if (instance == null)
            instance = new StorageUtils();
        return instance;
    }


    /**
     * @param context
     * @param pathName 权限判断 申请
     * @return
     */
    public void saveBitmapCheckStroagePermision(final String pathName, final String picName, final Context context, final Bitmap bitmap) {
        if (isMashRoomVersion()) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ((AppBaseCompatActivity) context).checkPermissonForStrorage(new RequestPermissionInterface() {
                    @Override
                    public void onPermissionRequestResult(boolean result, boolean first) {
                        if (result) {
                            if (bitmap != null)
                                saveBtimap2SD(getPathNameDirs(pathName), picName, context, bitmap);
                        } else {
                            ToastUtil.showToast(context, "权限被禁用");
                        }
                    }
                });
            } else {
                if (bitmap != null)
                    saveBtimap2SD(getPathNameDirs(pathName), picName, context, bitmap);
            }
        } else {
            if (bitmap != null) saveBtimap2SD(getPathNameDirs(pathName), picName, context, bitmap);
        }
    }

    /**
     * 根据  目录名 获取路径
     *
     * @param pathName
     * @return
     */
    public String getPathNameDirs(String pathName) {
        String sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory().getPath() + "/iShowTalk/" + pathName;//获取跟目录
        } else {
            sdDir = Environment.getDataDirectory().getPath() + "/iShowTalk/" + pathName;//获取跟目录
        }

        File file = new File(sdDir);
        if (!file.exists())
            file.mkdirs();
        return file.getPath().toString();

    }

    public String getRootNameDirs() {
        return Environment.getExternalStorageDirectory().getPath() + "/iShowTalk/";
    }

    @NonNull
    public File CheckFileExist(String mp3path) {
        File file = new File(mp3path);
        String fname = file.getName().substring(0, file.getName().indexOf("."))
                + ".wav";
        return new File(file.getParent() + "/" + fname);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @param context
     */
    public void getAviableSDSize(Context context) {
        long availableBlocks;
        long blockSize;

        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }

        String size = Formatter.formatFileSize(context, blockSize * availableBlocks);
        LogUtil.e("可用机身大小---" + size);
    }


    /**
     * 将一个bitmap对象保存在本地
     *
     * @param parentPath
     * @param picName
     * @param context
     * @param bitmap
     */
    public String saveBtimap2SD(String parentPath, String picName, Context context, Bitmap bitmap) {

        File f = new File(parentPath, picName + ".png");
        if (TextUtils.equals("avart",picName))
        {
            fileOutPut(bitmap, f);
        }else if (!f.exists())  fileOutPut(bitmap, f);
        return f.exists()?f.getPath():null;
    }

    private void fileOutPut(Bitmap bitmap, File f) {
        try {
            if (!f.exists())
                f.createNewFile();
            FileOutputStream out = new FileOutputStream(f);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获得本地缓存的轮播图 地址列表
     *
     * @param context
     * @param sdr
     * @return
     */
    public List<ViewFlowEntry> getBannerCachePath(Context context, String sdr) {
        List<ViewFlowEntry> filePaths = null;
        String pathSdr = getPathNameDirs(sdr);
        File file = new File(pathSdr);
        if (file.exists()) {
            filePaths = new ArrayList<ViewFlowEntry>();
            File[] strings = file.listFiles();
            for (File f : strings) {
                ViewFlowEntry entry = new ViewFlowEntry();
                entry.setTitle(f.getPath());
                filePaths.add(entry);
            }
        } else {
            return null;
        }
        return filePaths;
    }

    public String getSplash( String urlName) {
        String pathSdr = getPathNameDirs("splash");
        File file = new File(pathSdr, urlName);
        if (file.exists())
            return file.getPath();
        return null;
    }

    /**
     * 根据路径获得bitmap对象
     *
     * @param filePath
     * @return
     */
    public Bitmap getBannerBitmap(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            return bitmap;
        } else {
            return null;
        }
    }

    public boolean isMashRoomVersion() {
        if (Build.VERSION.SDK_INT >= 23)
            return true;
        return false;
    }

    /**
     * 进入 课程界面后 检测 每个课时的状态
     *
     * @param context
     * @param courseEntry
     * @param title
     * @return
     */
    public CourseEntry getCourseEntryInfo(Context context, CourseEntry courseEntry, String title) {
        String dirs = getPathNameDirs(title + "/" + courseEntry.getId());
        int maxDownSize = SharePrefrence.getInstance().getCourseJsonDownloadSie(context, courseEntry.getId());
        //如果本地缓存的 最大下载短音频为0  说明还没下载过  直接返回
        if (maxDownSize == 0) return courseEntry;
        //否则 拿到 该课时下的所有文件
        File file = new File(dirs);
        if (file.exists()) {
            File[] files = file.listFiles();
            int progressbar = files.length * 100 / maxDownSize;
            courseEntry.setBaseCourseHasDownloadSize(files.length);
            courseEntry.setBaseCourseMaxDownloadSize(maxDownSize);
            courseEntry.setDirPath(dirs);
            courseEntry.setProgressbar(progressbar);
            if (progressbar == 0) {
                courseEntry.setCourseState(0);
            } else if (progressbar > 0 && progressbar < 100) {
                courseEntry.setCourseState(2);
            } else if (progressbar >= 100) {
                courseEntry.setCourseState(3);
            }
            //这里设置成2  会比设置成1 好一些 少一些可能bug
            return courseEntry;
        } else {
            return courseEntry;
        }
    }


    /**
     * 获取sd卡中 音视频 大小的方法
     * @param size
     * @return
     */
    public  String getSdVideoSize(long size){
        double kb = (double) ((float)size/1000);
        DecimalFormat df2  = new DecimalFormat("#");

        if (kb<1024)
        {
            return  df2.format(kb)+"KB";
        }

        else
        {
            int mb = (int) (kb / 1024);
            kb  =kb%1024/100;
            if (kb>1)
            return mb+"."+ df2.format((int)kb)+"M";
            else return mb+"M";
        }
    }
}
