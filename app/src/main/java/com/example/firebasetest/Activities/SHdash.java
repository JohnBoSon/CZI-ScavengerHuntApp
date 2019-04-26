package com.example.firebasetest.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasetest.Activities.Beta.Swipe;
import com.example.firebasetest.Activities.Classes.SH;
import com.example.firebasetest.Adapters.SHAdapter;
import com.example.firebasetest.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SHdash extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private Button addBtn;

    ListView mListView;
    String ownerId;

    //test
    ListView lv;
    FirebaseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shdash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        ownerId = currentUser.getUid();

        addBtn = findViewById(R.id.addBtn);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("SHList").child(ownerId);

        //test listView from indian god like tutorial

        lv = (ListView) findViewById(R.id.listView);
        Query query = FirebaseDatabase.getInstance().getReference().child("SHList").child(ownerId);

        FirebaseListOptions<SH> options = new FirebaseListOptions.Builder<SH>()
                    .setLayout(R.layout.sh_adapter_view_layout)
                    .setLifecycleOwner(SHdash.this)
                    .setQuery(query,SH.class)
                    .build();

        adapter = new FirebaseListAdapter<SH>(options) {
            @Override
            protected void populateView(View v, SH model, int position) {
                SH cSH = (SH) model;
                String shTitle = cSH.getTitle();
                String onGoingStatus;

                if(cSH.checkOngoing()){
                    onGoingStatus = "OnGoing";
                }else{
                    onGoingStatus = "Ended";
                }

                String numPeople =  cSH.particitants.size() + " Participants";

                TextView title = (TextView) v.findViewById(R.id.textView1);
                TextView participants = (TextView) v.findViewById(R.id.textView2);
                TextView ongoingStatus = (TextView) v.findViewById(R.id.textView3);

                title.setText(shTitle);
                participants.setText(onGoingStatus);
                ongoingStatus.setText(numPeople);

                Animation animation = null;
                animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);
                v.startAnimation(animation);

            }
        };

        lv.setAdapter(adapter);





        //navi
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SHmake.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                //addSHList(ownerId);

                finish();

            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {

                //shList.get(i).getTitle();
                Toast.makeText(SHdash.this, "Clicked "+ index, Toast.LENGTH_SHORT).show();

            }
        });

        //updateListView(ownerId);



    }



    public void showList(DataSnapshot dataSnap){
            mListView = findViewById(R.id.listView);
            ArrayList<SH> shList = (ArrayList<SH>) dataSnap.getValue();
            SHAdapter adapter = new SHAdapter(getApplicationContext(), R.layout.sh_adapter_view_layout, shList);
            mListView.setAdapter(adapter);


    }

    public void updateListView(String ownerId){
        myRef = database.getReference("SHList").child(ownerId);
        database = FirebaseDatabase.getInstance();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList shList = (ArrayList<String>) dataSnapshot.getValue();

                    //populateSHList(shList);
                }else{

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void addSHList(final String ownerId) {

        myRef = database.getReference("SHList").child(ownerId);
        database = FirebaseDatabase.getInstance();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList shList = (ArrayList<String>) dataSnapshot.getValue();
                    showMessage(shList.size()+ "");
                    //database = FirebaseDatabase.getInstance();
                    //database.getReference("SHList").child(ownerId).setValue(shList);
                }else{

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    public void populateSHList(ArrayList<String> shList){
        myRef = database.getReference("SH");
        database = FirebaseDatabase.getInstance();

            myRef = database.getReference("SH");

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ArrayList shList = (ArrayList<String>) dataSnapshot.getValue();

                        ArrayList finalSHList = new ArrayList<SH>();

                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            for(int i = 0 ; i < shList.size();i++){
                                SH tempSH = userSnapshot.child(shList.get(i).toString()).getValue(SH.class);

                                finalSHList.add(tempSH);


                            }
                        }

                        SHAdapter adapter = new SHAdapter(getApplicationContext() , R.layout.sh_adapter_view_layout, finalSHList);
                        mListView.setAdapter(adapter);


                    } /*else {
                        ArrayList finalSHList = new ArrayList<SH>();
                        SHAdapter adapter = new SHAdapter(getApplicationContext() , R.layout.sh_adapter_view_layout, finalSHList);
                        mListView.setAdapter(adapter);
                    }*/

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

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
}
