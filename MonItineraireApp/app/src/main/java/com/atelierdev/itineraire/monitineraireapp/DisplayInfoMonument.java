package com.atelierdev.itineraire.monitineraireapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Elodie on 04/12/2017.
 */

public class DisplayInfoMonument extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_monument_info);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String monument = intent.getStringExtra(MainActivity.EXTRA_MONUMENT);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.resultatInformation);
        textView.setText(monument);
    }
}
