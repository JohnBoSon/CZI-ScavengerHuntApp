package com.example.firebasetest.Activities.Classes;

import java.util.ArrayList;

public class User {
    private String id;
    private String name;
    private int numCorrect;
    private int numResponse;
    private boolean submitted;
    private int sListIndex;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        numCorrect = 0;
        numResponse = 0;
        submitted = false;
    }


    public int getsListIndex() { return sListIndex; }

    public void setsListIndex(int sListIndex) { this.sListIndex = sListIndex; }

    public boolean isSubmitted() { return submitted; }

    public void setSubmitted(boolean submitted) { this.submitted = submitted; }

    public int getNumCorrect() { return numCorrect; }

    public void setNumCorrect(int numCorrect) { this.numCorrect = numCorrect; }

    public int getNumResponse() { return numResponse; }

    public void setNumResponse(int numResponse) { this.numResponse = numResponse; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
