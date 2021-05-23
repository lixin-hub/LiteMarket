package com.cqut.market.presenter;

import android.graphics.Bitmap;

import com.cqut.market.beans.Good;
import com.cqut.market.beans.Order;
import com.cqut.market.model.Constant;
import com.cqut.market.model.JsonUtil;
import com.cqut.market.model.MainModel;
import com.cqut.market.view.MainView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainPresenter extends BasePresenter<MainView> {
    MainModel model;

    public MainPresenter() {
        model = new MainModel();
    }

    public void requestGoodsData() {
        model.requestGoodsData(new MainModel.LoadGoodsListener() {
            @Override
            public void onSuccess(ArrayList<Good> goods) {
                getView().onGoodsResponse(goods);
            }

            @Override
            public void onFailed(Exception e) {
                getView().onGoodLoadFailed(e);
            }
        });
    }

    public void requestPicture(String name) {
        model.requestPicture(name, new MainModel.LoadBitmapsListener() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                getView().onLoadPicture(bitmap);
            }

            @Override
            public void onFailed(Exception e) {
                getView().onLoadPictureFailed(e);
            }
        });
    }
    public void getOrderCounts(MainView mainView){
        model.getOrderCounts(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainView.getGetOrderCounts(-1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonstr=response.body().string();
                if (jsonstr!=null) {
                    int counts = JsonUtil.parseOrderCounts(jsonstr);
                    mainView.getGetOrderCounts(counts);
                }else {
                    mainView.getGetOrderCounts(-1);
                }
            }
        });
    }
    public  String generateOrderCode(int counts) {
        String last = "";
        Random rand = new Random(counts);
        int shu1 = rand.nextInt(90) + 10;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String date = format.format(System.currentTimeMillis());
        if (counts < 10)
            last = "000" + counts;
        else if (counts < 100)
            last = "00" + counts;
        else if (counts < 1000)
            last = "0" + counts;
        else last = "" + counts;
        return date + shu1 + last;
    }
}
