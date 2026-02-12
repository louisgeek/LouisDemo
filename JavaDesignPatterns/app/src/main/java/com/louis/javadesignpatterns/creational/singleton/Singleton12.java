package com.louis.javadesignpatterns.creational.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 * 1.2 饿汉式 静态全局变量
 * 是最经典实现
 */
public class Singleton12 {
    //在类加载时立即创建实例
    private static Singleton12 sInstance = new Singleton12();

    private Singleton12() {
    }

    public static Singleton12 getInstance() {
        //直接返回已创建的实例
        return sInstance;
    }

    public void doSomething() {
        //
    }
}
