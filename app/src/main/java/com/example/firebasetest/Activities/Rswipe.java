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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebasetest.Activities.Beta.Swipe;
import com.example.firebasetest.Activities.Classes.Response;
import com.example.firebasetest.Activities.Classes.SH;
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
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

public class Rswipe extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Button counterB;
    SwipeFlingAdapterView flingContainer;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseListAdapter adapter;

    String cSHid;
    String ownerId;
    String rqId;
    String index;

    Boolean accepted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rswipe);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ownerId = currentUser.getUid();

        index = getIntent().getExtras().getString("CurrentIndex");
        cSHid = getIntent().getExtras().getString("CurrentSHid");
        rqId = getIntent().getExtras().getString("CurrentRQId");;

        counterB = findViewById(R.id.swipeBack);

        makeSwipeList();
        flingContainer = findViewById(R.id.frame);


        flingContainer = findViewById(R.id.frame);
        counterB = findViewById(R.id.CounterBtn);


        Query query = FirebaseDatabase.getInstance().getReference("swipe").child(cSHid);
        FirebaseListOptions<Response> options = new FirebaseListOptions.Builder<Response>()
                .setLayout(R.layout.item)
                .setLifecycleOwner(Rswipe.this)
                .setQuery(query, Response.class)
                .build();
        adapter = new FirebaseListAdapter<Response>(options) {
            @Override
            protected void populateView(View v, Response model, int position) {
                TextView title = (TextView) v.findViewById(R.id.textTV);
                ImageView image = (ImageView) v.findViewById(R.id.imageV);
                if(model.isImage()){
                    Glide.with(image.getContext()).load(model.getReply()).into(image);
                    title.setVisibility(View.GONE);

                    //showMessage(model.getReply());

                    //showMessage("found");
                }else {
                    title.setText(model.getReply());
                    image.setVisibility(View.GONE);
                    //showMessage("y tho");

                }
            }
        };

        flingContainer.setAdapter(adapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                myRef = database.getReference("swipe").child(cSHid);
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<ArrayList<Response>> t = new GenericTypeIndicator<ArrayList<Response>>() {};
                        ArrayList<Response> rList = dataSnapshot.getValue(t);
                        rList.remove(0);
                        database.getReference("swipe").child(cSHid).setValue(rList);
                        counterB.setText("Responses Left: " + rList.size());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Toast.makeText(Rswipe.this, "Left" + accepted + ((Response)dataObject).getReply(), Toast.LENGTH_SHORT).show();
                saveGrade(((Response)dataObject), false);

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(getApplicationContext(), "Right" + accepted, Toast.LENGTH_SHORT).show();
                saveGrade(((Response)dataObject), true);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        counterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(Rswipe.this, "Clicked", Toast.LENGTH_SHORT);

            }
        });

    }

    private void makeSwipeList(){
        myRef = database.getReference("SH").child(cSHid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH sh = dataSnapshot.getValue(SH.class);
                database.getReference("swipe").child(cSHid).setValue(sh.generateSwipeList(rqId));

                counterB.setText("Responses Left: " + sh.generateSwipeList(rqId).size());


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void saveGrade(final Response r, final boolean passed){
        myRef = database.getReference("SH").child(cSHid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH sh = dataSnapshot.getValue(SH.class);

                r.setGraded(true);
                r.setPass(passed);

                for(int i = 0; i < sh.responses.size(); i++){
                    if(sh.responses.get(i).getId().equals(r.getId())){
                        sh.responses.set(i,r);
                    }
                }

                for(int i = 0; i < sh.participants.size(); i++){
                    if(sh.participants.get(i).getId().equals(r.getReplierId())){

                        sh.participants.get(i).setNumCorrect(sh.findNumCorrectResponse(sh.participants.get(i).getId()));
                    }
                }
                database.getReference("SH").child(cSHid).setValue(sh);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), RQdash.class);
        intent.putExtra("CurrentSHid", cSHid);
        intent.putExtra("CurrentIndex", index);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view old_item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            Intent intent = new Intent(getApplicationContext(), SSHdash.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();


        } else if (id == R.id.nav_manage_sh) {

            Intent intent = new Intent(getApplicationContext(), SHdash.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();


        }else if (id == R.id.nav_new_sh) {

            Intent intent = new Intent(getApplicationContext(), SHenter.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();


        }else if (id == R.id.nav_signout) {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
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
