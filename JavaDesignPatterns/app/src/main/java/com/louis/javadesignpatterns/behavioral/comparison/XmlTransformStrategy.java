package com.louis.javadesignpatterns.behavioral.comparison;

public class XmlTransformStrategy implements DataTransformStrategy {
    @Override
    public String transform(String data) {
        System.out.println("使用XML策略转换");
        return "<xml>" + data + "</xml>";
    }
    
    @Override
    public String getFormatType() {
        return "XML";
    }
}