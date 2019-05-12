package com.example.firebasetest.Activities.Classes;
import com.anychart.chart.common.dataentry.CategoryValueDataEntry;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public ArrayList<Response> responses =new ArrayList<>();


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

    //getting ride of articles to make word cloud better
    public boolean badData(String word){
        boolean isBadData = false;
        switch (word) {
            case "a": isBadData =true;
                break;
            case "in": isBadData =true;
                break;
            case "the": isBadData =true;
                break;
            case "on": isBadData =true;
                break;
            case "an": isBadData =true;
                break;
            case "it": isBadData =true;
                break;
            case "this": isBadData =true;
                break;
            case "that": isBadData =true;
                break;
            case "my": isBadData =true;
                break;
            case "it's": isBadData =true;
                break;
            case "to": isBadData =true;
                break;
            case "i": isBadData =true;
                break;
            default:
                break;
        }

        return isBadData;
    }

    public ArrayList<String> getWords(int qIndex) {
        ArrayList<String> words = new ArrayList<>();
            for(int m = 0; m < responses.size(); m++){
                if(responses.get(m).getQuestionId().equals(questions.get(qIndex).getId()) && responses.get(m).isGraded()){
                    String[] splited = responses.get(m).getReply().toLowerCase().split("\\s+");
                    for(int n = 0; n < splited.length; n++){
                        if(!badData(splited[n])){
                            words.add(splited[n]);
                        }
                    }
                }
            }
        return words;
    }

    public int getFrequency(int qIndex, String word){
        int count = 0 ;
        ArrayList<String> words = getWords(qIndex);
        for(int i  = 0; i < words.size(); i++){
            if(words.get(i).equals(word)){
                count++;
            }

        }


        return count;
    }

    public List <DataEntry> cloudData() {
        List<DataEntry> data = new ArrayList<>();
        for(int n = 0; n < questions.size(); n++){
            ArrayList<String> words = getWords(n);

            //remove duplicates
            Set<String> set = new HashSet<>(words);
            words.clear();
            words.addAll(set);

            for(int m = 0; m < words.size(); m++){
                data.add(new CategoryValueDataEntry(words.get(m), "Q" + (n + 1), getFrequency(n, words.get(m))));

            }
        }
        return data;
    }


    public List<DataEntry> columnData(){
        List<DataEntry> data = new ArrayList<>();

        for(int n = 0; n < questions.size(); n++){
            int count = 0;
            for(int m = 0; m < responses.size(); m++){
                if(responses.get(m).getQuestionId().equals(questions.get(n).getId()) && responses.get(m).isPass()){
                    count++;
                    data.add(new ValueDataEntry("Question " + (n+1), count));
                }
            }
        }

        return data;
    }


    public int getGrade(){
        int grade = 0;
        for(int i = 0; i < responses.size(); i++){
            if(responses.get(i).isPass()){
                grade++;
            }
        }
        return grade;
    }

    public String randomWordGenerater(){
        int max = 90;
        int min = 65;
        int random1 = (int )(Math.random() * max + min);
        int random2 = (int )(Math.random() * max + min);

        String word = Character.toString((char)random1) + "ytho"; //+ Character.toString((char)random2);

        return word;
    }

    public void generateFakeData(int nP){
        participants.clear();
        responses.clear();

        for(int i = 0 ; i < nP;i++){
            User u = new User( + i + "fake", "la-a the " + i);
            u.setSubmitted(true);
            participants.add(u);
            int countR = 0;
            for(int n = 0 ; n < questions.size(); n++){
                if(questions.get(n).getReplyType().equals("TEXT")) {
                    Response r = new Response(randomWordGenerater(), i + "" + n + "" + randomWordGenerater(), participants.get(i).getId(), false, questions.get(n).getId());
                    responses.add(r);
                    participants.get(i).setNumResponse(++countR);

                }
            }
        }
    }

    public void generateGradedFakeData(int nP){
        participants.clear();
        responses.clear();

        for(int i = 0 ; i < nP;i++){
            User u = new User( + i + "fake", "la-a the " + i);
            u.setSubmitted(true);
            participants.add(u);
            int countR = 0;
            for(int n = 0 ; n < questions.size(); n++){
                if(questions.get(n).getReplyType().equals("TEXT")) {
                    Response r = new Response(randomWordGenerater(), i + "" + n + "" + randomWordGenerater(), participants.get(i).getId(), false, questions.get(n).getId());
                    r.setGraded(true);
                    responses.add(r);
                    participants.get(i).setNumResponse(++countR);

                }
            }
        }
    }

    public ArrayList<Response> generateSwipeList(String qId){
        ArrayList<Response> rList = new ArrayList<>();
        for(int n = 0 ; n < responses.size(); n++){
            if(responses.get(n).getQuestionId().equals(qId) && !responses.get(n).isGraded() ){
                for(int i = 0 ; i < participants.size(); i++){
                    if(participants.get(i).getId().equals(responses.get(n).getReplierId()) && participants.get(i).isSubmitted()){
                        rList.add(responses.get(n));
                    }
                }
            }
        }
        return rList;
    }

    public boolean isGraded(){
        for(int i = 0; i < responses.size(); i++){
            if(!responses.get(i).isGraded()){
                return false;
            }
        }
        return true;
    }

    public int getQuestionPosition(String qId){
        int index = -1;
        for(int i = 0; i < questions.size();i++){
            if(qId.equals(questions.get(i).getId())){
                index = i;
            }
        }
        return index;
    }

    public int findIndexOfResponse(String rId){
        for(int i =0; i < responses.size();i++){
            if(responses.get(i).getId().equals(rId)){
                return i;
            }
        }
        return -1;
    }


    public int findNumCorrectResponse(String replierId){
        int count = 0;

        for(int i = 0 ; i < responses.size();i++){
            if(responses.get(i).getReplierId().equals(replierId) && responses.get(i).isPass()){
                count++;
            }
        }
        return count;
    }

    public int findNumResponse(String replierId){
        int count = 0;

        for(int i = 0 ; i < responses.size();i++){
            if(responses.get(i).getReplierId().equals(replierId)){
                count++;
            }
        }
        return count;
    }

    public Response findReplierResponse(int qIndex, String replierId){
        Response r = null;
        for(int i = 0; i < responses.size(); i++){
            if(getQuestionPosition(responses.get(i).getQuestionId()) == qIndex && responses.get(i).getReplierId().equals(replierId)){
                r = responses.get(i);
            }
        }
        return r;
    }

    public ArrayList<Response> generateResponseList(String replierId){
        ArrayList<Response> rList = new ArrayList();

        //find responses from replier id
        for(int n = 0; n < responses.size(); n++){
            if(responses.get(n).getReplierId().equals(replierId)){
                rList.add(responses.get(n));
            }
        }

        //selection sort
        for (int i = 0; i < rList.size() - 1; i++) {
            int index = i;
            for (int j = i + 1; j < rList.size(); j++) {
                if (getQuestionPosition(rList.get(j).getQuestionId()) < getQuestionPosition(rList.get(index).getQuestionId())) {
                    index = j;
                }
            }
            Response smaller = rList.get(index);
            rList.set(index, rList.get(i));
            rList.set(i, smaller);
        }

        return rList;
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
            for(int i=0; i< responses.size(); i++) {
                Response currentResponse = responses.get(i);
                if(currentResponse.questionId.equals(q.id)) {
                    responses.remove(i);
                    i--;
                }
            }
        }
        questions.remove(questions.indexOf(q));
    }
/*


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
    }*/


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
