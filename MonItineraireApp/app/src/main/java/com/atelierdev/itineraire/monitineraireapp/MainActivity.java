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
import android.widget.CheckBox;
import android.widget.EditText;
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

    private boolean useWayPoint = false;

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
        } catch (SecurityException e){ /*TODO*/}
        finally {
            isLocationEnabled();
        }


        // Auto complétion pour chaque point
        PlaceAutocompleteFragment autocompleteStartPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointA);

        PlaceAutocompleteFragment autocompleteEndPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointB);

        PlaceAutocompleteFragment autocompleteWayPathPoint = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.pointInt);

        /*
        * The following code example shows setting an AutocompleteFilter on a PlaceAutocompleteFragment to
        * set a filter returning only results with a precise address.
        */
        AutocompleteFilter addressFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();

        autocompleteStartPoint.setFilter(addressFilter);
        autocompleteEndPoint.setFilter(addressFilter);
        autocompleteWayPathPoint.setFilter(addressFilter);
        autocompleteWayPathPoint.getView().setVisibility(View.GONE);

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

    public void displayMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);

        // TODO points intermediaires
//        EditText editTextInt = (EditText) findViewById(R.id.pointInt);


        String pointA;
        String pointB = this.endPoint != null ?
                String.valueOf(this.endPoint.latitude) + "," + String.valueOf(this.endPoint.longitude):
                "";

        String pointInt = this.middlePoint != null ?
                String.valueOf(this.middlePoint.latitude) + "," + String.valueOf(this.middlePoint.longitude):
                "";

        if(this.useMyLocForMap){
            pointA = String.valueOf(this.latitudeUser) + "," + String.valueOf(this.longitudeUser);
        } else {
            pointA = this.startPoint != null ?
                    String.valueOf(this.startPoint.latitude) + "," + String.valueOf(this.startPoint.longitude) :
                    "";
        }

        boolean searchAllowed = true;

        if(TextUtils.isEmpty(pointA) || TextUtils.isEmpty(pointB)){
            searchAllowed = false;
        }

        if (this.useMyLocForMap){
            if (this.latitudeUser == null || this.longitudeUser == null){
                searchAllowed = false;
            }
        }

        if(!searchAllowed){
            String msg="Impossible d'effectuer une recherche !";
            Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
        } else {
            this.startPoint = null;
            this.endPoint = null;

            PlaceAutocompleteFragment autocompleteStartPoint = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.pointA);

            PlaceAutocompleteFragment autocompleteEndPoint = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.pointB);

            PlaceAutocompleteFragment autocompleteWayPoint = (PlaceAutocompleteFragment)
                    getFragmentManager().findFragmentById(R.id.pointInt);

            autocompleteStartPoint.setText("");
            autocompleteEndPoint.setText("");
            autocompleteWayPoint.setText("");


            intent.putExtra(EXTRA_POINTA, pointA);
            intent.putExtra(EXTRA_POINTB, pointB);
            intent.putExtra(EXTRA_POINTSUPP, pointInt);
            startActivity(intent);
        }
    }

    public void showFieldWayPoint(View view){
        TextView textViewInt = (TextView) findViewById(R.id.pointIntTextView);
        PlaceAutocompleteFragment fr = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.pointInt);

        if (!this.useWayPoint){
            fr.getView().setVisibility(View.VISIBLE);
            textViewInt.setVisibility(View.VISIBLE);
            this.useWayPoint = true;
        } else {
            fr.getView().setVisibility(View.GONE);
            textViewInt.setVisibility(View.GONE);
            fr.setText("");
            this.useWayPoint = false;
        }



    }

    /**
     * Si la checkbox "Utiliser ma localisation" est cochée,
     * affiche la geolocalisation de l'utilisateur, sinon reaffiche l'input
     * @param view
     */
    public void onUseDeviceLocationClick(View view){
        CheckBox checkBoxPos = (CheckBox) findViewById(R.id.use_loc);
        EditText altText = (EditText) findViewById(R.id.pointA_alt);

        Fragment fr = getFragmentManager().findFragmentById(R.id.pointA);




        // TODO : utiliser strings litteral
        if (checkBoxPos.isChecked()){
            this.useMyLocForMap = true;
            fr.getView().setVisibility(View.GONE);
            altText.setVisibility(View.VISIBLE);

            updateLoc();

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

    private void updateLoc() {
        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null){
                setLatitudeUser(location.getLatitude());
                setLongitudeUser(location.getLongitude());
            }
        } catch (SecurityException e) {}
    }

    //LOCATION
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


}
