package com.example.firebasetest.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.firebasetest.Activities.Classes.Question;
import com.example.firebasetest.Activities.Classes.Response;
import com.example.firebasetest.Activities.Classes.SH;
import com.example.firebasetest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Rview extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    DatabaseReference myRef;
    private FirebaseDatabase database;
    private String ownerId;

    TextView questionTV;
    TextView titleTV;
    TextView replierNameTV;
    TextView TextReplyTv;
    EditText noteET;
    ToggleButton acceptTB;
    ToggleButton declineTB;
    ImageView imageView;
    Button saveBtn;


    String index;
    String cSHid;
    String pIndex;
    String rIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rview);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ownerId = currentUser.getUid();

        index = getIntent().getExtras().getString("CurrentIndex");
        cSHid = getIntent().getExtras().getString("CurrentSHid");
        pIndex = getIntent().getExtras().getString("CurrentPIndex");
        rIndex = getIntent().getExtras().getString("CurrentRIndex");

        questionTV = findViewById(R.id.questionTV);
        titleTV = findViewById(R.id.titleTV);
        replierNameTV = findViewById(R.id.replierNameTV);
        TextReplyTv = findViewById(R.id.TextReplyTv);
        noteET = findViewById(R.id.noteET);
        acceptTB = findViewById(R.id.acceptTB);
        declineTB = findViewById(R.id.declineTB);
        imageView = findViewById(R.id.imageView);
        saveBtn =findViewById(R.id.saveBtn);

        setUpView();
        menuBarSetUp();

        acceptTB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                declineTB.setChecked(false);
                acceptTB.setChecked(true);
            }
        });

        declineTB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptTB.setChecked(false);
                declineTB.setChecked(true);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEdit();

            }
        });

    }

    private void createResponseListandBack(){
        myRef = database.getReference("SH").child(cSHid);;

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH sh = dataSnapshot.getValue(SH.class);

                ArrayList<Response> rList = sh.generateResponseList(sh.participants.get(Integer.parseInt(pIndex)).getId());
                database.getReference().child("CurrentResponses").child(cSHid).child("responses").setValue(rList);
                Intent intent = new Intent(getApplicationContext(), Rdash.class);
                intent.putExtra("CurrentSHid", cSHid);
                intent.putExtra("CurrentPIndex", pIndex);
                intent.putExtra("CurrentIndex", index);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void saveEdit(){

        if(!acceptTB.isChecked()&& !declineTB.isChecked()){
            showMessage("Accept or Decline Reply");
        }else{
            database.getReference("SH").child(cSHid).child("responses").child(rIndex).child("graded").setValue(true);
            if(acceptTB.isChecked()){
                database.getReference("SH").child(cSHid).child("responses").child(rIndex).child("pass").setValue(true);
            }else{
                database.getReference("SH").child(cSHid).child("responses").child(rIndex).child("pass").setValue(false);
            }

            if(!noteET.getText().toString().isEmpty()){
                database.getReference("SH").child(cSHid).child("responses").child(rIndex).child("note").setValue(noteET.getText().toString());
            }
            showMessage("Save Successful");
        }

        myRef = database.getReference("SH").child(cSHid);;

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH sh = dataSnapshot.getValue(SH.class);
                sh.participants.get(Integer.parseInt(pIndex)).setNumCorrect(sh.findNumCorrectResponse(sh.participants.get(Integer.parseInt(pIndex)).getId()));

                ArrayList<Response> rList = sh.generateResponseList(sh.participants.get(Integer.parseInt(pIndex)).getId());
                database.getReference().child("SH").child(cSHid).setValue(sh);
                database.getReference().child("CurrentResponses").child(cSHid).child("responses").setValue(rList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void setUpView(){
        myRef = database.getReference("SH").child(cSHid);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH sh = dataSnapshot.getValue(SH.class);
                int qIndex = sh.getQuestionPosition(sh.responses.get(Integer.parseInt(rIndex)).getQuestionId());

                questionTV.setText("Question " + qIndex);
                titleTV.setText(sh.questions.get(qIndex).getTitle());
                replierNameTV.setText(sh.participants.get(Integer.parseInt(pIndex)).getName());

                //if exists
                if(!sh.responses.get(Integer.parseInt(rIndex)).getNote().isEmpty()){
                    noteET.setHint(sh.responses.get(Integer.parseInt(rIndex)).getNote());
                }else{
                    noteET.setHint("Enter Note");
                }

                String rType = sh.questions.get(qIndex).getReplyType();

                if(rType.equals("PHOTO")){
                    Glide.with(imageView.getContext()).load(sh.responses.get(Integer.parseInt(rIndex)).getReply()).into(imageView);
                    TextReplyTv.setVisibility(View.GONE);


                }else if(rType.equals("TEXT")){
                    TextReplyTv.setText(sh.responses.get(Integer.parseInt(rIndex)).getReply());
                    imageView.setVisibility(View.GONE);
                }

                if(sh.responses.get(Integer.parseInt(rIndex)).isGraded()){
                   if(sh.responses.get(Integer.parseInt(rIndex)).isPass()){
                       acceptTB.setChecked(true);
                       declineTB.setChecked(false);
                   }else{
                       acceptTB.setChecked(false);
                       declineTB.setChecked(true);
                   }
                }else{
                    acceptTB.setChecked(false);
                    declineTB.setChecked(false);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    @Override
    public void onBackPressed() {
        createResponseListandBack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view old_item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            Intent SSH = new Intent(getApplicationContext(), com.example.firebasetest.Activities.SSHdash.class);

            startActivity(SSH);

        } else if (id == R.id.nav_manage_sh) {

            this.startActivity(new Intent(getApplicationContext(), SHdash.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

        }else if (id == R.id.nav_new_sh) {

            this.startActivity(new Intent(getApplicationContext(), SHenter.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

        }else if (id == R.id.nav_signout) {

            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(loginActivity);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        TextView navUsername = headerView.findViewById(R.id.nav_username);
        TextView navUserMail = headerView.findViewById(R.id.nav_user_mail);

        navUserMail.setText(currentUser.getEmail());
        navUsername.setText(currentUser.getDisplayName());
    }

    private void menuBarSetUp(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateNavHeader();
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
}