package com.example.firebasetest.Activities.Beta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.firebasetest.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadGallery extends AppCompatActivity {

    private Button selectImage;
    private StorageReference storage;
    private ImageView imageView;

    private static final int GALLERY_INTENT = 2;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_gallery);

        storage = FirebaseStorage.getInstance().getReference();

        selectImage = (Button)findViewById(R.id.bselectimage);
        mProgressDialog = new ProgressDialog(this);

        imageView = findViewById(R.id.imageView2);

        selectImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mProgressDialog.setMessage("Uploading");
            mProgressDialog.show();

            Uri uri = data.getData();

            //final StorageReference filepath = storage.child("Photos").child(uri.getLastPathSegment());
            final StorageReference filepath = storage.child("Photos").child("BATMAN");

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // create post Object
                            Glide.with(imageView.getContext()).load(uri.toString()).into(imageView);
                            FirebaseDatabase.getInstance().getReference("beta").child("imageTest").setValue(uri.toString());

                            Toast.makeText(UploadGallery.this, "Upload Done",Toast.LENGTH_LONG).show();
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

/*
            filepath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storage.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UploadGallery.this, "Upload Done",Toast.LENGTH_LONG).show();
                        mProgressDialog.dismiss();

                        //Uri downloadUri = task.getResult();

                        //Picasso.with(UploadGallery.this).load(downloadUri).fit().centerCrop().into(imageView);

                        String downloadUri = getIntent().getExtras().getString("userPhoto");
                        Glide.with(this).load(downloadUri).into(imageView);


                    } else {
                        Toast.makeText(UploadGallery.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

            });
*/
