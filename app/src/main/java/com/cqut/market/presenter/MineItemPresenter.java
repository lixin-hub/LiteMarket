package com.cqut.market.presenter;

import com.cqut.market.beans.Order;
import com.cqut.market.beans.User;
import com.cqut.market.model.Constant;
import com.cqut.market.model.JsonUtil;
import com.cqut.market.model.MineItemModel;
import com.cqut.market.view.MineItemView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MineItemPresenter extends BasePresenter<MineItemView> {
    MineItemModel model = new MineItemModel();

    public void getOrderList(String userId, MineItemView mineItemView) {

        model.getOrders(userId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!Constant.NETWORK_INFO)
                    mineItemView.onCheckOrderFailed("连接失败");
                else
                    mineItemView.onCheckOrderFailed("抱歉，现在可能没有营业额，请您稍候再试吧");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonStr = response.body().string();
                String responseCode = JsonUtil.getResponseCode(jsonStr);
                if (responseCode != null && responseCode.equals(Constant.ORDER_NONE)) {
                    mineItemView.onCheckOrderSuccess(null);
                    return;
                }
                String[] strings = jsonStr.split("\n");
                if (strings.length > 0) {
                    ArrayList<Order> orders = new ArrayList<>();
                    for (String s : strings) {
                        orders.add(JsonUtil.parseJsonOrder(s));
                        mineItemView.onCheckOrderSuccess(orders);
                    }
                } else {
                    mineItemView.onCheckOrderSuccess(null);
                }
            }

        });
    }

    public void applyOrders(List<Order> orders, MineItemView mainView) {
        model.applyOrders(orders, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!Constant.NETWORK_INFO) {
                    mainView.onApplyOrders("网络不可用");
                } else
                    mainView.onApplyOrders("抱歉，现在可能还没有营业，请稍后购买吧");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                if (str != null) {
                    String code = JsonUtil.getResponseCode(str);
                    if (code != null && code.equals(Constant.ORDER_APPLY_SUCCESS))
                        mainView.onApplyOrders("提交成功,我们会尽快处理您的订单\n请到 我的->我的订单 查看订单状态有任何疑问请致电我们");
                    else if (code != null && code.equals(Constant.ORDER_APPLY_FAILED))
                        mainView.onApplyOrders("提交失败code" + code);
                    else mainView.onApplyOrders("提交失败code=null");
                } else mainView.onApplyOrders("提交失败jsonstr=null");

            }
        });
    }

    public void uploadUserInfo(User user, MineItemView mineItemView) {
        model.updateUserInfo(user, mineItemView);
    }

    public void deleteAccount(String id, MineItemView mineItemView) {
        model.deleteAccount(id,mineItemView);
    }

    public void uploadProblemCallback(String path,MineItemView mineItemView){
     model.uploadImage(path,mineItemView);
    }
    public void uploadProblemMessage(String message,MineItemView mineItemView){
        model.upLoadMessage(message,mineItemView);
    }

}
