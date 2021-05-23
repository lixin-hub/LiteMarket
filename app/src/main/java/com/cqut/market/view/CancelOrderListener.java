package com.cqut.market.view;

import com.cqut.market.beans.Order;
import com.cqut.market.view.CustomView.CheckOrderListAdapter;

public interface CancelOrderListener {
    void onCancelOrder(Order s, CheckOrderListAdapter.ViewHolder holder);

}
