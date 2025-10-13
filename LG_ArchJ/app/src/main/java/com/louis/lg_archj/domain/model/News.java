package com.louis.lg_archj.domain.model;

public class News {
    private String id;
    private String title;

    public News() {
    }

    public News(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}