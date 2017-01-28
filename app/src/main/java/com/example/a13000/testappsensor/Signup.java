package com.example.a13000.testappsensor;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
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

public class Signup extends AppCompatActivity {

    private GradientBackgroundPainter gradientBackgroundPainter;
    private EditText name,email,regdno,password;
    private Button signUp;
    private TextView pressHere;
    private ViewGroup activity_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Casting everything ...

        View backgroundImage = findViewById(R.id.activity_signup);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        regdno = (EditText) findViewById(R.id.regdNo);
        password = (EditText) findViewById(R.id.password);
        signUp= (Button) findViewById(R.id.signUp);
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

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Quicksand-Regular.otf");
        Typeface custom_font1 = Typeface.createFromAsset(getAssets(),  "fonts/Airways_PERSONAL_USE_ONLY.ttf");
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
                TransitionManager.beginDelayedTransition(activity_signup,fade);
                toggleView(name,email,signUp,name,password,regdno,pressHere);
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
