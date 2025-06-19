package com.louis.javadesignpatterns.creational.builder;

// 客户端代码
public class Main {
    public static void main(String[] args) {
        //使用建造者构建
        Dialog dialogA = new Dialog.Builder(null)
                .setTitle("title")
                .setMessage("message")
                .build();

        Dialog dialogB = new Dialog.Builder(null)
                .setTitle("title")
                .build();

    }
}