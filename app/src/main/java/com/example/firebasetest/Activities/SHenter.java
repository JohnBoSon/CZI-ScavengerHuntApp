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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasetest.Activities.Classes.Response;
import com.example.firebasetest.Activities.Classes.SH;
import com.example.firebasetest.Activities.Classes.User;
import com.example.firebasetest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SHenter extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private Button enterBtn;
private EditText accessCode;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shenter);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        enterBtn = findViewById(R.id.enterBtn);
        userId = currentUser.getUid();
        accessCode = findViewById(R.id.accessET);

        menuBarSetUp();

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(accessCode.getText().toString().isEmpty()){
                    showMessage("Enter an Access Code");
                }else{
                   //findSH(accessCode.getText().toString());
                    findSH("-Le9-LKmVWnpVnAZzczX");
                }
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
        // Handle action bar old_item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }
        */
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

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
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

    private void findSH(final String eSHid){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("SH").child(eSHid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {


                    SH sh = dataSnapshot.getValue(SH.class);


                    if(sh.checkOngoing()){
                        boolean exist = false;

                        for(int i = 0 ; i < sh.participants.size() ; i++){
                            if(sh.participants.get(i).getName().equals(currentUser.getDisplayName())){
                                exist = true;
                                showMessage("exists");
                            }
                        }

                        if(!exist){
                            User u = new User(currentUser.getUid(), currentUser.getDisplayName());
                            sh.participants.add(u);
                            database.getReference("SH").child(eSHid).setValue(sh);
                            addSHList(userId,sh);

                        }else{
                            showMessage("You are Already in this Scavenger Hunt");
                        }

                    }else{
                        showMessage("This Scavenger Hunt has Ended");
                    }
                } else {
                    showMessage("Access Code is Not Valid");

                }

            }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

    private void prepareBundleAndFinish(Class nextView, String index, String SHid) {
        Intent intent = new Intent(getApplicationContext(), nextView);
        intent.putExtra("CurrentSHid", SHid);
        intent.putExtra("CurrentIndex", index);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }


    private void addSHList( final String userId, final SH jSH) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("SSHList").child(userId);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    GenericTypeIndicator<ArrayList<SH>> t = new GenericTypeIndicator<ArrayList<SH>>() {};
                    final ArrayList shList = (ArrayList<SH>) dataSnapshot.getValue(t);
                    boolean exist = false;
                    int eindex = 0;
                    for(int i = 0 ; i < shList.size() ; i++){
                        if(jSH.getId().equals(((SH)shList.get(i)).getId())){
                            exist = true;
                            eindex = i;
                        }
                    }
                    if(!exist){
                        shList.add(jSH);
                        database = FirebaseDatabase.getInstance();
                        database.getReference("SSHList").child(userId).setValue(shList);

                        prepareBundleAndFinish(SQdash.class, "" + shList.size(), jSH.getId());

                    }else{
                        //showMessage("You are Already in this Scavenger Hunt");
                        //prepareBundleAndFinish(SQdash.class, "" + eindex, jSH.getId());
                    }
                }else{
                    final ArrayList shList = new ArrayList<SH>();
                    shList.add(jSH);
                    myRef = database.getReference("SSHList").child(userId);
                    myRef.setValue(shList);

                    prepareBundleAndFinish(SQdash.class, "" + 0, jSH.getId());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }
}
