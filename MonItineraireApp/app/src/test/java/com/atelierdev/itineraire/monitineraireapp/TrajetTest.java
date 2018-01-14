package com.atelierdev.itineraire.monitineraireapp;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elodie on 11/01/2018.
 */

public class TrajetTest {

    private Monument fontaine = new Monument(0, "fontaine", 1, "1", 0.0, 0.0, 2, 1800);
    private Monument palais = new Monument(1, "palais", 1, "1", 0.0, 0.0, 5, 2700);
    private Monument cathedrale = new Monument(2, "cathedrale", 1, "1", 0.0, 0.0, 4, 900);
    private Monument statue = new Monument(3, "statue", 1, "1", 0.0, 0.0, 2, 0);
    private Monument cascade = new Monument(0, "cascade", 1, "1", 0.0, 0.0, 2, 0);

    private List<Monument> monuments = new ArrayList<Monument>();

    private LatLng A = new LatLng(0.0,0.0);
    private LatLng B = new LatLng(1.0, 1.0);

    private List<List<Integer>> matrice_temps = new ArrayList();
    private List<String> ordre_matrice = new ArrayList<String>();

    private int temps_parcours = 6000;

    //TODO change

    @Before
    public void initTrajetTest(){
        //filling monuments with the monuments for the test
        this.monuments.add(palais);
        this.monuments.add(cathedrale);
        this.monuments.add(cascade);
        this.monuments.add(fontaine);
        this.monuments.add(statue);

        //filling matrice_temps
        List<Integer> l1 = new ArrayList<Integer>();
        List<Integer> l2 = new ArrayList<Integer>();
        List<Integer> l3 = new ArrayList<Integer>();
        List<Integer> l4 = new ArrayList<Integer>();
        List<Integer> l5 = new ArrayList<Integer>();
        List<Integer> l6 = new ArrayList<Integer>();
        List<Integer> l7 = new ArrayList<Integer>();

        l1.add(0); //A A
        l1.add(6000); // A B
        l1.add(2280); // A palais
        l1.add(4860); // A cathedrale
        l1.add(3960); // A cascade
        l1.add(1320); // A fontaine
        l1.add(1860); // A statue

        l2.add(6000); //B A
        l2.add(0); // B B
        l2.add(3960); // B palais
        l2.add(1320); // B cathedrale
        l2.add(2220); // B cascade
        l2.add(5220); // B fontaine
        l2.add(4260); // B statue

        l3.add(2280); //palais A
        l3.add(3960); // palais B
        l3.add(0); // palais palais
        l3.add(2640); // palais cathedrale
        l3.add(2340); // palais cascade
        l3.add(2160); // palais fontaine
        l3.add(780); // palais statue

        l4.add(4860); //cathedrale A
        l4.add(1320); // cathedrale B
        l4.add(2640); // cathedrale palais
        l4.add(0); // cathedrale cathedrale
        l4.add(1500); // cathedrale cascade
        l4.add(4140); // cathedrale fontaine
        l4.add(3000); // cathedrale statue

        l5.add(3960); //cascade A
        l5.add(2220); // cascade B
        l5.add(2340); // cascade palais
        l5.add(1500); // cascade cathedrale
        l5.add(0); // cascade cascade
        l5.add(3000); // cascade fontaine
        l5.add(2340); // cascade statue

        l6.add(1320); //fontaine A
        l6.add(5220); // fontaine B
        l6.add(2160); // fontaine palais
        l6.add(4140); // fontaine cathedrale
        l6.add(3000); // fontaine cascade
        l6.add(0); // fontaine fontaine
        l6.add(1500); // fontaine statue

        l7.add(1860); //statue A
        l7.add(4260); // statue B
        l7.add(780); // statue palais
        l7.add(3000); // statue cathedrale
        l7.add(2340); // statue cascade
        l7.add(1500); // statue fontaine
        l7.add(0); // statue statue

        this.matrice_temps.add(l1);
        this.matrice_temps.add(l2);
        this.matrice_temps.add(l3);
        this.matrice_temps.add(l4);
        this.matrice_temps.add(l5);
        this.matrice_temps.add(l6);
        this.matrice_temps.add(l7);

        //filling ordre_matrice with the names of the monuments in order
        this.ordre_matrice.add("A");
        this.ordre_matrice.add("B");
        this.ordre_matrice.add("palais");
        this.ordre_matrice.add("cathedrale");
        this.ordre_matrice.add("cascade");
        this.ordre_matrice.add("fontaine");
        this.ordre_matrice.add("statue");
    }



    @Test
    public void testTrajetVide(){
        List<Monument> monum = new ArrayList<>();
        Trajet trajet_nul = new Trajet(this.temps_parcours, 7200, monum, this.A, this.B, this.matrice_temps, this.ordre_matrice);
        assertTrue(trajet_nul.getTrajet().isEmpty());
        assertTrue(trajet_nul.getTemps_sous_parcours().size()==1);
        assertTrue(trajet_nul.getTemps_sous_parcours().get(0)==6000);
        assertTrue(trajet_nul.getTemps_de_visite().isEmpty());
        assertTrue(trajet_nul.getTemps_parcours()==trajet_nul.getTemps_sous_parcours().get(0));
        assertTrue(trajet_nul.get_temps("A", "cathedrale") == 4860);
        assertTrue(trajet_nul.get_temps("cathedrale", "palais") == 2640);
        assertTrue(trajet_nul.get_temps("palais", "cathedrale") == 2640);
        assertTrue(trajet_nul.get_temps("cathedrale", "B") == 1320);
    }

