package com.example.ishow.Utils;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.example.ishow.R;
import com.example.ishow.UIView.MaterialDialog;
import com.example.ishow.iShowConfig.iShowConfig;

import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class UpdaterManager {

    private static final int DOWNLOAD = 1;

    private static final int DOWNLOAD_FINISH = 2;

    HashMap<String, String> mHashMap;

    private String mSavePath;

    private int progress;

    private boolean cancelUpdate = false;

    private Context mContext;

    //private ProgressBar mProgress;
    private Dialog mDownloadDialog;
    private StringBuilder stringBuilder;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case DOWNLOAD:

                    //mProgress.setProgress(progress);
                    if(progress%10==0){

                        builder.setProgress(100, progress, false);
                        notificationManager.notify(0, builder.build());

                    }
                    break;
                case DOWNLOAD_FINISH:

                    //notificationManager.cancel(R.layout.ishow_softupdate_progress);
                    notificationManager.cancel(0);
                    installApk();
                    break;
                case 3:
                    if(dialog!=null){dialog.cancelDialog();}
                    showNoticeDialog();
                    break;
                case 4:
                    if(dialog!=null){dialog.cancelDialog();}
                    break;

                default:
                    break;
            }
        };
    };


    public UpdaterManager(Context context) {
        this.mContext = context;
    }


    MaterialDialog dialog = null;
    public void checkUpdate(Boolean showDialog) {
        if(showDialog){
            dialog =new MaterialDialog();
            dialog.showDloag(mContext,mContext.getResources().getString(R.string.request_server));
        }
        new Thread(isUpdate).start();

    }



    private Runnable isUpdate = new Runnable() {



        public void run() {


            int versionCode = getVersionCode(mContext);

            try {
                String result = getValueFromServerByUrl(iShowConfig.checknew);
                //LogUtil.IShowLg("检测更新"+result, null);
                mHashMap = new HashMap<String, String>();

                JSONObject mJSONObject = new JSONObject(result);
                mHashMap.put("version", mJSONObject.getString("versioncode"));//version
                mHashMap.put("keyupdate", mJSONObject.getString("keyupdate"));
                String url = mJSONObject.getString("download");
                mHashMap.put("name",url.substring(url.lastIndexOf("/") + 1));
                mHashMap.put("url", url);
                stringBuilder =new StringBuilder();
                String info =mJSONObject.getString("info");
                String[] infoStr = info.split("\\.");
                for (int i = 0; i < infoStr.length; i++) {
                    stringBuilder.append(infoStr[i]+"\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mHashMap != null && !mHashMap.isEmpty()) {
                int serviceCode = Integer.valueOf(mHashMap.get("version"));
                LogUtil.e("检测更新结果"+serviceCode+"---"+versionCode);
                if (serviceCode > versionCode) {
                    Message msg = new Message();
                    msg.what = 3;
                    mHandler.sendMessage(msg);
                }else{
                    Message msg = new Message();
                    msg.what = 4;
                    mHandler.sendMessage(msg);
                }
            }
        }
    };



    private void showNoticeDialog() {

        Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(stringBuilder.toString());
        int keyupdate = Integer.parseInt(mHashMap.get("keyupdate"));
        switch (keyupdate) {
            case 0:

                builder.setPositiveButton(R.string.soft_update_updatebtn,
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                showDownloadDialog();
                            }
                        });
                builder.setNegativeButton(R.string.soft_update_later,
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                break;

            case 1:

                builder.setPositiveButton(R.string.soft_update_updatebtn,
                        new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                showDownloadDialog();
                            }
                        });
                break;
        }

        Dialog noticeDialog = builder.create();
        noticeDialog.setCancelable(false);
        noticeDialog.show();
    }



    NotificationManager notificationManager=null;
    NotificationCompat.Builder builder  =null;
    private void showDownloadDialog() {

        builder =new NotificationCompat.Builder(mContext);

        builder.setTicker("正在下载...");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setDefaults(Notification.FLAG_ONLY_ALERT_ONCE);
        builder.setWhen(System.currentTimeMillis());

        builder.setAutoCancel(true);

        builder.setContentTitle("正在下载...");
        builder.setProgress(100, 0, false);
        //builder.setContent(new RemoteViews(mContext.getPackageName(), R.layout.ishow_softupdate_progress));
        Intent intent = new Intent("com.example.ishow.updatemanager.update_pause_start");
        PendingIntent pendingIntent =PendingIntent.getBroadcast(mContext, DOWNLOAD, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

        downloadApk();
    }


    private void downloadApk() {

        new DownloadApkThread().start();
    }


    private class DownloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                String sdPath = StorageUtils.getInstance().getPathNameDirs("apk");
                mSavePath = sdPath + "/" + "download";
                URL url = new URL(mHashMap.get("url"));

                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();

                int length = conn.getContentLength();


                InputStream is = conn.getInputStream();

                File file = new File(mSavePath);


                if (!file.exists()) {
                    file.mkdir();
                }

                File apkFile = new File(mSavePath, mHashMap.get("name"));

                FileOutputStream fos = new FileOutputStream(apkFile);
                int count = 0;



                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;

                    progress = (int) (((float) count / length) * 100);

                    mHandler.sendEmptyMessage(DOWNLOAD);

                    if (numread <= 0) {

                        mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                        break;
                    }

                    fos.write(buf, 0, numread);
                } while (!cancelUpdate);
                fos.close();
                is.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };


    private void installApk() {
        File apkfile = new File(mSavePath, mHashMap.get("name"));
        if (!apkfile.exists()) {
            return;
        }

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),"application/vnd.android.package-archive");
        mContext.startActivity(i);
    }

    public static String getValueFromServerByUrl(String uri) {

        try {
            URL url = new URL(uri);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(30000);
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            StringBuffer buf = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
            }
            reader.close();
            in.close();

            return buf.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {

            versionCode = context.getPackageManager().getPackageInfo("com.example.ishow", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


    public static String getVersionName(Context context) {
        String versionName = "";
        try {

            versionName = context.getPackageManager().getPackageInfo(
                    "com.example.ishow", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
