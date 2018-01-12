package com.atelierdev.itineraire.monitineraireapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

// Gère les permissions pour la localisation, donne la localisation actuelle
public final class LocalizationHandler {

    private static int MIN_TIME_FOR_UPDATES = 2000; //Temps min d'actualisation de la localisation
    private static int MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // Distance min d'actualisation de la localisation
    private static int TAG_CODE_PERMISSION_LOCATION = 1;
    private static Location location = null;

    // Demande permission à l'utilisateur d'accéder à sa localisation
    public static void requestPermissionIfNotGranted(android.app.Activity activity){
        if(!isGrantedPermission(activity))
        {
            ActivityCompat.requestPermissions(activity, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);
        }
    }

    // Vérifie si l'utilisateur a donné sa permission
    public static Boolean isGrantedPermission(android.app.Activity activity){
        return(ContextCompat
                .checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat
                .checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    // Si permission accordée, renvoie la latitude et la longitude de l'utilisateur dans un tableau
    @SuppressLint("MissingPermission")
    public static double[] getLatLng(android.app.Activity activity, LocationManager locationManager) {
        requestPermissionIfNotGranted(activity);
        if (isGrantedPermission(activity)) {
            if (location == null) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_FOR_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListenerGPS);
                Log.d("GPS Enabled", "GPS Enabled");
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
            double[] latlng = new double[2];
            latlng[0] = location.getLatitude();
            latlng[1] = location.getLongitude();
            return latlng;
        }
        else {
            return null;
        }
    }

    public static LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
