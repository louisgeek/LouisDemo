package com.louis.lg_archj.data.remote.dto;

public class NewsDto {
    private String id;
    private String title;

    public NewsDto(String id, String title) {
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
