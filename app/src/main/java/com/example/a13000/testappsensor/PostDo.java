package com.example.a13000.testappsensor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class PostDo extends AppCompatActivity {

    private ImageButton imageButton;
    private Button postButton;
    private EditText titleEditText, descEditText;
    private static final int GALLERY_INTENT = 1;
    private Uri resultUri = null;

    private ProgressDialog progressDialog;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUserCurrent;
    private DatabaseReference newDataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_do);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUserCurrent = firebaseAuth.getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("ComplaintHead");
        newDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUserCurrent.getUid());

        databaseRef.keepSynced(true);
        newDataRef.keepSynced(true);

        imageButton = (ImageButton) findViewById(R.id.imageButton);
        postButton = (Button) findViewById(R.id.postButton);
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        descEditText = (EditText) findViewById(R.id.descEditText);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(" Posting ...\n\n Please wait... !!!");
        progressDialog.setCancelable(false);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_INTENT);
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postingProcess();
            }
        });
    }

    private void postingProcess() {
        final String titleEntered = titleEditText.getText().toString().trim();
        final String descEntered = descEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(titleEntered) && !TextUtils.isEmpty(descEntered) && resultUri != null) {
            progressDialog.show();
            StorageReference filePath = storageRef.child("Images").child(resultUri.getLastPathSegment());
            filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadURL = taskSnapshot.getDownloadUrl();
                    final DatabaseReference dataRefImg = databaseRef.push();

                    newDataRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dataRefImg.child("title").setValue(titleEntered);
                            dataRefImg.child("desc").setValue(descEntered);
                            dataRefImg.child("image").setValue(downloadURL.toString());
                            dataRefImg.child("UID").setValue(firebaseUserCurrent.getUid());
                            dataRefImg.child("username")
                                    .setValue(dataSnapshot.child("name").getValue())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent in = new Intent(PostDo.this, FeedPage.class);
                                                in.putExtra("flag", 0);
                                                startActivity(in);
                                                finish();
                                            }
                                        }
                                    })
                            ;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(PostDo.this, "Sorry your request cannot be carried out!", Toast.LENGTH_LONG).show();
                        }
                    });
                    progressDialog.dismiss();
                }
            });
        } else {
            if (TextUtils.isEmpty(titleEntered)) {
                titleEditText.setError("You must enter a title");
            } else if (TextUtils.isEmpty(descEntered)) {
                descEditText.setError("Please enter small description so that we can know about the problem clearly and rectify it sooner.");
            } else if (resultUri == null) {
                Toast.makeText(this, "You need to select an image too.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(16, 9)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                imageButton.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setTitle("Compliant");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
