package com.cqut.market.beans;

public class Comment {
    private String content;
    private String time;
    private String userName;
    private String goodId;
    private String userId;
    private String commentId;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    private int likes=0;

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }


    public String getGoodId() {
        return goodId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setGoodId(String goodId) {
        this.goodId = goodId;
    }
    public Comment(String content,String userName,String id,String usrId) {
        this.userId=usrId;
        this.content = content;
        this.userName = userName;
        this.goodId =id;
        setTime(System.currentTimeMillis()+"");
    }
    public Comment(String content,String userName,String id) {
        this.content = content;
        this.userName = userName;
        this.goodId =id;
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
