package com.atelierdev.itineraire.monitineraireapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Arnaud on 08/12/2017.
 * Classe utilisée pour l'appel à l'API direction.
 * Utilisation :
 * 1) Instanciation de la classe
 * >> GoogleApiThread example = new GoogleApiThread(String pointA,
 *                  String pointB, String mode, List(String> listPointsInt);
 * Avec: pointA, pointB strings avec coordonnées GPS
 * mode string ayant pour valeur "walking", "driving", ...
 * listPointsInt liste de strings pour les points intermédiaires, ayant même format que point A et B
 *
 * 2) Appel asynchrone de la méthode run de la classe pour calculer le trajet
 * >> Thread callThreadExample = new Thread(example);
 * >> callThreadExample.start();
 *
 * 3) Récupération des résultats
 * >> callThread.join(); // Attend la fin du thread
 * >> String result = example.getResult(); stock les résultats dans un string
 *
 * 4) Traitement du résultat avec la classe GoogleApiResultManager
 * >> GoogleApiResultManager manageJsonExample = new GoogleApiResultManager(result);
 * >> manageJsonExample.ManageTextInstructions();
 * Pour calculer une liste d'instructions.
 * OU
 * >> manageJsonExample.ManageCoordinates();
 * Cette option récupère directement la polyline chiffrée dans la réponse à la requête et la déchiffre afin
 * d'avoir la bonne trajectoire de la ligne
 *
 * 5) Appel des résultats avec la classe GoogleApiResultManager
 * >> List<LatLng> points = manageJsonExample.getCoordsResult();
 * OU
 * >> List<String> instrucs = manageJsonExample.getInstructionsResult();
 */

public class GoogleApiThread implements Runnable {
    private volatile String result = "";
    private String origin;
    private String destination;
    private String mode;
    private List<String> waypoints;


    /**
     * getter for result
     * @return result
     */
    public synchronized String getResult() {
        return result;
    }

    GoogleApiThread(String origin, String destination, String mode, List<String> waypoints) {
        this.origin = origin;
        this.destination = destination;
        this.mode = mode;
        this.waypoints = waypoints;
    }


    @Override
    public void run(){
        try {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                // Pour chaque donnée, on remplace les espaces pour qu'ils soient traités dans la requetes
                String urlOrigin = "&origin=" + this.origin.replaceAll("\\s+", "%20");
                String urlDestination = "&destination=" + this.destination.replaceAll("\\s+", "%20");
                String urlMode = "&mode=" + this.mode;
                String baseUrl = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "key=AIzaSyBM27gzMoQUs11F4Zqkc4xMaxhfZS8RS9M&" +
                        "language=fr";

                // Traitement des points intérmediaires
                String tmpWaypoints = "";
                for (String wp : this.waypoints ) {
                    String wpClean = wp.replaceAll("\\s+", "%20");
                    tmpWaypoints = tmpWaypoints.concat(wpClean + "|");
                }
                if (tmpWaypoints.length() > 0) {
                    tmpWaypoints = tmpWaypoints.substring(0, tmpWaypoints.length() -1 );
                }

                String urlWaypoints = this.waypoints.size() == 0 ? "" :
                        "&waypoints=" + tmpWaypoints;

                String fullUrl = baseUrl + urlOrigin + urlDestination + urlMode + urlWaypoints;

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
                //TODO
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