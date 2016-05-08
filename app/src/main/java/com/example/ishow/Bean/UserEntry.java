package com.example.ishow.Bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MRME on 2016-04-11.
 */
public class UserEntry implements Parcelable {
    /**
     * id : 20757
     * db_id : 19632
     * name : LOL
     * password : 46f83abc72a9c45653fe4a6db72821a8
     * salt : pEGB
     * level : 5
     * sex : 帅哥
     * age :
     * campus : 衡阳校区
     * school :
     * mobile : 15555043403
     * mail :
     * wtime :
     * sessionid :
     * sign :
     * cps_id : 4
     * cs_id : 0
     * class_id : -1
     * grade :
     * timelong : 16500
     * img : http://7xlm33.com1.z0.glb.clouddn.com/2016-03-17/56ea6691dcdf5.jpg
     * tch_id : 0
     * reg_time :
     * login_time : 1460358466
     * login_status : 1
     * is_open_practice : 1
     * userid : 20757
     */

    private String id;
    private String db_id;

    @Override
    public String toString() {
        return "UserEntry{" +
                "id='" + id + '\'' +
                ", db_id='" + db_id + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", level='" + level + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", campus='" + campus + '\'' +
                ", school='" + school + '\'' +
                ", mobile='" + mobile + '\'' +
                ", mail='" + mail + '\'' +
                ", wtime='" + wtime + '\'' +
                ", sessionid='" + sessionid + '\'' +
                ", sign='" + sign + '\'' +
                ", cps_id='" + cps_id + '\'' +
                ", cs_id='" + cs_id + '\'' +
                ", class_id='" + class_id + '\'' +
                ", grade='" + grade + '\'' +
                ", timelong='" + timelong + '\'' +
                ", img='" + img + '\'' +
                ", tch_id='" + tch_id + '\'' +
                ", reg_time='" + reg_time + '\'' +
                ", login_time='" + login_time + '\'' +
                ", login_status='" + login_status + '\'' +
                ", is_open_practice='" + is_open_practice + '\'' +
                ", userid='" + userid + '\'' +
                ", isOnline='" + isOnline + '\'' +
                ", fans=" + fans +
                ", todaytime='" + todaytime + '\'' +
                ", weektime='" + weektime + '\'' +
                ", paiming='" + paiming + '\'' +
                ", is_focus=" + is_focus +
                '}';
    }

    private String name;
    private String password;
    private String salt;
    private String level;
    private String sex;
    private String age;
    private String campus;
    private String school;
    private String mobile;
    private String mail;
    private String wtime;
    private String sessionid;
    private String sign;
    private String cps_id;
    private String cs_id;
    private String class_id;
    private String grade;
    private String timelong;
    private String img;
    private String tch_id;
    private String reg_time;
    private String login_time;
    private String login_status;
    private String is_open_practice;
    private String userid;

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    private String isOnline;

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    private int fans;

    public String getTodaytime() {
        return todaytime;
    }

    public void setTodaytime(String todaytime) {
        this.todaytime = todaytime;
    }

    public String getWeektime() {
        return weektime;
    }

    public void setWeektime(String weektime) {
        this.weektime = weektime;
    }

    public String getPaiming() {
        return paiming;
    }

    public void setPaiming(String paiming) {
        this.paiming = paiming;
    }

    private String  todaytime;
    private String weektime;
    private String paiming;

    public boolean is_focus() {
        return is_focus;
    }

    public void setIs_focus(boolean is_focus) {
        this.is_focus = is_focus;
    }

    private boolean is_focus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDb_id() {
        return db_id;
    }

