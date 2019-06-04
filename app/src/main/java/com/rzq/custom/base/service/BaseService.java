package com.rzq.custom.base.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.rzq.custom.base.BaseAction;
import com.rzq.custom.base.BaseBroadcastReciver;
import com.rzq.custom.base.BradcastHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseService extends Service {

    private static final String TAG = "BaseService";
    public static final String BASESERVICEACTION = "com.tb.tblibry.base.service.BaseService.action";
    public static final String BASESERVICEMSG = "com.tb.tblibry.base.service.BaseService.msg";
    protected BradcastHelper bradcastHelper;
    protected ReceiveCallBack receiveCallBack;

    public void setReceiveCallBack(ReceiveCallBack receiveCallBack) {
        this.receiveCallBack = receiveCallBack;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registBroadcast();
    }

    /**
     *
     */
    private void registBroadcast() {
        if (bradcastHelper == null) {
            bradcastHelper = new BradcastHelper(getApplicationContext(), new BaseServiceBroadcast(this,getApplicationContext()));
            List<BaseAction> actionList = new ArrayList<>();
            actionList.add(new BaseAction(BASESERVICEACTION, null));
            bradcastHelper.setActions(actionList);
            bradcastHelper.regist();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registBroadcast();
        Log.w(TAG, "onStartCommand,BradcastHelper=" + (bradcastHelper != null));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bradcastHelper.unregisterReceiver();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    public interface ReceiveCallBack {
        void onReceive(Context context, Intent intent);
    }

    protected class BaseServiceBroadcast extends BaseBroadcastReciver<BaseService> {

        public BaseServiceBroadcast(BaseService baseService, Context context) {
            super(baseService, context);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (receiveCallBack != null) {
                receiveCallBack.onReceive(context, intent);
            }
        }
    }
}
