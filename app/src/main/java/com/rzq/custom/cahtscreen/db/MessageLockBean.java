package com.rzq.custom.cahtscreen.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "messagelock")
public class MessageLockBean {
    public static final String STATE = "msgstate";
    public static final String MSGID = "msgid";
    public static final int NOT = -1;
    public static final int OK = 1;
    @DatabaseField(id = true, generatedId = false, useGetSet = true, columnName = MSGID)
    private int msgid;
    @DatabaseField(useGetSet = true, columnName = STATE)
    private int state;

    public int getMsgid() {
        return msgid;
    }

    public void setMsgid(int msgid) {
        this.msgid = msgid;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
