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

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser ;
    private DatabaseReference myRef;
    private FirebaseDatabase database;

    private ToggleButton photoTB;
    private ToggleButton textTB;
    private Button saveBtn;
    private Button deleteBtn;
    private String ownerId;
    private EditText titleET;
    private EditText descET;
    private TextView qnumtitleTV;

    private String cSHid;
    private String index;
    private String qIndex;
    private String isNewQ;
    private String replyChosen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qview);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        ownerId = currentUser.getUid();
        database = FirebaseDatabase.getInstance();


        cSHid = getIntent().getExtras().getString("CurrentSHid");
        index = getIntent().getExtras().getString("CurrentIndex");
        qIndex = getIntent().getExtras().getString("CurrentQIndex");
        isNewQ = getIntent().getExtras().getString("isNewQ");


        photoTB = findViewById(R.id.photoTB);
        textTB = findViewById(R.id.textTB);
        titleET = findViewById(R.id.titleET);
        descET = findViewById(R.id.descET);
        saveBtn = findViewById(R.id.saveBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        qnumtitleTV = findViewById(R.id.qnumtitleTV);

        menuBarSetUp();
        updateView();

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
                if(isNewQ.equals("TRUE")) {
                    photoTB.setTextOff("photo");
                    photoTB.setChecked(false);
                    textTB.setChecked(true);
                }
            }
            });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            boolean newQ = isNewQ.equals("TRUE");

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
                    showMessage("Save Successful");
                }else{

                    if (!descET.getText().toString().isEmpty()) {
                        database.getReference("SH").child(cSHid).child("questions").child(qIndex).child("description").setValue(descET.getText().toString());
                    }

                    if (!titleET.getText().toString().isEmpty()) {
                        database.getReference("SH").child(cSHid).child("questions").child(qIndex).child("title").setValue(titleET.getText().toString());
                    }

                    if (!replyChosen.isEmpty()&&isNewQ.equals("TRUE")) {
                        database.getReference("SH").child(cSHid).child("questions").child(qIndex).child("replyType").setValue(replyChosen);
                    }
                    showMessage("save successful");

                }

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeQFromSHList(Integer.parseInt(qIndex));

            }
        });

    }

    private void updateView(){
        myRef = database.getReference("SH").child(cSHid).child("questions").child(qIndex);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Question q = dataSnapshot.getValue(Question.class);

                    if (q.getDescription().isEmpty()) {
                        descET.setHint("Enter Description");
                    } else {
                        descET.setHint(q.getDescription());
                    }

                    if (q.getDescription().isEmpty()) {
                        titleET.setHint("Enter Title");
                    } else {
                        titleET.setHint(q.getTitle());
                    }

                    if (q.getReplyType().equals("PHOTO")) {
                        textTB.setTextOff("text");
                        textTB.setChecked(false);
                        photoTB.setChecked(true);
                        textTB.setEnabled(false);

                    }

                    if (q.getReplyType().equals("TEXT")) {
                        photoTB.setTextOff("photo");
                        photoTB.setChecked(false);
                        textTB.setChecked(true);
                        photoTB.setEnabled(false);

                    }

                    qnumtitleTV.setText("Question " + Integer.parseInt(qIndex) + 1);

                }else{
                    descET.setHint("Enter Description");
                    titleET.setHint("Enter Title");
                    photoTB.setChecked(false);
                    textTB.setChecked(false);
                    qnumtitleTV.setText("Question " + Integer.parseInt(qIndex) + 1);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void addQList(final String desc, final String title, final String replyType) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("SH").child(cSHid).child("questions");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    final ArrayList qList = (ArrayList<Question>) dataSnapshot.getValue();
                    String id = database.getReference("SH").child(cSHid).child("questions").child(qIndex).child("id").push().getKey();

                    Question newQ = new Question( desc, id, title, replyType);

                    qList.add(newQ);

                    database = FirebaseDatabase.getInstance();
                    database.getReference("SH").child(cSHid).child("questions").setValue(qList);

                }else{

                    final ArrayList qList = new ArrayList();
                    String id = database.getReference("SH").child(cSHid).child("questions").child("0").child("id").push().getKey();

                    Question newQ = new Question( desc, id, title, replyType);
                    qList.add(newQ);

                    database = FirebaseDatabase.getInstance();
                    database.getReference("SH").child(cSHid).child("questions").setValue(qList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        updateNavHeader();
    }

    private void removeQFromSHList(final int cqIndex) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("SH").child(cSHid);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH sh = dataSnapshot.getValue(SH.class);

                sh.removeRespQues(sh.questions.get(cqIndex));

                database.getReference("SH").child(cSHid).setValue(sh);

                showMessage("Question Deleted");
                Intent intent = new Intent(getApplicationContext(), Qdash.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("CurrentSHid", cSHid);
                intent.putExtra("CurrentIndex", index);
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
        Intent intent = new Intent(getApplicationContext(), Qdash.class);
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

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
}
