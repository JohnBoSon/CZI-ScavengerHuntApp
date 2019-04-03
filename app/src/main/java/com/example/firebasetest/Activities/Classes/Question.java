package com.example.firebasetest.Activities.Classes;

public class Question {
    String description;
    String id;
    String title;
    String replyType;

    public Question(String description, String id, String title, String replyType) {
        this.description = description;
        this.id = id;
        this.title = title;
        this.replyType = replyType;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getReplyType() {
        return replyType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReplyType(String replyType) {
        this.replyType = replyType;
    }
}
