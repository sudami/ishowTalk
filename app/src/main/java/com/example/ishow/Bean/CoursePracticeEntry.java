package com.example.ishow.Bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by MRS on 2016/5/11.
 */
@Table(name = "CoursePracticeEntry")
public class CoursePracticeEntry {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getPracticeTime() {
        return practiceTime;
    }

    public void setPracticeTime(int practiceTime) {
        this.practiceTime = practiceTime;
    }

    public String getIsModify() {
        return isModify;
    }

    public void setIsModify(String isModify) {
        this.isModify = isModify;
    }

    @Column(name ="id",isId = true)
    int id;
    @Column(name ="courseId")
    int courseId;

    @Override
    public String toString() {
        return "CoursePracticeEntry{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", practiceTime=" + practiceTime +
                ", isModify='" + isModify + '\'' +
                '}';
    }

    @Column(name ="practiceTime")
    int practiceTime;
    @Column(name ="isModify")
    String isModify;
}
