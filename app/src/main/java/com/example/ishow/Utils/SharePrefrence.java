package com.example.ishow.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.ishow.Bean.CourseEntry;
import com.example.ishow.Bean.ShortCourseEntry;
import com.example.ishow.Bean.UserEntry;
import com.google.gson.Gson;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MRME on 2016-03-29.
 */
public class SharePrefrence {

    static SharePrefrence  instance =null;
    SharedPreferences sahred=null;
    SharedPreferences studentShared=null;
    public static SharePrefrence getInstance(){
        if (instance==null)
            instance =new SharePrefrence();
        return instance;
    }
    /**
     * 获取全局SharedPreferences 对象
     * @param context
     * @return
     */
    public  SharedPreferences getShared (Context context){
        sahred =  context.getSharedPreferences("iShowTalk",Context.MODE_PRIVATE);
        return sahred;
    }
    public SharedPreferences getCourseShared(Context context){
        sahred =  context.getSharedPreferences("iShowCourse",Context.MODE_PRIVATE);
        return sahred;
    }
    public SharedPreferences getStudentShared(Context context){
        studentShared =  context.getSharedPreferences("ishowStudentInfo",Context.MODE_PRIVATE);
        return studentShared;
    }
    public  SharedPreferences.Editor getStudentEditor(Context context){
        studentShared = getStudentShared(context);
        return studentShared.edit();
    }


    /**
     *
     * @param context
     * @return
     */
    public  SharedPreferences.Editor getEditor(Context context){
        sahred = getCourseShared(context);
        return sahred.edit();
    }

    /**
     * 根据用户uid获取  初级 中级 高级 班json缓存
     * @param context
     * @param uid
     * @param courseName
     * @return
     */
    public  String getCourseJson(Context context,String uid,String courseName){
        sahred = getCourseShared(context);

        return sahred.getString(uid+courseName, null);
    }

    /**
     * 将课程json缓存到 本地
     * @param context
     * @param uid
     * @param courseName
     * @param courseJson
     */
    public  void putCourseJson(Context context,String uid,String courseName,String courseJson){
        getEditor(context).putString(uid+courseName,courseJson).commit();
    }

    /**
     * 把一个课时 所有音频下载地址 还存起来
     * @param context
     * @param courseId
     * @param courseJson
     * @param size
     */
    public void putCourseJsonDownloadPath(Context context, int courseId, String courseJson, int size){
        getEditor(context).putString(courseId+"",courseJson).commit();
        //把一个课时需要下载的所有短链接大小  放起来
        putCourseJsonMaxDownloadSize(context,courseId,size);
    }

    /**
     * 得到一个课时所有音频地址的list列表
     * @param context
     * @param courseId
     * @return
     */
    public   List<String> getCourseJsonDownloadPath(Context context, int courseId){
       // LogUtil.e("getCourseJsonDownloadPath---"+ getCourseShared(context).getString(Integer.toString(courseId),null));
       String s= getCourseShared(context).getString(courseId+"",null);
       return JsonUtils.getMusicPathByCourseId(s);
    }
    //保存 一个课时需要下载的短音频个数
    public  void putCourseJsonMaxDownloadSize(Context context,int courseId,int size){
        getEditor(context).putInt(courseId+"MaxDownloadSize",size).commit();
    }
    //获取 一个课时需要下载多少个 短音频
    public  int getCourseJsonDownloadSie(Context context,int courseId){
        return getCourseShared(context).getInt(courseId+"MaxDownloadSize",0);
    }

    /**
     *  保存一个课时已经下载了多少了
     * @param context
     * @param courseId
     * @param downloadSzie
     */
    public void putCoursehasDownloadSize(Context context,int courseId,int downloadSzie){
        getEditor(context).putInt(courseId+"hasDownloadSize",downloadSzie).commit();
    }
    /**
     *  保存一个课时已经下载了多少了
     * @param context
     * @param courseId
     */
    public int getCoursehasDownloadSize(Context context,int courseId){
      return  getCourseShared(context).getInt(courseId+"hasDownloadSize",0);
    }

    public void putAudioSize(Context context,String size){
        getEditor(context).putString("AudioSize",size).commit();

    }
    public String getAudioSize(Context context){
        return  getCourseShared(context).getString("AudioSize","0");
    }
    public void putFansSize(Context context,String size){
        getEditor(context).putString("Fans",size).commit();

    }
    public String getFansSize(Context context){
        return  getCourseShared(context).getString("Fans","0");
    }
    public ArrayList<ShortCourseEntry> getCourseList(Context context, int courseId){
        LogUtil.e(courseId+"");
        String s= getCourseShared(context).getString(courseId+"",null);
        return JsonUtils.getCourseNamesList(s);
    }


    //***********************************以下全是对 学员信息的处理了 ********************************************************

    public void putStudentNameAndPassword(Context c,String name,String password){
        getStudentEditor(c).putString("name",name).commit();
        getStudentEditor(c).putString("password",password).commit();
    }
    public String getStudentName(Context c){
        return getStudentShared(c).getString("name","");
    }
    public String getStudentPassword(Context c){
        return getStudentShared(c).getString("password","");
    }
    //将学员完整信息保存在shareprefrence里面
    public void putStudentInfo(Context c,String studentInfo){
        LogUtil.e("putStudentInfo"+studentInfo);
        getStudentEditor(c).putString("studentInfo",studentInfo).commit();
    }
    //获取 学员的 一个完整对象
    public UserEntry getStudentInfo(Context c){
        Gson gson =new Gson();
        String info = getStudentShared(c).getString("studentInfo",null);
        if (info!=null)
            return  gson.fromJson(info,UserEntry.class);
       return null;
    }

    /**
     *  0 单曲 1顺序 2随机  获取音频播放模式
     * @param context
     * @return
     */
    public int getPlayMode(Context context){
        return getCourseShared(context).getInt("mode",0);
    }

    public void putPlayMode(Context context,int mode){
        getEditor(context).putInt("mode",mode).commit();
    }

    public void putplayProgress(Context context,int progress){
        getEditor(context).putInt("progress",progress).commit();
    }
    public  int getplayProgress(Context context){
        return getCourseShared(context).getInt("progress",0);
    }


    //***********************************************获得正在播放的course id  和 课程目录

    public void putPlayingCourseIdAndDir(Context context, String title, String text, int CourseId, String dir){
        getEditor(context).putInt("CourseId",CourseId).commit();
        getEditor(context).putString("dir",dir).commit();
        getEditor(context).putString("text",text).commit();
        getEditor(context).putString("title",title).commit();
    }
    public int getPlayingCourseId(Context context){
       return getCourseShared(context).getInt("CourseId",0);
    }
    public String getPlayingCourseDir(Context context){
        return getCourseShared(context).getString("dir","");
    }
    public String getPlayingCourseText(Context context){
        return getCourseShared(context).getString("text","");
    }
    public CourseEntry getLastPlayedCourseEntry(Context context){
        CourseEntry entry = new CourseEntry();
        int courseId = getCourseShared(context).getInt("CourseId", 0);
        String dir = getCourseShared(context).getString("dir", "");
        String title = getCourseShared(context).getString("title", "");
        String text = getCourseShared(context).getString("text", "");
        entry.setId(courseId);
        entry.setDirPath(dir);
        entry.setTitle(title);
        entry.setContent(text);
        return entry;
    }


    //正在播放的音乐对象***********************************************************

}
