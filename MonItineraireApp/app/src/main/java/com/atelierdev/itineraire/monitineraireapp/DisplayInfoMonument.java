package com.atelierdev.itineraire.monitineraireapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

        Thread t1 = new TestThread("A");
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Capture the layout's TextView and set the string as its text
        /* TextView textView = findViewById(R.id.resultatInformation);
        textView.setText(fonction_débile(monument));*/
    }

    /*protected String fonction_débile(String monument) {
        String retour = monument + " est une très bonne idée de visite!";
        return (retour);
    }*/

    public class TestThread extends Thread
    {
        public String result = "";

        public String GetResult(){
            return result;
        }

        public TestThread(String name){
            super(name);
        }

        @Override
        public void run() {
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL("https://api.paris.fr/api/data/1.0/Equipements/get_categories/?token=f6e85890f660abe2fd846df117a3cb215fc7d5bb80969d17026f3820a23728f2");

                    urlConnection = (HttpURLConnection) url
                            .openConnection();

                    BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    this.result = sb.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    this.result = "Nous n'avons pas pu vous connecter";
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();

                        Log.d("Resultat", result);

                        TextView textView = findViewById(R.id.resultatInformation);
                        textView.setText(result);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
