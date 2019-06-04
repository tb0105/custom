package com.rzq.custom.msg;

import java.util.Date;

public class MessageListBean {
    private String name;
    private int msgCount;
    private Date updatedt;
    private String head;
    private String friend;
    private String hint;
    private String topMsg;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMsgCount(int msgCount) {
        this.msgCount = msgCount;
    }

    public int getMsgCount() {
        return msgCount;
    }

    public void setUpdatedt(Date updatedt) {
        this.updatedt = updatedt;
    }

    public Date getUpdatedt() {
        return updatedt;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getHead() {
        return head;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getFriend() {
        return friend;
    }

    public String  getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
    public void setTopMsg(String topMsg) {
        this.topMsg = topMsg;
    }

    public String getTopMsg() {
        return topMsg;
    }
}
