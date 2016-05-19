package com.example.ishow.Utils;

import android.text.TextUtils;

import com.example.ishow.Application.iShowTalkApplication;
import com.example.ishow.Bean.Conversation;
import com.example.ishow.Bean.CoursePracticeEntry;
import com.example.ishow.Bean.MsgEntry;
import com.example.ishow.Bean.UserEntry;
import com.example.ishow.Xutils3.XHttpUtils;
import com.example.ishow.iShowConfig.iShowConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.List;

/**
 * Created by MRME on 2016-04-25.
 */
public class ChatManager {

    private DbManager dbManager;
    private long lastMsgTime = 0;
    public int saveTextMessage(MsgEntry entry, UserEntry studentInfo) {
        /*if (this.lastMsgTime==entry.getMsgTime())
            return 0;
        this.lastMsgTime =entry.getMsgTime();*/
        boolean equals = TextUtils.equals(entry.getFromUserid(), studentInfo.getUserid()==null? studentInfo.getId(): studentInfo.getUserid());
        Conversation conversation = new Conversation();
        conversation.setFromUserid(equals ? entry.getToUserid() : entry.getFromUserid());
        conversation.setFromNick(equals ? entry.getToNick() : entry.getFromNick());
        conversation.setFromImg(equals ? entry.getToImg() : entry.getFromImg());
        conversation.setFromMobile(equals ? entry.getToMobile() : entry.getFromMobile());
        conversation.setToUserid(equals ?  entry.getToUserid():entry.getFromUserid());
        conversation.setTextMsg(entry.getTextMsg());
        conversation.setMsgTime(entry.getMsgTime());
        conversation.setUnreadCount(entry.isRead() ? 0 : 1);

        if (dbManager == null)
            dbManager = x.getDb(iShowTalkApplication.getInstance().initDbConfig());
        try {

            Conversation first = getRecentConversation(equals ? entry.getToUserid() : entry.getFromUserid());
            if (first != null) {
                conversation.setUnreadCount(entry.isRead() ? first.getUnreadCount() : first.getUnreadCount() + 1);
                dbManager.deleteById(Conversation.class, first.getId());
                //dbManager.delete(first);
            }

            dbManager.saveBindingId(conversation);
            dbManager.saveBindingId(entry);

            if (equals) {
                entry = getRecentMessage(entry.getFromUserid(), entry.getToUserid());
                if (entry != null) return entry.getId();
            }
        } catch (DbException e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
        }
        return 0;
    }

