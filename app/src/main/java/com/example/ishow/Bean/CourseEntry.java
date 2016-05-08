package com.example.ishow.Bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.List;

/**
 * Created by MRME on 2016-03-31.
 */
@Table(name = "CourseEntry")
public class CourseEntry implements Serializable, Parcelable {
     /* {
            "id": "578",
            "title": "I'm really sorry",
            "img": "",
            "path": "http://7xlmbe.com1.z0.glb.clouddn.com/2016-04-01/56fde55cdeae1.mp3",
            "content": "中级",
            "wtime": "2016-04-01 11:05:07",
            "audio_txt": "",
            "video_url": ""
        }
      */

    private int courseState=0;//0没下载 1下载中 2暂停  3下载完成
    private int progressbar;
    @Column(name ="id",isId = true)
    private int id;

    @Override
    public String toString() {
        return "CourseEntry{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", dirPath='" + dirPath + '\'' +
                ", baseCourseMaxDownloadSize=" + baseCourseMaxDownloadSize +
                ", fileDownloadListener=" + fileDownloadListener +
                ", baseCourseHasDownloadSize=" + baseCourseHasDownloadSize +
                ", progressbar=" + progressbar +
                ", courseState=" + courseState +
                '}';
    }

    @Column(name ="title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "dirPath")
    private String dirPath;
    @Column(name = "baseCourseMaxDownloadSize")
    private int baseCourseMaxDownloadSize;


    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }



    public int getBaseCourseHasDownloadSize() {
        return baseCourseHasDownloadSize;
    }

    public void setBaseCourseHasDownloadSize(int baseCourseHasDownloadSize) {
        this.baseCourseHasDownloadSize = baseCourseHasDownloadSize;
    }

    public FileDownloadListener getFileDownloadListener() {
        return fileDownloadListener;
    }

    public void setFileDownloadListener(FileDownloadListener fileDownloadListener) {
        this.fileDownloadListener = fileDownloadListener;
    }

    private FileDownloadListener fileDownloadListener;
    private int baseCourseHasDownloadSize = 0;

    public int getBaseCourseMaxDownloadSize() {
        return baseCourseMaxDownloadSize;
    }

    public void setBaseCourseMaxDownloadSize(int baseCourseMaxDownloadSize) {
        this.baseCourseMaxDownloadSize = baseCourseMaxDownloadSize;
    }


    //课程下载进度
        public int getProgressbar() {
            return progressbar;
        }

        public void setProgressbar(int progressbar) {
            this.progressbar = progressbar;
        }
        public int getCourseState() {
            return courseState;
        }
        public void setCourseState(int courseState) {
            this.courseState = courseState;
        }

    public int getId() {
        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.courseState);
        dest.writeInt(this.progressbar);
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeString(this.dirPath);
        //dest.writeParcelable(this.fileDownloadListener, flags);
        dest.writeInt(this.baseCourseHasDownloadSize);
        dest.writeInt(this.baseCourseMaxDownloadSize);
    }

    public CourseEntry() {
    }

    protected CourseEntry(Parcel in) {
        this.courseState = in.readInt();
        this.progressbar = in.readInt();
        this.id = in.readInt();
        this.title = in.readString();
        this.content = in.readString();
        this.dirPath = in.readString();
       // this.fileDownloadListener = in.readParcelable(FileDownloadListener.class.getClassLoader());
        this.baseCourseHasDownloadSize = in.readInt();
        this.baseCourseMaxDownloadSize = in.readInt();
    }

    public static final Parcelable.Creator<CourseEntry> CREATOR = new Parcelable.Creator<CourseEntry>() {
        @Override
        public CourseEntry createFromParcel(Parcel source) {
            return new CourseEntry(source);
        }

        @Override
        public CourseEntry[] newArray(int size) {
            return new CourseEntry[size];
        }
    };
}

