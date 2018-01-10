package com.atelierdev.itineraire.monitineraireapp;

import com.orm.SugarRecord;

/**
 * Created by Guillaume on 18/12/2017.
 */

public class Monument extends SugarRecord<Monument> {

    //Variables, constructor, getter, setter

    public long id;
    public String name;
    public int category;
    public String type;
    public double lat;
    public double lon;
    public int rating;
    public int visitTime;

    public Monument(){

    }

    public Monument(long id, String name, int category, String type, double lat, double lon, int rating, int visitTime){
        super();
        this.id = id;
        this.name = name;
        this.category = category;
        this.type = type;
        this.lat = lat;
        this.lon = lon;
        this.rating = rating;
        this.visitTime = visitTime;
    }


    /*public long getId() {
        return id;
    }*/

    public void setId(int id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
