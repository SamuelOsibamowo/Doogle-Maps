package com.example.dooglemaps.viewModel;

import org.parceler.Parcel;

@Parcel
public class Post {

    private String imageUrl, description, reportId, animal, userId;
    private double lat, lng;


    public Post() {}

    public Post(String imageUrl, String description, String reportId, String animal, String userId, double lat, double lng) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.reportId = reportId;
        this.lat = lat;
        this.lng = lng;
        this.userId = userId;
        this.animal = animal;
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

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }
}
