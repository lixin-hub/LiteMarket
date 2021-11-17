package com.cqut.market.model;

import com.cqut.market.beans.User;
import com.cqut.market.view.MineView;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MineModel {
    final String url = Constant.HOST + "userinfo";

    public void getUserInfo(String id, MineView mineView) {
        if (!Constant.NETWORK_INFO) {
            getUserInfoInLocal(mineView);
            mineView.onImageUploadFailed("网络不可用");
            return;
        }
        NetWorkUtil.sendRequestAddParms(url, "id", id, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mineView.OnGetUserInfoFailed("连接失败");
                getUserInfoInLocal(mineView);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonstr = Objects.requireNonNull(response.body()).string();
                String responseCode = JsonUtil.getResponseCode(jsonstr);
                if (responseCode != null && responseCode.equals(Constant.USER_NOT_EXISTED)) {
                    mineView.OnGetUserInfoFailed("用户不存在，请重新登录");
                    return;
                }
                User user = JsonUtil.parseJsonToUser(jsonstr);
                if (user != null) {
                    mineView.onGetUserInfoSuccess(user);
                    FileUtil.saveData(jsonstr, "userInfo");
                } else {
                    getUserInfoInLocal(mineView);
                    mineView.OnGetUserInfoFailed("解析错误");
                }
            }
        });
    }

    private void getUserInfoInLocal(MineView mineView) {
        String jsonstr = FileUtil.getData("userInfo");
        if (jsonstr != null && !jsonstr.equals(""))
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
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (!Constant.NETWORK_INFO)
                    mainView.onPostUserInfoFailed("好像没有接入互联网");
                else mainView.onPostUserInfoFailed("跑腿的小弟现在可能在开小差，等会试试吧！");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonstr = Objects.requireNonNull(response.body()).string();
                String responseCode = JsonUtil.getResponseCode(jsonstr);
                if (responseCode == null) {
                    mainView.onPostUserInfoFailed("这次请求没有成功，请重新来一次吧。");
                    return;
                }
                if (responseCode.equals(Constant.UPDATE_SUCCESS))
                    mainView.onPostUserInfoSuccess();
                else
                    mainView.onPostUserInfoFailed("号码已经存在了");
            }
        });
    }
}
