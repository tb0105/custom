package com.rzq.custom;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.util.LinkedList;
import java.util.List;


public abstract class MyApp extends Application {
    public static MyApp instance;
    private List<Activity> activitys = new LinkedList<>();

    public static boolean isdebug = true;

    public static boolean isIsdebug() {
        return isdebug;
    }

    private void initDebig() {
        isdebug = this.getApplicationInfo() != null && (this.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initDebig();
        initCatchExcep();
        initTinkerPatch();
    }

    public void initCatchExcep() {
//        设置该CrashHandler为程序的默认处理器
        UnCeHandler catchExcep = new UnCeHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    /**
     * 热更新
     */
    protected void initTinkerPatch() {

    }

    public void addAc(Activity activity) {
        activitys.add(activity);
    }

    public void finiShAll() {
        try {
            for (Activity activity : activitys)
                if (activity != null && !activity.isFinishing()) activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeAc(Activity activity) {
        activitys.remove(activity);
    }
}
