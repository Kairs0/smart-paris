package com.atelierdev.itineraire.monitineraireapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;


import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

        // Récupère l'intent qui a lancé l'activité et extrait les données
        Intent intent = getIntent();
        String pointA = intent.getStringExtra(MainActivity.EXTRA_POINTA);
        String pointB = intent.getStringExtra(MainActivity.EXTRA_POINTB);
        String pointInt = intent.getStringExtra(MainActivity.EXTRA_POINTSUPP);

        // Transforme le point int en une liste pour pouvoir le passer au thread api
        List<String> listPointsInt = new ArrayList<>();
        if (!pointInt.equals("")){
            listPointsInt.add(pointInt);
        }

        // Apelle l'api. Voir documentation dans GoogleApiThread
        GoogleApiThread api = new GoogleApiThread(pointA, pointB, "walking", listPointsInt);
        Thread callThread = new Thread(api);
        callThread.start();
        try {
            callThread.join();
            String result = api.getResult();
            GoogleApiResultManager manageJson = new GoogleApiResultManager(result);

            manageJson.ManageJsonResult(true);
            List<LatLng> pointsPath = manageJson.getCoordsResult();

            // set camera on start point
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointsPath.get(0), 18));

            for(int i=0; i < pointsPath.size() ;i++) {
                mMap.addMarker(new MarkerOptions().position(pointsPath.get(i)).title("Point"));
            }

            mMap.addPolyline(new PolylineOptions().addAll(pointsPath).width(5).color(Color.RED));


        } catch (Exception e) {
            e.printStackTrace();
            // Créer des objets LatLng au niveau de l'hotel de ville, d'une origine et d'une destination

            // TODO: gérer d'une meilleure façon le cas où l'api ne retourne rien
            LatLng paris = new LatLng(48.8566, 2.35177);
            LatLng origine = new LatLng(48.86, 2.36);
            LatLng destination = new LatLng(48.857460, 2.351670);
            LatLng bhv = new LatLng(48.857734, 2.353692);
            LatLng gervais = new LatLng(48.855731, 2.354099);
            double ux=destination.latitude-origine.latitude;
            double uy=destination.longitude-origine.longitude;
            double ux_unitaire=ux/(Math.sqrt(Math.pow(ux,2)+Math.pow(uy,2)));
            double uy_unitaire=uy/(Math.sqrt(Math.pow(ux,2)+Math.pow(uy,2)));
            double ux_unitaire_normal=-uy_unitaire;
            double uy_unitaire_normal=ux_unitaire;
            double f=0.001;
            LatLng S1 = new LatLng(origine.latitude+f*ux_unitaire_normal, origine.longitude+f*uy_unitaire_normal);
            LatLng S2 = new LatLng(origine.latitude-f*ux_unitaire_normal, origine.longitude-f*uy_unitaire_normal);
            LatLng S3 = new LatLng(destination.latitude+f*ux_unitaire_normal, destination.longitude+f*uy_unitaire_normal);
            LatLng S4 = new LatLng(destination.latitude-f*ux_unitaire_normal, destination.longitude-f*uy_unitaire_normal);
            // Les mettre dans une liste
            List<LatLng> points=new ArrayList<LatLng>();
            List<LatLng> poly=new ArrayList<LatLng>();
            points.add(origine);
            points.add(destination);
            points.add(bhv);
            points.add(gervais);
            poly.add(S1);
            poly.add(S2);
            poly.add(S4);
            poly.add(S3);
            //points.add(paris);
            // Placer la camera au niveau du LatLng paris
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paris, 18));
            //Ajouter les marqueurs de la liste
            for(int i=0; i < points.size() ;i++) {
                mMap.addMarker(new MarkerOptions().position(points.get(i)).title("Point"));
            }
            //Dessiner le trajet entre tous les points de la liste
            mMap.addPolyline(new PolylineOptions().addAll(points).width(5).color(Color.RED));
            PolygonOptions rectOptions = new PolygonOptions().addAll(poly).fillColor(Color.BLACK);

            // Get back the mutable Polygon
            Polygon polygon = mMap.addPolygon(rectOptions);
            Boolean boo = PolyUtil.containsLocation(gervais, poly, true);
            Log.d("myTag", "Le point est-il dans le polygone ?"+ boo);
        }
    }
}