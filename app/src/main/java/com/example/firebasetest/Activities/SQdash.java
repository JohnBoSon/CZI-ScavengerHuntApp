package com.example.firebasetest.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebasetest.Activities.Classes.Question;
import com.example.firebasetest.Activities.Classes.Response;
import com.example.firebasetest.Activities.Classes.SH;
import com.example.firebasetest.Activities.Classes.User;
import com.example.firebasetest.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SQdash extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

        FirebaseAuth mAuth;
        FirebaseUser currentUser ;
        FirebaseDatabase database;
        DatabaseReference myRef;

        String userId;

        ListView lv;
        FirebaseListAdapter adapter;
        Button submitBtn;

        String index;
        String cSHid;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sqdash);

            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            userId = currentUser.getUid();

            index = getIntent().getExtras().getString("CurrentIndex");
            cSHid = getIntent().getExtras().getString("CurrentSHid");

            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("SH").child(userId);

            lv = (ListView) findViewById(R.id.listView);
            submitBtn = findViewById(R.id.submitBtn);

            updateQuestions();
            setupListView();
            menuBarSetUp();


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int qIndex, long l) {
                    prepareBundleAndFinish(qIndex + "");

                }
            });

            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkEndDateThenSubmitt();
                }
            });

        }

        private void updateQuestions(){
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("SH").child(cSHid);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    SH sh = dataSnapshot.getValue(SH.class);

                    ArrayList<Question> qList = sh.questions;
                    database.getReference("SSHList").child(userId).child(index).child("questions").setValue(qList);
                    compareQnR();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

        private void compareQnR(){
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("SSHList").child(userId).child(index);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    SH sh = dataSnapshot.getValue(SH.class);

                    int pIndex = -1;
                    for(int i = 0 ; i< sh.participants.size();i++){
                        if(sh.participants.get(i).getId().equals(userId)){
                            pIndex = i;
                        }
                    }

                    if(pIndex == -1){
                        //user not found in particpants
                    }else {

                        Response match = new Response("N/A", "N/A", "N/A", false, "N/A");
                        ArrayList<Response> nrList = new ArrayList<>();

                        for(int n = 0 ; n < sh.questions.size(); n++){
                            for(int i =0; i < sh.participants.get(pIndex).responses.size(); i++){
                                if(sh.participants.get(pIndex).responses.get(i).getQuestionId().equals(sh.questions.get(n).getId())){
                                    match = sh.participants.get(pIndex).responses.get(i);
                                }
                            }
                            nrList.add(match);
                            match = new Response("N/A", "N/A", "N/A", false, "N/A");
                        }
                        database.getReference("SSHList").child(userId).child(index).child("participants").child(""+pIndex).child("responses").setValue(nrList);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }



        private void checkEndDateThenSubmitt(){
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("SSHList").child(userId).child(index);

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        SH checkingSH = dataSnapshot.getValue(SH.class);
                        if(checkingSH.checkOngoing()){
                            submitSH();
                        }else{
                            showMessage("Deadline Has Passed");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }


        private void submitSH(){
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("SSHList").child(userId).child(index).child("participants");

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<ArrayList<User>> t = new GenericTypeIndicator<ArrayList<User>>() {};
                    ArrayList<User> pList = dataSnapshot.getValue(t);
                    int pIndex = -1;
                    for(int i = 0 ; i< pList.size();i++){
                        if(pList.get(i).getId().equals(userId)){
                            pIndex = i;
                        }
                    }

                    if(pIndex == -1){
                        //user not found in particpants
                    }else{
                        if (!pList.get(pIndex).responses.isEmpty()) {
                            ArrayList<Response> rList = pList.get(pIndex).responses;
                            ArrayList<Response> crList = new ArrayList<>();

                            for(int i = 0 ; i < rList.size(); i++){
                                if(!rList.get(i).getQuestionId().equals("N/A")){
                                    crList.add(rList.get(i));
                                }
                            }
                            sendResponseToTeacher(crList);
                        } else {
                            showMessage("There are no Responses to Send");
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });

        }

    private void sendResponseToTeacher(final ArrayList<Response> crList){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("SH").child(cSHid).child("participants");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                GenericTypeIndicator<ArrayList<User>> t = new GenericTypeIndicator<ArrayList<User>>() {};
                ArrayList<User> pList = dataSnapshot.getValue(t);
                int pIndex = -1;
                for(int i = 0 ; i< pList.size();i++){
                    if(pList.get(i).getId().equals(userId)){
                        pIndex = i;
                    }
                }

                if(pIndex == -1){
                    //user not found in particpants
                }else{
                    if (!pList.get(pIndex).responses.isEmpty()) {
                        ArrayList<Response> rList = pList.get(pIndex).responses;

                        boolean clone = false;
                        int cloneIndex = 0;

                        for(int i = 0 ; i < crList.size(); i++){
                            for(int j = 0 ; j < rList.size(); j++){
                                if(rList.get(j).getId().equals(crList.get(i).getId())){
                                    cloneIndex = j;
                                    clone = true;
                                }
                            }

                            if(clone){
                                rList.set(cloneIndex,crList.get(i));
                            }else{
                                rList.add(crList.get(i));
                            }
                            clone = false;
                        }

                        database.getReference("SH").child(cSHid).child("participants").child(""+pIndex).child("responses").setValue(rList);
                        showMessage("Responses Sent");
                    } else {
                        ArrayList<Response> rList = new ArrayList<>();

                        boolean clone = false;
                        int cloneIndex = 0;

                        for(int i = 0 ; i < crList.size(); i++){
                            for(int j = 0 ; j < rList.size(); j++){
                                if(rList.get(j).getId().equals(crList.get(i).getId())){
                                    cloneIndex = j;
                                    clone = true;
                                }
                            }

                            if(clone){
                                rList.set(cloneIndex,crList.get(i));
                            }else{
                                rList.add(crList.get(i));
                            }
                            clone = false;
                        }

                        database.getReference("SH").child(cSHid).child("participants").child(""+pIndex).child("responses").setValue(rList);
                        showMessage("Responses Sent");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }


        private void setupListView(){
            Query query = FirebaseDatabase.getInstance().getReference("SH").child(cSHid).child("questions");
            FirebaseListOptions<SH> options = new FirebaseListOptions.Builder<SH>()
                    .setLayout(R.layout.adapter_question_view)
                    .setLifecycleOwner(SQdash.this)
                    .setQuery(query,SH.class)
                    .build();

            adapter = new FirebaseListAdapter<SH>(options) {
                @Override
                protected void populateView(View v, SH model, int position) {
                    TextView title = (TextView) v.findViewById(R.id.textView1);

                    title.setText("Question " + position);

                    Animation animation = null;
                    animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);
                    v.startAnimation(animation);

                }
            };

            lv.setAdapter(adapter);
        }

    private void prepareBundleAndFinish(final String qIndex){


        myRef = database.getReference("SH").child(cSHid).child("questions").child(qIndex);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Question q = dataSnapshot.getValue(Question.class);
                Intent intent = new Intent(getApplicationContext(), SQview.class);
                intent.putExtra("CurrentQIndex", qIndex);
                intent.putExtra("CurrentSHid", cSHid);
                intent.putExtra("CurrentIndex", index);
                intent.putExtra("CurrentQid", q.getId());
                intent.putExtra("CurrentQType", q.getReplyType());
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

            Intent Swipe = new Intent(getApplicationContext(), com.example.firebasetest.Activities.Beta.Swipe.class);
            startActivity(Swipe);

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