package com.example.ishow.Application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;

import com.example.ishow.R;
import com.example.ishow.Utils.PixlesUtils;
import com.example.ishow.justalk.cloud.juscall.MtcCallDelegate;
import com.example.ishow.mipush.MiPush;
import com.example.ishow.mipush.MtcService;
import com.justalk.cloud.lemon.MtcApi;
import com.justalk.cloud.lemon.MtcUtil;
import com.justalk.cloud.lemon.MtcVer;
import com.justalk.cloud.zmf.Zmf;
import com.justalk.cloud.zmf.ZmfAudio;
import com.justalk.cloud.zmf.ZmfVideo;
import com.liulishuo.filedownloader.FileDownloader;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.umeng.socialize.PlatformConfig;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.List;


/**
 * Created by MRME on 2016-03-22.
 */
public class iShowTalkApplication extends Application {

    public static iShowTalkApplication Instance=null;
    @Override
    public void onCreate() {
       // StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll() .penaltyLog() .build());
      //  StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll() .penaltyLog() .build());
        super.onCreate();
      //  CrashHandler handler = new CrashHandler();
      //  handler.init(getApplicationContext());
        Instance =this;
        //初始化下载管理器
        FileDownloader.init(this);
        //xutils
        x.Ext.init(this);
        x.Ext.setDebug(true);
        initDbConfig();
        //imageloader
        initImageLoader(getApplicationContext());
       if (!getProcessName().equals(getPackageName()))
            return;

        //jusralk
        ZmfAudio.initialize(this);
        ZmfVideo.initialize(this);
        MtcApi.init(this, "bb2910a8a8a4e9b3821e5097");
        MtcCallDelegate.init(this);
        //MtcCallDelegate.setBackIntent("com.justalk.cloud.sample.call.action.backfromcall");
        String avatarVer = MtcVer.Mtc_GetAvatarVersion();
        String lemonVer = MtcVer.Mtc_GetLemonVersion();
        String melonVer = MtcVer.Mtc_GetMelonVersion();
        String mtcVer = MtcVer.Mtc_GetVersion();
        String zmfVer = Zmf.getVersion();
        MtcUtil.Mtc_AnyLogInfoStr("Cloud sample", "avatarVer" + avatarVer);
        MtcUtil.Mtc_AnyLogInfoStr("Cloud sample", "lemonVer" + lemonVer);
        MtcUtil.Mtc_AnyLogInfoStr("Cloud sample", "melonVer" + melonVer);
        MtcUtil.Mtc_AnyLogInfoStr("Cloud sample", "mtcVer" + mtcVer);
        MtcUtil.Mtc_AnyLogInfoStr("Cloud sample", "zmfVer" + zmfVer);
        MiPushClient.registerPush(getApplicationContext(), getString(R.string.MiPush_AppId), getString(R.string.MiPush_AppKey));

        MiPushClient.checkManifest(this);

        //APP ID1104835856APP KEY0nWtiAHjzi7nwajV  Qq
        //AppID：wxbcee176067fc70ed AppSecret：2d3646387a00b620e7dcc0aa4ceab881  WEIIXIN
        //App key：4245668879App secret：632564f60dc55d7b94430c6a3711a88f  XINLANG

        PlatformConfig.setWeixin("wxbcee176067fc70ed", "2d3646387a00b620e7dcc0aa4ceab881");
        //微信 appid appsecret
        PlatformConfig.setSinaWeibo("3921700954","04b48b094faeb16683c32669824ebdad");
        //新浪微博 appkey appsecret
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        // QQ和Qzone appid appkey
    }



    private String getProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }
    public  DbManager.DaoConfig initDbConfig() {
       DbManager.DaoConfig config = new DbManager.DaoConfig();
        config.setDbName("ishowTalkCourse.db");
        config.setDbDir(new File(com.example.ishow.Utils.StorageUtils.getInstance().getPathNameDirs("iShowDB")));
        config.setDbVersion(1);
        return config;
    }


    /**
     * 获取 application实例
     * @return
     */
    public static  iShowTalkApplication getInstance() {
        return Instance;
    }

    /**
     * 初始化 Xutils3 ImageOptions 图片下载类配置
     * @param context
     * @param size
     * @param radius
     * @return
     */
    public ImageOptions getIgetImageOptions(Context context, int size, int radius) {

        ImageOptions imageOptions = new ImageOptions.Builder()
                .setSize(PixlesUtils.dip2px(context, size), PixlesUtils.dip2px(context, size))//图片大小
                .setRadius(PixlesUtils.dip2px(context, radius))//ImageView圆角半径
                .setLoadingDrawableId(R.mipmap.ic_launcher_moren)//加载中默认显示图片
                .setFailureDrawableId(R.mipmap.ic_launcher_moren)//加载失败后默认显示图片
                .build();
        return imageOptions;
    }


    public DisplayImageOptions initImageLoader(Context context) {

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        File cacheDir = StorageUtils.getOwnCacheDirectory(context,"iShowTalk/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCache(new UnlimitedDiskCache(cacheDir))
                .defaultDisplayImageOptions(options)
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
        return options;
    }

    public void startMiPush() {
        getApplicationContext().startService(new Intent(getApplicationContext(), MtcService.class));
        MiPush.start(getApplicationContext(), getString(R.string.MiPush_AppId), getString(R.string.MiPush_AppKey));
    }

    public void stopMiPush() {
        getApplicationContext().stopService(new Intent(getApplicationContext(), MtcService.class));
        MiPush.stop(getApplicationContext());
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        MtcApi.destroy();
        ZmfVideo.terminate();
        ZmfAudio.terminate();
        super.onTerminate();
    }

}
