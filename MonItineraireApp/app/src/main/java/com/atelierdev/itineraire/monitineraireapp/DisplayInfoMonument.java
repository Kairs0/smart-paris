package com.atelierdev.itineraire.monitineraireapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;


public class DisplayInfoMonument extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private TextToSpeech engine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_monument_info);
        TextView textView = findViewById(R.id.resultatInformation);
        textView.setText("Chargement ...");

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String monument = intent.getStringExtra(MainActivity.EXTRA_MONUMENT_ID);

        Thread t1 = new TestThread("A", monument);
        t1.start();
        engine = new TextToSpeech(this, this);
        /*try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } */

    }

    public void UpdateTextView(String result) {
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

    public String GetStringFromGetEquipmentRequest(String result) throws JSONException {
        JSONObject obj = new JSONObject(result);
        if (obj == null) {
            return ("Pas d'information");
        }
        JSONArray data = obj.getJSONArray("data");
        if (data == null) {
            return ("Pas d'information");
        }
        JSONObject information = data.getJSONObject(0);
        if (information == null) {
            return ("Pas d'information");
        }
        String name = information.getString("name");
        String address = information.getString("address");

        String rawdescription = information.getString("description");
        String description = android.text.Html.fromHtml(rawdescription).toString();
        return ("Nom :" + name + "\n\nAdresse :" + address + "\n\nDescription :" + description);
    }


    public class TestThread extends Thread {
        private String _monument;
        public String result = "";

        public String GetResult() {
            return result;
        }

        public TestThread(String name, String monument) {
            super(name);
            _monument = monument;
        }

        @Override
        public void run() {
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    //int monument_id = Monument.findWithQuery(Monument.class, "Select * from Monument where name = ?", _monument).get(0).getMonumentId();
                    //url = new URL("https://api.paris.fr/api/data/1.0/Equipements/get_equipement/?token=f6e85890f660abe2fd846df117a3cb215fc7d5bb80969d17026f3820a23728f2&id=" + monument_id);
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

                        String test = GetStringFromGetEquipmentRequest(result);
                        UpdateTextView(test);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playText(View v) {
        TextView textView = findViewById(R.id.resultatInformation);
        String toSpeak=textView.getText().toString();
        if (toSpeak.length()>4000) {
            toSpeak=toSpeak.substring(0,3999);
        }
        engine.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);

    }

    @Override
    public void onInit(int i) {


        if (i == TextToSpeech.SUCCESS) {
            //Setting speech Language
            engine.setLanguage(Locale.FRENCH);
            engine.setPitch(1);
        }

        else {
            Toast.makeText(getApplicationContext(), "Impossible de lire le texte", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopPlaying(View view) {
        engine.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        engine.stop();
        engine.shutdown();
    }


}
