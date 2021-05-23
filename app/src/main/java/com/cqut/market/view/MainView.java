package com.cqut.market.view;

import android.graphics.Bitmap;

import com.cqut.market.beans.Good;
import com.cqut.market.beans.Order;
import com.cqut.market.view.BaseView;

import java.util.ArrayList;
import java.util.List;

public interface MainView extends BaseView {
    void onLoadPicture(Bitmap bitmaps);
    void onGoodsResponse(ArrayList<Good> goods);
    void onGoodLoadFailed(Exception e);
    void onLoadPictureFailed(Exception e);
    void getGetOrderCounts(int counts);
}
