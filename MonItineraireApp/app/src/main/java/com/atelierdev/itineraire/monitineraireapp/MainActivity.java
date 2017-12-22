package com.atelierdev.itineraire.monitineraireapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_POINTA = "com.atelierdev.itineraire.monitineraireapp.pointA";
    public static final String EXTRA_POINTB = "com.atelierdev.itineraire.monitineraireapp.pointB";
    public static final String EXTRA_POINTSUPP = "com.atelierdev.itineraire.monitineraireapp.pointInt";
    public static final String EXTRA_MONUMENT = "com.atelierdev.itineraire.monitineraireapp.monument";

    private boolean useMyLocForMap = false;
    private boolean useWayPoint = false;

    //Location
    // https://stackoverflow.com/questions/42218419/how-do-i-implement-the-locationlistener
    // https://developer.android.com/reference/android/location/LocationManager.html#getLastKnownLocation(java.lang.String)
    LocationManager locationManager;
    Context mContext;

    private Double longitudeUser;
    private Double latitudeUser;

    private LatLng startPoint;
    private LatLng middlePoint;
    private LatLng endPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create location manager
        mContext=this;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                    2000,
                    10, locationListenerGPS);
        } catch (SecurityException e){ /*TODO Arnaud*/}
        finally {
            isLocationEnabled();
        }

        // Initialise les inputs avec auto complétion
        initAutoCompleteFragments();

        // Initialise l'ensemble de la page pour l'affichage standard
        initViewsAll();
    }

    @Override
    protected void onResume(){
        super.onResume();
        initViewsAll();
    }

    /**
     *
     *
     * INTENTS LAUNCH
     *
     * Méthode appelées qui mènent à l'execution d'un nouvelle activitée
     *
     *
     */


    /**
     * Méthode appelée lors du click sur le bouton "Afficher mon trajet" (id displayMap)
     * @param view
     */
    public void displayMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);


        String pointA;

        // Initie point B. Si aucun point n'a été récupéré par l'autocomplétion, this.endPoint es null
        // et on met pointB à ""
        String pointB = this.endPoint != null ?
                String.valueOf(this.endPoint.latitude) + "," + String.valueOf(this.endPoint.longitude):
                "";

        // De même pour pointInt
        String pointInt = this.middlePoint != null ?
                String.valueOf(this.middlePoint.latitude) + "," + String.valueOf(this.middlePoint.longitude):
                "";

        // Si l'utilisateur souhaite utiliser sa localisation, on set le pointA aux valeurs de
        // ses coordonnées. Sinon, on effectue le même travail que pour le pointB.
        if(this.useMyLocForMap){
            pointA = String.valueOf(this.latitudeUser) + "," + String.valueOf(this.longitudeUser);
        } else {
            pointA = this.startPoint != null ?
                    String.valueOf(this.startPoint.latitude) + "," + String.valueOf(this.startPoint.longitude) :
                    "";
        }

        boolean searchAllowed = true;

        // Si l'un des deux points n'est pas spécifié, il est impossible de calculer le trajet.
        if(TextUtils.isEmpty(pointA) || TextUtils.isEmpty(pointB)){
            searchAllowed = false;
        }

        if (this.useMyLocForMap){
            // Si l'utilisateur veut utiliser sa position mais qu'on ne la connait pas,
            // il est impossible de calculer le trajet.
            if (this.latitudeUser == null || this.longitudeUser == null){
                searchAllowed = false;
            }
        }

        if(!searchAllowed){
            // Si la recherche n'est pas possible, on affiche un message Toast
            String msg="Impossible d'effectuer une recherche !";
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        } else {
            // on masque tout et on affiche la barre de chargement
            changeVisibilityAll(View.GONE);
            ProgressBar p = (ProgressBar)findViewById(R.id.progressBar1);
            p.setVisibility(View.VISIBLE);

            // On vide le startPoint et endPoint pour que les valeurs ne restent pas associées
            // lors d'une seconde recherche incorrecte (ex l'utilisateur n'a pas respecifié de point)
            this.startPoint = null;
            this.endPoint = null;

            // On vide la valeur affichée des fragments d'autocomplétion
            clearOutFragments();

            // On démarre l'activité map.
            intent.putExtra(EXTRA_POINTA, pointA);
            intent.putExtra(EXTRA_POINTB, pointB);
            intent.putExtra(EXTRA_POINTSUPP, pointInt);
            startActivity(intent);
        }
    }

    /**
     * Called when the user press info button
     */
    public void displayInfo(View view){
        Intent intent = new Intent(this, DisplayInfoMonument.class);
        EditText editMonument = (EditText) findViewById(R.id.monument);


        String monument = editMonument.getText().toString();

        intent.putExtra(EXTRA_MONUMENT, monument);
        startActivity(intent);
    }

    /**
     *
     * BUTTONS AND CHECKBOX
     * Méthodes "internes" à l'activité appelés lorsque l'utilisateur
     * effectue une action sur une checkbox ou un bouton,
     * ne menant pas à l'execution d'une activitées
     *
     *
     */

    /**
     * Méthode appelée lorsque l'utilisateur presse le point bouton ajour point int
     */
    public void showFieldWayPoint(View view){
        TextView textViewInterm = (TextView) findViewById(R.id.pointIntTextView);
        PlaceAutocompleteFragment fragmentPointInterm = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointInt);

        Button buttonPointInterm = (Button) findViewById(R.id.alternative_path);

        if (!this.useWayPoint){
            // TODO arnaud: use litterals and check exceptions
            fragmentPointInterm.getView().setVisibility(View.VISIBLE);
            textViewInterm.setVisibility(View.VISIBLE);
            buttonPointInterm.setText("- Point");
            this.useWayPoint = true;
        } else {
            fragmentPointInterm.getView().setVisibility(View.GONE);
            textViewInterm.setVisibility(View.GONE);
            fragmentPointInterm.setText("");
            buttonPointInterm.setText("+ Point");
            this.useWayPoint = false;
        }
    }

    /**
     * Méthode appelée lorsque l'utilisateur fait appel à la géolocalisation
     * Si la checkbox "Utiliser ma localisation" est cochée,
     * affiche la geolocalisation de l'utilisateur, sinon reaffiche l'input
     */
    public void onUseDeviceLocationClick(View view){
        CheckBox checkBoxPos = (CheckBox) findViewById(R.id.use_loc);
        EditText altText = (EditText) findViewById(R.id.pointA_alt);

        Fragment fr = getFragmentManager().findFragmentById(R.id.pointA);

        // TODO arnaud: use litterals and check exceptions
        if (checkBoxPos.isChecked()){
            this.useMyLocForMap = true;
            fr.getView().setVisibility(View.GONE);
            altText.setVisibility(View.VISIBLE);
            updateLoc();
            // Si la position n'a pas pu être récupérée, on informe l'utilisateur que
            // sa loc est impossible
            if (this.longitudeUser == null || this.latitudeUser == null){
                altText.setText("Localisation impossible");
            } else {
                altText.setText(this.latitudeUser + "," + this.longitudeUser);
            }
        } else {
            altText.setText("");
            altText.setVisibility(View.GONE);
            fr.getView().setVisibility(View.VISIBLE);
            this.useMyLocForMap = false;
        }
    }


    /**
     *
     * UTILS
     * Méthodes privées utilisées dans les différentes fonctions ci dessous
     *
     */



    /**
     * Essaie de récuperer la dernière position connue
      */
    private void updateLoc() {
        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null){
                setLatitudeUser(location.getLatitude());
                setLongitudeUser(location.getLongitude());
            }
        } catch (SecurityException e) {/*TODO arnaud*/}
    }

    // Location: initialise le gps listener qui va être utilisé lors de l'initalisation dans onCreate
    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude=location.getLatitude();
            setLatitudeUser(latitude);
            double longitude=location.getLongitude();
            setLongitudeUser(longitude);
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

    // Check si la geoloc est active sur l'appareil de l'utilisateur
    // Appelée uniquement sur onCreate
    // TODO: voir si utile -> l'application peut elle être utilisée sans localisation?
    private void isLocationEnabled() {
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(mContext);
            alertDialog.setTitle("Activer la localisation");
            alertDialog.setMessage("La localisation est désactivée dans vos paramètres. Merci de l'activer");
            alertDialog.setPositiveButton("Paramètres de localisation", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            alert.show();
        }
    }



    /**
     * Methode utilisée pour changer la visibilté de tous les éléments de la main page,
     * sauf la barre de chargement
     */
    private void changeVisibilityAll(int value){
        // TODO Arnaud: clean

        // FRAMGENTS
        PlaceAutocompleteFragment autocompleteStartPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointA);

        PlaceAutocompleteFragment autocompleteEndPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointB);

        PlaceAutocompleteFragment autocompleteWayPathPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointInt);

        // TextsView
        TextView pointA_alt = (TextView) findViewById(R.id.pointATextView);
        TextView pointIntTxtView = (TextView) findViewById(R.id.pointIntTextView);
        TextView pointBTxtView = (TextView) findViewById(R.id.pointBTextBox);
        TextView txtViewMonument = (TextView) findViewById(R.id.textViewmonument);

        //Buttons
        Button buttonAltPath = (Button) findViewById(R.id.alternative_path);
        Button displayPath = (Button) findViewById(R.id.displayMap);
        Button infoMunum = (Button) findViewById(R.id.infoMonument);

        // CheckBox
        CheckBox checkBoxPos = (CheckBox) findViewById(R.id.use_loc);

        //EditTexts
        EditText txtMonument = (EditText) findViewById(R.id.monument);
        EditText editPointAalt = (EditText) findViewById(R.id.pointA_alt);

        autocompleteEndPoint.getView().setVisibility(value);
        autocompleteStartPoint.getView().setVisibility(value);
        autocompleteWayPathPoint.getView().setVisibility(value);
        pointA_alt.setVisibility(value);
        pointIntTxtView.setVisibility(value);
        pointBTxtView.setVisibility(value);
        txtViewMonument.setVisibility(value);
        buttonAltPath.setVisibility(value);
        displayPath.setVisibility(value);
        infoMunum.setVisibility(value);
        checkBoxPos.setVisibility(value);
        txtMonument.setVisibility(value);
        editPointAalt.setVisibility(value);
    }

    /**
     * Méthode utilisée pour initialiser la page
     */
    private void initViewsAll(){
        changeVisibilityAll(View.VISIBLE);

        EditText textAltPointA = (EditText) findViewById(R.id.pointA_alt);
        textAltPointA.setVisibility(View.GONE);

        TextView textViewInt = (TextView) findViewById(R.id.pointIntTextView);
        textViewInt.setVisibility(View.GONE);

        PlaceAutocompleteFragment autocompleteWayPathPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointInt);
        autocompleteWayPathPoint.getView().setVisibility(View.GONE);

        // Cache la barre de chargement
        ProgressBar p = (ProgressBar)findViewById(R.id.progressBar1);
        p.setVisibility(View.GONE);
    }

    /**
     * Utilisé pour initialiser les differents inputs avec autocomplétion
     */
    private void initAutoCompleteFragments(){
        // Auto complétion pour chaque point
        PlaceAutocompleteFragment autocompleteStartPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointA);

        PlaceAutocompleteFragment autocompleteEndPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointB);

        PlaceAutocompleteFragment autocompleteWayPathPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointInt);

        /*
        * Filter: allows returning only results with a precise address.
        */
        AutocompleteFilter addressFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();

        autocompleteStartPoint.setFilter(addressFilter);
        autocompleteEndPoint.setFilter(addressFilter);
        autocompleteWayPathPoint.setFilter(addressFilter);

        // Listeners pour autocomplete point: permet de récupérer les valeurs selectionnées
        autocompleteStartPoint.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                setStartPoint(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });

        autocompleteEndPoint.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                setEndPoint(place.getLatLng());
            }

            @Override
            public void onError(Status status) {
                // TODO
            }
        });

        autocompleteWayPathPoint.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                setMiddlePoint(place.getLatLng());
            }

            @Override
            public void onError(Status status) {

            }
        });
    }

    /**
     * Méthode utilisée pour nettoyer les valeurs des fragments
     */
    private void clearOutFragments(){
        PlaceAutocompleteFragment autocompleteStartPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointA);

        PlaceAutocompleteFragment autocompleteEndPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointB);

        PlaceAutocompleteFragment autocompleteWayPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointInt);

        autocompleteStartPoint.setText("");
        autocompleteEndPoint.setText("");
        autocompleteWayPoint.setText("");
    }

    /**
     *
     *
     * GETTERS AND SETTERS
     *
     *
     */

    public void setLongitudeUser(Double longitudeUser) {
        this.longitudeUser = longitudeUser;
    }

    public void setLatitudeUser(Double latitudeUser) {
        this.latitudeUser = latitudeUser;
    }

    public void setStartPoint(LatLng startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndPoint(LatLng endPoint) {
        this.endPoint = endPoint;
    }

    public void setMiddlePoint(LatLng middlePoint) {
        this.middlePoint = middlePoint;
    }
}
