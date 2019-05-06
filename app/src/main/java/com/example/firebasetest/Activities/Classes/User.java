package com.example.firebasetest.Activities.Classes;

import java.util.ArrayList;

public class User {
    private String id;
    private String name;
    public ArrayList<Response> responses =new ArrayList<>();


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

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
