package com.example.a13000.testappsensor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Signup extends AppCompatActivity {

    private GradientBackgroundPainter gradientBackgroundPainter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        View backgroundImage = findViewById(R.id.activity_signup);

        final int[] drawables = new int[3];
        drawables[0] = R.drawable.gradient_1;
        drawables[1] = R.drawable.gradient_2;
        drawables[2] = R.drawable.gradient_3;

        gradientBackgroundPainter = new GradientBackgroundPainter(backgroundImage, drawables);
        gradientBackgroundPainter.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gradientBackgroundPainter.stop();
    }
}
