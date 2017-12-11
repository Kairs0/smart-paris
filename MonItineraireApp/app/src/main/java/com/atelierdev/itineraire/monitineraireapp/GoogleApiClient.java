package com.atelierdev.itineraire.monitineraireapp;

import android.os.AsyncTask;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import javax.xml.transform.Result;

/**
 * Created by Arnaud on 04/12/2017.
 */
//String urlString = "https://maps.googleapis.com/maps/api/directions/json?origin=Paris&destination=Antony&mode=driving&key=AIzaSyBM27gzMoQUs11F4Zqkc4xMaxhfZS8RS9M";
//http://loopj.com/android-async-http/

/**
 * Params:
 * origin
 * destination
 * mode(driving)
 * key*
 */

public class GoogleApiClient extends AsyncTask<> {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    @Override
    public static void doInBackgroun(Params){
        
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
