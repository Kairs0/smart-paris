package com.atelierdev.itineraire.monitineraireapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
//    private String GoogleUri = "https://maps.googleapis.com/maps/api/directions/json?origin=Paris&destination=Antony&mode=driving&key=AIzaSyBM27gzMoQUs11F4Zqkc4xMaxhfZS8RS9M";
    public static final String EXTRA_POINTA = "com.atelierdev.itineraire.monitineraireapp.pointA";
    public static final String EXTRA_POINTB = "com.atelierdev.itineraire.monitineraireapp.pointB";

    private String calculTrajetResult = new String();

    public String getCalculTrajetResult() {
        return calculTrajetResult;
    }

    public void setCalculTrajetResult(String calculTrajetResult) {
        this.calculTrajetResult = calculTrajetResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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


        callApi(pointA, pointB, "driving");



        intent.putExtra(EXTRA_POINTA, getCalculTrajetResult());
        intent.putExtra(EXTRA_POINTB, pointB);
        startActivity(intent);

    }

    public void callApi(String pointA, String pointB, String mode) {
        RequestParams rp = new RequestParams();
        rp.add("origin", pointA);
        rp.add("destination", pointB);
        rp.add("mode", mode); //TODO: set to walking
        GoogleApiClient.get("json?", rp, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected
                setCalculTrajetResult("ONSCCESSOBJECT");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                setCalculTrajetResult("ONSCCESSARRAY");
                // Pull out the first event on the public timeline
//                JSONObject firstEvent = timeline.get(0);
//                String tweetText = firstEvent.getString("text");

                // Do something with the response
//                System.out.println(tweetText);
            }

        });
    }
}
