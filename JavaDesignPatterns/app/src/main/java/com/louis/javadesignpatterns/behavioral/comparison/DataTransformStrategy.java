package com.louis.javadesignpatterns.behavioral.comparison;

// 策略模式 - 定义算法接口
public interface DataTransformStrategy {
    String transform(String data);
    String getFormatType();
}