package com.louis.mymap;


import android.view.View;

public class MapMarker {
    private String id;
    private double latitude;
    private double longitude;
    private String title;
    private String snippet;

    private View customView;

    public MapMarker(String id, double latitude, double longitude, String title) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
    }
    public MapMarker position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        return this;
    }

    public MapMarker title(String title) {
        this.title = title;
        return this;
    }

    public MapMarker snippet(String snippet) {
        this.snippet = snippet;
        return this;
    }

    public MapMarker customView(View customView) {
        this.customView = customView;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public View getCustomView() {
        return customView;
    }
}
