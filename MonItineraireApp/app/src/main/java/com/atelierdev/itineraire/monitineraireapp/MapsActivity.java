package com.atelierdev.itineraire.monitineraireapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String pointA = intent.getStringExtra(MainActivity.EXTRA_POINTA);
        String pointB = intent.getStringExtra(MainActivity.EXTRA_POINTB);

        GoogleApiThread api = new GoogleApiThread(pointA, pointB, "walking");
        Thread callThread = new Thread(api);
        callThread.start();
        try {
            callThread.join();
            String result = api.getResult();
            GoogleApiResultManager manageJson = new GoogleApiResultManager(result);

            manageJson.ManageJsonResult(true);
            List<List<Double>> list_pairs_coord = manageJson.getCoordsResult();

            List<LatLng> pointsPath = new ArrayList<>();

            for (List<Double> pair : list_pairs_coord){
                LatLng point = new LatLng(pair.get(0), pair.get(1));
                pointsPath.add(point);
            }

            // set camera on start point
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointsPath.get(0), 18));

            for(int i=0; i < pointsPath.size() ;i++) {
                mMap.addMarker(new MarkerOptions().position(pointsPath.get(i)).title("Point"));
            }

            mMap.addPolyline(new PolylineOptions().addAll(pointsPath).width(5).color(Color.RED));


        } catch (Exception e) {
            e.printStackTrace();
            // Créer des objets LatLng au niveau de l'hotel de ville, d'une origine et d'une destination
            LatLng paris = new LatLng(48.8566, 2.35177);
            LatLng origine = new LatLng(48.856062, 2.353267);
            LatLng destination = new LatLng(48.857460, 2.351670);
            // Les mettre dans une liste
            List<LatLng> points=new ArrayList<LatLng>();
            points.add(origine);
            points.add(destination);
            points.add(paris);
            // Placer la camera au niveau du LatLng paris
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paris, 18));
            //Ajouter les marqueurs de la liste
            for(int i=0; i < points.size() ;i++) {
                mMap.addMarker(new MarkerOptions().position(points.get(i)).title("Point"));
            }
            //Dessiner le trajet entre tous les points de la liste
            mMap.addPolyline(new PolylineOptions().addAll(points).width(5).color(Color.RED));

        }
    }
}