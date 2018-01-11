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
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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


        List<String> types = new ArrayList<String>();
        if(MainActivity.type1)
            types.add("1");
        if(MainActivity.type2)
            types.add("2");
        if(MainActivity.type3)
            types.add("3");
        if(MainActivity.type4)
            types.add("4");
        if(MainActivity.type5)
            types.add("5");
        if(MainActivity.type6)
            types.add("6");
        for(String type : types)
            Log.d("types", type);


        // Transforme le point int en une liste pour pouvoir le passer au thread api
        List<String> listPointsInt = new ArrayList<>();
        if (!pointInt.equals("")) {
            listPointsInt.add(pointInt);
        }

        // Apelle l'api. Voir documentation dans GoogleApiThread
        GoogleApiThread api = new GoogleApiThread(pointA, pointB, "walking", listPointsInt);
        Thread callThread = new Thread(api);


        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            callThread.start();

            // We set here the time maximum for waiting the result from api
            Future<?> f = service.submit(callThread);
            f.get(5, TimeUnit.SECONDS);

            callThread.join();
        } catch (InterruptedException|ExecutionException e) {
            setErrorMessage("La route n'a pas été trouvée suite à un problème interne à l'application");
            return;
        } catch (TimeoutException t){
            setErrorMessage("La connection internet a été perdue durant le calcul du trajet.");
            return;
        }


        String result = api.getResult();
        GoogleApiResultManager manageJson = new GoogleApiResultManager(result);

        try {
            if (!manageJson.isStatusOk()) {
                setErrorMessage(manageJson.getErrorMessage());
                return;
            }
            manageJson.ManageCoordinates();
        } catch (JSONException e) {
            setErrorMessage("La route n'a pas été trouvée suite à un problème interne à l'application");
            return;
        }

        List<LatLng> pointsPath = manageJson.getCoordinatesLatLng();

        // set camera on start point
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointsPath.get(0), 18));

        putMarkerOnImportantPoints(pointsPath, pointInt);

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
        LatLng destination = pointsPath.get(pointsPath.size() - 1);
        float[] distance_vol_oiseau_metres = new float[]{12};
        distanceBetween(origine.latitude, origine.longitude, destination.latitude, destination.longitude, distance_vol_oiseau_metres);
        double vitesse_topo = distance_vol_oiseau_metres[0] / duree_trajet_direct;
        String temps_disponible_h_numb = temps_disponible_h.replace("h", "");
        String temps_disponible_min_numb = temps_disponible_min.replace("min", "");
        int temps_disponible_h_int = Integer.parseInt(temps_disponible_h_numb);
        int temps_disponible_min_int = Integer.parseInt(temps_disponible_min_numb);
        int temps_disponible = temps_disponible_h_int * 3600 + temps_disponible_min_int * 60;
        //Log.d("myTag2", "en secondes"+ temps_disponible_test);
        double temps_restant = temps_disponible - duree_trajet_direct;
        double distance_restante = temps_restant * vitesse_topo;

        //Convertit la distance restante en degrés (lat et long) 1 metre = 0.000009° environ
        double coeff = 0.000009 * distance_restante / 2;

        //Calcule les sommets du rectangle
        double ux = destination.latitude - origine.latitude;
        double uy = destination.longitude - origine.longitude;
        double ux_unitaire = ux / (Math.sqrt(Math.pow(ux, 2) + Math.pow(uy, 2)));
        double uy_unitaire = uy / (Math.sqrt(Math.pow(ux, 2) + Math.pow(uy, 2)));
        double ux_unitaire_normal = -uy_unitaire;
        double uy_unitaire_normal = ux_unitaire;
        LatLng S1 = new LatLng(origine.latitude + coeff * ux_unitaire_normal, origine.longitude + coeff * uy_unitaire_normal);
        LatLng S2 = new LatLng(origine.latitude - coeff * ux_unitaire_normal, origine.longitude - coeff * uy_unitaire_normal);
        LatLng S3 = new LatLng(destination.latitude + coeff * ux_unitaire_normal, destination.longitude + coeff * uy_unitaire_normal);
        LatLng S4 = new LatLng(destination.latitude - coeff * ux_unitaire_normal, destination.longitude - coeff * uy_unitaire_normal);
        List<LatLng> poly = new ArrayList<LatLng>();
        poly.add(S1);
        poly.add(S2);
        poly.add(S4);
        poly.add(S3);
        PolygonOptions rectOptions = new PolygonOptions().addAll(poly).strokeColor(Color.argb(0, 50, 0, 255)).fillColor(Color.argb(70, 50, 0, 255));
        Polygon polygon = mMap.addPolygon(rectOptions);

        //Liste de monuments obtenu via appel à la base de données
        List<Monument> liste_monuments = new ArrayList<Monument>();
        for(int i = 0; i < types.size(); i++){
            List<Monument> allMonumentsOfType = Monument.findWithQuery(Monument.class, "Select * from Monument where types LIKE ?", "%"+types.get(i)+"%");
            liste_monuments.addAll(allMonumentsOfType);
        }

        List<LatLng> selected_monuments = new ArrayList<LatLng>();

        //On parcourt la liste de monuments et on retire ceux qui ne sont pas dans la zone
        for (int i = 0; i < liste_monuments.size(); i++) {
            Monument monument = liste_monuments.get(i);
            LatLng latlng = new LatLng(monument.getLat(), monument.getLon());
            if (PolyUtil.containsLocation(latlng, poly, true)) {
                mMap.addMarker(new MarkerOptions().position(latlng).title(monument.getName()).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            } else {
                mMap.addMarker(new MarkerOptions().position(latlng).title(monument.getName()).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                liste_monuments.remove(monument);
            }
        }

        // Si le monument est dans le rectangle il est ajouté à la liste des monuments sélectionnés et son marqueur est vert (sinon il est jaune)
        /*for (int i = 0; i < liste_monuments.size(); i++) {
            if (PolyUtil.containsLocation(liste_monuments.get(i), poly, true)) {
                mMap.addMarker(new MarkerOptions().position(liste_monuments.get(i)).title("Le point est dans la zone").icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                selected_monuments.add(liste_monuments.get(i));
            } else {
                mMap.addMarker(new MarkerOptions().position(liste_monuments.get(i)).title("Le point n'est PAS dans la zone").icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            }
        }*/
        Log.d("myTag2", "Monuments sélectionnés" + selected_monuments);

    }

    private void setErrorMessage(String message) {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getView().setVisibility(View.GONE);
        TextView error = findViewById(R.id.map_error_message);
        error.setText(message);
        error.setVisibility(View.VISIBLE);
    }

    private void putMarkerOnImportantPoints(List<LatLng> pointsPath, String wayPoint){
        // Ajoute un marqueur pour point de départ, arrivée et intermédiaire
        mMap.addMarker(new MarkerOptions().position(pointsPath.get(0)).title("Départ"));
        int indexLastPoint = pointsPath.size() - 1;
        mMap.addMarker(new MarkerOptions().position(pointsPath.get(indexLastPoint)).title("Arrivée"));
        //Ajoute un marqueur pour le point intérmédiaire
        if (!wayPoint.equals("")) {
            String[] arrayPointInt = wayPoint.split(",");
            LatLng pointInter = new LatLng(Double.parseDouble(arrayPointInt[0]), Double.parseDouble(wayPoint.split(",")[1]));
            mMap.addMarker(new MarkerOptions().position(pointInter));
        }
    }
}