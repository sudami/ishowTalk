package com.example.ishow.Utils;

import android.content.Context;

import com.example.ishow.Bean.CourseEntry;
import com.example.ishow.Bean.ShortCourseEntry;
import com.example.ishow.Service.MusicDownLoadService;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by MRME on 2016-04-05.
 */
public class JsonUtils {


    /*{
            "id": "578",
            "title": "I'm really sorry",
            "img": "",
            "path": "http://7xlmbe.com1.z0.glb.clouddn.com/2016-04-01/56fde55cdeae1.mp3",
            "content": "中级",
            "wtime": "2016-04-01 11:05:07",
            "audio_txt": "",
            "video_url": ""
        }*/

    /**
     * 获取课时信息
     *
     * @param json
     * @return
     */
    public static LinkedList<CourseEntry> getCourseEntryFromJson(String json) {
        LinkedList<CourseEntry> entries = new LinkedList<>();
        JSONArray array = null;
        try {
            array = new JSONArray(json);
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                try {
                    String emlent = array.getJSONObject(i).toString();
                    CourseEntry courseEntry = gson.fromJson(emlent, CourseEntry.class);
                    entries.add(courseEntry);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entries;
    }

    /**
     * 获取课时信息
     *
     * @param json
     * @param downLoadService
     * @param parentCourseName 哪一个课程下的 title比如 初级班 中级班
     * @return
     */
    public static LinkedList<CourseEntry> getCourseEntryFromLocalJson(Context context,String json,String parentCourseName, MusicDownLoadService downLoadService) {
        LinkedList<CourseEntry> entries = new LinkedList<>();
        JSONArray array = null;
        try {
            array = new JSONArray(json);
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                try {
                    String emlent = array.getJSONObject(i).toString();

                    CourseEntry courseEntry = gson.fromJson(emlent, CourseEntry.class);
                    //从本地检测每个课时的状态
                    courseEntry = StorageUtils.getInstance().getCourseEntryInfo(context,courseEntry,parentCourseName);
                    //从下载service检测每个课时的状态
                    courseEntry = downLoadService.getCourseEntryStatu(courseEntry);

                    entries.add(courseEntry);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entries;
    }

    public static List<String>  getMusicPathByCourseId( String result){

      if (result==null)
          return  null;
        List<String> paths = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(result);
                JSONArray array = object.getJSONArray("marr");
                for (int i=0;i<array.length();i++){
                    JSONObject ob = array.getJSONObject(i);
                    paths.add(ob.getString("path"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
      //  LogUtil.e("课时网络地址"+result);
        return paths;
    }


    public static ArrayList<ShortCourseEntry>  getCourseNamesList(String result){

        if (result==null)
            return  null;
        LogUtil.e(result);
        ArrayList<ShortCourseEntry> paths = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("marr");
            for (int i=0;i<array.length();i++){
                ShortCourseEntry entry = new ShortCourseEntry();
                JSONObject ob = array.getJSONObject(i);
                entry.setPath(ob.getString("path"));
                entry.setTitle(ob.getString("title"));
                paths.add(entry);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("课时网络地址"+result);
        return paths;
    }
}
