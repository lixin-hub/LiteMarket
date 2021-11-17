package com.cqut.market.model;

import com.cqut.market.beans.Message;
import com.cqut.market.view.MessageView;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
                String jsonStr = Objects.requireNonNull(response.body()).string();
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
                if (!Constant.NETWORK_INFO)
                    messageView.onSendMessageResult("网络不可用");
                else messageView.onSendMessageResult("小弟没来上班，啥也做不了啊");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonStr = response.body().string();
                String responseCode = JsonUtil.getResponseCode(jsonStr);
                if (responseCode != null && responseCode.equals(Constant.SEND_MESSAGE_SUCCESS))
                    messageView.onSendMessageResult(Constant.SEND_MESSAGE_SUCCESS);
                else messageView.onSendMessageResult("what?鬼知道发生了什么");
            }
        });
    }

    public void clearMessage(String messageId, MessageView messageView) {
        NetWorkUtil.sendRequestAddParms(url, "clearMessage", messageId, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                messageView.onClear("哎呀，好像没能成功。");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String string = response.body().string();
                String responseCode = JsonUtil.getResponseCode(string);
                if (responseCode != null && responseCode.equals(Constant.CLEAR_MESSAGE_SUCCESS))
                    messageView.onClear("成功了");
                else
                    messageView.onClear("发生甚么事了，竟然失败了");
            }
        });
    }
    public void clearAllMessage(String userId, MessageView messageView) {
        NetWorkUtil.sendRequestAddParms(url, "clearAllMessage", userId, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                messageView.onClear("失败...是成功他妈. 狗头.jpg");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String string = response.body().string();
                String responseCode = JsonUtil.getResponseCode(string);
                if (responseCode != null && responseCode.equals(Constant.CLEAR_MESSAGE_SUCCESS))
                    messageView.onClear("消息删除成功");
                else
                    messageView.onClear("oh no！没成功。");
            }
        });
    }

    public interface NewMessageListener {
        void newMessageCount(int count);
    }
}
