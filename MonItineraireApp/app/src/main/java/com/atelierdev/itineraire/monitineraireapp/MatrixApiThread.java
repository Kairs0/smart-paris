package com.atelierdev.itineraire.monitineraireapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arnaud on 10/01/2018.
 * Classe utilisée pour l'appel à l'API Google Matrix.
 * Utilisation :
 * 1) Instanciation de la classe
 * >> MatrixApiThread example = new MatrixApiThread(List<String> origins, List<String> destinations, String mode);
 * Avec origins liste d'origines sous forme de liste de Strings (coordonnées au format Lat,Lng ou adresse)
 * Destinations liste de destinations sous forme de liste de Strings
 * mode type de transport (walking pour à pied)
 *
 * 2) Appel asynchrone de la méthode run de la classe pour calculer le trajet
 * >> Thread callThreadExample = new Thread(example);
 * >> callThreadExample.start();
 *
 * 3) Récupération des résultats
 * >> callThread.join(); // Attend la fin du thread
 * >> String result = example.getResult(); stocke les résultats dans un string
 *
 * 4) Traitement du résultat avec la classe MatrixApiResultManager
 * >> MatrixApiResultManager manageJsonExample = new MatrixApiResultManager(result);
 * >> manageJsonExample.calculTime();
 *
 * 5) Appel des résultats
 * >> List<List<Integer>> results = manageJsonExample.getTimesResult();
 * Avec results list de list de integer,
 * result.get(i).get(j) = tps en secondes de l'origine i à la destination j, où i et j commencent à 0
 * par exemple, avec les paramètres suivants :
 * Origines :
 * Or_0 "Parvis Notre-Dame - Pl. Jean-Paul II, 75004 Paris-4E-Arrondissement, France",
 * Or_1 "Place Charles de Gaulle, 75008 Paris, France",
 * Or_2 "Avenue Anatole France, 75007 Paris, France"
 * Destinations :
 * Dest_0 "Boulevard du Palais, 75001 Paris, France",
 * Dest_1 "Rue de Rivoli, 75004 Paris, France",
 * Dest_2 "Rue du Faubourg Saint-Honoré, 75008 Paris, France"
 * On a le tableau de temps suivant
 * **********Or_0 to Dest_0**********Or_0 to Dest_1**********Or_0 to Dest_2
 * **********Or_1 to Dest_0**********Or_1 to Dest_1**********Or_1 to Dest_2
 * **********Or_2 to Dest_0**********Or_2 to Dest_1**********Or_2 to Dest_2
 *
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
                replaceOrigins.add(origin.replaceAll("\\s+", "%20"));
            }
            this.origins = replaceOrigins;
            List<String> replaceDestinations = new ArrayList<>();
            for (String destination : this.destinations){
                replaceDestinations.add(destination.replaceAll("\\s+", "%20"));
            }
            this.destinations = replaceDestinations;
            String urlOrigins = "&origins=" + myJoinFunction(this.origins, "%7C");
            String urlDestinations = "&destinations=" + myJoinFunction(this.destinations, "%7C");
            String urlMode = "&mode=" + this.mode;
            String keyUrl = "&key=AIzaSyCafibIK9JGSTlaKTmNYVIcF4h5HH-HdmI";
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
        return result.substring(0, result.length() - delim.length());
    }
}
