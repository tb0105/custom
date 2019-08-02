package com.rzq.custom;

public class CustomBean {
    public String  name;
    public String qqwnum;
    public String img;
    public int code;
    public String  phone;
    int notnumber;

    public int getNotnumber() {
        return notnumber;
    }

    public void setNotnumber(int notnumber) {
        this.notnumber = notnumber;
    }

    public CustomBean(String name, String qqwnum, String img, int code,String phone) {
        this.name = name;
        this.qqwnum = qqwnum;
        this.img = img;
        this.code = code;
        this.phone = phone;
    }
}
