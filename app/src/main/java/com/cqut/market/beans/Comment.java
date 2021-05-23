package com.cqut.market.beans;

public class Comment {
    private String content;
    private String time;
    private String userName;
    private String id;
    private String userId;

    public String getId() {
        return id;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Comment(String content,String userName,String id,String usrId) {
        this.userId=usrId;
        this.content = content;
        this.userName = userName;
        this.id=id;
        setTime(System.currentTimeMillis()+"");
    }
    public Comment(String content,String userName,String id) {
        this.content = content;
        this.userName = userName;
        this.id=id;
        setTime(System.currentTimeMillis()+"");
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
