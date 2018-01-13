package com.atelierdev.itineraire.monitineraireapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;




public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_POINTA = "com.atelierdev.itineraire.monitineraireapp.pointA";
    public static final String EXTRA_POINTB = "com.atelierdev.itineraire.monitineraireapp.pointB";
    public static final String EXTRA_POINTSUPP = "com.atelierdev.itineraire.monitineraireapp.pointInt";
    public static final String EXTRA_MONUMENT_ID = "com.atelierdev.itineraire.monitineraireapp.monument_id";
    public static final String TEMPS_DISPONIBLE_H = "com.atelierdev.itineraire.monitineraireapp.temps_disponible_h";
    public static final String TEMPS_DISPONIBLE_MIN = "com.atelierdev.itineraire.monitineraireapp.temps_disponible_min";

    private boolean useMyLocForMap = false;
    private boolean useWayPoint = false;

    public static boolean type1 = true;
    public static boolean type2 = true;
    public static boolean type3 = true;
    public static boolean type4 = true;
    public static boolean type5 = true;
    public static boolean type6 = true;

    LocationManager locationManager;
    Context mContext;

    private Double longitudeUser;
    private Double latitudeUser;

    private LatLng startPoint;
    private LatLng middlePoint;
    private LatLng endPoint;

    Spinner spinnerhour;
    Spinner spinnermin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialise la base de données (pour l'instant systématique mais voir à quelle frquence on le fait)
        DatabaseHandler.Initialize(getBaseContext());
        
        initSpinners();

        // Create location manager
        mContext=this;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        if(!LocalizationHandler.isGrantedPermission(this))
            LocalizationHandler.requestPermissionIfNotGranted(this);
        /*try {
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                    2000,
                    10, locationListenerGPS);
        } catch (SecurityException e){
            Log.d("mainOnCreate_err", e.getMessage());
        }
        finally {
            isLocationEnabled();
        }*/

        // Initialise les inputs avec auto complétion
        initAutoCompleteFragments();

        // Initialise l'ensemble de la page pour l'affichage standard
        initViewsAll();
    }

    @Override
    protected void onResume(){
        super.onResume();
        initViewsAll();
        if (this.useMyLocForMap){
            replacePointAfragmentByAlt();
        }
    }

    /**
     *
     *
     * INTENTS LAUNCH
     *
     * Méthodes appelées qui mènent à l'execution d'un nouvelle activitée
     *
     *
     */


    /**
     * Méthode appelée lors du click sur le bouton "Afficher mon trajet" (id displayMap)
     * @param view
     */
    public void displayMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);

        String temps_disponible_h = spinnerhour.getSelectedItem().toString();
        String temps_disponible_min = spinnermin.getSelectedItem().toString();
        String pointA;

        // Initie point B. Si aucun point n'a été récupéré par l'autocomplétion, this.endPoint est null
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

        // Si l'utilisateur veut utiliser sa position mais qu'on ne la connait pas,
        // il est impossible de calculer le trajet.
        if (this.useMyLocForMap){
            if (this.latitudeUser == null || this.longitudeUser == null){
                searchAllowed = false;
            }
        }

        if(!searchAllowed){
            // Si la recherche n'est pas possible, on affiche un message Toast et on arrête l'opération
            String msg="Impossible d'effectuer une recherche !";
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
            return;
        }

        // on masque tout et on affiche la barre de chargement
        changeVisibilityAll(View.GONE);
        ProgressBar p = findViewById(R.id.progressBar1);
        p.setVisibility(View.VISIBLE);

        // On vide le startPoint et endPoint pour que les valeurs ne restent pas associées
        // lors d'une seconde recherche incorrecte (ex l'utilisateur n'a pas respecifié de point)
        this.startPoint = null;
        this.endPoint = null;

        // On reset la valeur "utiliser point intermediaire"
        this.useWayPoint = false;

        // On vide la valeur affichée des fragments d'autocomplétion
        clearOutFragments();
        // On démarre l'activité map.
        intent.putExtra(EXTRA_POINTA, pointA);
        intent.putExtra(EXTRA_POINTB, pointB);
        intent.putExtra(EXTRA_POINTSUPP, pointInt);
        intent.putExtra(TEMPS_DISPONIBLE_H, temps_disponible_h);
        intent.putExtra(TEMPS_DISPONIBLE_MIN, temps_disponible_min);
        startActivity(intent);
    }

    /**
     *
     * BUTTONS AND CHECKBOXS
     * Méthodes "internes" à l'activité main appelés lorsque l'utilisateur
     * effectue une action sur une checkbox ou un bouton,
     * ne menant pas à l'execution d'une autre activitée
     *
     *
     */

    /**
     * Méthode appelée lorsque l'utilisateur presse le point bouton ajouter point int
     */
    public void showFieldWayPoint(View view){
        TextView textViewInterm = findViewById(R.id.pointIntTextView);
        PlaceAutocompleteFragment fragmentPointInterm = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointInt);

        Button buttonPointInterm = findViewById(R.id.alternative_path);

        try{
            if (!this.useWayPoint){
                fragmentPointInterm.getView().setVisibility(View.VISIBLE);
                textViewInterm.setVisibility(View.VISIBLE);
                buttonPointInterm.setText(R.string.hide_atlernative_point);
                this.useWayPoint = true;
            } else {
                fragmentPointInterm.getView().setVisibility(View.GONE);
                textViewInterm.setVisibility(View.GONE);
                fragmentPointInterm.setText("");
                buttonPointInterm.setText(R.string.show_alternative_point);
                this.useWayPoint = false;
            }
        } catch (NullPointerException e){
            e.getMessage();
            Log.d("showFieldWayPoint_err", e.getMessage());
        }

    }

    /**
     * Méthode appelée lorsque l'utilisateur fait appel à la géolocalisation
     * Si la checkbox "Utiliser ma localisation" est cochée,
     * affiche la geolocalisation de l'utilisateur, sinon reaffiche l'input
     */
    public void onUseDeviceLocationClick(View view){
        CheckBox checkBoxPos = findViewById(R.id.use_loc);
        EditText altText = findViewById(R.id.pointA_alt);

        Fragment fr = getFragmentManager().findFragmentById(R.id.pointA);

        try {
            if (checkBoxPos.isChecked()){
                this.useMyLocForMap = true;
                double[] latlng = LocalizationHandler.getLatLng(this, locationManager);
                setLatitudeUser(latlng[0]);
                setLongitudeUser(latlng[1]);
                replacePointAfragmentByAlt();
            } else {
                altText.setText("");
                altText.setVisibility(View.GONE);
                fr.getView().setVisibility(View.VISIBLE);
                this.useMyLocForMap = false;
            }
        } catch (NullPointerException e){
            Log.d("onUseDeviceLocation_err", e.getMessage());
        }
    }

    /**
     * Méthodes appelée lorsque l'utilisateur choisit quels types de monuments visiter
     */
    public void chooseType1Click(View view){
        CheckBox checkBoxType1 = (CheckBox) findViewById(R.id.type1);

        // TODO arnaud: check exceptions
        if (checkBoxType1.isChecked()){
            this.type1 = true;
        } else {
            this.type1 = false;
        }
    }

    public void chooseType2Click(View view){
        CheckBox checkBoxType2 = (CheckBox) findViewById(R.id.type2);

        // TODO arnaud: check exceptions
        if (checkBoxType2.isChecked()){
            this.type2 = true;
        } else {
            this.type2 = false;
        }
    }

    public void chooseType3Click(View view){
        CheckBox checkBoxType3 = (CheckBox) findViewById(R.id.type3);

        // TODO arnaud: check exceptions
        if (checkBoxType3.isChecked()){
            this.type3 = true;
        } else {
            this.type3 = false;
        }
    }

    public void chooseType4Click(View view){
        CheckBox checkBoxType4 = (CheckBox) findViewById(R.id.type4);

        // TODO arnaud: check exceptions
        if (checkBoxType4.isChecked()){
            this.type4 = true;
        } else {
            this.type4 = false;
        }
    }

    public void chooseType5Click(View view){
        CheckBox checkBoxType5 = (CheckBox) findViewById(R.id.type5);

        // TODO arnaud: check exceptions
        if (checkBoxType5.isChecked()){
            this.type5 = true;
        } else {
            this.type5 = false;
        }
    }

    public void chooseType6Click(View view){
        CheckBox checkBoxType6 = (CheckBox) findViewById(R.id.type6);

        // TODO arnaud: check exceptions
        if (checkBoxType6.isChecked()){
            this.type6 = true;
        } else {
            this.type6 = false;
        }
    }


    /**
     *
     * UTILS
     * Méthodes privées utilisées dans les différentes fonctions ci dessus
     *
     */

    // Check si la geoloc est active sur l'appareil de l'utilisateur
    // Appelée uniquement sur onCreate
    // TODO: voir si utile -> l'application peut elle être utilisée sans localisation?
    /*private void isLocationEnabled() {
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
    }*/

    /**
     * Methode utilisée pour changer la visibilté de tous les éléments de la main page,
     * sauf la barre de chargement
     */
    private void changeVisibilityAll(int value){

        // FRAMGENTS
        PlaceAutocompleteFragment autocompleteStartPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointA);

        PlaceAutocompleteFragment autocompleteEndPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointB);

        PlaceAutocompleteFragment autocompleteWayPathPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointInt);

        // TextsView
        TextView pointA_alt = findViewById(R.id.pointATextView);
        TextView pointIntTxtView = findViewById(R.id.pointIntTextView);
        TextView pointBTxtView = findViewById(R.id.pointBTextBox);