    @Test
    public void testTrajetLong(){
        Trajet trajet_long = new Trajet(this.temps_parcours, 3000000, this.monuments, this.A, this.B, this.matrice_temps, this.ordre_matrice);
        assertTrue(trajet_long.getDuree_souhaitee()>=trajet_long.getTemps_parcours());
        assertTrue(trajet_long.getTemps_parcours()==15660);

        assertTrue(trajet_long.getTrajet().size()==5);
        assertTrue(trajet_long.getTemps_de_visite().size()==5);
        assertTrue(trajet_long.getTemps_sous_parcours().size()==6);

        assertTrue(trajet_long.getTrajet().get(0)==fontaine);
        assertTrue(trajet_long.getTrajet().get(1)==statue);
        assertTrue(trajet_long.getTrajet().get(2)==palais);
        assertTrue(trajet_long.getTrajet().get(3)==cascade);
        assertTrue(trajet_long.getTrajet().get(4)==cathedrale);

        assertTrue(trajet_long.getTemps_sous_parcours().get(0)==1320);
        assertTrue(trajet_long.getTemps_sous_parcours().get(1)==1500);
        assertTrue(trajet_long.getTemps_sous_parcours().get(2)==780);
        assertTrue(trajet_long.getTemps_sous_parcours().get(3)==2340);
        assertTrue(trajet_long.getTemps_sous_parcours().get(4)==1500);
        assertTrue(trajet_long.getTemps_sous_parcours().get(5)==1320);

        assertTrue(trajet_long.getTemps_de_visite().get(0)==2100);
        assertTrue(trajet_long.getTemps_de_visite().get(1)==300);
        assertTrue(trajet_long.getTemps_de_visite().get(2)==3000);
        assertTrue(trajet_long.getTemps_de_visite().get(3)==300);
        assertTrue(trajet_long.getTemps_de_visite().get(4)==1200);

    }

    @Test
    public void testTrajetCourt(){
        Trajet trajet_court = new Trajet(this.temps_parcours, 9600, this.monuments, this.A, this.B, this.matrice_temps, this.ordre_matrice);
        assertTrue(trajet_court.getDuree_souhaitee()>=trajet_court.getTemps_parcours());
        assertTrue(trajet_court.getTemps_parcours()==9540);

        assertTrue(trajet_court.getTrajet().size()==2);
        assertTrue(trajet_court.getTemps_de_visite().size()==2);
        assertTrue(trajet_court.getTemps_sous_parcours().size()==3);

        assertTrue(trajet_court.getTrajet().get(0)==palais);
        assertTrue(trajet_court.getTrajet().get(1)==cathedrale);

        assertTrue(trajet_court.getTemps_sous_parcours().get(0)==2280);
        assertTrue(trajet_court.getTemps_sous_parcours().get(1)==2640);
        assertTrue(trajet_court.getTemps_sous_parcours().get(2)==1320);

        assertTrue(trajet_court.getTemps_de_visite().get(0)==3000);
        assertTrue(trajet_court.getTemps_de_visite().get(1)==300);

    }

    @Test
    public void testTrajetMoyen(){
        Trajet trajet_moyen = new Trajet(this.temps_parcours, 10000, this.monuments, this.A, this.B, this.matrice_temps, this.ordre_matrice);
        assertTrue(trajet_moyen.getDuree_souhaitee()>=trajet_moyen.getTemps_parcours());
        assertTrue(trajet_moyen.getTemps_parcours()==10000);

        assertTrue(trajet_moyen.getTrajet().size()==3);
        assertTrue(trajet_moyen.getTemps_de_visite().size()==3);
        assertTrue(trajet_moyen.getTemps_sous_parcours().size()==4);

        assertTrue(trajet_moyen.getTrajet().get(0)==statue);
        assertTrue(trajet_moyen.getTrajet().get(1)==palais);
        assertTrue(trajet_moyen.getTrajet().get(2)==cathedrale);

        assertTrue(trajet_moyen.getTemps_sous_parcours().get(0)==1860);
        assertTrue(trajet_moyen.getTemps_sous_parcours().get(1)==780);
        assertTrue(trajet_moyen.getTemps_sous_parcours().get(2)==2640);
        assertTrue(trajet_moyen.getTemps_sous_parcours().get(3)==1320);

        assertTrue(trajet_moyen.getTemps_de_visite().get(0)==100);
        assertTrue(trajet_moyen.getTemps_de_visite().get(1)==3000);
        assertTrue(trajet_moyen.getTemps_de_visite().get(2)==300);

    }
}
