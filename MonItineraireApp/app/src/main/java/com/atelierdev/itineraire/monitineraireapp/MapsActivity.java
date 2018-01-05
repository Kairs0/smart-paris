package com.atelierdev.itineraire.monitineraireapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
//import google.maps.geometry.encoding;


import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.location.Location.distanceBetween;


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
        String temps_disponible_h = intent.getStringExtra(MainActivity.TEMPS_DISPONIBLE_H);
        String temps_disponible_min = intent.getStringExtra(MainActivity.TEMPS_DISPONIBLE_MIN);

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
            if (!manageJson.isStatusOk()){
                setErrorMessage(manageJson.getErrorMessage());
                return;
            }

            try {
                manageJson.ManageCoordinates();
            } catch (JSONException e){
                setErrorMessage("ERREUR JSON");
                return;
            }

            List<LatLng> pointsPath = manageJson.getCoordinatesLatLng();

            // set camera on start point
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointsPath.get(0), 18));

            // Ajoute un marqueur pour le point de départ et d'arrivée
            mMap.addMarker(new MarkerOptions().position(pointsPath.get(0)).title("Point"));
            int indexLastPoint = pointsPath.size() - 1;
            mMap.addMarker(new MarkerOptions().position(pointsPath.get(indexLastPoint)).title("Point"));

            //Ajoute un marqueur pour le point intérmédiaire
            if (!pointInt.equals("")) {
                String[] arrayPointInt = pointInt.split(",");
                LatLng pointInter = new LatLng(Double.parseDouble(arrayPointInt[0]), Double.parseDouble(pointInt.split(",")[1]));
                mMap.addMarker(new MarkerOptions().position(pointInter));
            }

            PolylineOptions optionsMap = new PolylineOptions();
            optionsMap.geodesic(true);
            optionsMap.addAll(pointsPath);
            optionsMap.width(5);
            optionsMap.color(Color.BLUE);

            mMap.addPolyline(optionsMap);

            //Affiche le rectangle de sélection des monuments
            manageJson.calculTime();
            float duree_trajet_direct = manageJson.getDurationInSeconds();
            LatLng origine = pointsPath.get(0);
            LatLng destination = pointsPath.get(pointsPath.size()-1);
            float[] distance_vol_oiseau_metres=new float[]{12};
            distanceBetween(origine.latitude, origine.longitude, destination.latitude, destination.longitude, distance_vol_oiseau_metres);
            double vitesse_topo = distance_vol_oiseau_metres[0]/duree_trajet_direct;
            String temps_disponible_h_numb=temps_disponible_h.replace("h", "");
            String temps_disponible_min_numb=temps_disponible_min.replace("min", "");
            int temps_disponible_h_int=Integer.parseInt(temps_disponible_h_numb);
            int temps_disponible_min_int=Integer.parseInt(temps_disponible_min_numb);
            int temps_disponible=temps_disponible_h_int*3600+temps_disponible_min_int*60;
            //Log.d("myTag2", "en secondes"+ temps_disponible_test);
            double temps_restant=temps_disponible-duree_trajet_direct;
            double distance_restante=temps_restant*vitesse_topo;

            //Convertit la distance restante en degrés (lat et long) 1 metre = 0.000009° environ
            double coeff=0.000009*distance_restante/2;

            //Calcule les sommets du rectangle
            double ux=destination.latitude-origine.latitude;
            double uy=destination.longitude-origine.longitude;
            double ux_unitaire=ux/(Math.sqrt(Math.pow(ux,2)+Math.pow(uy,2)));
            double uy_unitaire=uy/(Math.sqrt(Math.pow(ux,2)+Math.pow(uy,2)));
            double ux_unitaire_normal=-uy_unitaire;
            double uy_unitaire_normal=ux_unitaire;
            LatLng S1 = new LatLng(origine.latitude+coeff*ux_unitaire_normal, origine.longitude+coeff*uy_unitaire_normal);
            LatLng S2 = new LatLng(origine.latitude-coeff*ux_unitaire_normal, origine.longitude-coeff*uy_unitaire_normal);
            LatLng S3 = new LatLng(destination.latitude+coeff*ux_unitaire_normal, destination.longitude+coeff*uy_unitaire_normal);
            LatLng S4 = new LatLng(destination.latitude-coeff*ux_unitaire_normal, destination.longitude-coeff*uy_unitaire_normal);
            List<LatLng> poly=new ArrayList<LatLng>();
            poly.add(S1);
            poly.add(S2);
            poly.add(S4);
            poly.add(S3);
            PolygonOptions rectOptions = new PolygonOptions().addAll(poly).strokeColor(Color.argb(0, 50, 0, 255)).fillColor(Color.argb(70, 50, 0, 255));
            Polygon polygon = mMap.addPolygon(rectOptions);

            //Liste de monuments fictifs à remplacer par la notre
            List<LatLng> liste_monuments=new ArrayList<LatLng>();
            LatLng m1 = new LatLng(48.8566, 2.35177);
            LatLng m2 = new LatLng(48.86, 2.36);
            LatLng m3 = new LatLng(48.857460, 2.351670);
            LatLng m4 = new LatLng(48.875788, 2.308562);
            LatLng m5 = new LatLng(48.857606, 2.300345);
            LatLng m6 = new LatLng(48.845651, 2.309428);
            LatLng m7 = new LatLng(48.836900, 2.351453);
            LatLng m8 = new LatLng(48.883881, 2.349439);
            LatLng m9 = new LatLng(48.866756, 2.365709);
            LatLng m10 = new LatLng(48.855809, 2.334118);
            LatLng m11 = new LatLng(48.861531, 2.333718);
            LatLng m12 = new LatLng(48.854057, 2.347342);
            liste_monuments.add(m1);
            liste_monuments.add(m2);
            liste_monuments.add(m3);
            liste_monuments.add(m4);
            liste_monuments.add(m5);
            liste_monuments.add(m6);
            liste_monuments.add(m7);
            liste_monuments.add(m8);
            liste_monuments.add(m9);
            liste_monuments.add(m10);
            liste_monuments.add(m11);
            liste_monuments.add(m12);

            List<LatLng> selected_monuments=new ArrayList<LatLng>();


            // Si le monument est dans le rectangle il est ajouté à la liste des monuments sélectionnés et son marqueur est vert (sinon il est jaune)
            for(int i=0; i < liste_monuments.size() ;i++) {
                if (PolyUtil.containsLocation(liste_monuments.get(i), poly, true)) {
                    mMap.addMarker(new MarkerOptions().position(liste_monuments.get(i)).title("Le point est dans la zone").icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    selected_monuments.add(liste_monuments.get(i));
                }
                else {
                    mMap.addMarker(new MarkerOptions().position(liste_monuments.get(i)).title("Le point n'est PAS dans la zone").icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));  }
            }
            Log.d("myTag2", "Monuments sélectionnés"+ selected_monuments);

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: gérer d'une meilleure façon le cas où l'api ne retourne rien
        }
    }

    private void setErrorMessage(String message){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getView().setVisibility(View.GONE);
        TextView error = findViewById(R.id.map_error_message);
        error.setText(message);
        error.setVisibility(View.VISIBLE);
    }
}