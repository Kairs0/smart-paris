package com.atelierdev.itineraire.monitineraireapp;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Elodie on 11/01/2018.
 */

public class Trajet {
    //Variables, constructor, getter, setter
    private int temps_parcours; //temps de parcours du trajet en considérant la visite des monuments (en s)
    private int duree_souhaitee; //temps de parcours souhaite par l'utilisateur (en s)

    private List<Monument> monuments_interet; // les monuments de la zone, correspondant aux choix de l'utilisateur

    private LatLng A;
    private LatLng B;
    private List<Monument> trajet; //liste des monuments à visiter dans l'ordre

    private List<Integer> temps_de_visite; //du trajet reellement effectue
    private List<Integer> temps_sous_parcours; //du trajet reellement effectue

    private List<List<Integer>> matrice_temps; // matrice de tous les temps de parcours entre les monuments
    private List<String> ordre_matrice; //pour récuperer facilement les temps de la matrice precedente

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

    public List<Monument> getTrajet() {
        return trajet;
    }

    //Methode pour ajouter un monument au trajet
    public void construction_trajet() {
        //TODO parametre de temps minimal pour continuer à chercher?
        while ((this.temps_parcours < this.duree_souhaitee) && (!this.monuments_interet.isEmpty())){
            //on prend le monument avec le plus grand interet de la zone
            Monument monument = this.monuments_interet.remove(0);

            //on calcule ou integrer le nouveau monument
            int l = this.trajet.size();
            int bat_pos = 0;
            int tps1 = 0;
            int tps2 = 0;
            int temps_min = 0;
            if (l == 0) {
                tps1 = get_temps("A", monument.getName());
                tps2 = get_temps(monument.getName(), "B");
                temps_min = tps1 + tps2 - this.temps_sous_parcours.get(0);
            }
            else{
                tps1 = get_temps("A", monument.getName());
                tps2 = get_temps(monument.getName(), this.trajet.get(0).getName());
                temps_min = tps1 + tps2 - this.temps_sous_parcours.get(0);
                for (int m = 0; m < l - 1; m++) {
                    int t1 = get_temps(this.trajet.get(m).getName(), monument.getName());
                    int t2 = get_temps(monument.getName(), this.trajet.get(m + 1).getName());
                    int t = t1 + t2 - this.temps_sous_parcours.get(m + 1);
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
                if (t < temps_min) {
                    bat_pos = l;
                    temps_min = t;
                    tps1 = t1;
                    tps2 = t2;
                }
            }
            //on insere le monument au trajet si le temps le permet
            if (this.temps_parcours + temps_min < this.duree_souhaitee) {
                this.trajet.add(bat_pos, monument);
                int t_visite = monument.getVisitTime();
                if (this.temps_parcours + temps_min + t_visite < this.duree_souhaitee){
                    this.temps_de_visite.add(bat_pos, t_visite);
                    this.temps_parcours += temps_min + t_visite;
                }
                else {
                    //TODO on met vraiment O ou 5 min pour les photos
                    this.temps_de_visite.add(bat_pos, 0);
                    this.temps_parcours += temps_min;
                }
                this.temps_sous_parcours.remove(bat_pos);
                this.temps_sous_parcours.add(bat_pos, tps2);
                this.temps_sous_parcours.add(bat_pos, tps1);

            }
        }

    }

    public int get_temps(String m1, String m2){
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
        int t = this.matrice_temps.get(a).get(b);
        return t;
    }
}