//        TextView txtViewMonument = findViewById(R.id.textViewmonument);
        TextView txtViewDuree = findViewById(R.id.duree);
        TextView textSelectTypes = (TextView) findViewById(R.id.textSelectTypes);


        //Buttons
        Button buttonAltPath = findViewById(R.id.alternative_path);
        Button displayPath = findViewById(R.id.displayMap);
//        Button infoMunum = findViewById(R.id.infoMonument);

        // CheckBox
        CheckBox checkBoxPos = findViewById(R.id.use_loc);
        CheckBox checkBoxType1 = (CheckBox) findViewById(R.id.type1);
        CheckBox checkBoxType2 = (CheckBox) findViewById(R.id.type2);
        CheckBox checkBoxType3 = (CheckBox) findViewById(R.id.type3);
        CheckBox checkBoxType4 = (CheckBox) findViewById(R.id.type4);
        CheckBox checkBoxType5 = (CheckBox) findViewById(R.id.type5);
        CheckBox checkBoxType6 = (CheckBox) findViewById(R.id.type6);

        //EditTexts
//        EditText txtMonument = findViewById(R.id.monument);
        EditText editPointAalt = findViewById(R.id.pointA_alt);

        spinnerhour.setVisibility(value);
        spinnermin.setVisibility(value);
        txtViewDuree.setVisibility(value);

        autocompleteEndPoint.getView().setVisibility(value);
        autocompleteStartPoint.getView().setVisibility(value);
        autocompleteWayPathPoint.getView().setVisibility(value);
        pointA_alt.setVisibility(value);
        pointIntTxtView.setVisibility(value);
        pointBTxtView.setVisibility(value);
