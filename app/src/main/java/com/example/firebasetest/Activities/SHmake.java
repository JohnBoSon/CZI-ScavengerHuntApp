package com.example.firebasetest.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.example.firebasetest.Activities.Classes.SH;
import com.example.firebasetest.R;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class SHmake extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth mAuth;
    FirebaseUser currentUser ;
    DatabaseReference myRef;


    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Button createBtn;
    private FirebaseDatabase database;
    private EditText TitleET;

    private String endDate = "EMPTY";
    private String shTitle;


    boolean found;
    private ArrayList shList;

    int shListSize = -9;
    boolean stopLoop = false;

    private Bundle bundle;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shmake);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        bundle =  new Bundle();


        mDisplayDate = findViewById(R.id.dateTV);
        createBtn = findViewById(R.id.createBtn);
        TitleET = findViewById(R.id.TitleET);

        //test

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();



        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shTitle = TitleET.getText().toString();

                if( endDate.equals("EMPTY") || shTitle.isEmpty()) {
                    // something goes wrong : all fields must be filled
                    // we need to display an error message
                    showMessage("Please Verify all fields") ;

                }
                else {
                    // everything is ok and all fields are filled now we can start creating user account
                    // CreateUserAccount method will try to create the user if the email is valid

                    createSH(endDate,shTitle);


                    //bundle.putInt("SHIndex", shListSize);

                    Intent intent = new Intent(getApplicationContext(), SHedit.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    //intent.putExtra("SHIndex", shListSize);

                    //showMessage(""+shListSize);
                    startActivity(intent);

                    //startActivity(new Intent(getApplicationContext(), SHedit.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    finish();
                }


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
                        SHmake.this,
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

                SimpleDateFormat formatterED = new SimpleDateFormat("yyyy-MM-dd");
                endDate = formatterED.format(newDate);

                mDisplayDate.setText(date);
            }
        };
    }

    private void addSHList(final String SHid, final String ownerId) {

        myRef = database.getReference("SHList").child(ownerId);


        database = FirebaseDatabase.getInstance();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                        shList = (ArrayList<String>) dataSnapshot.getValue();
                        shList.add(SHid);
                        database = FirebaseDatabase.getInstance();
                        database.getReference("SHList").child(ownerId).setValue(shList);


                        if(shList.size()!=0){
                            //bundle.putInt("SHIndex", shList.size());
                            //shListSize = shList.size();
                            //showMessage(shList.size()+" - "+shListSize);




                        }

                    //showMessage("size exist" + shList.size() );
                    //showMessage("exists" + shList.size() + " " +shList.get(0)) ;
                    found = true;
                    stopLoop = true;

                }else{
                    //showMessage("does not exists") ;

                    //showMessage("size dnt" + shList.size() );
                    shList = new ArrayList<String>();
                    shList.add(SHid);
                    myRef = database.getReference("SHList").child(ownerId);
                    myRef.setValue(shList);

                    if(shList.size()!=0){
                        //bundle.putInt("SHIndex", shList.size());
                        //shListSize = shList.size();
                        //showMessage(shList.size()+" - "+shListSize);


                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }


    private void createSH(String date, String title) {
        currentUser = mAuth.getCurrentUser();
        String ownerId = currentUser.getUid();
        String id = database.getReference("SH").push().getKey();
        showMessage("create new SH ") ;

        SH newSH = new SH(id,ownerId, title, "", date);
        database.getReference("SH").child(id).setValue(newSH);

        addSHList(id,ownerId);
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
}
