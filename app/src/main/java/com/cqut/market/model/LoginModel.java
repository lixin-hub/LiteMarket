package com.cqut.market.model;

import androidx.annotation.NonNull;

import com.cqut.market.beans.User;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginModel implements Callback {

    private final String url = Constant.HOST + "login";
    private LoginListener listener;

    public static boolean isMobileNumber(String mobiles) {//判断手机号码格式
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public void login( String userName, String password, LoginListener loginListener) {
        this.listener = loginListener;
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
            User user = JsonUtil.parseJsonToUser(result);
            String code = JsonUtil.getResponseCode(result);
            if (code == null) {
                listener.onSuccess(user, Constant.LOGIN_FAILED);
                return;
            }
            listener.onSuccess(user, code);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public interface LoginListener {
        void onFailed();

        void onSuccess(User user, String responseCode);
    }


}