//        txtViewMonument.setVisibility(value);
//        buttonAltPath.setVisibility(value);
        displayPath.setVisibility(value);
//        infoMunum.setVisibility(value);
        checkBoxPos.setVisibility(value);
        textSelectTypes.setVisibility(value);
        checkBoxType1.setVisibility(value);
        checkBoxType2.setVisibility(value);
        checkBoxType3.setVisibility(value);
        checkBoxType4.setVisibility(value);
        checkBoxType5.setVisibility(value);
        checkBoxType6.setVisibility(value);
//        txtMonument.setVisibility(value);
        editPointAalt.setVisibility(value);
    }

    private void initSpinners(){
        //Récupération du Spinner déclaré dans le fichier main.xml de res/layout
        spinnerhour = findViewById(R.id.dureehour);
        spinnermin = findViewById(R.id.dureemin);
        //Création d'une liste d'élément à mettre dans le Spinner
        List hourList = new ArrayList();
        hourList.add("1h");
        hourList.add("2h");
        hourList.add("3h");
        hourList.add("4h");
        hourList.add("5h");
        hourList.add("6h");

        List minList = new ArrayList();
        minList.add("0min");
        minList.add("10min");
        minList.add("20min");
        minList.add("30min");
        minList.add("40min");
        minList.add("50min");

		/*Le Spinner a besoin d'un adapter pour sa presentation alors on lui passe le context(this) et
                un fichier de presentation par défaut( android.R.layout.simple_spinner_item)
		Avec la liste des elements */
        ArrayAdapter adapterhour = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                hourList
        );


               /* On definit une présentation du spinner quand il est déroulé
        adapterhour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
        //Enfin on passe l'adapter au Spinner et c'est tout
        spinnerhour.setAdapter(adapterhour);

        ArrayAdapter adaptermin = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                minList
        );


               /* On definit une présentation du spinner quand il est déroulé
        adaptermin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); */
        //Enfin on passe l'adapter au Spinner et c'est tout
        spinnermin.setAdapter(adaptermin);
    }

    /**
     * Méthode utilisée pour initialiser la page
     */
    private void initViewsAll(){
        changeVisibilityAll(View.VISIBLE);

        EditText textAltPointA = findViewById(R.id.pointA_alt);
        textAltPointA.setVisibility(View.GONE);

        Button buttonPointInterm = findViewById(R.id.alternative_path);

        // Si on utilise un point intérmediaire, l'affichage du boutton doit être à "-Point"
        buttonPointInterm.setText(R.string.hide_atlernative_point);

        if(!this.useWayPoint){
            PlaceAutocompleteFragment autocompleteWayPathPoint = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.pointInt);
            autocompleteWayPathPoint.getView().setVisibility(View.GONE);

            TextView textViewInt = findViewById(R.id.pointIntTextView);
            textViewInt.setVisibility(View.GONE);
            buttonPointInterm.setText(R.string.show_alternative_point);
        }



        // Cache la barre de chargement
        ProgressBar p = findViewById(R.id.progressBar1);
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
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                .build();

        // contrainte sur ile de france (rectangle approx)
        // sud ouest : 48°13'38.0"N 1°25'51.1"E
        // nord est : 49°09'45.6"N 3°17'54.7"E
        LatLngBounds rectangleIleDeFrance = new LatLngBounds(
                new LatLng(48.13380, 1.25511), new LatLng(49.09456, 3.17547)
        );
        autocompleteStartPoint.setBoundsBias(rectangleIleDeFrance);
        autocompleteEndPoint.setBoundsBias(rectangleIleDeFrance);
        autocompleteWayPathPoint.setBoundsBias(rectangleIleDeFrance);

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

    private void replacePointAfragmentByAlt(){
        Fragment fr = getFragmentManager().findFragmentById(R.id.pointA);
        EditText altText = findViewById(R.id.pointA_alt);

        fr.getView().setVisibility(View.GONE);
        altText.setVisibility(View.VISIBLE);
//        updateLoc();
        // Si la position n'a pas pu être récupérée, on informe l'utilisateur que
        // sa loc est impossible
        if (this.longitudeUser == null || this.latitudeUser == null){
            altText.setText(R.string.localisation_impossible);
        } else {
            // TODO Arnaud: call for litterals
            altText.setText(this.latitudeUser + "," + this.longitudeUser);
//                String text = getString(R.string.display_value_location, this.latitudeUser, this.longitudeUser);
//                altText.setText(text);
        }
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
