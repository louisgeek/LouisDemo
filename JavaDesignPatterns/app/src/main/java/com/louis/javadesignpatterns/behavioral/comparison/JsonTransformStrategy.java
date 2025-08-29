package com.louis.javadesignpatterns.behavioral.comparison;

public class JsonTransformStrategy implements DataTransformStrategy {
    @Override
    public String transform(String data) {
        System.out.println("使用JSON策略转换");
        return "{\"data\":\"" + data + "\"}";
    }
    
    @Override
    public String getFormatType() {
        return "JSON";
    }
}