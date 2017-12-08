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
        TextView textView = findViewById(R.id.resultatInformation);
        textView.setText("Chargement ...");

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String monument = intent.getStringExtra(MainActivity.EXTRA_MONUMENT);

        Thread t1 = new TestThread("A", monument);
        t1.start();
        /*try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public void UpdateTextView(String result)
    {
        final String res = result;
        //Déposer le Runnable dans la file d'attente de l'UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //code exécuté par l'UI thread
                TextView textView = findViewById(R.id.resultatInformation);
                textView.setText(res);
            }
        });
    }

    public class TestThread extends Thread
    {
        private String _monument;
        public String result = "";

        public String GetResult(){
            return result;
        }

        public TestThread(String name, String monument){
            super(name);
            _monument = monument;
        }

        @Override
        public void run() {
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL("https://api.paris.fr/api/data/1.0/Equipements/get_equipement/?token=f6e85890f660abe2fd846df117a3cb215fc7d5bb80969d17026f3820a23728f2&id=" + _monument);

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

                        UpdateTextView(result);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
