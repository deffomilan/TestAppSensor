package com.example.a13000.testappsensor;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    private GradientBackgroundPainter gradientBackgroundPainter;
    private EditText name, email, regdno, password;
    private Button signUp;
    private TextView pressHere;
    private ViewGroup activity_signup;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting up your account!\n\nPlease be patience!!");

        // Casting everything ...

        View backgroundImage = findViewById(R.id.activity_signup);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        regdno = (EditText) findViewById(R.id.regdNo);
        password = (EditText) findViewById(R.id.password);
        signUp = (Button) findViewById(R.id.signUp);
        pressHere = (TextView) findViewById(R.id.pressHere);
        activity_signup = (ViewGroup) findViewById(R.id.activity_signup);

        // Setting everything invisible for first 100 milliseconds ...
        name.setVisibility(View.INVISIBLE);
        email.setVisibility(View.INVISIBLE);
        regdno.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);
        signUp.setVisibility(View.INVISIBLE);
        pressHere.setVisibility(View.INVISIBLE);

        // Setting custom font .ttf format ...

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/Airways_PERSONAL_USE_ONLY.ttf");
        name.setTypeface(custom_font);
        email.setTypeface(custom_font);
        regdno.setTypeface(custom_font);
        password.setTypeface(custom_font);
        signUp.setTypeface(custom_font);
        pressHere.setTypeface(custom_font);

        // For background animation ...

        final int[] drawables = new int[3];
        drawables[0] = R.drawable.gradient_1;
        drawables[1] = R.drawable.gradient_2;
        drawables[2] = R.drawable.gradient_3;

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
                TransitionManager.beginDelayedTransition(activity_signup, fade);
                toggleView(name, email, signUp, name, password, regdno, pressHere);
            }
        }, 100);

        // Button click handling ...

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameVal = name.getText().toString().trim();
                String emailVal = email.getText().toString().trim();
                String regdNoVal = regdno.getText().toString().trim();
                String passwordVal = password.getText().toString().trim();

                if (!TextUtils.isEmpty(nameVal) &&
                        !TextUtils.isEmpty(emailVal) &&
                        !TextUtils.isEmpty(regdNoVal) &&
                        !TextUtils.isEmpty(passwordVal)) {
                    // This method is down below ..
                    registrationBegins(nameVal, emailVal, regdNoVal, passwordVal);
                }

            }
        });
    }

    private void toggleView(View... views) {
        for (View current : views) {
            if (current.getVisibility() == View.INVISIBLE) {
                current.setVisibility(View.VISIBLE);
            }
        }
    }

    private void registrationBegins(final String nameVal, String emailVal, final String regdNoVal, String passwordVal) {
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(emailVal, passwordVal).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Ali ghumaundo process ... will explain personally ...
                    String uid = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference newDatabaseRef = databaseReference.child(uid);
                    newDatabaseRef.child("name").setValue(nameVal);
                    newDatabaseRef.child("regdno").setValue(regdNoVal);
                    newDatabaseRef.child("image").setValue("For simplicity nothing here now");

                    progressDialog.dismiss();
                    Intent in = new Intent(Signup.this, FeedPage.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gradientBackgroundPainter.stop();
    }
}
