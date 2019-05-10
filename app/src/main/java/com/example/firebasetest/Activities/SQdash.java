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
            database = FirebaseDatabase.getInstance();

            index = getIntent().getExtras().getString("CurrentIndex");
            cSHid = getIntent().getExtras().getString("CurrentSHid");


            showMessage(index + " ," + cSHid);


            lv = (ListView) findViewById(R.id.listView);
            submitBtn = findViewById(R.id.submitBtn);

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


        private void checkEndDateThenSubmitt(){
            myRef = database.getReference("SH").child(cSHid);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        SH checkingSH = dataSnapshot.getValue(SH.class);
                        if(checkingSH.checkOngoing()){
                            for(int n = 0; n < checkingSH.participants.size(); n++){
                                if(checkingSH.participants.get(n).getId().equals(userId)&& checkingSH.findNumResponse(userId) > 0){
                                    checkingSH.participants.get(n).setNumResponse(checkingSH.findNumResponse(userId));
                                    checkingSH.participants.get(n).setNumCorrect(checkingSH.findNumCorrectResponse(userId));
                                    checkingSH.participants.get(n).setSubmitted(true);
                                    database.getReference("SH").child(cSHid).setValue(checkingSH);

                                    showMessage("Successfully Submitted");
                                }
                            }
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

                    title.setText("Question " + position + 1);

                    Animation animation = null;
                    animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);
                    v.startAnimation(animation);
                }
            };

            lv.setAdapter(adapter);
        }

    private void prepareBundleAndFinish(final String qIndex){

        myRef = database.getReference("SH").child(cSHid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH sh = dataSnapshot.getValue(SH.class);

                ArrayList<String> rList = new ArrayList<>();



                //database.getReference("SList").child(userId).child("ResponseMap").setValue(rList);


                Intent intent = new Intent(getApplicationContext(), SQview.class);
                intent.putExtra("CurrentQIndex", qIndex);
                intent.putExtra("CurrentSHid", cSHid);
                intent.putExtra("CurrentIndex", index);
                intent.putExtra("CurrentQid", sh.questions.get(Integer.parseInt(qIndex)).getId());
                intent.putExtra("CurrentQType", sh.questions.get(Integer.parseInt(qIndex)).getReplyType());
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
        Intent intent = new Intent(getApplicationContext(), SSHdash.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
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