package com.louis.javadesignpatterns.creational.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 * 2.2 懒汉式 线程安全
 * 通过同步机制保证线程安全
 */
public class Singleton22 {
    private static Singleton22 sInstance;

    private Singleton22() {
    }

    //对 getInstance 方法进行了线程同步，每次 getInstance 都要进行同步，大大增加了开销
    public synchronized static Singleton22 getInstance() {
        //复合操作-start
        //对整个创建实例的过程进行同步
        if (sInstance == null) {
            sInstance = new Singleton22();
        }
        //复合操作-end
        return sInstance;
    }

    public void doSomething() {
        //
    }
}
