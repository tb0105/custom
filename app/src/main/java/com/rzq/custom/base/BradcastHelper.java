package com.rzq.custom.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class BradcastHelper {
    BaseBroadcastReciver baseBradcastReciver;
    private final Context context;

    public BradcastHelper(Context context, BaseBroadcastReciver baseBradcastReciver) {
        this.context = context;
        this.baseBradcastReciver = baseBradcastReciver;
    }

    public void regist() {
        baseBradcastReciver.regist();
    }

    public void setActions(List<BaseAction> actions) {
        baseBradcastReciver.setActions(actions);
    }

    public static void sendBradcastMsg(Context context, Intent intent, String broadcastReceiver) {
        ComponentName componentName = new ComponentName(context.getClass().getPackage().getName(), broadcastReceiver);//参数1-包名 参数2-广播接收者所在的路径名
        intent.setComponent(componentName);
        context.sendBroadcast(intent);
    }

    public static void sendBradcastMsg(Context context, Intent intent) {
        context.sendBroadcast(intent);
    }

    public void unregisterReceiver() {
        try {
            if (context != null && baseBradcastReciver != null)
                context.unregisterReceiver(baseBradcastReciver);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
