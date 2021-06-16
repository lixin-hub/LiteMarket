package com.cqut.market.model;

import androidx.annotation.NonNull;

import com.cqut.market.beans.User;
import com.sun.mail.util.MailSSLSocketFactory;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpModel implements Callback {
    private static final String url =  Constant.HOST +"signup";
    private static final String mail =  Constant.HOST +"mail?action=register_verify&UserMail=";
    private static final String host="smtp.qq.com";
    private static final String email="763819849@qq.com";
    private static final String password="alhirggvcbsxbcfh";
    private SignupListener listener;

    public void signup(@NonNull String userName, @NonNull String password, @NonNull SignUpModel.SignupListener signupListener) {
        this.listener = signupListener;
        RequestBody body = new FormBody.Builder()
                .add("userName", userName)
                .add("password", password)
                .build();
        OkHttpClient client = new OkHttpClient();
        Request request = new okhttp3.Request.Builder().url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(this);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();
        listener.onFailed();
    }

    @Override
    public void onResponse(Call call, Response response) {
        try {
            String result = response.body().string();
                listener.onSuccess(JsonUtil.parseJsonToUser(result),JsonUtil.getResponseCode(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void sendCheckCode(String toEmail,Callback callback){
        NetWorkUtil.sendRequest(mail + toEmail,callback);
    }
    public interface SignupListener {
        void onFailed();

        void onSuccess(User user,String responseCode);
    }
}