    private Conversation getRecentConversation(String toUid) {
        // Selector<Conversation> selector = dbManager.selector(Conversation.class).where("fromUserid", "in", new String[]{fromUid, toUid}).or("toUserid", "in", new String[]{fromUid, toUid});

        Selector<Conversation> selector = null;
        try {
            selector = dbManager.selector(Conversation.class);
            if (selector != null)
                return selector.where("fromUserid", "=",  toUid).orderBy("id", false).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MsgEntry getRecentMessage(String fromUid, String toUid) {
        Selector<MsgEntry> selector = null;
        try {
            selector = dbManager.selector(MsgEntry.class);
            if (selector != null)
                return selector.where("fromUserid", "in", new String[]{fromUid, toUid}).and("toUserid", "in", new String[]{fromUid, toUid}).orderBy("id", true).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int updateMsgState(boolean ok, int dbId) {

        if (dbManager == null)
            dbManager = x.getDb(iShowTalkApplication.getInstance().initDbConfig());
        int ListPosition = -1;
        try {
            Selector<MsgEntry> selectorMsgEntry = dbManager.selector(MsgEntry.class).where("id", "=", dbId);
            MsgEntry firstMsgEntry = selectorMsgEntry.findFirst();
            if (firstMsgEntry != null) {
                dbManager.delete(firstMsgEntry);
                firstMsgEntry.setState(ok ? 1 : 2);
                dbManager.saveBindingId(firstMsgEntry);
                ListPosition = firstMsgEntry.getListPosition();

            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return ListPosition;

    }

    public List<MsgEntry> getChat15MessagesOrLoadMore(boolean isChating, String fromUid, String toUid, int offset) {
        if (dbManager == null)
            dbManager = x.getDb(iShowTalkApplication.getInstance().initDbConfig());
        List<MsgEntry> list = null;
        try {
            if (isChating)
                list = dbManager.selector(MsgEntry.class).where("fromUserid", "in", new String[]{fromUid, toUid})
                        .and("toUserid", "in", new String[]{fromUid, toUid}).where("isRead","=",false).limit(10).orderBy("id", true).findAll();
            else
                list = dbManager.selector(MsgEntry.class).where("fromUserid", "in", new String[]{fromUid, toUid})
                        .and("toUserid", "in", new String[]{fromUid, toUid}).orderBy("id", true).limit(15).offset(offset).findAll();

                if (list!=null){
                    Conversation recentConversation = getRecentConversation(toUid);
                    if (recentConversation != null) {
                        dbManager.delete(recentConversation);
                        recentConversation.setUnreadCount(0);
                        dbManager.saveBindingId(recentConversation);
                    }
                    for (MsgEntry entry:list) {
                        entry.setRead(true);
                        dbManager.update(entry,"isRead");
                    }
                }

        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Conversation> getConversationsList(String userid) {
        if (dbManager == null)
            dbManager = x.getDb(iShowTalkApplication.getInstance().initDbConfig());
        try {
            List<Conversation> list = dbManager.selector(Conversation.class).orderBy("msgTime", false).findAll();
            return list;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getConversationUnreadCount() {
        if (dbManager == null)
            dbManager = x.getDb(iShowTalkApplication.getInstance().initDbConfig());
        int unreadCount = 0;
        try {
            Selector<Conversation> selector = dbManager.selector(Conversation.class).expr("unreadCount");
            List<Conversation> all = selector.findAll();
            if (all != null) {
                for (Conversation conversation : all) {
                    unreadCount += conversation.getUnreadCount();
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return unreadCount;
    }

    /**
     *   根据 service里传过来的 courseId 从数据库检索 该Id对应的  CoursePracticeEntry对象
     * @param courseId
     * @return
     */
    public CoursePracticeEntry getCurrentPracticeEntry(int courseId)
    {   if (dbManager == null)
            dbManager = x.getDb(iShowTalkApplication.getInstance().initDbConfig());
        try {
            Selector<CoursePracticeEntry> selector = dbManager.selector(CoursePracticeEntry.class);
            if (selector!=null)
                return selector.where("courseId","=",courseId+"").findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据 课时courseid updateTime来 更新该courseid对应对象的数据
     * @param courseId
     * @param updateTime
     */
    public void saveCoursePracticeEntry(int courseId,int updateTime)
    {
        CoursePracticeEntry entry = getCurrentPracticeEntry(courseId);
        if (entry!=null)
        {
            entry.setPracticeTime(entry.getPracticeTime()+updateTime);
            try {
                dbManager.update(entry,"practiceTime");
            } catch (DbException e) {
                e.printStackTrace();
            }
        }else {
            CoursePracticeEntry emptyEntry = new CoursePracticeEntry();
            emptyEntry.setCourseId(courseId);
            emptyEntry.setPracticeTime(updateTime);
            try {
                dbManager.saveBindingId(emptyEntry);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }}


    public void uploadCoursePractice(String uid)
    {
        List<CoursePracticeEntry> historys=null;
        StringBuilder courseIdBuilder= new StringBuilder();
        StringBuilder practiceBuilder = new StringBuilder();
        if (dbManager == null)
            dbManager = x.getDb(iShowTalkApplication.getInstance().initDbConfig());
        try {
            List<CoursePracticeEntry> all = dbManager.findAll(CoursePracticeEntry.class);
           if (all!=null){
               if (all.size()>0){
                   for (CoursePracticeEntry entry: all) {
                       courseIdBuilder.append(entry.getCourseId());
                       practiceBuilder.append(entry.getPracticeTime());
                       courseIdBuilder.append("@");
                       practiceBuilder.append("@");
                   }
                   //一次 更新时间后删除该表  防止多次上传 时间错乱
                   dbManager.dropTable(CoursePracticeEntry.class);
                   JSONObject object = new JSONObject();
                   try {
                       object.put("uid",uid);
                       object.put("practicetime",  practiceBuilder.toString());
                       object.put("flag", "0");
                       object.put("practicecid", courseIdBuilder.toString());
                       XHttpUtils.getInstace().getValue(iShowConfig.uploadCoursePracticeTime,object,null);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

               }
           }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
