package com.example.ishow.Bean;

import java.io.Serializable;

/**
 * Created by MRME on 2016-03-28.
 */
public class ViewFlowEntry implements Serializable{
   /* [
    {
        "code": 1,
            "title": "优惠活动-广告栏",
            "img": "http://7xlm33.com1.z0.glb.clouddn.com/2016-03-17/56ea75f4c8f79.jpg",
            "pic_id": "14",
            "video_title": "10天活动",
            "link": "http://fuduji.ishowedu.com/home/activity/promotio?startTime=2016-03-19&endTime=2016-03-29",
            "video_id": "18"
    }
    ]*/

    int code;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getPic_id() {
        return pic_id;
    }

    public void setPic_id(String pic_id) {
        this.pic_id = pic_id;
    }

    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String img;
    String pic_id;
    String video_title;
    String link;
    String video_id;
    String title;
}
