package com.cqut.market.model;

import com.cqut.market.beans.Comment;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderModel {
    private final String url =  Constant.HOST +"comments";
    String goodUrl =  Constant.HOST +"goods";


    public void applyComment(Comment comment, Callback callback) {
        RequestBody body = new FormBody.Builder()
                .add("userName", comment.getUserName())
                .add("time", comment.getTime())
                .add("id", comment.getId())
                .add("content", comment.getContent())
                .add("userId",comment.getUserId())
                .add("flag", "apply")//提交
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new okhttp3.Request.Builder().url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void getComments(String id, Callback callback) {

        RequestBody body = new FormBody.Builder()
                .add("flag", "find")//查询
                .add("id", id)
                .build();
        OkHttpClient client = new OkHttpClient();
        Request request = new okhttp3.Request.Builder().url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void getGoodById(String id, Callback callback) {
        NetWorkUtil.sendRequestAddParms(goodUrl, "id", id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onResponse(call, response);
            }
        });
    }
}
