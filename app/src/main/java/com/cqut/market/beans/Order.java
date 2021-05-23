package com.cqut.market.beans;

import com.cqut.market.model.OrderState;

import java.util.Comparator;

public class Order implements Comparator<Order> {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;//订单id
    private Good good;//订单里面的货物
    private String orderCode;//订单号
    private long orderTime;//订单提交时间
    private int count; //订单包含同类商品数量
    private int transport_fee;//配送费
    private String beizhu;//备注
    private OrderState state;
    private String userId;

    public String getBeizhu() {
        return beizhu;
    }

    public void setBeizhu(String beizhu) {
        this.beizhu = beizhu;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Good getGood() {
        return good;
    }

    public void setGood(Good good) {
        this.good = good;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTransport_fee() {
        return transport_fee;
    }

    public void setTransport_fee(int transport_fee) {
        this.transport_fee = transport_fee;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    @Override
    public int compare(Order o1, Order o2) {
        return (int) (o1.getOrderTime()-o2.getOrderTime());
    }
}
