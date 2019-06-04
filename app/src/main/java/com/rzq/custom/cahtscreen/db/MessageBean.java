package com.rzq.custom.cahtscreen.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "message")
public class MessageBean {
    /**
     * {\"senderid\":7,\"msgid\":10,\"receiverid\":6,\"msginfo\":\"%20%E7%9C%8B%E8%AE%B0%E5%BD%95\",\"updatedt\":\"2019-05-26T11:24:35.083\",\"sendtime\":\"2019-05-26T11:24:35.083\",\"status\":\"0\"
     */
    public static final String COLUMNNAME_IDX = "idx";
    public static final String COLUMNNAME_SERVERIDX = "msgid";
    public static final String COLUMNNAME_MSG = "msg";
    public static final String COLUMNNAME_UPDATEDT = "updatedt";
    public static final String COLUMNNAME_SENDTIME = "sendtime";
    public static final String COLUMNNAME_GIFT = "gift";
    public static final String COLUMNNAME_REDPACKET = "redPacket";
    public static final String COLUMNNAME_RECEIVERID = "receiverid";
    public static final String COLUMNNAME_SENDERID = "senderid";
    public static final String COLUMNNAME_SENDSTATE = "status";
    public static final String COLUMNNAME_IMG = "img";
    public static final int SEND = 2;
    public static final int OK = 1;
    public static final int NOT = -1;


    @DatabaseField(generatedId = true, useGetSet = true, columnName = COLUMNNAME_IDX)
    private int idx;
    @DatabaseField(unique = true, useGetSet = true, columnName = COLUMNNAME_SERVERIDX)
    private int msgid;
    @DatabaseField(useGetSet = true, columnName = COLUMNNAME_MSG)
    private String msginfo;
    @DatabaseField(useGetSet = true, columnName = COLUMNNAME_UPDATEDT)
    private Date updatedt;
    @DatabaseField(useGetSet = true, columnName = COLUMNNAME_SENDTIME)
    private Date sendtime;
    @DatabaseField(useGetSet = true, columnName = COLUMNNAME_GIFT)
    private String gift;
    @DatabaseField(useGetSet = true, columnName = COLUMNNAME_REDPACKET)
    private String redPacket;
    @DatabaseField(useGetSet = true, columnName = COLUMNNAME_IMG)
    private String img;
    @DatabaseField(useGetSet = true, columnName = COLUMNNAME_RECEIVERID)
    private int receiverid;
    @DatabaseField(useGetSet = true, columnName = COLUMNNAME_SENDERID)
    private int senderid;
    @DatabaseField(useGetSet = true, columnName = COLUMNNAME_SENDSTATE)
    private int status;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getMsgid() {
        return msgid;
    }

    public void setMsgid(int msgid) {
        this.msgid = msgid;
    }

    public String getMsginfo() {
        return msginfo;
    }

    public void setMsginfo(String msginfo) {
        this.msginfo = msginfo;
    }

    public Date getUpdatedt() {
        return updatedt;
    }

    public void setUpdatedt(Date updatedt) {
        this.updatedt = updatedt;
    }

    public Date getSendtime() {
        return sendtime;
    }

    public void setSendtime(Date sendtime) {
        this.sendtime = sendtime;
    }

    public String getGift() {
        return gift;
    }

    public void setGift(String gift) {
        this.gift = gift;
    }

    public String getRedPacket() {
        return redPacket;
    }

    public void setRedPacket(String redPacket) {
        this.redPacket = redPacket;
    }

    public int getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(int receiverid) {
        this.receiverid = receiverid;
    }

    public int getSenderid() {
        return senderid;
    }

    public void setSenderid(int senderid) {
        this.senderid = senderid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
