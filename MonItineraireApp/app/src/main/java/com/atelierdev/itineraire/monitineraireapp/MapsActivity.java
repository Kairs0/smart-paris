package com.atelierdev.itineraire.monitineraireapp;

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