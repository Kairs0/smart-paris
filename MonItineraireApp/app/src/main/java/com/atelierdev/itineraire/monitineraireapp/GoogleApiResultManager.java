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
        //TODO STRING CONSTANTS
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
                getTextInstructFromLeg(leg);
            }
        } catch (JSONException|NullPointerException e){
            this.instructionsResult.add("Pas d'itinéraire");
        }
    }

    public void ManageCoordinates() throws JSONException{
        // Exception is managed when calling the method
        JSONObject jsonObject = new JSONObject(this.jsonString);
        JSONArray routes = jsonObject.getJSONArray("routes");
        JSONObject firstItinerary = routes.getJSONObject(0);
        JSONObject containerPoly = firstItinerary.getJSONObject("overview_polyline");
        String encodedPoly = containerPoly.getString("points");
        coordinatesLatLng = decodePolyline(encodedPoly);
    }

    private void getTextInstructFromLeg(JSONObject leg) throws JSONException {
        JSONArray steps = leg.getJSONArray("steps");
        // On récupère les instructions
        for (int i = 0 ; i < steps.length() ; i++){
            String instruct = steps.getJSONObject(i).getString("html_instructions");
            this.instructionsResult.add(instruct);
        }
    }

    /**
     * Decode la polyline encodée dans le json de google
     * from https://github.com/scoutant/polyline-decoder
     */
    private List<LatLng> decodePolyline(String encoded){
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

    public String getStatus()throws JSONException {
        JSONObject jsonObject = new JSONObject(this.jsonString);
        return jsonObject.getString("status");
    }

    public boolean isStatusOk() throws JSONException {
        JSONObject jsonObject = new JSONObject(this.jsonString);
        String status = jsonObject.getString("status");
        if (status.equals("OK")) {
            return true;
        } else {
            return false;
        }
    }

    public String getErrorMessage() throws JSONException {
        String status = this.getStatus();
        //TODO mettre message erreur en strings
        switch (status){
            case "NOT_FOUND":
                return "L'un des points spécifié n'a pas pu être localisé";
            case "ZERO_RESULTS":
                return "Aucune route n'a pu être trouvée entre les deux points spécifiés";
            case "MAX_WAYPOINTS_EXCEEDED":
                return "Trop de points intérmediaires ont été spécifiés";
            case "MAX_ROUTE_LENGTH_EXCEEDED":
                return "Les deux points spécifiés sont trop éloignés";
            case "INVALID_REQUEST":
                return "La route n'a pas été trouvée suite à un problème interne à l'application";
            case "OVER_QUERY_LIMIT ":
                return "Trop de requêtes. Contactez le developpeur de l'application.";
            case "REQUEST_DENIED":
                return "Erreur inconnue provenant des services Google. Reessayez à nouveau";
            case "UNKNOWN_ERROR":
                return "Erreur inconnue provenant des services Google. Reessayez à nouveau";
            case "API_CONNECT_ERROR":
                return "La route n'a pas étée trouvée suite à un problème interne à l'apllication";
            default:
                return "";
        }
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
