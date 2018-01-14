package com.atelierdev.itineraire.monitineraireapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class DisplayInfoMonument extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private TextToSpeech engine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_monument_info);
        TextView textView = findViewById(R.id.resultatInformation);
        textView.setText("Chargement ...");

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String monument = intent.getStringExtra(MainActivity.EXTRA_MONUMENT_ID);

        Thread getInfoMonumentThread = new ApiParisThread("ThreadInfoMonument", monument);

        engine = new TextToSpeech(this, this);

        if(isOnline()){
            getInfoMonumentThread.start();
        } else {
            UpdateTextView("La connection internet a été perdue durant la récupération des informations.");
        }
    }

    public void UpdateTextView(String result) {
        final String res = result;
        //Déposer le Runnable dans la file d'attente de l'UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //code exécuté par l'UI thread
                TextView textView = findViewById(R.id.resultatInformation);
                textView.setText(res);
            }
        });
    }

    public String GetStringFromGetEquipmentRequest(String result) throws JSONException {
        JSONObject obj = new JSONObject(result);
        if (obj == null) {
            return ("Pas d'information");
        }
        JSONArray data = obj.getJSONArray("data");
        if (data == null) {
            return ("Pas d'information");
        }
        JSONObject information = data.getJSONObject(0);
        if (information == null) {
            return ("Pas d'information");
        }
        String name = information.getString("name");

        String rawdescription = information.getString("description");
        String description = android.text.Html.fromHtml(rawdescription).toString();

        String open_status = "";

        // On récupère les infos sur les horaires si elles existent et on les compare à l'heure de consultation
        try {
            JSONObject calendars = information.getJSONObject("calendars");
            Date currentTime = Calendar.getInstance().getTime();
            String datenow = new SimpleDateFormat("yyyy-MM-dd").format(currentTime);
            // récupère l'objet json "calendar" qui contient les horaires des deux prochaines semaines. Voir doc api.paris
            JSONArray schedulenow = calendars.getJSONArray(datenow).getJSONArray(0);
            SimpleDateFormat hh_mm_ss = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            String opening = datenow + "-" + schedulenow.getString(0);
            String closure = datenow + "-" + schedulenow.getString(1);
            // Convertit les string en objet "date" pour pouvoir les comparer
            Date opening_hour = hh_mm_ss.parse(opening);
            Date closure_hour = hh_mm_ss.parse(closure);

            // Vérifie si on se situe dans la bonne plage horaire
            if(currentTime.compareTo(opening_hour) > 0 &&  currentTime.compareTo(closure_hour) < 0)
            {
                open_status = "Ouvert - Ferme à " + new SimpleDateFormat("HH:mm").format(closure_hour);
            }
            else if(currentTime.compareTo(opening_hour) > 0){
                open_status = "Fermé - ouvre à " + new SimpleDateFormat("HH:mm").format(opening_hour);
            }
            else
                open_status = "Fermé";
        }catch(Exception e)
        {
            open_status = "Pas d'information sur les horaires";
        }
        finally {
            return (name + "\n\n" + open_status + "\n\n" + description);
        }
    }


    public class ApiParisThread extends Thread {
        private String _monument;
        public String result = "";

        public String GetResult() {
            return result;
        }

        public ApiParisThread(String name, String monument) {
            super(name);
            _monument = monument;
        }

        @Override
        public void run() {
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    //int monument_id = Monument.findWithQuery(Monument.class, "Select * from Monument where name = ?", _monument).get(0).getMonumentId();
                    //url = new URL("https://api.paris.fr/api/data/1.0/Equipements/get_equipement/?token=f6e85890f660abe2fd846df117a3cb215fc7d5bb80969d17026f3820a23728f2&id=" + monument_id);
                    url = new URL("https://api.paris.fr/api/data/1.0/Equipements/get_equipement/?token=f6e85890f660abe2fd846df117a3cb215fc7d5bb80969d17026f3820a23728f2&id=" + _monument);

                    urlConnection = (HttpURLConnection) url
                            .openConnection();

                    BufferedReader br = new BufferedReader(new InputStreamReader((urlConnection.getInputStream())));
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null) {
                        sb.append(output);
                    }
                    String rawResult = sb.toString();
                    Log.d("Resultat", rawResult);
                    this.result = GetStringFromGetEquipmentRequest(rawResult);

                } catch (Exception e) {
                    e.printStackTrace();
                    this.result = "Nous n'avons pas pu vous connecter";
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();

                        UpdateTextView(result);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Démarrer la lecture vocale des infos
    public void playText(View v) {
        TextView textView = findViewById(R.id.resultatInformation);
        String toSpeak=textView.getText().toString();
        if (toSpeak.length()>4000) {
            toSpeak=toSpeak.substring(0,3999);
        }
        engine.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);

    }

    //Surcharge de l'initialisation de la synthèse vocale pour régler le language
    @Override
    public void onInit(int i) {
        Log.d("mytag", "oninit");

        if (i == TextToSpeech.SUCCESS) {
            //Setting speech Language
            engine.setLanguage(Locale.FRENCH);
            engine.setPitch(1);
        }
        else {
            Toast.makeText(getApplicationContext(), "Impossible de lire le texte", Toast.LENGTH_SHORT).show();
        }
    }

    //Arrêter la synthèse vocale
    public void stopPlaying(View view) {
        engine.stop();
    }

    //Si l'activité est mise en pause (appui sur l'un des boutons de commande android) on stoppe la lecture
    @Override
    public void onPause() {
        if(engine != null) {

            engine.stop();
            Log.d("mytag", "TTS stopped on pause");
        }
        super.onPause();
    }

    //Si l'activité était seulement en pause et qu'on y retourne (multitaches ou bouton acceuil) on réinitialise la synthèse vocale
    @Override
    public void onResume() {
        super.onResume();
        engine = new TextToSpeech(this, this);
    }

    //Si l'activité est détruite (flèche précédent) on ferme la synthèse vocale
    @Override
    protected void onDestroy()
    {
        engine.shutdown();
        super.onDestroy();
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}