package com.example.firebasetest.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebasetest.Activities.Beta.UploadGallery;
import com.example.firebasetest.Activities.Classes.Question;
import com.example.firebasetest.Activities.Classes.Response;
import com.example.firebasetest.Activities.Classes.SH;
import com.example.firebasetest.Activities.Classes.User;
import com.example.firebasetest.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class SQview extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;

    private static final int GALLERY_INTENT = 2;
    ProgressDialog mProgressDialog;
    private StorageReference storage;


    Button saveBtn;
    Button uploadBtn;
    EditText replyET;
    ImageView imageView;
    TextView questionTV;
    TextView titleTV;
    TextView noteTV;

    String index;
    String cSHid;
    String qId;
    String qIndex;
    String userId;
    String qType;
    String cUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqview);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();
        database = FirebaseDatabase.getInstance();

        index = getIntent().getExtras().getString("CurrentIndex");
        cSHid = getIntent().getExtras().getString("CurrentSHid");
        qIndex = getIntent().getExtras().getString("CurrentQIndex");
        qType = getIntent().getExtras().getString("CurrentQType");
        qId = getIntent().getExtras().getString("CurrentQid");
        cUri = "";

        saveBtn = findViewById(R.id.saveBtn);
        uploadBtn = findViewById(R.id.uploadBtn);
        replyET = findViewById(R.id.replyET);
        imageView = findViewById(R.id.imageView);
        questionTV = findViewById(R.id.questionTV);
        titleTV = findViewById(R.id.titleTV);
        noteTV = findViewById(R.id.noteTV);

        mProgressDialog = new ProgressDialog(this);
        storage = FirebaseStorage.getInstance().getReference();

        updateViewQ();
        updateViewR();
        menuBarSetUp();

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (qType.equals("PHOTO")) {
                    if (!cUri.isEmpty()) {
                        saveReply(qType.equals("PHOTO"));

                    } else {
                        showMessage("Upload a Photo Before Saving");
                    }
                } else {
                    if (!replyET.getText().toString().isEmpty()) {
                        saveReply(qType.equals("PHOTO"));
                    } else {
                        showMessage("Enter all fields");
                    }
                }
            }
        });

    }



    private void saveReply(final boolean isImage) {
        myRef = database.getReference("SH").child(cSHid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH checkingSH = dataSnapshot.getValue(SH.class);

                Response cResponse = checkingSH.findReplierResponse(Integer.parseInt(qIndex), userId);

                if (cResponse != null) {
                    int responseIndex = checkingSH.findIndexOfResponse(cResponse.getId());

                    if(isImage){
                        cResponse.setReply(cUri);

                    }else{
                        cResponse.setReply(replyET.getText().toString());
                    }

                    cResponse.setImage(isImage);
                    database.getReference("SH").child(cSHid).child("responses").child(""+responseIndex).setValue(cResponse);
                    showMessage("Save Successful, Changed Reply");

                } else {
                    Response r;
                    String lastIndex = "" + checkingSH.responses.size();

                    if (isImage) {
                        r = new Response(cUri, "000", userId, isImage, qId);
                    } else {
                        r = new Response(replyET.getText().toString(), "000", userId, isImage, qId);
                    }
                    checkingSH.responses.add(r);
                    database.getReference("SH").child(cSHid).child("responses").setValue(checkingSH.responses);
                    String id = database.getReference("SH").child(cSHid).child("responses").child(lastIndex).child("id").push().getKey();
                    database.getReference("SH").child(cSHid).child("responses").child(lastIndex).child("id").setValue(id);

                    showMessage("Saved Successful, First Reply");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void updateViewQ() {
        myRef = database.getReference("SH").child(cSHid).child("questions").child(qIndex);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Question q = dataSnapshot.getValue(Question.class);
                questionTV.setText(q.getDescription());
                if (q.getReplyType().equals("TEXT")) {
                    uploadBtn.setVisibility(View.GONE);
                }else{
                    replyET.setVisibility(View.GONE);
                }

                titleTV.setText(q.getTitle());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void updateViewR() {
        myRef = database.getReference("SH").child(cSHid);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SH checkingSH = dataSnapshot.getValue(SH.class);
                Response cResponse = checkingSH.findReplierResponse(Integer.parseInt(qIndex), userId);

                if (cResponse!= null) {
                    if(qType.equals("TEXT")){
                        if (cResponse.getReply().isEmpty()) {
                            replyET.setHint("Enter Your Answer Here");
                        } else {
                            replyET.setHint(cResponse.getReply());
                        }
                        imageView.setVisibility(View.GONE);
                    }else{
                        //PHOTO
                        if (qType.equals("PHOTO")&& !cResponse.getQuestionId().isEmpty()) {
                            Glide.with(imageView.getContext()).load(cResponse.getReply()).into(imageView);
                        }
                        replyET.setVisibility(View.GONE);
                    }

                    if (!(cResponse.getNote().isEmpty())) {
                        noteTV.setText(cResponse.getNote());
                    } else {
                        noteTV.setVisibility(View.GONE);
                    }
                } else {
                    //No Previous Response
                    if(qType.equals("TEXT")){
                        replyET.setHint("Enter Your Answer Here");
                        imageView.setVisibility(View.GONE);
                    }else{
                        replyET.setVisibility(View.GONE);
                    }
                    noteTV.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), SQdash.class);
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

            Intent SSH = new Intent(getApplicationContext(), com.example.firebasetest.Activities.SSHdash.class);

            startActivity(SSH);

        } else if (id == R.id.nav_manage_sh) {

            this.startActivity(new Intent(getApplicationContext(), SHdash.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));


        } else if (id == R.id.nav_new_sh) {

            this.startActivity(new Intent(getApplicationContext(), SHenter.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));


        } else if (id == R.id.nav_signout) {

            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
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
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mProgressDialog.setMessage("Uploading");
            mProgressDialog.show();

            Uri uri = data.getData();

            cUri = uri.toString();

            //final StorageReference filepath = storage.child("Photos").child(uri.getLastPathSegment());
            final StorageReference filepath = storage.child("Photos").child(userId).child(qId);

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // create post Object
                            Glide.with(imageView.getContext()).load(cUri).into(imageView);
                            //FirebaseDatabase.getInstance().getReference("beta").child("imageTest").setValue(uri.toString());
                            Toast.makeText(SQview.this, "Upload Done",Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // something goes wrong uploading picture


                        }
                    });


                }
            });
        }
    }

}