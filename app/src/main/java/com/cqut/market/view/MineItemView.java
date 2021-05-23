package com.cqut.market.view;

import com.cqut.market.beans.Order;

import java.util.ArrayList;

public interface MineItemView extends BaseView{
    void onCheckOrderSuccess(ArrayList<Order> orders);
    void onCheckOrderFailed(String message);
    void onApplyOrders(String message);
    void onUploadReceiveGoodInfo(String message);//上传收货信息
     void onDeleteAccount(String message);
     void onUploadFile(String message);

}
