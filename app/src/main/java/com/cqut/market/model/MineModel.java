package com.cqut.market.model;

import com.cqut.market.beans.User;
import com.cqut.market.view.MineView;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MineModel {
    final String url =  Constant.HOST +"userinfo";

    public void getUserInfo(String id, MineView mineView) {
        if (!Constant.NETWORK_INFO) {
            getUserInfoInLocal(mineView);
            mineView.onImageUploadFailed("网络不可用");
            return;
        }
        NetWorkUtil.sendRequestAddParms(url, "id", id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mineView.OnGetUserInfoFailed(e.getMessage());
                getUserInfoInLocal(mineView);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonstr = response.body().string();
                String responseCode = JsonUtil.getResponseCode(jsonstr);
                if (responseCode != null && responseCode.equals(Constant.USER_NOT_EXISTED)) {
                    mineView.OnGetUserInfoFailed("用户不存在，请重新登录");
                }
                if (!response.equals(Constant.ORDER_NONE)) {
                    FileUtil.saveData(jsonstr, "userInfo");
                    User user = JsonUtil.parseJsonToUser(jsonstr);
                    if (user != null)
                        mineView.onGetUserInfoSuccess(user);
                    else {
                        getUserInfoInLocal(mineView);
                        mineView.OnGetUserInfoFailed("user=null，更新失败");
                    }
                } else {
                    getUserInfoInLocal(mineView);
                    mineView.OnGetUserInfoFailed("没有查询到订单额！");
                }
            }
        });
    }

    private void getUserInfoInLocal(MineView mineView) {
        String jsonstr = FileUtil.getData("userInfo");
        if (jsonstr != null&& !jsonstr.equals(""))
            mineView.onGetUserInfoSuccess(JsonUtil.parseJsonToUser(jsonstr));
    }

    public void updateUserInfo(User user, MineView mainView) {
        Document document = new Document()
                .append("_id", new ObjectId(user.getId()))
                .append("nickName", user.getNickName())
                .append("qqNumber", user.getQqNumber())
                .append("userName", user.getUserName())
                .append("password", user.getPassword())
                .append("phoneNumber", user.getPhoneNumber())
                .append("addr", user.getAddr())
                .append("email", user.getEmail());

        NetWorkUtil.sendRequestAddParms(url, "update", document.toJson(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!Constant.NETWORK_INFO)
                    mainView.onPostUserInfoFailed("网络未连接");
                else mainView.onPostUserInfoFailed(e.getMessage() + ":" + "失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonstr = response.body().string();
                if (jsonstr != null) {
                    String responseCode = JsonUtil.getResponseCode(jsonstr);
                    if (responseCode != null && responseCode.equals(Constant.UPDATE_SUCCESS))
                        mainView.onPostUserInfoSuccess();
                } else
                    mainView.onPostUserInfoFailed("失败");
            }
        });
    }
}
