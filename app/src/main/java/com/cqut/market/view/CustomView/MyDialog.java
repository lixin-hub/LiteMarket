package com.cqut.market.view.CustomView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.cqut.market.view.activity.LoginActivity;
import com.cqut.market.view.activity.LunchActivity;
import com.cqut.market.view.activity.MainActivity;


public class MyDialog {

    public static void showLoginInvalid(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);            //点击对话框以外的区域是否让对话框消失
        builder.setNegativeButton("确定", (dialog, which) -> {
            Intent intent=new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            ((LunchActivity)context).finish();
        });
/*
        builder.setPositiveButton("不登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(context, MainActivity.class);
                context.startActivity(intent);
                ((LunchActivity)context).finish();

            }
        });
*/
        AlertDialog dialog = builder.create();      //创建AlertDialog对象
        //对话框显示的监听事
        dialog.show();                            //显示对话框
    }

    public static AlertDialog.Builder getDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);            //点击对话框以外的区域是否让对话框消失
        //创建AlertDialog对象
        //对话框显示的监听事
       return builder;                          //显示对话框
    }
    public static void showToast(Context context,String content){
        ((Activity)context).runOnUiThread(() -> Toast.makeText(context, content, Toast.LENGTH_SHORT).show());
    }
    public static void showToastLong(Context context,String content){
        ((Activity)context).runOnUiThread(() -> Toast.makeText(context, content, Toast.LENGTH_LONG).show());
    }
    public static ProgressDialog getProgressDialog(Context context,String title,String message){
        ProgressDialog progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(true);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        return progressDialog;
    }
}
