package com.example.dooglemaps.model;

public class Post {

    private String imageUrl, description, reportId;

    public Post(String imageUrl, String description, String reportId) {
        this.imageUrl = imageUrl;
        this.description = description;
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

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
}
