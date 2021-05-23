package com.cqut.market.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.cqut.market.beans.Good;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainModel {
    String imageUrl = Constant.HOST + "image?imageName=";
    String goodUrl = Constant.HOST + "goods";
    String orderUrl = Constant.HOST + "orders";

    public void getOrderCounts(okhttp3.Callback callback) {
        NetWorkUtil.sendRequestAddParms(orderUrl, "order", "getOrderCounts", callback);
    }

    public void requestGoodsData(LoadGoodsListener loadListener) {

        if (!Constant.NETWORK_INFO) {
            String jsonstr = FileUtil.getData("goods");
            if (jsonstr == null || jsonstr.equals("")) return;
            ArrayList<Good> goods = new ArrayList<>();
            for (String s : jsonstr.split("\n"))
                goods.add(JsonUtil.parseJsonGoods(s));
            loadListener.onSuccess(goods);
            return;
        }
        NetWorkUtil.sendRequest(goodUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                loadListener.onFailed(e);
                String jsonstr = FileUtil.getData("goods");
                if (jsonstr == null || jsonstr.equals("")) return;
                ArrayList<Good> goods = new ArrayList<>();
                for (String s : jsonstr.split("\n"))
                    goods.add(JsonUtil.parseJsonGoods(s));
                loadListener.onSuccess(goods);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonstr = response.body().string();
                if (jsonstr == null) {
                    if (loadListener != null)
                        loadListener.onFailed(new Exception("jsonstr=null"));
                    return;
                }
                FileUtil.saveData(jsonstr, "goods");
                ArrayList<Good> goods = new ArrayList<>();
                for (String s : jsonstr.split("\n"))
                    goods.add(JsonUtil.parseJsonGoods(s));
                if (loadListener != null)
                    loadListener.onSuccess(goods);
            }
        });
    }

    public void requestPicture(String name, LoadBitmapsListener loadListener) {
        if (!Constant.NETWORK_INFO) {
            return;
        }
        NetWorkUtil.sendRequest(imageUrl + name, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                loadListener.onFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = new BufferedInputStream(response.body().byteStream());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                loadListener.onSuccess(bitmap);
            }
        });

    }


    public interface LoadGoodsListener {
        void onSuccess(ArrayList<Good> goods);

        void onFailed(Exception e);
    }

    public interface LoadBitmapsListener {
        void onSuccess(Bitmap bitmap);

        void onFailed(Exception e);
    }
}
