package com.atelierdev.itineraire.monitineraireapp;

import com.google.android.gms.maps.model.LatLng;

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
    private List<String> instructionsResult = new ArrayList<>();
    private List<LatLng> coordinatesLatLng = new ArrayList<>();
    private int durationInSeconds = 0;

    public GoogleApiResultManager(String jsonString){
        this.jsonString = jsonString;
    }

    public void calculTime(){
        int result = 0;
        try{
            JSONObject jsonObject = new JSONObject(this.jsonString);
            JSONArray routes = jsonObject.getJSONArray("routes");
            if (routes == null){
                return;
            }
            JSONObject firstItinerary = routes.getJSONObject(0);
            if (firstItinerary == null) {
                return;
            }
            JSONArray legs = firstItinerary.getJSONArray("legs");
            if (legs == null) {
                return;
            }
            for (int i = 0 ; i < legs.length(); i++) {
                JSONObject leg = legs.getJSONObject(i);
                JSONObject duration = leg.getJSONObject("duration");
                if (duration == null) {
                    return;
                } else {
                    result += duration.getInt("value");
                }
            }

        } catch (JSONException | NullPointerException exp){
            return;
        }
        this.durationInSeconds = result;
    }

    public void ManageTextInstructions(){
        try{
            JSONObject jsonObject = new JSONObject(this.jsonString);
            JSONArray routes = jsonObject.getJSONArray("routes");
            if (routes == null){
                this.instructionsResult.add("Pas d'itinéraire");
            }
            JSONObject firstItinerary = routes.getJSONObject(0);
            if (firstItinerary == null) {
                this.instructionsResult.add("Pas d'itinéraire");
            }
            JSONArray legs = firstItinerary.getJSONArray("legs");
            if (legs == null) {
                this.instructionsResult.add("Pas d'itinéraire");
            }
            for (int i = 0 ; i < legs.length(); i++) {
                JSONObject leg = legs.getJSONObject(i);
                getTextInstructFromLeg(leg, false);
            }
        } catch (JSONException jsonExcept){
            this.instructionsResult.add("Pas d'itinéraire possible");
        } catch (NullPointerException nullPointer){
            this.instructionsResult.add("Pas d'itinéraire possible");
        }
    }

    public void ManageCoordinates(){
        try {
            JSONObject jsonObject = new JSONObject(this.jsonString);
            JSONArray routes = jsonObject.getJSONArray("routes");
            JSONObject firstItinerary = routes.getJSONObject(0);
            JSONObject containerPoly = firstItinerary.getJSONObject("overview_polyline");
            String encodedPoly = containerPoly.getString("points");
            coordinatesLatLng = polylineDecoder(encodedPoly);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void getTextInstructFromLeg(JSONObject leg, boolean modeMap){
        try{
            JSONArray steps = leg.getJSONArray("steps");
            if (steps == null){
                this.instructionsResult.add("Pas d'inintéraire");
            }
            // On récupère les instructions
            for (int i=0 ; i < steps.length();++i) {
                String instruct = steps.getJSONObject(i).getString("html_instructions");
                this.instructionsResult.add(instruct);
            }
        } catch (JSONException|NullPointerException exp){
            this.instructionsResult.add("Pas d'itinéraire possible");
        }
    }

    /**
     * Decode la polyline encodée dans le json de google
     * from https://github.com/scoutant/polyline-decoder
     */
    private List<LatLng> polylineDecoder(String encoded){
        double precision = 1E5;
        List<LatLng> track = new ArrayList<>();
        int index = 0;
        int lat = 0, lng = 0;

        while (index < encoded.length()) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            double latitude = (double) lat/precision;
            double longitude = (double) lng/precision;
            track.add(new LatLng(latitude, longitude));
        }
        return track;
    }

    /**
     * GETTERS AND SETTERS
     */

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public List<String> getInstructionsResult() {
        return instructionsResult;
    }

    public List<LatLng> getCoordinatesLatLng() {
        return coordinatesLatLng;
    }
}
