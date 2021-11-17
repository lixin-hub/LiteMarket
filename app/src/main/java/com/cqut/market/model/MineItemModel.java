package com.cqut.market.model;

import com.cqut.market.beans.Good;
import com.cqut.market.beans.Order;
import com.cqut.market.beans.User;
import com.cqut.market.view.CancelOrderListener;
import com.cqut.market.view.CustomView.CheckOrderListAdapter;
import com.cqut.market.view.MineItemView;
import com.cqut.market.view.MineView;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MineItemModel {
    static String applyOrdersUrl = Constant.HOST + "orders";
    final String url = Constant.HOST + "userinfo";
    String orderUrl = Constant.HOST + "orders";
    String problemUrl = Constant.HOST + "problem";
    String fileUrl = Constant.HOST + "file";

    private static String getMimeType(String fileName) {
        FileNameMap filenameMap = URLConnection.getFileNameMap();
        String contentType = filenameMap.getContentTypeFor(fileName);
        if (contentType == null) {
            contentType = "application/octet-stream"; //* exe,所有的可执行程序
        }
        return contentType;
    }

    public static void cancelOrder(Order order, String message, CheckOrderListAdapter.ViewHolder holder, CancelOrderListener cancelOrderListener) {
        String id = order.getId();
        HashMap<String, String> prams = new HashMap();
        prams.put("cancelOrder", id);
        prams.put("message", message);
        NetWorkUtil.sendRequestAddParms(applyOrdersUrl, prams, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                cancelOrderListener.onCancelOrder(null, holder);
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String str = response.body().string();
                String responseCode = JsonUtil.getResponseCode(str);
                if (responseCode != null && responseCode.equals(Constant.ORDER_CANCEL_SUCCESS))
                    cancelOrderListener.onCancelOrder(order, holder);
                else {
                    cancelOrderListener.onCancelOrder(null, holder);
                }
            }
        });
    }
    public void hasNewOrder(String userId,Callback callback){
        NetWorkUtil.sendRequestAddParms(orderUrl,"userIdForNewOrder",userId,callback);
    }

    public void getOrders(String userId, okhttp3.Callback callback) {
        NetWorkUtil.sendRequestAddParms(orderUrl, "userId", userId, callback);
    }

    public void applyOrders(List<Order> orders, Callback callback) {
        StringBuilder builder = new StringBuilder();
        for (Order order : orders) {
            Good good = order.getGood();
            Document goodDoc = new Document("name", good.getName())
                    .append("price", good.getPrice())
                    .append("category", good.getCategory())
                    .append("addTime", good.getAddTime())
                    .append("description", good.getDescription())
                    .append("imageName", good.getImageName())
                    .append("sales", good.getSales())
                    .append("stock", good.getStock())
                    .append("_id", new ObjectId(good.getId()));
            Document orderDoc = new Document()
                    .append("orderCode", order.getOrderCode())
                    .append("count", order.getCount())
                    .append("orderTime", order.getOrderTime())
                    .append("state", order.getState().name())
                    .append("transportFee", order.getTransport_fee())
                    .append("userId", order.getUserId())
                    .append("good", goodDoc)
                    .append("beizhu", order.getBeizhu());
            builder.append(orderDoc.toJson()).append("\n");
        }
        NetWorkUtil.sendRequestAddParms(applyOrdersUrl, "order", builder.toString(), callback);
    }

    public void updateUserInfo(User user, MineItemView mineItemView) {
        new MineModel().updateUserInfo(user, new MineView() {
            @Override
            public void onImageUploadSuccess() {

            }

            @Override
            public void onImageUploadFailed(String message) {

            }

            @Override
            public void onGetUserInfoSuccess(User user) {

            }

            @Override
            public void OnGetUserInfoFailed(String message) {

            }

            @Override
            public void onPostUserInfoSuccess() {
                mineItemView.onUploadReceiveGoodInfo("成功");
            }

            @Override
            public void onPostUserInfoFailed(String message) {
                mineItemView.onUploadReceiveGoodInfo(message);
            }
        });
    }

    public void deleteAccount(String id, MineItemView mineItemView) {
        NetWorkUtil.sendRequestAddParms(url, "deleteAccountId", id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mineItemView.onDeleteAccount("注销失败了，如果网络没问题，那就是我在偷懒。");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonstr = response.body().string();
                String code = JsonUtil.getResponseCode(jsonstr);
                if (code != null && code.equals(Constant.DELETE_ACCOUNT_SUCESS))
                    mineItemView.onDeleteAccount("注销成功");
                else mineItemView.onDeleteAccount("不知道为什么注销失败了，或许你应该再给我一次机会。");
            }
        });
    }

//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                if (!Constant.NETWORK_INFO)
//                    mineItemView.onUploadFile("提交失败:网络不可用");
//                else
//                    mineItemView.onUploadFile("提交失败:"+e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                String json = response.body().string();
//                String code = JsonUtil.getResponseCode(json);
//                if (code != null && code.equals(Constant.UPLOAD_FILE_SUCCESS))
//                    mineItemView.onUploadFile("您的反馈已经提交，感谢您的关注，我们将尽解决！");
//                else if (code != null && code.equals(Constant.UPLOAD_FILE_FAILED))
//                    mineItemView.onUploadFile("提交失败:服务端");
//                else mineItemView.onUploadFile("提交失败:未知");
//
//            }
//        });


    public void upLoadMessage(String message, MineItemView mineItemView) {
        NetWorkUtil.sendRequestAddParms(problemUrl, "message", message, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (!Constant.NETWORK_INFO)
                    mineItemView.onUploadFile("信息提交失败:网络不可用");
                else
                    mineItemView.onUploadFile("信息提交失败:因为程序狗在偷懒。滑稽.gif");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = Objects.requireNonNull(response.body()).string();
                String code = JsonUtil.getResponseCode(json);
                if (code != null && code.equals(Constant.UPLOAD_FILE_SUCCESS))
                    mineItemView.onUploadFile("您的反馈已经提交，感谢您的参与，我们将尽解决写bug的人");
                else if (code != null && code.equals(Constant.UPLOAD_FILE_FAILED))
                    mineItemView.onUploadFile("提交失败:服务端");
                else mineItemView.onUploadFile("提交失败:第196行");

            }
        });
    }

    public void uploadImage(String path, MineItemView mineItemView) {
        File file = new File(path);

        RequestBody requestBody = RequestBody.create(file, MediaType.parse("image/jpg"));
//        MultipartBody multipartBody=new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("message","1234456")
//                .addFormDataPart("image",file.getName(),requestBody)
//                .build();
        Request request = new Request.Builder()
                .addHeader("fileName", file.getName())
                .url(fileUrl).post(requestBody).build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (!Constant.NETWORK_INFO)
                    mineItemView.onUploadFile("提交失败:网络不可用");
                else
                    mineItemView.onUploadFile("提交失败:不是美女图片一律不接受。(请在尽量在晚上间提交)");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                file.delete();
                String json = Objects.requireNonNull(response.body()).string();
                String code = JsonUtil.getResponseCode(json);
                if (code != null && code.equals(Constant.UPLOAD_FILE_SUCCESS))
                    mineItemView.onUploadFile("图片上传成功");
                else if (code != null && code.equals(Constant.UPLOAD_FILE_FAILED))
                    mineItemView.onUploadFile("提交失败:位于服务端");
                else mineItemView.onUploadFile("提交失败:Mine第232行");
            }
        });

    }

}
