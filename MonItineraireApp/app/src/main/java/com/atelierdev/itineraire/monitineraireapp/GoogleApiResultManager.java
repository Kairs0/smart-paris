package com.atelierdev.itineraire.monitineraireapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arnaud on 08/12/2017.
 */

public class GoogleApiResultManager {
    private String jsonString;
    private List<String> result = new ArrayList<>();

    public List<String> getResult() {
        return result;
    }

    public GoogleApiResultManager(String jsonString){
        this.jsonString = jsonString;
    }

    public void ManageJsonResult(){
        try{
            JSONObject jsonObject = new JSONObject(this.jsonString);
            JSONArray routes = jsonObject.getJSONArray("routes");
            if (routes == null){
                this.result.add("Pas d'itinéraire");
            }
            JSONObject firstItinerary = routes.getJSONObject(0);
            if (firstItinerary == null) {
                this.result.add("Pas d'itinéraire");
            }
            JSONArray legs = firstItinerary.getJSONArray("legs");
            if (legs == null) {
                this.result.add("Pas d'itinéraire");
            }
            JSONObject legOne = legs.getJSONObject(0);
            if (legOne== null) {
                this.result.add("Pas d'itinéraire");
            }
            JSONArray steps = legOne.getJSONArray("steps");
            if (steps == null) {
                this.result.add("Pas d'itinéraire");
            }
            for (int i=0 ; i < steps.length();++i) {
                String instruct = steps.getJSONObject(i).getString("html_instructions");
                this.result.add(instruct);
            }

        } catch (JSONException jsonExcept){
            this.result.add("Pas d'itinéraire possible");
        } catch (NullPointerException nullPointer){
            this.result.add("Pas d'itinéraire possible");
        }

    }
}
