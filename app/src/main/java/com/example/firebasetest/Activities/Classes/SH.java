package com.example.firebasetest.Activities.Classes;
import java.util.ArrayList;

public class SH {

    private boolean onGoing;
    private String id;
    private String ownerId;
    private String title;
    private String description;


    ArrayList<Response> responses =new ArrayList<>();
    ArrayList<Response> questions =new ArrayList<>();


    public SH() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public SH(String id, String ownerId, String title, String description) {
        this.onGoing = false;
        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
    }


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

}
