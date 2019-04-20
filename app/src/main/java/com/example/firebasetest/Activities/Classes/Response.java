package com.example.firebasetest.Activities.Classes;

public class Response {
    String reply;
    String id;
    String replierId;
    boolean isImage;
    String questionId;
    boolean graded;
    boolean pass;

    public Response(String reply, String id, String replierId, boolean isImage, String questionId) {
        this.reply = reply;
        this.id = id;
        this.replierId = replierId;
        this.isImage = isImage;
        this.questionId = questionId;
    }
}
