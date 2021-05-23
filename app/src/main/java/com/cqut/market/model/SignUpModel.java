package com.cqut.market.model;

import androidx.annotation.NonNull;

import com.cqut.market.beans.User;
import com.sun.mail.util.MailSSLSocketFactory;

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
    private final String url =  Constant.HOST +"signup";
    private final String host="smtp.qq.com";
    private final String email="763819849@qq.com";
    private final String password="alhirggvcbsxbcfh";
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

    public void sendCheckCode(String toEmail,String code){
        new Thread(() -> {
            Properties props = new Properties();
            try {
                // 开启debug调试
               // props.setProperty("mail.debug", "true");
                // 发送服务器需要身份验证
                props.setProperty("mail.smtp.auth", "true");
                // 设置邮件服务器主机名
                props.setProperty("mail.host", "smtp.qq.com");
                // 发送邮件协议名称
                props.setProperty("mail.transport.protocol", "smtp");

                MailSSLSocketFactory sf = new MailSSLSocketFactory();
                sf.setTrustAllHosts(true);
                props.put("mail.smtp.ssl.enable", "true");
                props.put("mail.smtp.ssl.socketFactory", sf);

                Session session = Session.getInstance(props);

                Message msg = new MimeMessage(session);
                msg.setSubject("您的验证码到了");
                msg.setSentDate(new Date());
                msg.setText("您的验证码是："+code);

                msg.setFrom(new InternetAddress(email,"Market","utf-8"));

                Transport transport = session.getTransport();
                transport.connect(host, email, password);//发送方密码为POP3/SMTP服务码
                transport.sendMessage(msg, new Address[]{new InternetAddress(toEmail)});
                transport.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();


    }
    public interface SignupListener {
        void onFailed();

        void onSuccess(User user,String responseCode);
    }
}
