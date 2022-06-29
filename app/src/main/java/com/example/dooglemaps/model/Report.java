package com.example.dooglemaps.model;

import com.google.android.gms.maps.model.LatLng;

public class Report {

    private String imageUrl;
    private String description;
    private double lat, lng;

    public Report() {}


    public Report(String imageUrl, String description, double lat, double lng) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
