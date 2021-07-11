package com.cqut.market.model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cqut.market.beans.User;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpModel implements Callback {
    private static final String url = Constant.HOST + "signup";
    private static final String mail = Constant.HOST + "mail?action=register_verify&UserMail=";
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
            listener.onSuccess(JsonUtil.parseJsonToUser(result), JsonUtil.getResponseCode(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendCheckCode(String toEmail, Callback callback) {
        NetWorkUtil.sendRequest(mail + toEmail, callback);
    }



    public static void uploadDeviceId(UUID uuid, String userId) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("DEVICE_ID", uuid.toString());
        hashMap.put("userId", userId);
        NetWorkUtil.sendRequestAddParms(url, hashMap, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }

    public interface SignupListener {
        void onFailed();

        void onSuccess(User user, String responseCode);
    }
}
