package com.example.a13000.testappsensor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private GradientBackgroundPainter gradientBackgroundPainter;
    private TextView signUp, title, alreadyText, forgetPass;
    private Button signIn;
    private EditText email, password;
    private ViewGroup activity_main;
    private CheckBox rememberMe;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing you in ... \n\n ............");
        progressDialog.setCancelable(false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseRef.keepSynced(true);

        checkFirstRun();

        View backgroundImage = findViewById(R.id.activity_main);
        signUp = (TextView) findViewById(R.id.signUp);
        signIn = (Button) findViewById(R.id.signIn);
        title = (TextView) findViewById(R.id.titleText);
        email = (EditText) findViewById(R.id.email);
        alreadyText = (TextView) findViewById(R.id.alreadyText);
        activity_main = (ViewGroup) findViewById(R.id.activity_main);
        password = (EditText) findViewById(R.id.password);
        forgetPass = (TextView) findViewById(R.id.forgotPass);
        rememberMe = (CheckBox) findViewById(R.id.rememberMe);

        final int[] drawables = new int[3];
        drawables[0] = R.drawable.gradient_1;
        drawables[1] = R.drawable.gradient_2;
        drawables[2] = R.drawable.gradient_3;

        signIn.setVisibility(View.INVISIBLE);
        signUp.setVisibility(View.INVISIBLE);
        email.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);
        alreadyText.setVisibility(View.INVISIBLE);
        forgetPass.setVisibility(View.INVISIBLE);
        rememberMe.setVisibility(View.INVISIBLE);

        gradientBackgroundPainter = new GradientBackgroundPainter(backgroundImage, drawables);
        gradientBackgroundPainter.start();

        // Setting custom font ki text vannum ...
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(), "fonts/Airways_PERSONAL_USE_ONLY.ttf");
        signIn.setTypeface(custom_font);
        signUp.setTypeface(custom_font);
        title.setTypeface(custom_font1);
        password.setTypeface(custom_font);
        alreadyText.setTypeface(custom_font);
        email.setTypeface(custom_font);
        forgetPass.setTypeface(custom_font);
        rememberMe.setTypeface(custom_font);

        // For animation during enter
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                Fade fade = new Fade();
                fade.setDuration(5000);
                TransitionManager.beginDelayedTransition(activity_main, fade);
                toggleView(title, signUp, signIn, email, password, alreadyText, rememberMe, forgetPass);
            }
        }, 100);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Signup.class));
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailVal = email.getText().toString().trim();
                String passwordVal = password.getText().toString().trim();
                if (!TextUtils.isEmpty(emailVal) && !TextUtils.isEmpty(passwordVal)) {
                    checkLogin(emailVal, passwordVal);
                }else if (TextUtils.isEmpty(emailVal)){
                    email.setError("You need to enter your e-mail here");
                }else if(TextUtils.isEmpty(passwordVal)){
                    password.setError("You need a password to sign, obviously!");
                }
            }
        });

    }

    private void checkLogin(String emailVal, String passwordVal) {
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(emailVal, passwordVal).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userExistOrNot();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Sorry! We cannot log you in. Please recheck you email and password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void userExistOrNot() {
        final String UID = firebaseAuth.getCurrentUser().getUid();
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(UID)){
                    progressDialog.dismiss();
                    Intent in = new Intent(MainActivity.this,FeedPage.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Sorry, You need to setup your account.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Begin safe ..
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gradientBackgroundPainter.stop();
    }

    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            String title = "Welcome to Complaint Board.\n";
            String message = "App under development for final year project.\n\n\n-By Milan Pandey";

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    private void toggleView(View... views) {
        for (View current : views) {
            if (current.getVisibility() == View.INVISIBLE) {
                current.setVisibility(View.VISIBLE);
            }
        }
    }
}
