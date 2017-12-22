package com.atelierdev.itineraire.monitineraireapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Guillaume on 18/12/2017.
 */

public abstract class DatabaseHandler {

    public static boolean Initialize(Context context){

        Monument.deleteAll(Monument.class); //On supprime anciennes donnees
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
                        int Id = Integer.parseInt(properties[0]);
                        String Name = properties[1];
                        int Category = Integer.parseInt(properties[2]);
                        String Type = properties[3];
                        double Lat = Double.parseDouble(properties[6]);
                        double Long = Double.parseDouble(properties[7]);
                        int visitTime = Integer.parseInt(properties[9]);
                        int Rating = Integer.parseInt(properties[10]);

                        Monument new_monument = new Monument(Id, Name, Category, Type, Lat, Long, Rating, visitTime);
                        new_monument.save();
                    }
                }catch(Exception e){
                Log.d("Exception", e.getMessage());
                }
            }
        } catch (Exception e){
            Log.d("Exception", e.getMessage());
        }

        List<Monument> monuments = Monument.listAll((Monument.class));
        for (Monument monument : monuments) {
            Log.d("Monument ", monument.getId() + monument.getName());
        }


        return true;
    }
}
