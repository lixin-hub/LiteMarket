package com.cqut.market.presenter;

import com.cqut.market.beans.Message;
import com.cqut.market.model.MessageModel;
import com.cqut.market.view.MessageView;
import com.cqut.market.view.activity.MessageActivity;

import java.util.ArrayList;
import java.util.List;

public class MessagePresenter extends BasePresenter<MessageView> {
    private MessageModel messageModel;

    public void sendMessage(Message message, MessageView messageView) {
        messageModel = new MessageModel();
        messageModel.sendMessage(message, messageView);
    }

    public void requestMessage(int size, String userId, MessageView messageView) {
        messageModel = new MessageModel();
        messageModel.getNewMessage(size, userId, messageView);
    }

}
