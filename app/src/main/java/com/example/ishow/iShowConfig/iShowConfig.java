package com.example.ishow.iShowConfig;

/**
 * Created by MRME on 2016-03-28.
 */
public class iShowConfig {

    //测试服务器地址
    public static final String SERVER_URL = "http://120.26.219.135:8080";

    //正式服务器地址
   // public static final String SERVER_URL = "http://fuduji.ishowedu.com";
    //首页获取轮播图地址 参数要传 用户手机号
    public static final String getBanner = SERVER_URL + "/common/getBannernVideo?code=3add718fff59a6ca93e57202fa45111d&data=";
    //获取初级 高级 中级 等课程列表信息
    public static final String getCourseById = SERVER_URL + "/course/getTraining?code=947a79810479c2ca46fb42ed37ec44a0&data=";
    //根据课程id去拿每个课程对应的所有短音频地址
    public static final String getAllMusicPathById = SERVER_URL + "/course/getContent?code=eb1b80de3f7b964e3ac0b7cecbb958e1&data=";
    //登陆
    public static final String login = SERVER_URL + "/user/login?code=b2d2840c5a9f25995fad69844cb19980&data=";
    //注册
    public static final String register = SERVER_URL + "/user/register?code=70c529ef2838826a9d4bded511e3628b&data=";
    //获取验证码
    public static final String getmsgcode = SERVER_URL+"/user/getCode?code=1f0bf1053cbd720f0c09c3965ba302b5&data=";
   //忘记密码 后 修改新密码
   public static final String forgetpassword = SERVER_URL+"/user/findpwd?code=458ca3f7e8febd8c83d90dea5228a2c2&data=";
   //修改密码
   public static final String changgePassword = SERVER_URL+"/user/changePwd?code=2acf54b382681b9971452b68f21593a7&data=";
    //提交个人资料
    public static final String commitePersonInfo = SERVER_URL+"/user/memberedit?code=d2974b4ca6af15798b0d1454f784da16&data=";
    //提交头像
    public static  String commitePersonAvart=SERVER_URL+"/common/upload_ok?code=47bd3a4d79a3b43d8d0ec40567311e30";
    //个人中心获取数据的
    public static final String getCommitePersonInfo = SERVER_URL+"/course/getNiurenbangg?code=e81d87f19abcc17256134db384204aa6&data=";
    //排行榜
    public static final String getUserTextPracticeRank = SERVER_URL+"/course/getTrainingTop?code=b78e1697c7cb78ca4941280e5b81a9dd&data=";
    //对练记录
    public static final String getPracticeHistory=SERVER_URL+"/talk/gettalklist?code=3e66b0868e12b3b7166b83cc99fc9475&data=";
    //关于我们
    public static final String aboutUS = SERVER_URL+"/common/getContentn?code=4f118739b5dcd678644deaed1480e702&data=";
    //意见反馈
    public static final String suggestion = SERVER_URL+"/common/setMessage?code=55ffc560a2fb4640e4d9d96b7a72d17e&data=";
    //检测更新
    public static final String checknew = SERVER_URL+"/common/getVersion?code=df36e31f0acbd90cbad11866889fd5bf&data={\"app\":\"Android\"}";
    //退出app
    public static final String exitapp=SERVER_URL+"/talk/setLineStatus?code=4a98b2fca504c3de64e754f1191afa18&data=";

    //快速对练
    public static final String QUICK_PRACTICE=SERVER_URL+"/course/getCourseAll?code=dcf21d9a209300b490911d795e1cd39f&data=";
    //选择快速对练的人
    public static final String QUICK_PRACTICE_PERSONINFO=SERVER_URL+"/course/getRandomStudentMsg?code=14ca2584395dc5d6523b6f715a4c9a69&data=";
    //获取班级人数
    public static final String getAvaiableStudent = SERVER_URL+"/talk/getAvailable?code=8972dc0fb8a323bdec47c10f91018bee&data=";
    //关注的人
    public static final String getFans =SERVER_URL+"/talk/getFavList?code=db36f916ca0412082ef876ca125f9321&data=";
    //粉一个人
    public static final String Follow=SERVER_URL+"/talk/setFollow?code=42d136e8d31887e4b01b0340b880a8b9&data=";
    //关键词搜索
    public static final String searchByKeyWords =SERVER_URL+"/common/searchOneStudent?code=def70765feb32aaa8a26a50c9677e006&data=";

    //获取开启开屏页图片
    public static final String getFlashImage=SERVER_URL+"/common/getIndexPicture?code=9017b73b14e440d938201a64c2ec95df&data=";

    //上传课时练习时间
    public static final String uploadCoursePracticeTime = SERVER_URL+"/talk/recordUseTime?code=f4e6ed553f2e9552a66960241d98a649&data=";
    //上传视频文件
    public static final String uploadVideo=SERVER_URL+"/common/upload_media?code=340e8215bcef19b1ba5dcf0278b532ee";
    //获取个人视频 口测视频 娱乐视频{mediaBelong:15555043402,mediaOffset:0,isTest:0}
    public static final String getPersonVideo =SERVER_URL+"/media/getPersonalList?code=10306e119a3c6d1db17152068b4a5feb&data=";

    //*****************************获取课程信息时附带的课程id*******************************************//
    public static final String COURSEID_BY_PRIMARY = "37";
    public static final String COURSEID_BY_MIDDLE = "38";
    public static final String COURSEID_BY_HIGH = "39";
    public static final String COURSEID_BY_MORNING = "44";
    public static final String COURSEID_BY_TRANNING = "45";
    public static final String COURSEID_BY_OTHER = "46";
    public static final String COURSEID_BY_HIGH20 = "47";
    public static final String COURSEID_BY_VIDEO = "48";
    public static final String COURSEID_BY_VIDEO20 = "49";
    /*权限相关*/
    public static int Manifest_permission_EXTERNAL_STORAGE = 0;
    public static int Manifest_permission_RECORD_AUDIO = 1;
    public static int Manifest_permission_CAMERA= 2;
    public static final String ExitAPPBroadCastReciver = "com.example.ishow.ExitAPPBroadCastReciver";

    /*排行榜相关*/
    public static final String TIME_TYPE_DAY = "1";
    public static final String TIME_TYPE_WEEK = "2";

    public static String talingUid ="";//正在会话的人的uid
    public static boolean chatISOpen =false;//是否正处于聊天界面
    public static String morentouxiang ="http://7xlm33.com1.z0.glb.clouddn.com/fuduji_morentouxiang.jpg";

}
