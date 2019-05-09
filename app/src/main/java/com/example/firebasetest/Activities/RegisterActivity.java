
package com.example.firebasetest.Activities;

        import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ProgressBar;
        import android.widget.Toast;
        import android.widget.ToggleButton;

        import com.example.firebasetest.Activities.Classes.SH;
        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.AuthResult;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.auth.UserProfileChangeRequest;


        import com.example.firebasetest.R;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {


    private EditText userEmail,userPassword,userPAssword2,userName;
    private ProgressBar loadingProgress;
    private Button regBtn;
    private FirebaseAuth mAuth;

    private ToggleButton teacherTB;
    private ToggleButton studentTB;

    private String accType = "EMPTY";
    private FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String ownerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //ini views
        userEmail = findViewById(R.id.regMail);
        userPassword = findViewById(R.id.regPassword);
        userPAssword2 = findViewById(R.id.regPassword2);
        userName = findViewById(R.id.regName);
        loadingProgress = findViewById(R.id.regProgressBar);
        regBtn = findViewById(R.id.regBtn);
        loadingProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();





        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                regBtn.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPAssword2.getText().toString();
                final String name = userName.getText().toString();

                if( email.isEmpty() || name.isEmpty() || password.isEmpty()  || !password.equals(password2) || accType.equals("EMPTY")) {
                    // something goes wrong : all fields must be filled
                    // we need to display an error message
                    showMessage("Please Verify all fields") ;
                    regBtn.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);
                }
                else {
                    // everything is ok and all fields are filled now we can start creating user account
                    // CreateUserAccount method will try to create the user if the email is valid
                    CreateUserAccount(email,name,password);

                }

            }
        });

        teacherTB = findViewById(R.id.teacherTB);
        studentTB = findViewById(R.id.studentTB);

        teacherTB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentTB.setTextOff("Student");
                studentTB.setChecked(false);
                accType = "TEACHER";
            }
        });

        studentTB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teacherTB.setTextOff("Teacher");
                teacherTB.setChecked(false);
                accType = "STUDENT";
            }
        });





    }

    private void CreateUserAccount(String email, final String name, String password) {


        // this method create user account with specific email and password

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // user account created successfully
                            showMessage("Account created");
                            updateUserInfo( name ,mAuth.getCurrentUser());

                        }
                        else
                        {
                            // account creation failed
                            showMessage("account creation failed" + task.getException().getMessage());
                            regBtn.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);

                        }
                    }
                });
    }



    private void updateUI() {

        //Write user type onto database before going to next activity
        currentUser = mAuth.getCurrentUser();
        ownerId = currentUser.getUid();
        database = FirebaseDatabase.getInstance();
        database.getReference("User").child(ownerId).child("accountType").setValue(accType);
        database.getReference("User").child(ownerId).child("currentQ").setValue("");
        database.getReference("User").child(ownerId).child("currentR").setValue("");
        database.getReference("User").child(ownerId).child("currentSH").setValue("");

        Intent homeActivity = new Intent(getApplicationContext(),SSHdash.class);
        startActivity(homeActivity);
        finish();

    }

    // simple method to show toast message
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        //intent.putExtra("CurrentSHid", getIntent().getExtras().getString("CurrentSHid"));
        //intent.putExtra("CurrentIndex", getIntent().getExtras().getString("CurrentIndex"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    // update user photo and name
    private void updateUserInfo(final String name, final FirebaseUser currentUser) {

        // first we need to upload user photo to firebase storage and get url

        UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();


        currentUser.updateProfile(profleUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            // user info updated successfully
                            showMessage("Register Complete");
                            updateUI();
                        }

                    }
                });
    }
}