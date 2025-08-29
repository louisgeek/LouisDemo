package com.louis.javadesignpatterns.behavioral.comparison;

public class XmlProcessor extends DataProcessor {
    @Override
    protected String transformData(String data) {
        System.out.println("转换为XML格式");
        return "<xml>" + data + "</xml>";
    }
    
    @Override
    protected void saveData(String data) {
        System.out.println("保存到XML文件: " + data);
    }
}