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

import com.example.firebasetest.Activities.Beta.Swipe;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Pdash extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ListView lv;
    FirebaseListAdapter adapter;
    String index;
    String cSHid;
    String ownerId;

    private Button statsBtn;
    private Button qGradeBtn;
    private ProgressBar bar;

    private ImageView turtle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdash);

        qGradeBtn = (Button) findViewById(R.id.qGradeBtn);
        statsBtn = (Button) findViewById(R.id.statsBtn);
        bar = findViewById(R.id.progress_bar);
        turtle = findViewById(R.id.turtle);

        bar.setVisibility(View.GONE);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        ownerId = currentUser.getUid();
        database = FirebaseDatabase.getInstance();

        index = getIntent().getExtras().getString("CurrentIndex");
        cSHid = getIntent().getExtras().getString("CurrentSHid");


        menuBarSetUp();

        lv = (ListView) findViewById(R.id.listView);
        Query query = FirebaseDatabase.getInstance().getReference().child("SH").child(cSHid).child("participants");//.orderByChild("submitted").equalTo(true);


        FirebaseListOptions<User> options = new FirebaseListOptions.Builder<User>()
                .setLayout(R.layout.sh_adapter_view_layout)
                .setLifecycleOwner(Pdash.this)
                .setQuery(query,User.class)
                .build();

        adapter = new FirebaseListAdapter<User>(options) {
            @Override
            protected void populateView(View v, User model, int position) {
                if(position > 2){
                    turtle.setVisibility(View.GONE);
                }
                makeView(model,v);
            }
        };

        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pIndex, long l) {
                //Toast.makeText(Pdash.this, "Clicked "+ pIndex, Toast.LENGTH_SHORT).show();

                createResponseList(((User) adapter.getItem(pIndex)).getId());
            }
        });

        statsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getApplicationContext(), Rcloud.class);
                Intent intent = new Intent(getApplicationContext(), Rcolumn.class);

                intent.putExtra("CurrentSHid", cSHid);
                intent.putExtra("CurrentIndex", index);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });

        qGradeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //generatefakes();
                Intent intent = new Intent(getApplicationContext(), RQdash.class);
                //Intent intent = new Intent(getApplicationContext(), Rswipe.class);

                intent.putExtra("CurrentSHid", cSHid);
                intent.putExtra("CurrentIndex", index);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        });
    }

    private void generatefakes(){
        myRef = database.getReference("SH").child(cSHid);;

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH sh = dataSnapshot.getValue(SH.class);
                //sh.generateGradedFakeData(300);
                sh.generateFakeData(10);

                database.getReference("SH").child(cSHid).setValue(sh);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void prepareBundleAndFinish( final String pIndex) {
        Intent intent = new Intent(getApplicationContext(), Rdash.class);
        intent.putExtra("CurrentSHid", cSHid);
        intent.putExtra("CurrentPIndex", pIndex);
        intent.putExtra("CurrentIndex", index);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void createResponseList(final String userId){
        myRef = database.getReference("SH").child(cSHid);;

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH sh = dataSnapshot.getValue(SH.class);

                int pIndex = sh.findPindex(userId);
                if(sh.participants.get(pIndex).isSubmitted()){
                    ArrayList<Response> rList = sh.generateResponseList(sh.participants.get(pIndex).getId());
                    database.getReference().child("CurrentResponses").child(cSHid).child("responses").setValue(rList);
                    prepareBundleAndFinish(""+pIndex);
                }else{
                    showMessage("No Submissions Available");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void makeView(final User model, final View v){
        myRef = database.getReference("SH").child(cSHid);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    SH sh = dataSnapshot.getValue(SH.class);
                    TextView name = (TextView) v.findViewById(R.id.textView1);
                    TextView grade = (TextView) v.findViewById(R.id.textView2);
                    TextView responseSubmitted = (TextView) v.findViewById(R.id.textView3);

                    name.setText(model.getName());

                    if (sh.responses.size() > 0) {
                        if (model.isSubmitted() && sh.isUserGraded(model.getId())) {
                            grade.setText("Score: " + model.getNumCorrect() + " Out of " + sh.getMaxScore());
                        } else if (model.isSubmitted()) {
                            grade.setText("Ungraded ");
                        } else {
                            grade.setText("Nothing Submitted");
                        }

                        responseSubmitted.setText(model.getNumResponse() + " Responses");

                    } else {
                        grade.setText("");
                        responseSubmitted.setText("No Responses");
                    }

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), SHedit.class);
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

    @SuppressWarnings("StatementWithEmptyBody")
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