    public void setDb_id(String db_id) {
        this.db_id = db_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getWtime() {
        return wtime;
    }

    public void setWtime(String wtime) {
        this.wtime = wtime;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getCps_id() {
        return cps_id;
    }

    public void setCps_id(String cps_id) {
        this.cps_id = cps_id;
    }

    public String getCs_id() {
        return cs_id;
    }

    public void setCs_id(String cs_id) {
        this.cs_id = cs_id;
    }

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTimelong() {
        return timelong;
    }

    public void setTimelong(String timelong) {
        this.timelong = timelong;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTch_id() {
        return tch_id;
    }

    public void setTch_id(String tch_id) {
        this.tch_id = tch_id;
    }

    public String getReg_time() {
        return reg_time;
    }

    public void setReg_time(String reg_time) {
        this.reg_time = reg_time;
    }

    public String getLogin_time() {
        return login_time;
    }

    public void setLogin_time(String login_time) {
        this.login_time = login_time;
    }

    public String getLogin_status() {
        return login_status;
    }

    public void setLogin_status(String login_status) {
        this.login_status = login_status;
    }

    public String getIs_open_practice() {
        return is_open_practice;
    }

    public void setIs_open_practice(String is_open_practice) {
        this.is_open_practice = is_open_practice;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
    /**
     * "data": {
     "id": "20757",
     "db_id": "19632",
     "name": "LOL",
     "password": "46f83abc72a9c45653fe4a6db72821a8",
     "salt": "pEGB",
     "level": "5",
     "sex": "帅哥",
     "age": "",
     "campus": "衡阳校区",
     "school": "",
     "mobile": "15555043403",
     "mail": "",
     "wtime": "",
     "sessionid": "",
     "sign": "",
     "cps_id": "4",
     "cs_id": "0",
     "class_id": "-1",
     "grade": "",
     "timelong": "16500",
     "img": "http://7xlm33.com1.z0.glb.clouddn.com/2016-03-17/56ea6691dcdf5.jpg",
     "tch_id": "0",
     "reg_time": "",
     "login_time": "1460358466",
     "login_status": "1",
     "is_open_practice": "1",
     "userid": "20757"
     is_focus":"false
     todaytime
     weektime
     paiming
     }
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.db_id);
        dest.writeString(this.name);
        dest.writeString(this.password);
        dest.writeString(this.salt);
        dest.writeString(this.level);
        dest.writeString(this.sex);
        dest.writeString(this.age);
        dest.writeString(this.campus);
        dest.writeString(this.school);
        dest.writeString(this.mobile);
        dest.writeString(this.mail);
        dest.writeString(this.wtime);
        dest.writeString(this.sessionid);
        dest.writeString(this.sign);
        dest.writeString(this.cps_id);
        dest.writeString(this.cs_id);
        dest.writeString(this.class_id);
        dest.writeString(this.grade);
        dest.writeString(this.timelong);
        dest.writeString(this.img);
        dest.writeString(this.tch_id);
        dest.writeString(this.reg_time);
        dest.writeString(this.login_time);
        dest.writeString(this.login_status);
        dest.writeString(this.is_open_practice);
        dest.writeString(this.userid);
        dest.writeString(this.isOnline);
        dest.writeInt(this.fans);
        dest.writeString(this.todaytime);
        dest.writeString(this.weektime);
        dest.writeString(this.paiming);
        dest.writeByte(is_focus ? (byte) 1 : (byte) 0);
    }

    public UserEntry() {
    }

    protected UserEntry(Parcel in) {
        this.id = in.readString();
        this.db_id = in.readString();
        this.name = in.readString();
        this.password = in.readString();
        this.salt = in.readString();
        this.level = in.readString();
        this.sex = in.readString();
        this.age = in.readString();
        this.campus = in.readString();
        this.school = in.readString();
        this.mobile = in.readString();
        this.mail = in.readString();
        this.wtime = in.readString();
        this.sessionid = in.readString();
        this.sign = in.readString();
        this.cps_id = in.readString();
        this.cs_id = in.readString();
        this.class_id = in.readString();
        this.grade = in.readString();
        this.timelong = in.readString();
        this.img = in.readString();
        this.tch_id = in.readString();
        this.reg_time = in.readString();
        this.login_time = in.readString();
        this.login_status = in.readString();
        this.is_open_practice = in.readString();
        this.userid = in.readString();
        this.isOnline = in.readString();
        this.fans = in.readInt();
        this.todaytime = in.readString();
        this.weektime = in.readString();
        this.paiming = in.readString();
        this.is_focus = in.readByte() != 0;
    }

    public static final Parcelable.Creator<UserEntry> CREATOR = new Parcelable.Creator<UserEntry>() {
        @Override
        public UserEntry createFromParcel(Parcel source) {
            return new UserEntry(source);
        }

        @Override
        public UserEntry[] newArray(int size) {
            return new UserEntry[size];
        }
    };
}
