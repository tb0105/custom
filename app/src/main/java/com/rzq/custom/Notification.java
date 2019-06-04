package com.rzq.custom;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.google.gson.Gson;
import com.rzq.custom.cahtscreen.AcChatScreen;
import com.rzq.custom.cahtscreen.UserInfo;
import com.rzq.custom.cahtscreen.db.FrendBean;


public class Notification {
    public void sendNotification(Context context, int count, String id) {
        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //渠道
        NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = null;
            channel = new NotificationChannel(id,
                    "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.GREEN); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            notifyManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, channel.getId())
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("新消息")
                    .setNumber(3)
                    .setContentText("您有" + count + "条新消息");
        } else {
            builder = new NotificationCompat.Builder(context, null)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("新消息")
                    .setNumber(3)
                    .setContentText("您有" + count + "条新消息");
        }
        // 创建一个启动其他Activity的Intent
        Intent intent = new Intent(context
                , MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                context, 0, intent, 0);
        //设置通知栏点击跳转
        builder.setContentIntent(pi);
        notifyManager.notify(1, builder.build());
    }
}
