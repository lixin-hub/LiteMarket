package com.cqut.market.model;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.core.app.NotificationCompat;

import com.cqut.market.R;
import com.cqut.market.view.CustomView.MyDialog;
import com.cqut.market.view.activity.ShowMineItemActivity;

import java.lang.reflect.Method;

public class Util {
    public static void vibrator(Context context, long ms) {
        SharedPreferences preferences = context.getSharedPreferences(Constant.MY_MARKET_NAME, Context.MODE_PRIVATE);
        if (preferences.getBoolean(Constant.VIBRATORABLE, true)) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(ms);
        }
    }

    public static void aphAnim(View view, float... values) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", values);
        objectAnimator.setDuration(200);
        objectAnimator.start();
    }

    public static void transAnim(View view, String name, float... values) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, name, values);
        objectAnimator.setDuration(200);
        objectAnimator.start();
    }
    public static void openWebTo(WebView webView, String url) {
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }
        });
    }
    public static void clickAnimator(View v) {
        vibrator(v.getContext(), 50);
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
            NotificationChannel notificationChannel = new NotificationChannel(id, name, importance);
            manager.createNotificationChannel(notificationChannel);
        }
        return manager;
    }

    public static Notification createNotification(Context context, String title, String content, String id) {
        Intent intent = new Intent(context, ShowMineItemActivity.class);
        intent.putExtra("item", Constant.MINE_ORDER);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(context, id)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher)
                .build();
    }

    public static int getStatusBarHeight(Activity mActivity) {
        Resources resources = mActivity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static int getNavigationBarHeight(Activity mActivity) {
        Resources resources = mActivity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            //do something
        }
        return hasNavigationBar;
    }


    public static boolean isNavigationBarShow(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);
        int screenHeight = dm.heightPixels + getStatusBarHeight(activity);

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(realDisplayMetrics);
        } else {
            Class c;
            try {
                c = Class.forName("android.view.Display");
                Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
                method.invoke(display, realDisplayMetrics);
            } catch (Exception e) {
                realDisplayMetrics.setToDefaults();
                e.printStackTrace();
            }
        }

        int screenRealHeight = realDisplayMetrics.heightPixels;
        return (screenRealHeight - screenHeight) > 0;
    }
    public static void joinQQGroup(Context context,String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            MyDialog.showToast(context, "未安装手Q或安装的版本不支持");
        }
    }
}
