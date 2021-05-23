package com.cqut.market.model;

public enum OrderState {
    STATE_APPLY(1),
    STATE_CANCEL(2),
    STATE_PROCESSING(3),
    STATE_COMPLETED(4);
    private final int value;

     OrderState(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        switch (this.value) {
            case 1:
                return "订单已提交";
            case 2:
                return "订单已取消";
            case 3:
                return "订单处理中";
            case 4:
                return "订单已完成";
        }
        return null;
    }


}
