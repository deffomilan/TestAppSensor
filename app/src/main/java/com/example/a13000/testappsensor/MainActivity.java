package com.example.a13000.testappsensor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private GradientBackgroundPainter gradientBackgroundPainter;
    private TextView signInNotAccount,title;
    private Button signIn;
    private EditText email,password;
    private ViewGroup activity_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        checkFirstRun();

        View backgroundImage = findViewById(R.id.activity_main);
        signInNotAccount = (TextView) findViewById(R.id.signUpNoAcc);
        signIn = (Button) findViewById(R.id.signIn);
        title = (TextView) findViewById(R.id.titleText);
        email = (EditText) findViewById(R.id.email);
        activity_main = (ViewGroup) findViewById(R.id.activity_main);
        password = (EditText) findViewById(R.id.password);

        final int[] drawables = new int[3];
        drawables[0] = R.drawable.gradient_1;
        drawables[1] = R.drawable.gradient_2;
        drawables[2] = R.drawable.gradient_3;

        signIn.setVisibility(View.INVISIBLE);
        signInNotAccount.setVisibility(View.INVISIBLE);
        email.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);

        gradientBackgroundPainter = new GradientBackgroundPainter(backgroundImage, drawables);
        gradientBackgroundPainter.start();

        // For animation during enter
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                Fade fade = new Fade();
                fade.setDuration(5000);
                TransitionManager.beginDelayedTransition(activity_main,fade);
                toggleView(title,signInNotAccount,signIn,email,password);
            }
        }, 100);

        signInNotAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Signup.class));
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this, TryAct.class);
                startActivity(in);
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
            String message = "App under development for final year project.\n\n\n-By Milan Pander";

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

    private void toggleView(View... views){
        for(View current : views){
            if(current.getVisibility() == View.INVISIBLE){
                current.setVisibility(View.VISIBLE);
            }
        }
    }
}
