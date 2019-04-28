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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.firebasetest.Activities.Classes.Question;
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

public class Qview extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    DatabaseReference myRef;
    private FirebaseDatabase database;


    private ToggleButton photoTB;
    private ToggleButton textTB;
    private Button saveBtn;
    private Button deleteBtn;
    private String ownerId;
    private EditText titleET;
    private EditText descET;
    private TextView qnumtitleTV;

    String SHid;
    String index;
    String qindex;

    String replyChosen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        ownerId = currentUser.getUid();
        database = FirebaseDatabase.getInstance();


        SHid = getIntent().getExtras().getString("CurrentSHid");
        index = getIntent().getExtras().getString("CurrentIndex");
        qindex = getIntent().getExtras().getString("CurrentQIndex");



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();

        photoTB = findViewById(R.id.photoTB);
        textTB = findViewById(R.id.textTB);
        titleET = findViewById(R.id.titleET);
        descET = findViewById(R.id.descET);
        saveBtn = findViewById(R.id.saveBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        qnumtitleTV = findViewById(R.id.qnumtitleTV);


        //showMessage("owner" + ownerId + ", index" + index + ", iQ:" +getIntent().getExtras().getString("CurrentQIndex"));
        myRef = database.getReference("SHList").child(ownerId).child(index).child("questions").child(getIntent().getExtras().getString("CurrentQIndex"));

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //for now change when listView works
                Question q = dataSnapshot.getValue(Question.class);

                if(q.getDescription().isEmpty()) {
                    descET.setHint("Enter Description");
                }else{
                    descET.setHint(q.getDescription());
                }

                if(q.getDescription().isEmpty()) {
                    titleET.setHint("Enter Title");
                }else{
                    titleET.setHint(q.getTitle());
                }

                if(q.getReplyType().equals("PHOTO")){
                    textTB.setTextOff("text");
                    textTB.setChecked(false);
                    photoTB.setChecked(true);
                }

                if(q.getReplyType().equals("TEXT")){
                    photoTB.setTextOff("photo");
                    photoTB.setChecked(false);
                    textTB.setChecked(true);
                }

                qnumtitleTV.setText("Question " + qindex);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        photoTB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textTB.setTextOff("text");
                textTB.setChecked(false);
                photoTB.setChecked(true);

            }
        });

        textTB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoTB.setTextOff("photo");
                photoTB.setChecked(false);
                textTB.setChecked(true);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            boolean newQ = getIntent().getExtras().getString("isNewQ").equals("TRUE");

            @Override
            public void onClick(View view) {

                if(textTB.isChecked()) {
                    replyChosen = "TEXT";
                }

                if(photoTB.isChecked()) {
                    replyChosen = "PHOTO";
                }

                if (newQ && (descET.getText().toString().isEmpty() || titleET.getText().toString().isEmpty() || replyChosen.isEmpty()))  {
                    showMessage("Complete all fields");
                }else if(newQ) {
                    addQList(descET.getText().toString(),titleET.getText().toString(),replyChosen);
                    showMessage("save successful 77777");
                }else{

                    if (!descET.getText().toString().isEmpty()) {

                        database.getReference("SHList").child(ownerId).child(index).child("questions").child(getIntent().getExtras().getString("CurrentQIndex")).child("description").setValue(descET.getText().toString());
                        database.getReference("SH").child(getIntent().getExtras().getString("CurrentSHid")).child("questions").child(getIntent().getExtras().getString("CurrentQIndex")).child("description").setValue(descET.getText().toString());
                    }

                    if (!titleET.getText().toString().isEmpty()) {
                        database.getReference("SHList").child(ownerId).child(index).child("questions").child(getIntent().getExtras().getString("CurrentQIndex")).child("title").setValue(titleET.getText().toString());
                        database.getReference("SH").child(getIntent().getExtras().getString("CurrentSHid")).child("questions").child(getIntent().getExtras().getString("CurrentQIndex")).child("title").setValue(titleET.getText().toString());
                    }

                    if (!replyChosen.isEmpty()) {
                        database.getReference("SHList").child(ownerId).child(index).child("questions").child(getIntent().getExtras().getString("CurrentQIndex")).child("replyType").setValue(replyChosen);
                        database.getReference("SH").child(getIntent().getExtras().getString("CurrentSHid")).child("questions").child(getIntent().getExtras().getString("CurrentQIndex")).child("replyType").setValue(replyChosen);
                    }
                    showMessage("save successful");

                }

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeQFromSHList(Integer.parseInt(qindex));

            }
        });

    }

    private void addQList(final String desc, final String title, final String replyType) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("SH").child(getIntent().getExtras().getString("CurrentSHid")).child("questions");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    final ArrayList qList = (ArrayList<Question>) dataSnapshot.getValue();
                    String id = database.getReference("SH").child(getIntent().getExtras().getString("CurrentSHid")).child("questions").child(getIntent().getExtras().getString("CurrentQIndex")).child("id").push().getKey();

                    Question newQ = new Question( desc, id, title, replyType);

                    qList.add(newQ);

                    database = FirebaseDatabase.getInstance();
                    database.getReference("SH").child(getIntent().getExtras().getString("CurrentSHid")).child("questions").setValue(qList);
                    database.getReference("SHList").child(ownerId).child(getIntent().getExtras().getString("CurrentIndex")).child("questions").setValue(qList);

                    Intent intent = new Intent(getApplicationContext(), Qdash.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    //startActivity(intent);
                    //finish();

                }else{

                    final ArrayList qList = new ArrayList();
                    String id = database.getReference("SH").child(getIntent().getExtras().getString("CurrentSHid")).child("questions").child("0").child("id").push().getKey();

                    Question newQ = new Question( desc, id, title, replyType);
                    qList.add(newQ);

                    database = FirebaseDatabase.getInstance();
                    database.getReference("SH").child(getIntent().getExtras().getString("CurrentSHid")).child("questions").setValue(qList);
                    database.getReference("SHList").child(ownerId).child(getIntent().getExtras().getString("CurrentIndex")).child("questions").setValue(qList);

                    Intent intent = new Intent(getApplicationContext(), Qdash.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    //startActivity(intent);

                    //finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void removeQFromSHList(final int index) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("SHList").child(ownerId).child(getIntent().getExtras().getString("CurrentIndex")).child("questions");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList qList = (ArrayList<Question>) dataSnapshot.getValue();
                qList.remove(index);
                database.getReference("SHList").child(ownerId).child(getIntent().getExtras().getString("CurrentIndex")).child("questions").setValue(qList);
                database.getReference("SH").child(getIntent().getExtras().getString("CurrentSHid")).child("questions").setValue(qList);
                showMessage("Question Deleted");
                Intent intent = new Intent(getApplicationContext(), Qdash.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                intent.putExtra("CurrentSHid", SHid);
                intent.putExtra("CurrentIndex", getIntent().getExtras().getString("CurrentIndex"));

                startActivity(intent);
                //finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Qdash.class);
        //intent.putExtra("CurrentQIndex", getIntent().getExtras().getString("CurrentQIndex"));
        intent.putExtra("CurrentSHid", getIntent().getExtras().getString("CurrentSHid"));
        intent.putExtra("CurrentIndex", getIntent().getExtras().getString("CurrentIndex"));

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        //finish();
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
}
