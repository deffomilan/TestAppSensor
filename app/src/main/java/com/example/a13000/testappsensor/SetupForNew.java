package com.example.a13000.testappsensor;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupForNew extends AppCompatActivity {

    private GradientBackgroundPainter gradientBackgroundPainter;
    private EditText name, regdno, password;
    private Button signUp;
    private TextView pressHere;
    private ImageView profPic;
    private final int galleryCodeRequest = 1;
    private ViewGroup activity_setup_for_new;
    private Uri resultUri = null;

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_for_new);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePicture");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting up your account!\n\nPlease be patience!!");
        progressDialog.setCancelable(false);

        name = (EditText) findViewById(R.id.name);
        regdno = (EditText) findViewById(R.id.regdNo);
        password = (EditText) findViewById(R.id.password);
        signUp = (Button) findViewById(R.id.signUp);
        profPic = (ImageView) findViewById(R.id.profPic);
        pressHere = (TextView) findViewById(R.id.pressHere);
        activity_setup_for_new = (ViewGroup) findViewById(R.id.activity_setup_for_new);

        // Setting everything invisible for first 100 milliseconds ...
        name.setVisibility(View.INVISIBLE);
        regdno.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);
        signUp.setVisibility(View.INVISIBLE);
        pressHere.setVisibility(View.INVISIBLE);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/Airways_PERSONAL_USE_ONLY.ttf");
        name.setTypeface(custom_font);
        regdno.setTypeface(custom_font);
        password.setTypeface(custom_font);
        signUp.setTypeface(custom_font);
        pressHere.setTypeface(custom_font);

        View backgroundImage = findViewById(R.id.activity_setup_for_new);
        final int[] drawables = new int[3];
        drawables[0] = R.drawable.gradient_1;
        drawables[1] = R.drawable.gradient_2;
        drawables[2] = R.drawable.gradient_3;

        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.milkshake);

        gradientBackgroundPainter = new GradientBackgroundPainter(backgroundImage, drawables);
        gradientBackgroundPainter.start();

        // For fade type of animation ... 100 millisecond ko kuro ...
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                Fade fade = new Fade();
                fade.setDuration(5000);
                TransitionManager.beginDelayedTransition(activity_setup_for_new, fade);
                toggleView(name, signUp, name, password, regdno, pressHere);
            }
        }, 100);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameVal = name.getText().toString().trim();
                String regdNoVal = regdno.getText().toString().trim();
                String passwordVal = password.getText().toString().trim();

                if (!TextUtils.isEmpty(nameVal) &&
                        !TextUtils.isEmpty(regdNoVal) &&
                        !TextUtils.isEmpty(passwordVal) &&
                        resultUri != null) {
                    // This method is down below ..
                    insertingValues(nameVal, regdNoVal, passwordVal);
                } else if (TextUtils.isEmpty(nameVal)) {
                    signUp.startAnimation(animation);
                    name.setError("This field cannot be empty");
                } else if (TextUtils.isEmpty(regdNoVal)) {
                    signUp.startAnimation(animation);
                    regdno.setError("This field cannot be empty");
                } else if (TextUtils.isEmpty(passwordVal)) {
                    signUp.startAnimation(animation);
                    password.setError("Password is must");
                } else if (resultUri == null) {
                    signUp.startAnimation(animation);
                    Toast.makeText(SetupForNew.this, "Please select a profile picture too.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        profPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, galleryCodeRequest);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gradientBackgroundPainter.stop();
    }

    private void toggleView(View... views) {
        for (View current : views) {
            if (current.getVisibility() == View.INVISIBLE) {
                current.setVisibility(View.VISIBLE);
            }
        }
    }

    private void insertingValues(final String nameVal, final String regdNoVal, String passwordVal) {
        progressDialog.show();

        StorageReference filePath = storageReference.child(resultUri.getLastPathSegment());
        filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String uid = firebaseAuth.getCurrentUser().getUid();
                Uri downloadURL = taskSnapshot.getDownloadUrl();
                final DatabaseReference newDatabaseRef = databaseReference.child(uid);
                newDatabaseRef.child("name").setValue(nameVal);
                newDatabaseRef.child("regdno").setValue(regdNoVal);
                newDatabaseRef.child("image").setValue(downloadURL.toString());
            }
        });
        progressDialog.dismiss();
        Intent in = new Intent(SetupForNew.this, FeedPage.class);
        startActivity(in);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == galleryCodeRequest && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                profPic.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}



