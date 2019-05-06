package com.example.firebasetest.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.firebasetest.Activities.Classes.Response;
import com.example.firebasetest.Activities.Classes.SH;
import com.example.firebasetest.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageSHActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    TextView sh_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_sh);

        //connection to authentication
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        //Creation and connection to database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("SH");

        sh_title = findViewById(R.id.title_sh);
/*

        //id is create when added to database
        String id = "";
        String ownerId = currentUser.getUid();
        String title = "Not Free SH";
        String description = "Do fun do ya";

        //test
        SH testingScav1 = new SH("THIS", "TEST ", "TITLE IS WORKING", "SCAVENGER", "2029-05-15");

        SH testingScav2 = new SH("THIS", "TEST ", "TITLE IS amazing WORKING", "SCAVENGER", "2008-05-15");

//            public Response(String reply, String id, String replierId, boolean isImage, String questionId) {

        Response t1 = new Response("replye", "ides", "ddasd",true,"dsada");
        //testingScav1.responses.add(t1);

        ArrayList<SH> shList = new ArrayList<>();
        shList.add(testingScav1);
        shList.add(testingScav2);
        shList.add(testingScav1);
        shList.add(testingScav2);
        shList.add(testingScav1);

        //create a new key id for SH
        myRef = database.getReference("listTest").push();

        SH shDemo = new SH(myRef.getKey(), ownerId, title, description, "2020-04-19");

        // add sh data to firebase database
        myRef.setValue(shList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("SH Added successfully");

            }
        });

/*
        myRef = database.getReference("SH").child(shDemo.getId());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH sh = dataSnapshot.getValue(SH.class);
                sh_title.setText(sh.getEndDate());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

*/

        //sh_title.setText(shDemo.getTitle());
        //sh_title.setText("banana");


    }
}

