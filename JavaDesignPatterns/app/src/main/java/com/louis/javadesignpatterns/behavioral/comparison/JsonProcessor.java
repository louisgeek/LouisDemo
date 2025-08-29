package com.louis.javadesignpatterns.behavioral.comparison;

public class JsonProcessor extends DataProcessor {
    @Override
    protected String transformData(String data) {
        System.out.println("转换为JSON格式");
        return "{\"data\":\"" + data + "\"}";
    }
}