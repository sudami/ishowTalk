package com.example.ishow.Bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by MRME on 2016-04-25.
 */
@Table(name = "MsgEntry")
public class MsgEntry {
    @Column(name = "fromUserid")
    String fromUserid;

    @Override
    public String toString() {
        return "MsgEntry{" +
                "fromUserid='" + fromUserid + '\'' +
                ", fromMobile='" + fromMobile + '\'' +
                ", fromImg='" + fromImg + '\'' +
                ", fromNick='" + fromNick + '\'' +
                ", toUserid='" + toUserid + '\'' +
                ", toMobile='" + toMobile + '\'' +
                ", toImg='" + toImg + '\'' +
                ", toNick='" + toNick + '\'' +
                ", msgTime=" + msgTime +
                ", textMsg='" + textMsg + '\'' +
                ", isRead=" + isRead +
                ", state=" + state +
                ", listPosition=" + listPosition +
                '}';
    }
    @Column(name = "fromMobile")
    String fromMobile;
    @Column(name = "fromImg")
    String fromImg;
    @Column(name = "fromNick")
    String fromNick;
    @Column(name = "toUserid")
    String toUserid;
    @Column(name = "toMobile")
    String toMobile;
    @Column(name = "toImg")
    String toImg;
    @Column(name = "toNick")
    String toNick;
    @Column(name = "msgTime")
    long msgTime;
    @Column(name = "textMsg")
    String textMsg;
    @Column(name = "isRead")
    boolean isRead;
    @Column(name = "state")
    int state;
    @Column(name ="id",isId = true)
    int id;
    @Column(name = "listPosition")
    int listPosition;
    @Column(name = "msgType")
    String msgType;

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }


    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getTextMsg() {
        return textMsg;
    }

    public void setTextMsg(String textMsg) {
        this.textMsg = textMsg;
    }
    public String getToNick() {
        return toNick;
    }

    public void setToNick(String toNick) {
        this.toNick = toNick;
    }

    public String getFromUserid() {
        return fromUserid;
    }

    public void setFromUserid(String fromUserid) {
        this.fromUserid = fromUserid;
    }

    public String getFromMobile() {
        return fromMobile;
    }

    public void setFromMobile(String fromMobile) {
        this.fromMobile = fromMobile;
    }

    public String getFromImg() {
        return fromImg;
    }

    public void setFromImg(String fromImg) {
        this.fromImg = fromImg;
    }

    public String getFromNick() {
        return fromNick;
    }

    public void setFromNick(String fromNick) {
        this.fromNick = fromNick;
    }

    public String getToUserid() {
        return toUserid;
    }

    public void setToUserid(String toUserid) {
        this.toUserid = toUserid;
    }

    public String getToMobile() {
        return toMobile;
    }

    public void setToMobile(String toMobile) {
        this.toMobile = toMobile;
    }

    public String getToImg() {
        return toImg;
    }

    public void setToImg(String toImg) {
        this.toImg = toImg;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


}
