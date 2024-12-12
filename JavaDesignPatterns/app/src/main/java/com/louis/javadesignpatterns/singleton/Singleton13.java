package com.louis.javadesignpatterns.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 */
//1.3 饿汉式 静态全局变量 静态代码块
public class Singleton13 {
    private static Singleton13 sInstance;

    static {
        sInstance = new Singleton13();
    }

    private Singleton13() {
    }

    public static Singleton13 getInstance() {
        return sInstance;
    }

    public void doSomething() {
        //
    }
}
