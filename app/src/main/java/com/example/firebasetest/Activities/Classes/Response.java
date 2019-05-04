package com.example.firebasetest.Activities.Classes;

public class Response {
    String reply;
    String id;
    String replierId;
    boolean isImage;
    String questionId;
    boolean graded;
    boolean pass;
    String note;

    public Response() {

    }

    public Response(String reply, String id, String replierId, boolean isImage, String questionId) {
        this.reply = reply;
        this.id = id;
        this.replierId = replierId;
        this.isImage = isImage;
        this.questionId = questionId;
        this.note = "";
    }


    public String getNote() { return note; }

    public void setNote(String reply) { this.note = reply; }

    public String getReply() { return reply; }

    public void setReply(String reply) { this.reply = reply; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getReplierId() { return replierId; }

    public void setReplierId(String replierId) { this.replierId = replierId; }

    public boolean isImage() { return isImage; }

    //public void setImage(boolean image) { isImage = image; }

    public String getQuestionId() { return questionId; }

    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public boolean isGraded() { return graded; }

    public void setGraded(boolean graded) { this.graded = graded; }

    public boolean isPass() { return pass; }

    public void setPass(boolean pass) { this.pass = pass; }
}
