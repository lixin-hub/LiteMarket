package com.cqut.market.model;

import com.cqut.market.beans.Message;
import com.cqut.market.view.MessageView;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.cqut.market.model.Constant.getLocalMessages;

public class MessageModel {
    private static final String url = Constant.HOST + "message";

    public static void hasNewMessage(String userId, NewMessageListener listener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("type", "messageCount");
        NetWorkUtil.sendRequestAddParms(url, params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.newMessageCount(-1);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonStr = response.body().string();
                Document document = null;

                if (jsonStr == null) {
                    listener.newMessageCount(-1);
                    return;
                }
                try {
                    document = Document.parse(jsonStr);
                } catch (Exception e) {
                    listener.newMessageCount(-1);
                }
                if (document != null) {
                    int counts = document.getInteger("counts");
                    listener.newMessageCount(counts);
                } else {
                    listener.newMessageCount(-1);
                }
            }
        });
    }

    public void getNewMessage(int size, String userId, MessageView messageView) {
        List<Message> messages = Constant.getLocalMessages();
        int temp = size;
        String type = "";
        if (size == Constant.GET_MESSAGE_NEW) {
            size = messages.size();
        } else if (size == Constant.GET_MESSAGE_ALL) {
            size = 0;
        } else if (size == Constant.GET_MESSAGE_NEW_ACTIVITY) {
            size = messages.size();
            type = "newActivity";
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", userId);
        params.put("size", size + "");
        params.put("type", type);
        NetWorkUtil.sendRequestAddParms(url, params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                messageView.onMessageResult(getLocalMessages());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonStr = response.body().string();
                String responseCode = JsonUtil.getResponseCode(jsonStr);
                if (responseCode != null && responseCode.equals(Constant.MESSAGE_NONE)) {
                    messageView.onMessageResult(messages);
                    return;
                }
                String[] strings = jsonStr.split("\n");
                if (temp != Constant.GET_MESSAGE_NEW_ACTIVITY)
                    FileUtil.saveDataAppend(jsonStr, "message");
                for (String s : strings) {
                    messages.add(JsonUtil.parseMessage(s));
                }
                messageView.onMessageResult(messages);
            }
        });
    }

    public void sendMessage(Message message, MessageView messageView) {
        String jsonSt = message.toDocument().toJson();
        NetWorkUtil.sendRequestAddParms(url, "Message", jsonSt, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (Constant.NETWORK_INFO)
                    messageView.onSendMessageResult("网络不可用");
                else messageView.onSendMessageResult(Constant.SEND_MESSAGE_FAILED);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonStr = response.body().string();
                String responseCode = JsonUtil.getResponseCode(jsonStr);
                if (responseCode != null && responseCode.equals(Constant.SEND_MESSAGE_SUCCESS))
                    messageView.onSendMessageResult(Constant.SEND_MESSAGE_SUCCESS);
                else messageView.onSendMessageResult(Constant.SEND_MESSAGE_FAILED);
            }
        });
    }

    public interface NewMessageListener {
        void newMessageCount(int count);
    }
}
