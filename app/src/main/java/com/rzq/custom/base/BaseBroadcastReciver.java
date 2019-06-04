package com.rzq.custom.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseBroadcastReciver<F> extends BroadcastReceiver {
    public static final String DEFULLOUTACTION = "com.tb.tblibry.base.notify.BaseBroadcastReciver";
    public static final String MSG = "com.tb.tblibry.base.notify.BradcastHelper.msg";

    protected final Context context;
    protected final F classf;
    protected List<BaseAction> actions = new ArrayList<>();


    public void setActions(List<BaseAction> actions) {
        this.actions = actions;
    }

    public BaseBroadcastReciver(F f, Context context) {
        this.context = context;
        this.classf=f;
        actions.add(new BaseAction(DEFULLOUTACTION, null));
    }

    protected void regist() {
        IntentFilter intentFilter = new IntentFilter();
        for (BaseAction action : actions) {
            if (action.getAction() != null)
                intentFilter.addAction(action.getAction());
            if (action.getCategory() != null)
                intentFilter.addCategory(action.getCategory());
        }
        context.registerReceiver(this, intentFilter);
    }
}
