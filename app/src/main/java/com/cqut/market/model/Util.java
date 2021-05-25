package com.cqut.market.model;

import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Vibrator;
import android.view.View;

import androidx.core.app.NotificationCompat;

import com.cqut.market.R;
import com.cqut.market.view.activity.ShowMineItemActivity;

public class Util {
    public static void vibrator(Context context, long ms) {
        SharedPreferences preferences = context.getSharedPreferences(Constant.MY_MARKET_NAME, Context.MODE_PRIVATE);
        if (preferences.getBoolean(Constant.VIBRATORABLE, true)) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(ms);
        }
    }

    public static void clickAnimator(View v) {
        vibrator(v.getContext(),50);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(v, "alpha", 1, 0, 1);
        objectAnimator.setDuration(200);
        objectAnimator.start();
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(v, "translationY", 0, 50, 0);
        objectAnimator1.setDuration(200);
        objectAnimator1.start();
    }

    public static NotificationManager createNotificationManger(Context context, String id, String name, int importance) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(id, name,importance);
            manager.createNotificationChannel(notificationChannel);
        }
        return manager;
    }

    public static Notification createNotification(Context context, String title, String content, String id) {
        Intent intent=new Intent(context, ShowMineItemActivity.class);
        intent.putExtra("item",Constant.MINE_ORDER);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(context, id)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher)
                .build();
    }
}
