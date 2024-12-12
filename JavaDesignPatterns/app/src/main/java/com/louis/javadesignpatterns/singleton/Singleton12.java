package com.louis.javadesignpatterns.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 */
//1.2 饿汉式 静态全局变量
public class Singleton12 {
    private static Singleton12 sInstance = new Singleton12();

    private Singleton12() {
    }

    public static Singleton12 getInstance() {
        return sInstance;
    }

    public void doSomething() {
        //
    }
}
