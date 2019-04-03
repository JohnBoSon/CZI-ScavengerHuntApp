package com.example.firebasetest.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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

public class ManageSHActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

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

        //id is create when added to database
        String id = "";
        String ownerId = currentUser.getUid();
        String title = "Free SH";
        String description = "Do this SH for fun";

        //create a new key id for SH
        myRef = database.getReference("SH").push();

        SH shDemo = new SH(myRef.getKey(), ownerId, title, description);

        // add sh data to firebase database
        myRef.setValue(shDemo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("SH Added successfully");

            }
        });


        myRef = database.getReference("SH").child(shDemo.getId());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH sh = dataSnapshot.getValue(SH.class);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        TextView sh_title = findViewById(R.id.title_sh);

        sh_title.setText(shDemo.getTitle());
        //sh_title.setText("banana");


    }
}

