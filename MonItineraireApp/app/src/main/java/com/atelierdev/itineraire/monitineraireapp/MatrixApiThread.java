package com.atelierdev.itineraire.monitineraireapp;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arnaud on 10/01/2018.
 */

public class MatrixApiThread implements Runnable {
    private volatile String result = "";
    private List<String> origins;
    private List<String> destinations;
    private String mode;

    /**
     * getter for result
     * @return result
     */
    public synchronized String getResult() {return result;}

    MatrixApiThread(List<String> origins, List<String> destinations, String mode){
        this.destinations = destinations;
        this.origins = origins;
        this.mode = mode;
    }

    public void run(){
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            // In case there is any full written destination or origin,
            // we replace each space by +
            List<String> replaceOrigins = new ArrayList<>();
            for (String origin : this.origins){
                replaceOrigins.add(origin.replaceAll("\\s+", "+"));
            }
            this.origins = replaceOrigins;
            List<String> replaceDestinations = new ArrayList<>();
            for (String destination : this.destinations){
                replaceDestinations.add(destination.replaceAll("\\s+", "+"));
            }
            this.destinations = replaceDestinations;
            String urlOrigins = "&origins=" + myJoinFunction(this.origins, "|");
            String urlDestinations = "&destinations=" + myJoinFunction(this.destinations, "|");
            String urlMode = "mode=" + this.mode;
            String keyUrl = "&key=AIzaSyC6kv8mZQhM8p8AlDdUz41s_ae8H9hGHlU";
            String baseUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?" +
                    urlMode + keyUrl;

            String fullUrl = baseUrl + urlOrigins + urlDestinations + urlMode;

            url = new URL(fullUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null){
                sb.append(output);
            }
            this.result = sb.toString();


        } catch (Exception e) {
            this.result = "{\"status\": \"API_CONNECT_ERROR\"}";
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private String myJoinFunction(List<String> list, String delim){
        String result = "";
        for (String word : list){
            result = result.concat(word).concat(delim);
        }

        return result.substring(0, result.length() -1);
    }



}
