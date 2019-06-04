package com.rzq.custom.cahtscreen.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

public abstract class BaseDao<B> {
    protected Context context;
    protected Dao<B, Integer> dao;

    public BaseDao(Context context) {
        this.context = context;
    }

    public BaseDao(Context context, Class b) {
        this.context = context;
        try {
            this.dao = DatabaseHelper.getInstance(context).getDao(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
