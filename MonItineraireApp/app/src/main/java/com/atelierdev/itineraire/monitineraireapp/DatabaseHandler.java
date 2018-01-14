package com.atelierdev.itineraire.monitineraireapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Guillaume on 18/12/2017.
 */

public abstract class DatabaseHandler {

    // Vérifie si la table est initialisée en effectuant une opération simple dessus
    public static boolean isCreatedTable(){
        try{
            Monument.executeQuery("Select * From Monument");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static void Initialize(Context context){
        AssetManager assetManager = context.getAssets(); //Pour recuperer fichier dans dossiers "assets"

        try{
            // On ouvre le fichier, qu'on lit ligne par ligne
            InputStream is= assetManager.open("Monuments.txt");
            BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while ((line = bReader.readLine()) != null) {
                try {
                    if (line != null) {
                        // On recupere les differents champs
                        String[] properties = line.split(",+");
                        int monument_id = Integer.parseInt(properties[0]);
                        String Name = properties[1];
                        int Category = Integer.parseInt(properties[2]);
                        String Types = properties[3];
                        double Lat = Double.parseDouble(properties[4]);
                        double Long = Double.parseDouble(properties[5]);
                        int visitTime = Integer.parseInt(properties[6]);
                        int Rating = Integer.parseInt(properties[7]);

                        Monument new_monument = new Monument(monument_id, Name, Category, Types, Lat, Long, Rating, visitTime);
                        new_monument.save();
                    }
                }catch(Exception e){
                Log.d("Exception", e.getMessage());
                }
            }
        } catch (Exception e){
            Log.d("Exception", e.getMessage());
        }

        //Exemple de requête : sélectionner tous les monuments dont le nom commence par T
        /*List<Monument> testM = Monument.findWithQuery(Monument.class, "Select * from Monument where name LIKE ?", "T%");
        for (Monument monument : testM) {
            Log.d("Monument ", monument.getId() + monument.getName());
        }*/
    }
}
