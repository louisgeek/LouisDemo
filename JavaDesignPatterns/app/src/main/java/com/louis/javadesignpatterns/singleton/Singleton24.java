package com.louis.javadesignpatterns.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 */
//2.4 懒汉式 线程安全
public class Singleton24 {
    private static Singleton24 sInstance;

    private Singleton24() {
    }

    public static Singleton24 getInstance() {
        synchronized (Singleton24.class) {
            if (sInstance == null) {
                sInstance = new Singleton24();
            }
        }
        return sInstance;
    }

    public void doSomething() {
        //
    }
}
