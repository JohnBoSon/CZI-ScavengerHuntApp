package com.example.firebasetest.Activities;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebasetest.Activities.Classes.SH;
import com.example.firebasetest.Adapters.SHAdapter;
import com.example.firebasetest.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SHdash extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser ;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Button addBtn;

    private String ownerId;
    private ListView lv;
    private FirebaseListAdapter adapter;
    private ProgressBar bar;
    NavigationView navigationView;
    private ImageView turtle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shdash);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        ownerId = currentUser.getUid();
        database = FirebaseDatabase.getInstance();

        addBtn = findViewById(R.id.addBtn);
        bar = findViewById(R.id.progress_bar);
        turtle = findViewById(R.id.turtle);

        bar.setVisibility(View.GONE);



        lv = (ListView) findViewById(R.id.listView);

        menuBarSetUp();

        Query query = FirebaseDatabase.getInstance().getReference().child("TList").child(ownerId);
        FirebaseListOptions<String> options = new FirebaseListOptions.Builder<String>()
                    .setLayout(R.layout.sh_adapter_view_layout)
                    .setLifecycleOwner(SHdash.this)
                    .setQuery(query,String.class)
                    .build();

        adapter = new FirebaseListAdapter<String>(options) {
            @Override
            protected void populateView(View v, String id, int position) {
                if(position > 2){
                    turtle.setVisibility(View.GONE);
                }
                setUpView(id, v);

            }
        };

        lv.setAdapter(adapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SHmake.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                finish();
            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                //Toast.makeText(SHdash.this, "Clicked "+ index, Toast.LENGTH_SHORT).show();
                prepareBundleAndFinish(""+index);

            }
        });


    }

    private void menuBarSetUp() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        checkAccountType();
        updateNavHeader();
    }

    private void setUpView(String SHid, final View v){
        myRef = database.getReference("SH").child(SHid);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    SH cSH = dataSnapshot.getValue(SH.class);

                    String shTitle = cSH.getTitle();
                    String onGoingStatus;

                    if (cSH.checkOngoing()) {
                        onGoingStatus = "OnGoing";
                    } else {
                        onGoingStatus = "Ended";
                    }

                    String numPeople = cSH.participants.size() + " Participants";

                    TextView title = (TextView) v.findViewById(R.id.textView1);
                    TextView participants = (TextView) v.findViewById(R.id.textView2);
                    TextView ongoingStatus = (TextView) v.findViewById(R.id.textView3);

                    title.setText(shTitle);
                    participants.setText(onGoingStatus);
                    ongoingStatus.setText(numPeople);

                    Animation animation = null;
                    animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);
                    v.startAnimation(animation);
                    bar.setVisibility(View.GONE);
                }else{
                    bar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void checkAccountType() {
        myRef = database.getReference("User").child(ownerId).child("accountType");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String checking = dataSnapshot.getValue(String.class);
                if(checking.equals("TEACHER")){
                    Menu menuNav = navigationView.getMenu();
                    MenuItem nav_item = menuNav.findItem(R.id.nav_manage_sh);
                    nav_item.setEnabled(true);
                    nav_item.setVisible(true);
                }else{
                    Menu menuNav = navigationView.getMenu();
                    MenuItem nav_item = menuNav.findItem(R.id.nav_manage_sh);
                    nav_item.setEnabled(false);
                    nav_item.setVisible(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void prepareBundleAndFinish( final String index) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("TList").child(ownerId).child(index);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String sh = dataSnapshot.getValue(String.class);
                Intent intent = new Intent(getApplicationContext(), SHedit.class);
                intent.putExtra("CurrentSHid", sh);
                intent.putExtra("CurrentIndex", index);
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

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
}
