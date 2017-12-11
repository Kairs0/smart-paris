package com.atelierdev.itineraire.monitineraireapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Arnaud on 08/12/2017.
 */

public class GoogleApiThread implements Runnable {
    private volatile String result = "";
    private String origin;
    private String destination;
    private String mode;


    /**
     * getter for result
     * @return result
     */
    public synchronized String getResult() {
        return result;
    }

    GoogleApiThread(String origin, String destination, String mode) {
        this.origin = origin;
        this.destination = destination;
        this.mode = mode;
    }


    @Override
    public void run(){
        try {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                String urlOrigin = "&origin=" + this.origin.replaceAll("\\s+", "%20");
                String urlDestination = "&destination=" + this.destination.replaceAll("\\s+", "%20");
                String urlMode = "&mode=" + this.mode;
                String baseUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "key=AIzaSyBM27gzMoQUs11F4Zqkc4xMaxhfZS8RS9M&" +
                        "language=fr";
                String fullUrl = baseUrl + urlOrigin + urlDestination + urlMode;

                url = new URL(fullUrl);
                urlConnection = (HttpURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null){
                    sb.append(output);
                }
                this.result = sb.toString();
            } catch (Exception e){
                e.printStackTrace();
                this.result = "Nous n'avons pas pu vous connecter";
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}