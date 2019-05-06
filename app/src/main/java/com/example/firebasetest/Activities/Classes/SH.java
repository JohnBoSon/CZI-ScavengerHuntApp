package com.example.firebasetest.Activities.Classes;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SH {

    private boolean onGoing;
    private String id;
    private String ownerId;
    private String title;
    private String description;



    private String endDate;
    private int maxScore;

    public ArrayList<Question> questions =new ArrayList<>();
    public ArrayList<User> participants =new ArrayList<>();

    public SH() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public SH(String id, String ownerId, String title, String description, String date) {
        this.onGoing = false;
        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.maxScore = 0;
        this.endDate = date;
    }


    public boolean checkOngoing(){
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = formatter.format(todayDate);

        int year = Integer.parseInt(endDate.substring(0,4)) - 1900;
        int month = Integer.parseInt(endDate.substring(5,7)) - 1;
        int date = Integer.parseInt(endDate.substring(8));

        Date dueDate = new Date(year,month,date);

        if(dueDate.after(todayDate) ){
            return true;
        }

        return false;
    }

    public String getFormattedEndDate() {

        int year = Integer.parseInt(endDate.substring(0,4)) - 1900;
        int month = Integer.parseInt(endDate.substring(5,7)) - 1;
        int date = Integer.parseInt(endDate.substring(8));

        Date dueDate = new Date(year,month,date);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");

        return formatter.format(dueDate);
    }


    public void removeRespQues(Question q){
        for(int n =0; n < participants.size(); n++){
            for(int i=0; i<participants.get(n).responses.size(); i++) {
                Response currentResponse = participants.get(n).responses.get(i);
                if(currentResponse.questionId.equals(q.id)) {
                    participants.get(n).responses.remove(i);
                    i--;
                }
            }
        }
        questions.remove(questions.indexOf(q));
    }



    public int ungradedCounter(String QuesId){
        int countUngraded = 0;

        for(int n =0; n < participants.size(); n++){
            for(int i=0; i<participants.get(n).responses.size(); i++) {
                Response currentResponse = participants.get(n).responses.get(i);
                if(currentResponse.questionId == QuesId && !currentResponse.graded) {
                    countUngraded++;
                }
            }
        }
        return countUngraded;
    }

    public int studentScore(String studentId){
        int countGraded = 0;

        int index = -1;
        for(int i =0; i < participants.size();i++){
            if(participants.get(i).getId() == studentId){
                index = i;
            }
        }

        if(index == -1){
            //not found
        }else {
            for (int i = 0; i < participants.get(index).responses.size(); i++) {
                Response currentResponse = participants.get(index).responses.get(i);
                if (currentResponse.replierId == studentId && currentResponse.graded && currentResponse.pass) {
                    countGraded++;
                }
            }
        }
        return countGraded;
    }


    public String getEndDate() { return endDate; }

    public void setEndDate(String endDate) { this.endDate = endDate; }

    public boolean isOnGoing() {
        return onGoing;
    }

    public void setOnGoing(boolean onGoing) {
        this.onGoing = onGoing;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxScore() { return questions.size(); }

    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }

}
