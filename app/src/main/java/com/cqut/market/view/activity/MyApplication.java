package com.cqut.market.view.activity;

import android.app.Application;
import android.content.SharedPreferences;

import com.cqut.market.model.Constant;
import com.cqut.market.model.FileUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //记录崩溃信息
        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                //获取崩溃时的UNIX时间戳
                long timeMillis = System.currentTimeMillis();
                //将时间戳转换成人类能看懂的格式，建立一个String拼接器
                StringBuilder stringBuilder = new StringBuilder(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(timeMillis)));
                stringBuilder.append(":\n");
                //获取错误信息
                stringBuilder.append(throwable.getMessage());
                stringBuilder.append("\n");
                //获取堆栈信息+
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                stringBuilder.append(sw.toString());
                SharedPreferences sharedPreferences = getSharedPreferences(Constant.MY_MARKET_NAME, MODE_PRIVATE);
                String userId = sharedPreferences.getString(Constant.USER_ID,"");
                //这就是完整的错误信息了，你可以拿来上传服务器，或者做成本地文件保存等等等等
                String errorLog = stringBuilder.toString();
                errorLog = "userId:"+userId + "\n" + errorLog;
                FileUtil.saveData(errorLog, "uncaughtException.txt");
//                最后如何处理这个崩溃，这里使用默认的处理方式让APP停止运行
                defaultHandler.uncaughtException(thread, throwable);
            }
        });
    }
}
