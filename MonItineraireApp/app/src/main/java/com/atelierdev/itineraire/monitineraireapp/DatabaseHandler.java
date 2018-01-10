package com.atelierdev.itineraire.monitineraireapp;

import android.util.Log;

/**
 * Created by Guillaume on 18/12/2017.
 */

public abstract class DatabaseHandler {

    public static boolean Initialize(){
        Monument test_monument = new Monument(1, "Tour Eiffel", 253, "Incontournable", 48.858841, 2.294329, 5, 90);
        test_monument.save();

        Monument  recup_monument = Monument.findById(Monument.class, (long)1);
        Log.d("Monument récupéré: ", recup_monument.getName());

        return true;
    }
}
