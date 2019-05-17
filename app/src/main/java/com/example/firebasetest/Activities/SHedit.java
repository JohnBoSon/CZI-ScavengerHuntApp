package com.example.firebasetest.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasetest.Activities.Classes.Question;
import com.example.firebasetest.Activities.Classes.SH;
import com.example.firebasetest.Activities.Classes.User;
import com.example.firebasetest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SHedit extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser ;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private Button viewQBtn;
    private Button deleteBtn;
    private Button saveBtn;
    private Button resultsBtn;
    private TextView mDisplayDate;
    private TextView accessCodeTV;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private NavigationView navigationView;

    private EditText descET;
    private EditText titleET;

    private String ownerId;
    private String eDate;
    private String index;
    private String cSHid;

    int numFakes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shedit);

        numFakes = 10;

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        ownerId = currentUser.getUid();

        viewQBtn = findViewById(R.id.viewQBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        saveBtn = findViewById(R.id.saveBtn);
        resultsBtn = findViewById(R.id.resultsBtn);
        mDisplayDate = findViewById(R.id.dateTV);
        descET = findViewById(R.id.descET);
        titleET = findViewById(R.id.titleET);
        accessCodeTV = findViewById(R.id.accessCodeTV);


        index = getIntent().getExtras().getString("CurrentIndex");
        cSHid = getIntent().getExtras().getString("CurrentSHid");
        eDate = "";

        menuBarSetUp();
        updateView();

        viewQBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareBundleAndFinish(Qdash.class);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupRemove( Integer.parseInt(index));
                //generatefakes();
                //showMessage("done");

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!descET.getText().toString().isEmpty()){
                    database.getReference("SH").child(cSHid).child("description").setValue(descET.getText().toString());
                }

                if(!titleET.getText().toString().isEmpty()){
                    database.getReference("SH").child(cSHid).child("title").setValue(titleET.getText().toString());
                }

                if(!eDate.isEmpty()){
                    database.getReference("SH").child(cSHid).child("endDate").setValue(eDate);
                    database.getReference("SH").child(cSHid).child("formattedEndDate").setValue(mDisplayDate.getText().toString());
                }

                showMessage("Save Successful");

            }
        });

        resultsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prepareBundleAndFinish(Pdash.class);
            }
        });

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        SHedit.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Date newDate = new Date(year-1900,month-1,day);
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd,yyyy");
                String date = formatter.format(newDate);
                SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
                eDate = formatter2.format(newDate);

                mDisplayDate.setText(date);
            }
        };
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

    private void generatefakes(){
        myRef = database.getReference("SH").child(cSHid);;

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH sh = dataSnapshot.getValue(SH.class);
                //sh.generateGradedFakeData(300);
                sh.generateFakeData(numFakes);

                database.getReference("SH").child(cSHid).setValue(sh);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void prepareBundleAndFinish(Class nextView) {
        Intent intent = new Intent(getApplicationContext(), nextView);
        intent.putExtra("CurrentSHid", cSHid);
        intent.putExtra("CurrentIndex", index);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void updateView(){
        myRef = database.getReference("SH").child(cSHid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH currentSH = dataSnapshot.getValue(SH.class);

                if(currentSH.getDescription().isEmpty()) {
                    descET.setHint("Description is currently Empty");
                }else{
                    descET.setHint(currentSH.getDescription());
                }

                accessCodeTV.setText("Code: " + currentSH.getId());
                titleET.setHint(currentSH.getTitle());
                mDisplayDate.setText(currentSH.getFormattedEndDate());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void RemoveSH(final int index,final SH sh) {
        myRef = database.getReference("TList").child(ownerId);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList shList = (ArrayList<String>) dataSnapshot.getValue();


                for(int i = 0 ; i < sh.participants.size(); i ++){
                        RemoveSSH(sh, sh.participants.get(i).getId());
                }

                shList.remove(index);
                database.getReference("TList").child(ownerId).setValue(shList);
                database.getReference("SH").child(cSHid).removeValue();
                showMessage("Scavenger Hunt Deleted");
                Intent intent = new Intent(getApplicationContext(), SHdash.class);
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

    private void RemoveSSH(final SH sh,final String id) {
        myRef = database.getReference("SList").child(id).child("SHmap");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    final ArrayList shList = (ArrayList<String>) dataSnapshot.getValue();
                    for (int i = 0; i < shList.size(); i++) {
                        if (sh.getId().equals(shList.get(i))) {
                            shList.remove(i);
                            database.getReference("SList").child(id).child("SHmap").setValue(shList);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void setupRemove(final int index){
        myRef = database.getReference("SH").child(cSHid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final SH sh = dataSnapshot.getValue(SH.class);
                RemoveSH(index, sh);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), SHdash.class);
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

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
}
