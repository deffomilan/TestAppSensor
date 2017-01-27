package com.example.a13000.testappsensor;

import android.annotation.SuppressLint;
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

public class Signup extends AppCompatActivity {

    private GradientBackgroundPainter gradientBackgroundPainter;
    private EditText name,email,regdno,password;
    private Button signUp;
    private ViewGroup activity_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        View backgroundImage = findViewById(R.id.activity_signup);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        regdno = (EditText) findViewById(R.id.regdNo);
        password = (EditText) findViewById(R.id.password);
        signUp= (Button) findViewById(R.id.signUp);
        activity_signup = (ViewGroup) findViewById(R.id.activity_signup);

        name.setVisibility(View.INVISIBLE);
        email.setVisibility(View.INVISIBLE);
        regdno.setVisibility(View.INVISIBLE);
        password.setVisibility(View.INVISIBLE);
        signUp.setVisibility(View.INVISIBLE);

        final int[] drawables = new int[3];
        drawables[0] = R.drawable.gradient_1;
        drawables[1] = R.drawable.gradient_2;
        drawables[2] = R.drawable.gradient_3;

        gradientBackgroundPainter = new GradientBackgroundPainter(backgroundImage, drawables);
        gradientBackgroundPainter.start();

        // For fade type of animation ...
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                Fade fade = new Fade();
                fade.setDuration(5000);
                TransitionManager.beginDelayedTransition(activity_signup,fade);
                toggleView(name,email,signUp,name,password,regdno);
            }
        }, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gradientBackgroundPainter.stop();
    }

    private void toggleView(View... views){
        for(View current : views){
            if(current.getVisibility() == View.INVISIBLE){
                current.setVisibility(View.VISIBLE);
            }
        }
    }

}
