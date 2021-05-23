package com.cqut.market.beans;

import org.bson.Document;

public class Message {
    public static int TYPE_SEND = 2;//发送的普通
    public static int TYPE_RECEIVE = 1;//接收到的普通消息
    public static int TYPE_ACTIVITY = 0;//活动消息
    private String id;
    private int messageType;//消息类型
    private String content;//消息内容
    private String title;//标题
    private String imagePath;//图片
    private String sendTo;//接收方标识
    private String from;//发送方标识
    private long date;//时间
    private String userId;//用户id

    public static Message getDefaultMessage(String content,String userId) {
        return new Message.Builder()
                .setContent(content)
                .setUserId(userId)
                .setDate(System.currentTimeMillis())
                .setMessageType(Message.TYPE_RECEIVE)
                .build();
    }
    public Document toDocument(){
        Document append = new Document()
                .append("userId", this.getUserId())
                .append("messageType", this.getMessageType())
                .append("title", this.getTitle())
                .append("imagePath", this.getImagePath())
                .append("sendTo", this.getSendTo())
                .append("content", this.getContent())
                .append("from", this.getFrom())
                .append("date", this.getDate());
        return append;
    }
    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", messageType=" + messageType +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", sendTo='" + sendTo + '\'' +
                ", from='" + from + '\'' +
                ", date=" + date +
                ", userId='" + userId + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public String getId() {
        return id;
    }

    public int getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getSendTo() {
        return sendTo;
    }

    public String getFrom() {
        return from;
    }

    public long getDate() {
        return date;
    }

    public static class Builder {
        private final Message message;

        public Builder() {
            message = new Message();
        }

        public Builder setUserId(String userId) {
            message.userId = userId;
            return Builder.this;
        }

        public Builder setId(String id) {
            message.id = id;
            return Builder.this;
        }

        public Builder setMessageType(int messageType) {
            //消息类型
            message.messageType = messageType;
            return Builder.this;
        }

        public Builder setContent(String content) {
            //消息内容
            message.content = content;
            return Builder.this;
        }

        public Builder setTitle(String title) {
            //标题
            message.title = title;
            return Builder.this;
        }

        public Builder setImagePath(String imagePath) {
            //图片
            message.imagePath = imagePath;
            return Builder.this;
        }

        public Builder setSendTo(String sendTo) {
            //接收方标识
            message.sendTo = sendTo;
            return Builder.this;
        }

        public Builder setFrom(String from) {
            //发送方标识
            message.from = from;
            return Builder.this;
        }

        public Builder setDate(long date) {
            //时间
            message.date = date;
            return Builder.this;
        }

        public Message build() {
            return message;
        }
    }
}
