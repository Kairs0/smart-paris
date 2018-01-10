package com.atelierdev.itineraire.monitineraireapp;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by Guillaume on 18/12/2017.
 */

//sugar se charge de la création de la table
public class Monument extends SugarRecord<Monument>{

    //Variables, constructor, getter, setter

    public int monument_id;
    public String name;
    public int category;
    public String types;
    public double lat;
    public double lon;
    public int rating;
    public int visitTime;

    public Monument(){

    }

    public Monument(int monument_id, String name, int category, String types, double lat, double lon, int rating, int visitTime){
        super();
        this.monument_id = monument_id;
        this.name = name;
        this.category = category;
        this.types = types;
        this.lat = lat;
        this.lon = lon;
        this.rating = rating;
        this.visitTime = visitTime;
    }


    public int getMonumentId() {
        return monument_id;
    }

    public void setMonumentId(int monument_id) {
        this.monument_id = monument_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String type) {
        this.types = type;
    }

    public int getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(int visitTime) {
        this.visitTime = visitTime;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

}