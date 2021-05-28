package com.cqut.market.presenter;

import android.view.View;

import com.cqut.market.beans.Comment;
import com.cqut.market.model.Constant;
import com.cqut.market.model.JsonUtil;
import com.cqut.market.model.OrderModel;
import com.cqut.market.view.OrderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OrderPresenter extends BasePresenter<OrderView> {
    private final OrderModel model;

    public OrderPresenter() {
        model = new OrderModel();
    }

    public void applyLikes(String id, OrderView orderView, View view) {
        model.commentLikes(id, orderView, view);
    }

    public void applyComment(Comment comment) {
        model.applyComment(comment, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getView().onCommentApplyFailed("提交评论失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String code = JsonUtil.getResponseCode(Objects.requireNonNull(response.body()).string());
                if (code != null && code.equals(Constant.COMMENT_SUCCESS)) {
                    getView().onCommentApply();
                } else {
                    getView().onCommentApplyFailed("评论失败");
                }
            }
        });
    }

    public void getComments(String id) {
        model.getComments(id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OrderView view = getView();
                if (view != null) view.onGetCommentsFailed("评论获取失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsons = response.body().string();
                ArrayList<Comment> comments = new ArrayList<>();
                for (String str : jsons.split("\n"))
                    comments.add(JsonUtil.parseComment(str));
                OrderView view = getView();
                if (view != null) view.onGetCommentsSuccess(comments);
            }
        });
    }

    public void getGood(String goodId) {
        model.getGoodById(goodId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                OrderView view = getView();
                if (view != null)
                    view.onGetGoodFailed("商品详情获取失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                if (str != null) {
                    OrderView view = getView();
                    if (view != null) view.onGetGoodSuccess(JsonUtil.parseJsonGoods(str));
                }
            }
        });
    }
}
