package com.atelierdev.itineraire.monitineraireapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class DisplayStepsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_steps);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String trajet = intent.getStringExtra(MapsActivity.EXTRA_TRAJET);
        TextView textView = findViewById(R.id.resultatInformation);
        textView.setText(trajet);

    }
}
