package com.atelierdev.itineraire.monitineraireapp;

import android.annotation.SuppressLint;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.location.Location.distanceBetween;
import static com.atelierdev.itineraire.monitineraireapp.MainActivity.EXTRA_MONUMENT_ID;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String EXTRA_TRAJET = "com.atelierdev.itineraire.monitineraireapp.trajet";
    public String trajet = "Voici les étapes de votre trajet";
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
    @SuppressLint("MissingPermission")
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

        // Récupère la liste des types qui ont été sélectionné
        List<String> types = new ArrayList<>();
        if(MainActivity.type1)
            types.add("%1%");
        if(MainActivity.type2)
            types.add("%2%");
        if(MainActivity.type3)
            types.add("%3%");
        if(MainActivity.type4)
            types.add("%4%");
        if(MainActivity.type5)
            types.add("%5%");
        if(MainActivity.type6)
            types.add("%6%");

        if(LocalizationHandler.isGrantedPermission(this))
        {
            mMap.setMyLocationEnabled(true);
        }


        // Transforme le point int en une liste pour pouvoir le passer au thread api
        List<String> listPointsInt = new ArrayList<>();
        if (!pointInt.equals("")) {
            listPointsInt.add(pointInt);
        }

        // Apelle l'api direction. Voir documentation dans GoogleApiThread
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

        //Regler la camera pour afficher tout le trajet
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pointsPath.get(0));
        builder.include(pointsPath.get(pointsPath.size()-1));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20));

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
        //Facteur multiplicateur pour la largeur du rectangle
        double coeff = 0.000009 * distance_restante / 2;
        //Facteur multiplicateur pour prendre un rectangle plus long que la distance Origine-Destination (dépasse de 100 m de chaque coté)
        double coeff_2= 0.000009 * 200;

        //Calcule les sommets du rectangle
        double ux = destination.latitude - origine.latitude;
        double uy = destination.longitude - origine.longitude;
        double ux_unitaire = ux / (Math.sqrt(Math.pow(ux, 2) + Math.pow(uy, 2)));
        double uy_unitaire = uy / (Math.sqrt(Math.pow(ux, 2) + Math.pow(uy, 2)));
        double ux_unitaire_normal = -uy_unitaire;
        double uy_unitaire_normal = ux_unitaire;
        LatLng S1 = new LatLng(origine.latitude - coeff_2*ux_unitaire + coeff * ux_unitaire_normal, origine.longitude - coeff_2*uy_unitaire + coeff * uy_unitaire_normal);
        LatLng S2 = new LatLng(origine.latitude - coeff_2*ux_unitaire - coeff * ux_unitaire_normal, origine.longitude - coeff_2*uy_unitaire - coeff * uy_unitaire_normal);
        LatLng S3 = new LatLng(destination.latitude + coeff_2*ux_unitaire + coeff * ux_unitaire_normal, destination.longitude + coeff_2*uy_unitaire + coeff * uy_unitaire_normal);
        LatLng S4 = new LatLng(destination.latitude + coeff_2*ux_unitaire - coeff * ux_unitaire_normal, destination.longitude + coeff_2*uy_unitaire - coeff * uy_unitaire_normal);

        //Si la largeur du rectangle est supérieure à la distance entre origine et destination on va chercher aussi des monuments qui ne sont pas placés le long du trajet
        float[] largeur_rectangle = new float[]{12};
        distanceBetween(S1.latitude, S1.longitude, S2.latitude, S2.longitude, largeur_rectangle);
        if (largeur_rectangle[0]>distance_vol_oiseau_metres[0]) {
            coeff = 0.000009 * distance_restante / 4;
            coeff_2= coeff;
            LatLng S1b = new LatLng(origine.latitude - coeff_2*ux_unitaire + coeff * ux_unitaire_normal, origine.longitude - coeff_2*uy_unitaire + coeff * uy_unitaire_normal);
            LatLng S2b = new LatLng(origine.latitude - coeff_2*ux_unitaire - coeff * ux_unitaire_normal, origine.longitude - coeff_2*uy_unitaire - coeff * uy_unitaire_normal);
            LatLng S3b = new LatLng(destination.latitude + coeff_2*ux_unitaire + coeff * ux_unitaire_normal, destination.longitude + coeff_2*uy_unitaire + coeff * uy_unitaire_normal);
            LatLng S4b = new LatLng(destination.latitude + coeff_2*ux_unitaire - coeff * ux_unitaire_normal, destination.longitude + coeff_2*uy_unitaire - coeff * uy_unitaire_normal);
            S1=S1b;
            S2=S2b;
            S3=S3b;
            S4=S4b;
        }
        List<LatLng> poly = new ArrayList<LatLng>();
        poly.add(S1);
        poly.add(S2);
        poly.add(S4);
        poly.add(S3);
        PolygonOptions rectOptions = new PolygonOptions().addAll(poly).strokeColor(Color.argb(0, 50, 0, 255)).fillColor(Color.argb(70, 50, 0, 255));
        Polygon polygon = mMap.addPolygon(rectOptions);


        //Liste de monuments obtenu via appel à la base de données
        List<Monument> allMonuments = new ArrayList<Monument>();
        String[] types_arg = types.toArray(new String[0]);
        String query = "SELECT * FROM Monument WHERE types LIKE ?"; //On écrit la requête à envoyer à la base de données
        for(int i=1;i<types_arg.length;i++){
            query += " OR types LIKE ?";
        }
        query += " ORDER BY rating";
        List<Monument> allMonumentsOfType = Monument.findWithQuery(Monument.class, query, types_arg);
        Log.d("Monuments", String.valueOf(allMonumentsOfType.size()));
        allMonuments.addAll(allMonumentsOfType);

        List<Monument> selected_monuments = new ArrayList<Monument>();

        //Dictionnaire pour associer les ids (valeur) des monuments à leur titre (clé) et les récupérer lors d'un clic sur l'étiquette
        final HashMap<String, String> markerIds = new HashMap<>();

        //Ajoute un marqueur vert aux monuments qui sont dans la zone et un marqueur jaune sinon
        for (int i = 0; i < allMonuments.size(); i++) {
            Monument monument = allMonuments.get(i);
            LatLng latlng = new LatLng(monument.getLat(), monument.getLon());
            String monument_id_str = Integer.toString(monument.getMonumentId());
            if (PolyUtil.containsLocation(latlng, poly, true)) {
                mMap.addMarker(new MarkerOptions().position(latlng).title(monument.getName()).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latlng)
                        .title(monument.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                markerIds.put(marker.getTitle(), monument_id_str);
                selected_monuments.add(monument);
            } else {
                Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title(monument.getName()).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                markerIds.put(marker.getTitle(), monument_id_str);
            }
        }

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker.getTitle().equals("Départ") || marker.getTitle().equals("Arrivée")){
                    return;
                }

                Intent intent = new Intent(getBaseContext(), DisplayInfoMonument.class);
                intent.putExtra(EXTRA_MONUMENT_ID, markerIds.get(marker.getTitle()));
                // Starting the  Activity
                startActivity(intent);
            }
        });
    }

    public void displaySteps(View view){
        Intent intent = new Intent(getBaseContext(), DisplayStepsActivity.class);
        intent.putExtra(EXTRA_TRAJET, trajet);
        // Starting the  Activity
        startActivity(intent);
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