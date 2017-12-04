package com.atelierdev.itineraire.monitineraireapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayPathAtoB extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_path_ato_b);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String pointA = intent.getStringExtra(MainActivity.EXTRA_POINTA);
        String pointB = intent.getStringExtra(MainActivity.EXTRA_POINTB);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.resultatChemin);
        textView.setText(pointA + pointB);

    }
}
