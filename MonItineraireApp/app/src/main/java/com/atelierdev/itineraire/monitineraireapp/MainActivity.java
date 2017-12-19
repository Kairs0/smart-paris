package com.atelierdev.itineraire.monitineraireapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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


public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_POINTA = "com.atelierdev.itineraire.monitineraireapp.pointA";
    public static final String EXTRA_POINTB = "com.atelierdev.itineraire.monitineraireapp.pointB";
    public static final String EXTRA_POINTSUPP = "com.atelierdev.itineraire.monitineraireapp.pointInt";
    public static final String EXTRA_MONUMENT = "com.atelierdev.itineraire.monitineraireapp.monument";

    private boolean useMyLocForMap = false;
    private boolean isSearchAllowed = true;

    //Location
    // https://stackoverflow.com/questions/42218419/how-do-i-implement-the-locationlistener
    LocationManager locationManager;
    Context mContext;

    private Double longitudeUser;
    private Double latitudeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Location manager
        mContext=this;
        locationManager=(LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                    2000,
                    10, locationListenerGPS);
        } catch (SecurityException e){ }
        finally {
            isLocationEnabled();
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

    public void displayMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);

        EditText editTextA = (EditText) findViewById(R.id.pointA);
        EditText editTextB = (EditText) findViewById(R.id.pointB);
        EditText editTextInt = (EditText) findViewById(R.id.pointInt);

        String pointA;
        String pointB = editTextB.getText().toString();
        String pointInt = editTextInt.getText().toString();

        if(this.useMyLocForMap){
            pointA = String.valueOf(this.latitudeUser) + "," + String.valueOf(this.longitudeUser);
        } else {
            pointA = editTextA.getText().toString();
        }

        intent.putExtra(EXTRA_POINTA, pointA);
        intent.putExtra(EXTRA_POINTB, pointB);
        intent.putExtra(EXTRA_POINTSUPP, pointInt);

        if (TextUtils.isEmpty(pointA) || TextUtils.isEmpty(pointB)){
            this.isSearchAllowed = false;
        }

        if(!this.isSearchAllowed){
            String msg="Impossible d'effectuer une recherche !";
            Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
        } else {
            startActivity(intent);
        }
    }

    public void showFieldWayPoint(View view){
        EditText editTextInt = (EditText) findViewById(R.id.pointInt);
        TextView textViewInt = (TextView) findViewById(R.id.pointIntTextView);
        editTextInt.setVisibility(View.VISIBLE);
        textViewInt.setVisibility(View.VISIBLE);
    }

    public void useDeviceLocation(View view){

        CheckBox checkBoxPos = (CheckBox) findViewById(R.id.use_loc);

        EditText editTextA = (EditText) findViewById(R.id.pointA);
        TextView textViewA = (TextView) findViewById(R.id.pointATextView);

        if (checkBoxPos.isChecked()){
            this.useMyLocForMap = true;
            if (this.longitudeUser == null || this.longitudeUser == null){
                editTextA.setText("Localisation impossible");
                editTextA.setEnabled(false);
                this.isSearchAllowed = false;
            } else {
                editTextA.setText(this.latitudeUser + "," + this.longitudeUser);
                editTextA.setEnabled(false);
                this.isSearchAllowed = true;
            }
        } else {
            editTextA.setText("");
            editTextA.setEnabled(true);
            this.useMyLocForMap = false;
            this.isSearchAllowed = true;
        }
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

    /**
     * N'est plus utilisé (affichage du trajet sous forme de texte)
     */
    public void calculTrajet(View view){
        Intent intent = new Intent(this, DisplayPathAtoB.class);
        EditText editTextA = (EditText) findViewById(R.id.pointA);
        EditText editTextB = (EditText) findViewById(R.id.pointB);

        String pointA = editTextA.getText().toString();
        String pointB = editTextB.getText().toString();

        intent.putExtra(EXTRA_POINTA, pointA);
        intent.putExtra(EXTRA_POINTB, pointB);
        startActivity(intent);
    }
}
