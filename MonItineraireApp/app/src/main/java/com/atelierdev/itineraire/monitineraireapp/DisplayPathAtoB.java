package com.atelierdev.itineraire.monitineraireapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayPathAtoB extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_path_a_to_b);
        TextView textView = findViewById(R.id.resultatChemin);
        updateTextView("Chargement ...");


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String pointA = intent.getStringExtra(MainActivity.EXTRA_POINTA);
        String pointB = intent.getStringExtra(MainActivity.EXTRA_POINTB);

        GoogleApiThread api = new GoogleApiThread(pointA, pointB, "walking", new ArrayList<String>());
        Thread callThread = new Thread(api);
        callThread.start();
        try {
            callThread.join();
            String result = api.getResult();
            GoogleApiResultManager manageJson = new GoogleApiResultManager(result);

            manageJson.ManageJsonResult(false);

            StringBuilder sb = new StringBuilder();
            for (String instruct : manageJson.getInstructionsResult()){
                instruct = instruct.replaceAll( "<(.|\n)*?>","");
                sb.append(instruct);
                sb.append("\n");
            }

            updateTextView(sb.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateTextView(String result){
        final String res = result;
        //Dépose le Runnable dans la file d'attente de l'UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //code exécuté par l'UI thread
                TextView textView = findViewById(R.id.resultatChemin);
                textView.setText(res);
            }
        });
    }
}
