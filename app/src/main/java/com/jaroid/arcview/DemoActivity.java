package com.jaroid.arcview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        ArcView arcView = findViewById(R.id.arcView);
        arcView.setProgress(30);
        arcView.setRounded(true);
        arcView.setStrokeWidth(20, 30);

    }
}