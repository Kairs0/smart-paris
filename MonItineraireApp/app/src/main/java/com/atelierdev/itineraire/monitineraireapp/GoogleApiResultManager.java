package com.atelierdev.itineraire.monitineraireapp;

import android.graphics.Point;

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
    private List<LatLng> coordsResult = new ArrayList<>();
    private List<LatLng> decodedPolylineResult = new ArrayList<>();

    public List<LatLng> getCoordsResult(){
        return coordsResult;
    }

    public List<String> getInstructionsResult() {
        return instructionsResult;
    }

    public List<LatLng> getDecodedPolylineResult() {
        return decodedPolylineResult;
    }

    public GoogleApiResultManager(String jsonString){
        this.jsonString = jsonString;
    }

    public void ManageJsonResult(Boolean modeMap){
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
                visiteALeg(leg, modeMap);
            }

        } catch (JSONException jsonExcept){
            this.instructionsResult.add("Pas d'itinéraire possible");
        } catch (NullPointerException nullPointer){
            this.instructionsResult.add("Pas d'itinéraire possible");
        }
    }

    public void ManagePolylineResult(){
        try {
            JSONObject jsonObject = new JSONObject(this.jsonString);
            JSONArray routes = jsonObject.getJSONArray("routes");
            if (routes == null){
                this.instructionsResult.add("Pas d'itinéraire");
            }
            JSONObject firstItinerary = routes.getJSONObject(0);
            if (firstItinerary == null) {
                this.instructionsResult.add("Pas d'itinéraire");
            }
            JSONObject containerPoly = firstItinerary.getJSONObject("overview_polyline");
            if (containerPoly == null) {
                this.instructionsResult.add("Pas d'itinéraire");
            }
            String encodedPoly = containerPoly.getString("points");
            decodedPolylineResult = polylineDecoder(encodedPoly);

        } catch (Exception e){
            this.instructionsResult.add("Pas d'itinéraire possible");
        }

    }

    private void visiteALeg(JSONObject leg, boolean modeMap){
        try{
            JSONArray steps = leg.getJSONArray("steps");
            if (steps == null){
                this.instructionsResult.add("Pas d'inintéraire");
            }

            if (modeMap){
                /**
                 * On veut récupérer les coordonnées gps du chemin
                 * On récupère juste les start location (sauf pour dernière étape)
                 * car à chaque étape, le start location est
                 * égale au end_location de l'étape précédènte
                 */
                for (int i = 0 ; i < steps.length(); i++){
                    JSONObject startLoc = steps.getJSONObject(i).getJSONObject("start_location");
                    LatLng coordStep = new LatLng(startLoc.getDouble("lat"), startLoc.getDouble("lng"));
                    coordsResult.add(coordStep);
                }
                //ajoute la dernière coordonnée
                int last_index = steps.length();
                JSONObject endLoc = steps.getJSONObject(last_index).getJSONObject("end_location");
                LatLng coordStep = new LatLng(endLoc.getDouble("lat"), endLoc.getDouble("lng"));
                coordsResult.add(coordStep);

            } else{
                // On récupère les instructions
                for (int i=0 ; i < steps.length();++i) {
                    String instruct = steps.getJSONObject(i).getString("html_instructions");
                    this.instructionsResult.add(instruct);
                }
            }
        } catch (JSONException jsonExcept){
            this.instructionsResult.add("Pas d'itinéraire possible");
        }
    }

    /**
     * Decode la polyline encodée dans le json de google
     * /!\ CREDITS: https://github.com/scoutant/polyline-decoder
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
}
