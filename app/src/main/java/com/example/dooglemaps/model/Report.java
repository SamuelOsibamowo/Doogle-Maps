package com.example.dooglemaps.model;

public class Report {

    private String imageUrl;
    private String description;

    public Report() {}


    public Report(String imageUrl, String description) {
        this.imageUrl = imageUrl;
        this.description = description;
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
}
