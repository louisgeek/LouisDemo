package com.louis.javadesignpatterns.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 */
//2.2 懒汉式 线程安全
public class Singleton22 {
    private static Singleton22 sInstance;

    private Singleton22() {
    }

    //对 getInstance() 进行了线程同步，每次 getInstance 要进行同步，大大增加了开销
    public synchronized static Singleton22 getInstance() {
        if (sInstance == null) {
            sInstance = new Singleton22();
        }
        return sInstance;
    }

    public void doSomething() {
        //
    }
}
