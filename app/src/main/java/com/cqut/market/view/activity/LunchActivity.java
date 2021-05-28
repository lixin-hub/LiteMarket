package com.cqut.market.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cqut.market.R;
import com.cqut.market.model.Constant;
import com.cqut.market.view.CustomView.MyDialog;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LunchActivity extends AppCompatActivity {
    int i = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //设置修改状态栏
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏的颜色
        window.setStatusBarColor(Color.WHITE);
        setContentView(R.layout.activity_lunch);
        SharedPreferences sharedPreferences = this.getSharedPreferences(Constant.MY_MARKET_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int day = new Date(System.currentTimeMillis()).getDay();
        if (sharedPreferences.getInt(Constant.DAY,0)!=day){//如果是新的一天就清除图片缓存
            editor.remove(Constant.BING_PIC);
            editor.putInt(Constant.DAY,day);
            editor.apply();
            new Thread(() -> Glide.get(this).clearDiskCache()).start();
        }
        long time = sharedPreferences.getLong(Constant.LAST_LOGIN_TIME, System.currentTimeMillis());
        if ((System.currentTimeMillis() - time) >= Constant.INVALID_TIME) {//登录过期
            editor.clear();
            editor.apply();
            MyDialog.showLoginInvalid(this, "登录过期", "重新登录");
        } else if ((System.currentTimeMillis() - time) < 500) {//第一次
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {//免登录
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    i++;
                    if (i == 1) {
                        Intent intent = new Intent(LunchActivity.this, MainActivity.class);
                        startActivity(intent);
                        timer.cancel();
                        finish();
                    }
                }
            }, Constant.LUNCH_TIME);
        }
    }
}
