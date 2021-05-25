package com.cqut.market.view;

import com.cqut.market.beans.Message;

import java.util.List;

public interface MessageView extends BaseView{

    void onMessageResult(List<Message> messages);
    void onSendMessageResult(String message);
    void onClear(String result);
}
