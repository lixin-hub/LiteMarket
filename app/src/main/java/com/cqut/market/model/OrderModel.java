package com.cqut.market.model;

import android.view.View;

import com.cqut.market.beans.Comment;
import com.cqut.market.view.OrderView;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderModel {
    private final String url = Constant.HOST + "comments";
    String goodUrl = Constant.HOST + "goods";

    public void commentLikes(String commentId, OrderView orderView, View view) {//评论点赞
        NetWorkUtil.sendRequestAddParms(url, "likes", commentId, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                orderView.onLikesSuccess("未知错误", view);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = Objects.requireNonNull(response.body()).string();
                String responseCode = JsonUtil.getResponseCode(result);
                if (responseCode != null && responseCode.equals(Constant.LIKES_SUCCESS)) {
                    orderView.onLikesSuccess(Constant.LIKES_SUCCESS, view);
                } else {
                    orderView.onLikesSuccess("未知错误", view);
                }
            }
        });
    }

    public void applyComment(Comment comment, Callback callback) {
        comment.setCommentId(new ObjectId().toString());
        Document document = new Document()
                .append("_id", comment.getCommentId())
                .append("userName", comment.getUserName())
                .append("time", comment.getTime())
                .append("goodId", comment.getGoodId())
                .append("content", comment.getContent())
                .append("userId", comment.getUserId())
                .append("likes", comment.getLikes());
        RequestBody body = new FormBody.Builder()
                .add("comment", document.toJson())
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
                .add("goodId", id)
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
