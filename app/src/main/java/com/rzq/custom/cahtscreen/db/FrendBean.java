package com.rzq.custom.cahtscreen.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "frendInfo")
public class FrendBean {
    // 定义字段在数据库中的字段名
    public static final String COLUMNNAME_USERID = "userid";
    public static final String COLUMNNAME_FRIEND = "friendid";
    public static final String COLUMNNAME_UPDATE = "updatedt";
    public static final String COLUMNNAME_STATUS = "status";
    public static final String COLUMNNAME_COMMENT = "comment";
    public static final String COLUMNNAME_HEAD = "head";
    public static final String COLUMNNAME_GROP = "grop";
    @DatabaseField(id = true, generatedId = false, columnName = COLUMNNAME_FRIEND, useGetSet = true, canBeNull = false)
    private int friendid;
    @DatabaseField(columnName = COLUMNNAME_COMMENT, useGetSet = true, canBeNull = false)
    private String comment;
    @DatabaseField(columnName = COLUMNNAME_HEAD, useGetSet = true)
    private String head;
    @DatabaseField(columnName = COLUMNNAME_USERID, useGetSet = true, canBeNull = false)
    private int userid;
    @DatabaseField(columnName = COLUMNNAME_UPDATE, useGetSet = true, canBeNull = false)
    private Date updatedt;

    @DatabaseField(columnName = COLUMNNAME_STATUS, useGetSet = true, canBeNull = false)
    private int status;
    @DatabaseField(columnName = COLUMNNAME_GROP, useGetSet = true)
    private String grop;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getGrop() {
        return grop;
    }

    public void setGrop(String grop) {
        this.grop = grop;
    }


    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public Date getUpdatedt() {
        return updatedt;
    }

    public void setUpdatedt(Date updatedt) {
        this.updatedt = updatedt;
    }

    public int getFriendid() {
        return friendid;
    }

    public void setFriendid(int friendid) {
        this.friendid = friendid;
    }
}
