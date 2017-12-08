package com.atelierdev.itineraire.monitineraireapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Object;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.loopj.android.http.*;

public class MainActivity extends AppCompatActivity {
    private String GoogleUri = "https://maps.googleapis.com/maps/api/directions/json?origin=Paris&destination=Antony&mode=driving&key=AIzaSyBM27gzMoQUs11F4Zqkc4xMaxhfZS8RS9M";
    public static final String EXTRA_POINTA = "com.atelierdev.itineraire.monitineraireapp.pointA";
    public static final String EXTRA_POINTB = "com.atelierdev.itineraire.monitineraireapp.pointB";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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


        String urlString = "https://maps.googleapis.com/maps/api/directions/json?origin=Paris&destination=Antony&mode=driving&key=AIzaSyBM27gzMoQUs11F4Zqkc4xMaxhfZS8RS9M";
        String resultCalcul = new String();

//        https://www.androidauthority.com/use-remote-web-api-within-android-app-617869/
        //https://stackoverflow.com/questions/14250989/how-to-use-asynctask-correctly-in-android


        AsyncHttpClient client = new AsyncHttpClient();
//        client.get("https://www.google.com", new AsyncHttpResponseHandler() {
//
//            @Override
//            public void onStart() {
//                // called before request is started
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//                // called when response HTTP status is "200 OK"
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
//                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//            }
//
//            @Override
//            public void onRetry(int retryNo) {
//                // called when request is retried
//            }
//
//
//        });

//        try{
//            URL url = new URL(urlString);
//            HttpURLConnection request = (HttpURLConnection) url.openConnection();
////            request.setRequestMethod("GET");
////            int responseCode = request.getResponseCode();
//
//            //https://stackoverflow.com/a/9856272
//            InputStream in = url.openStream();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            StringBuilder result = new StringBuilder();
//            String line;
//            while((line = reader.readLine()) != null){
//                result.append(line);
//            }
//            resultCalcul = result.toString();
//
//        } catch (MalformedURLException e){
//
//        } catch (IOException a){
//
//        }

//        URL url = new URL("http://www.android.com/");



        intent.putExtra(EXTRA_POINTA, resultCalcul);
        intent.putExtra(EXTRA_POINTB, pointB);
        startActivity(intent);

    }

    public void displayMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
