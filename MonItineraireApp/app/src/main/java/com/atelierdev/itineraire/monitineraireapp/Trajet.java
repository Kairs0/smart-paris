package com.atelierdev.itineraire.monitineraireapp;


import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Elodie on 11/01/2018.
 */

public class Trajet {
    //Variables, constructor, getter, setter
    public int temps_parcours; //temps de parcours du trajet en considérant la visite des monuments (en s)
    public int duree_souhaitee; //temps de parcours souhaite par l'utilisateur (en s)

    public List<Monument> monuments_interet; // les monuments de la zone, correspondant aux choix de l'utilisateur

    public LatLng A;
    public LatLng B;
    private List<Monument> trajet; //liste des monuments à visiter dans l'ordre

    public List<Integer> temps_de_visite; //du trajet reellement effectue
    public List<Integer> temps_sous_parcours; //du trajet reellement effectue

    public List<List<Integer>> matrice_temps; // matrice de tous les temps de parcours entre les monuments
    public List<String> ordre_matrice; //pour récuperer facilement les temps de la matrice precedente

    public List<Monument> getTrajet() {
        return trajet;
    }

    // A, B, temps de parcours de A à B, listes de monuments, matrice des distances
    public Trajet(int tps_parcours, int duree_souhaitee,  List<Monument> monuments_interet,
                       LatLng A, LatLng B, List<List<Integer>> matrice_temps, List<String> ordre_matrice){
        this.temps_parcours = tps_parcours;
        this.duree_souhaitee = duree_souhaitee;
        this.monuments_interet = monuments_interet;
        this.A = A;
        this.B = B;
        this.trajet = new ArrayList<>();
        this.temps_de_visite = new ArrayList<Integer>();
        this.temps_sous_parcours = new ArrayList<Integer>();
        this.temps_sous_parcours.add(tps_parcours);
        this.matrice_temps = matrice_temps;
        this.ordre_matrice = ordre_matrice;
        construction_trajet();

    }

    //Methode pour ajouter un monument au trajet
    public void construction_trajet() {
        Log.d("Temps parcours", String.valueOf(this.temps_parcours));
        while ((this.temps_parcours < this.duree_souhaitee) && (this.monuments_interet.isEmpty() == false)){
            //on prend le monument avec le plus grand interet de la zone
            Monument monument = this.monuments_interet.remove(0);
            Log.d("Monument considéré :", monument.getName());

            //on calcule ou integrer le nouveau monument
            int l = this.trajet.size();
            int bat_pos = 0;
            int tps1 = 0;
            int tps2 = 0;
            int temps_min = 0;
            if (l == 0) {
                Log.d("", "C'est le premier monument !");
                tps1 = get_temps("A", monument.getName());
                tps2 = get_temps(monument.getName(), "B");
                temps_min = tps1 + tps2 - this.temps_sous_parcours.get(0);
            }
            else{
                tps1 = get_temps("A", monument.getName());
                tps2 = get_temps(monument.getName(), this.trajet.get(0).getName());
                temps_min = tps1 + tps2 - this.temps_sous_parcours.get(0);
                Log.d("tps 1", String.valueOf(temps_min));
                Log.d("tps 2", String.valueOf(temps_min));
                Log.d("temps min", String.valueOf(temps_min));
                for (int m = 0; m < l - 1; m++) {
                    int t1 = get_temps(this.trajet.get(m).getName(), monument.getName());
                    int t2 = get_temps(monument.getName(), this.trajet.get(m + 1).getName());
                    int t = t1 + t2 - this.temps_sous_parcours.get(m + 1);
                    Log.d("t1", String.valueOf(t1));
                    Log.d("t2", String.valueOf(t2));
                    Log.d("t", String.valueOf(t));
                    if (t < temps_min) {
                        bat_pos = m + 1;
                        temps_min = t;
                        tps1 = t1;
                        tps2 = t2;
                    }
                }
                int t1 = get_temps(this.trajet.get(l - 1).getName(), monument.getName());
                int t2 = get_temps(monument.getName(), "B");
                int t = t1 + t2 - this.temps_sous_parcours.get(l);
                Log.d("tps 1 fin", String.valueOf(t1));
                Log.d("tps 2 fin", String.valueOf(t2));
                Log.d("tps min fin", String.valueOf(t));
                if (t < temps_min) {
                    bat_pos = l;
                    temps_min = t;
                    tps1 = t1;
                    tps2 = t2;
                }
            }

            Log.d("Position du monument", String.valueOf(bat_pos));
            //on insere le monument au trajet si le temps le permet
            if (this.temps_parcours + temps_min < this.duree_souhaitee) {
                Log.d("", "On insère le monument au trajet");
                this.trajet.add(bat_pos, monument);
                int t_visite = monument.getVisitTime();
                if (this.temps_parcours + temps_min + t_visite < this.duree_souhaitee){
                    //il y a le temps pour une visite
                    Log.d("", "Il y a le temps pour une visite");
                    if (this.temps_parcours + temps_min + t_visite + 300 < this.duree_souhaitee){
                        //ajout de 5 min pour prendre des photos si il y a le temps
                        //c'est surtout important pour les visites de temps nul dans la base de données
                        this.temps_de_visite.add(bat_pos, 300 + t_visite);
                        this.temps_parcours += temps_min + t_visite + 300;
                    }
                    else{
                        this.temps_de_visite.add(bat_pos, this.duree_souhaitee - this.temps_parcours - temps_min);
                        this.temps_parcours = this.duree_souhaitee;
                    }
                }
                else { //pas le temps pour une visite
                    Log.d("", "Pas le temps pour une visite");
                    if (this.temps_parcours + temps_min + 300 < this.duree_souhaitee){
                        // ajout de 5min pour prendre des photos si il y a le temps
                        this.temps_de_visite.add(bat_pos, 300);
                        this.temps_parcours += temps_min + 300;
                    }
                    else {
                        this.temps_de_visite.add(bat_pos, this.duree_souhaitee - this.temps_parcours - temps_min);
                        this.temps_parcours = this.duree_souhaitee;
                    }
                }
                this.temps_sous_parcours.remove(bat_pos);
                this.temps_sous_parcours.add(bat_pos, tps2);
                this.temps_sous_parcours.add(bat_pos, tps1);

            }
        }

    }

    public int get_temps(String m1, String m2){
        int t = 2357;
        int l = this.ordre_matrice.size();
        int a = 0;
        int b = 0;
        for(int i = 0; i < l; i++)
        {
            if (this.ordre_matrice.get(i) == m1) {
                a = i;
            }
            if (this.ordre_matrice.get(i) == m2) {
                b = i;
            }
        }
        t = this.matrice_temps.get(a).get(b);
        return t;
    }


}
