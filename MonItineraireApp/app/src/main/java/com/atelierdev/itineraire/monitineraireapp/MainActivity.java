package com.atelierdev.itineraire.monitineraireapp;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;



public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_POINTA = "com.atelierdev.itineraire.monitineraireapp.pointA";
    public static final String EXTRA_POINTB = "com.atelierdev.itineraire.monitineraireapp.pointB";
    public static final String EXTRA_POINTSUPP = "com.atelierdev.itineraire.monitineraireapp.pointInt";
    public static final String EXTRA_MONUMENT = "com.atelierdev.itineraire.monitineraireapp.monument";
    public static final String EXTRA_USELOC = "com.atelierdev.itineraire.monitineraireapp.use_loc";

    //TODO Arnaud: pour utiliser la location
    // https://developer.android.com/training/location/retrieve-current.html




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DatabaseHandler.Initialize(getBaseContext());
    }


    /**
     * Called when the user press info button
     */
    public void displayInfo(View view){
        Intent intent = new Intent(this, DisplayInfoMonument.class);
        EditText editMonument = (EditText) findViewById(R.id.monument);

        String monument = editMonument.getText().toString();

        intent.putExtra(EXTRA_MONUMENT, monument);
        startActivity(intent);
    }
    /**
     * Called when the user press calcul button
     */
    public void calculTrajet(View view){
        Intent intent = new Intent(this, DisplayPathAtoB.class);
        EditText editTextA = (EditText) findViewById(R.id.pointA);
        EditText editTextB = (EditText) findViewById(R.id.pointB);

        String pointA = editTextA.getText().toString();
        String pointB = editTextB.getText().toString();

        intent.putExtra(EXTRA_POINTA, pointA);
        intent.putExtra(EXTRA_POINTB, pointB);
        startActivity(intent);
    }

    public void displayMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);

        EditText editTextA = (EditText) findViewById(R.id.pointA);
        EditText editTextB = (EditText) findViewById(R.id.pointB);
        EditText editTextInt = (EditText) findViewById(R.id.pointInt);
        CheckBox checkBoxUseLoc = (CheckBox) findViewById(R.id.use_loc);

        String pointA = editTextA.getText().toString();
        String pointB = editTextB.getText().toString();
        String pointInt = editTextInt.getText().toString();
        String useLoc = String.valueOf(checkBoxUseLoc.isChecked());

        intent.putExtra(EXTRA_POINTA, pointA);
        intent.putExtra(EXTRA_POINTB, pointB);
        intent.putExtra(EXTRA_POINTSUPP, pointInt);
        intent.putExtra(EXTRA_USELOC, useLoc);

        startActivity(intent);
    }

    public void showFieldWayPoint(View view){
        EditText editTextInt = (EditText) findViewById(R.id.pointInt);
        TextView textViewInt = (TextView) findViewById(R.id.pointIntTextView);
        editTextInt.setVisibility(View.VISIBLE);
        textViewInt.setVisibility(View.VISIBLE);
    }

    public void hidePointA(View view){

        CheckBox checkBoxPos = (CheckBox) findViewById(R.id.use_loc);

        EditText editTextA = (EditText) findViewById(R.id.pointA);
        TextView textViewA = (TextView) findViewById(R.id.pointATextView);

        if (checkBoxPos.isChecked()){
            editTextA.setText("");
            editTextA.setVisibility(View.GONE);
            textViewA.setVisibility(View.GONE);
        } else {
            editTextA.setVisibility(View.VISIBLE);
            textViewA.setVisibility(View.VISIBLE);
        }



    }
}
