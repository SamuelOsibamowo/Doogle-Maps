package com.example.dooglemaps.viewModel;

import org.parceler.Parcel;

@Parcel
public class Post {

    private String imageUrl, description, reportId;
    private double lat, lng;


    public Post() {}

    public Post(String imageUrl, String description, String reportId, double lat, double lng) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.reportId = reportId;
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
}
