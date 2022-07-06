package com.example.dooglemaps.viewModel;

import com.google.android.gms.maps.model.LatLng;

public class Report {

    private String imageUrl, description, reportId, animal;
    private double lat, lng;

    public Report() {}


    public Report(String imageUrl, String description, String reportId,String animal, double lat, double lng) {
        this.reportId = reportId;
        this.imageUrl = imageUrl;
        this.description = description;
        this.animal = animal;
        this.lat = lat;
        this.lng = lng;

    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
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

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }
}
