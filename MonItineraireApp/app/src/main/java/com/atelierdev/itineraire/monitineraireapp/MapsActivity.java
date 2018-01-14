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
import java.util.Arrays;
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
    public String trajet = "Voici les étapes de votre trajet :";
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
        String pointInt = intent.getStringExtra(MainActivity.EXTRA_POINTSUPP); // DEPRECATED
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

        // Calcul du temps de trajet sans passer par des monuments
        int baseTime = getTimeBase(pointA, pointB, "walking");

        // Temps souhaité par l'utilisateur
        int temps_souhaite_sec = Integer.parseInt(temps_disponible_h.replaceAll("h", "")) * 60 * 60 +
                Integer.parseInt(temps_disponible_min.replaceAll("min", "")) * 60;

        if (baseTime > temps_souhaite_sec){
            setErrorMessage("Vous avez spécifié un temps trop court pour effectuer le trajet.");
            return;
        }

        // Calul le rectangle d'intêret dans lequel l'utilisateur peut voir des batiments
        List<LatLng> rectangleInteret = calculRectangle(pointA, pointB, baseTime, temps_disponible_h, temps_disponible_min);

        // Recupère l'ensemble des monuments utile pour l'utilisateurs
        List<Monument> allRelevantMonument = getMonumentsFromBdd(types);

        // Récupère l'ensemble des monuments intéressants dans la zone d'interet
        List<Monument> relevantMonuments = getMonumentsInZone(allRelevantMonument, rectangleInteret);

        // TODO: AUGMENTER VALEUR A PLUS DE BATIMENT SANS POUR AUTANT PETER L'API
        // On s'intéresse aux 4 premiers monuments pour la contrainte de l'api matrix
        List<Monument> restrainedMonumentList;
        try {
            restrainedMonumentList = relevantMonuments.subList(0, 4);
        } catch (IndexOutOfBoundsException e){
            if (relevantMonuments.size() > 0){
                int i_max = relevantMonuments.size() - 1;
                restrainedMonumentList = relevantMonuments.subList(0, i_max);
            } else {
                restrainedMonumentList = new ArrayList<>();
            }
        }

        List<Monument> monumentsOnPath = new ArrayList<>(restrainedMonumentList);

        // Construction de la liste des coordonnées de l'ensemble des points intéressant + pointA et B
        List<String> listCoords = new ArrayList<>();
        listCoords.add(pointA);
        for (Monument m : restrainedMonumentList){
            listCoords.add(String.valueOf(m.getLat()) + "," + String.valueOf(m.getLon()));
        }
        listCoords.add(pointB);

        // Appelle l'api matrix et retourne la matrice des distances
        List<List<Integer>> matrix = getMatrix(listCoords, listCoords, "walking");

        if(matrix.size() == 0){
            setErrorMessage("La route n'a pas été trouvée suite à un problème interne à l'application");
            return;
        }

        // instancie la classe Trajet pour le calcul du trajet du touriste
        Trajet trajetCalulcator = new Trajet(baseTime, temps_souhaite_sec, restrainedMonumentList, pointA, pointB, matrix, listCoords);

        List<Monument> trajet = trajetCalulcator.getTrajet();

        // Appelle a api direction avec trajet final
        List<String> wayPointsForApi = new ArrayList<>();
        int k = 0;
        for (Monument monument : trajet) {

            this.trajet += "\n\nTemps de marche estimé : " + trajetCalulcator.getTemps_sous_parcours().get(k);
            k += 1;
            wayPointsForApi.add(String.valueOf(monument.getLat()) + "," + monument.getLon());
            // On ajoute le monument à la liste d'étapes du trajet
            this.trajet += "\n\nEtape " + k + ": " + monument.getName();
            this.trajet += "\nTemps de visite estimé : " + trajetCalulcator.getTemps_de_visite().get(k-1)/60 + " min";
        }

        String finalJsonDir = callApiDirectionAndGetJson(pointA, pointB, "walking", wayPointsForApi);

        GoogleApiResultManager managerFinalWay = new GoogleApiResultManager(finalJsonDir);

        try {
            if (!managerFinalWay.isStatusOk()) {
                setErrorMessage(managerFinalWay.getErrorMessage());
                return;
            }
            managerFinalWay.ManageCoordinates();
        } catch (JSONException e) {
            setErrorMessage("La route n'a pas été trouvée suite à un problème interne à l'application");
            return;
        }

        List<LatLng> pointsPath = managerFinalWay.getCoordinatesLatLng();

        // Trace la ligne, ...
        InitalizeMapForPath(pointsPath);

        //Dictionnaire pour associer les ids (valeur) des monuments à leur titre (clé) et les récupérer lors d'un clic sur l'étiquette
        final HashMap<String, String> markerIds = putMarkersOnMonuments(allRelevantMonument, monumentsOnPath);

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

    private void InitalizeMapForPath(List<LatLng> pointsPath){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pointsPath.get(0));
        builder.include(pointsPath.get(pointsPath.size()-1));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20));

        putMarkerOnImportantPoints(pointsPath, "");

        PolylineOptions optionsMap = new PolylineOptions();
        optionsMap.geodesic(true);
        optionsMap.addAll(pointsPath);
        optionsMap.width(5);
        optionsMap.color(Color.BLUE);
        mMap.addPolyline(optionsMap);
    }

    private HashMap<String, String> putMarkersOnMonuments(List<Monument> allRelevantMonument, List<Monument> monumentsPath){
        HashMap<String, String> markerIds = new HashMap<>();

        //Ajoute un marqueur vert aux monuments qui sont dans le trajet et un marqueur jaune sinon
        for (int i = 0; i < allRelevantMonument.size(); i++) {
            Monument monument = allRelevantMonument.get(i);
            LatLng latlng = new LatLng(monument.getLat(), monument.getLon());
            String monument_id_str = Integer.toString(monument.getMonumentId());
            if (monumentsPath.contains(monument)) {
                mMap.addMarker(new MarkerOptions().position(latlng).title(monument.getName()).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latlng)
                        .title(monument.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                markerIds.put(marker.getTitle(), monument_id_str);
//                selected_monuments.add(monument);
            } else {
                Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title(monument.getName()).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                markerIds.put(marker.getTitle(), monument_id_str);
            }
        }
        return  markerIds;
    }

    private String callApiDirectionAndGetJson(String pointA, String pointB, String mode,  List<String> waypoints){
        GoogleApiThread api = new GoogleApiThread(pointA, pointB, mode, waypoints);
        Thread callThread = new Thread(api);

        ExecutorService service = Executors.newSingleThreadExecutor();
        String result;
        try {
            callThread.start();

            // We set here the time maximum for waiting the result from api
            Future<?> f = service.submit(callThread);
            f.get(5, TimeUnit.SECONDS);

            callThread.join();
            result = api.getResult();
        } catch (InterruptedException|ExecutionException e) {
            setErrorMessage("La route n'a pas été trouvée suite à un problème interne à l'application");
            result = "0";
        } catch (TimeoutException t){
            setErrorMessage("La connection internet a été perdue durant le calcul du trajet.");
            result = "0";
        }
        return result;
    }

    private int getTimeBase(String pointA, String pointB, String mode){
        String json = callApiDirectionAndGetJson(pointA, pointB, mode, new ArrayList<String>());
        GoogleApiResultManager manager = new GoogleApiResultManager(json);
        manager.calculTime();
        return manager.getDurationInSeconds();
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

    private List<Monument> getMonumentsInZone(List<Monument> allRelevantMonuments, List<LatLng> zone){
        List<Monument> result = new ArrayList<>();
        for(Monument m : allRelevantMonuments){
            LatLng coordMonument = new LatLng(m.getLat(), m.getLon());
            if(PolyUtil.containsLocation(coordMonument, zone, true)){
                result.add(m);
            }
        }
        return result;
    }

    private List<Monument> getMonumentsFromBdd(List<String> typesMonuments){
        List<Monument> allMonuments = new ArrayList<Monument>();
        String[] types_arg = typesMonuments.toArray(new String[0]);
        String query = "SELECT * FROM Monument WHERE types LIKE ?"; //On écrit la requête à envoyer à la base de données
        for(int i=1;i<types_arg.length;i++){
            query = query.concat(" OR types LIKE ?");
        }
        query = query.concat(" ORDER BY rating DESC");
        List<Monument> allMonumentsOfType = Monument.findWithQuery(Monument.class, query, types_arg);
        Log.d("Monuments", String.valueOf(allMonumentsOfType.size()));
        allMonuments.addAll(allMonumentsOfType);
        return allMonuments;
    }

    private String callApiMatrixAndGetJson(List<String> origins, List<String> destinations, String mode){
        MatrixApiThread api = new MatrixApiThread(origins, destinations, mode);
        Thread callThread = new Thread(api);
        ExecutorService service = Executors.newSingleThreadExecutor();
        String result;
        try {
            callThread.start();

            // We set here the time maximum for waiting the result from api
            Future<?> f = service.submit(callThread);
            f.get(15, TimeUnit.SECONDS);

            callThread.join();
            result = api.getResult();
        } catch (InterruptedException|ExecutionException e) {
            setErrorMessage("La route n'a pas été trouvée suite à un problème interne à l'application");
            result = "0";
        } catch (TimeoutException t){
            setErrorMessage("La connection internet a été perdue durant le calcul du trajet.");
            result = "0";
        }
        return result;
    }

    private List<List<Integer>> getMatrix(List<String> origins, List<String> destinations, String mode){
        String json = callApiMatrixAndGetJson(origins, destinations, mode);
        MatrixApiResultManager manager = new MatrixApiResultManager(json);
        try {
            manager.calculTime();
        } catch (JSONException e){
            setErrorMessage("La route n'a pas été trouvée suite à un problème interne à l'application");
        }
        return manager.getTimesResult();
    }

    private List<LatLng> calculRectangle(String pointA, String pointB, int timeBase, String temps_disponible_h, String temps_disponible_min){
        float duree_trajet_direct = (float) timeBase;
        // TODO : changer pour accepter autre chose que des strings en coordonées (adresse, ..)

        String[] arrayPointA = pointA.split(",");
        String[] arrayPointB = pointB.split(",");

//        LatLng origine = pointsPath.get(0);
        LatLng origine = new LatLng(Double.parseDouble(arrayPointA[0]), Double.parseDouble(arrayPointA[1]));
//        LatLng destination = pointsPath.get(pointsPath.size() - 1);
        LatLng destination = new LatLng(Double.parseDouble(arrayPointB[0]), Double.parseDouble(arrayPointB[1]));

        float[] distance_vol_oiseau_metres = new float[]{12};
        distanceBetween(origine.latitude, origine.longitude, destination.latitude, destination.longitude, distance_vol_oiseau_metres);
        double vitesse_topo = distance_vol_oiseau_metres[0] / duree_trajet_direct;
        String temps_disponible_h_numb = temps_disponible_h.replace("h", "");
        String temps_disponible_min_numb = temps_disponible_min.replace("min", "");
        int temps_disponible_h_int = Integer.parseInt(temps_disponible_h_numb);
        int temps_disponible_min_int = Integer.parseInt(temps_disponible_min_numb);
        int temps_disponible = temps_disponible_h_int * 3600 + temps_disponible_min_int * 60;
        double temps_restant = temps_disponible - duree_trajet_direct;
        double distance_restante = temps_restant * vitesse_topo;

        //Convertit la distance restante en degrés (lat et long) 1 metre = 0.000009° environ
        //Facteur multiplicateur pour la largeur du rectangle
        double coeff = 0.000009 * distance_restante / 2;
        //Facteur multiplicateur pour prendre un rectangle plus long que la distance Origine-Destination
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

        List<LatLng> result = new ArrayList<>(
                Arrays.asList(S1, S2, S3, S4)
        );
        return result;
    }
}