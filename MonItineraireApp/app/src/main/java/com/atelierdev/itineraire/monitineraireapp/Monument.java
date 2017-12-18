package com.atelierdev.itineraire.monitineraireapp;

/**
 * Created by Guillaume on 18/12/2017.
 */

public class Monument {

    //Variables, constructor, getter, setter
    public int id;
    public String name;
    public int category;
    public String type;
    public String address;
    public double lat;
    public double lon;
    public int rating;

    public Monument(int id, String name, int category, String type, String address, int zipCode, double lat, double lon, int rating){
        super();
        this.id = id;
        this.name = name;
        this.category = category;
        this.type = type;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.rating = rating;
    }


    public int getId() {
        return id;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
