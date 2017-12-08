package com.atelierdev.itineraire.monitineraireapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Guillaume on 04/12/2017.
 */

/*public class TestThread extends Thread
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}*/
