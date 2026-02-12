package com.louis.javadesignpatterns.creational.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 * 2.4 懒汉式 线程安全，和 2.2 原理一致
 */
public class Singleton24 {
    private static Singleton24 sInstance;

    private Singleton24() {
    }

    public static Singleton24 getInstance() {
        synchronized (Singleton24.class) {
            //复合操作-start
            //对整个创建实例的过程进行同步
            if (sInstance == null) {
                sInstance = new Singleton24();
            }
            //复合操作-end
        }
        return sInstance;
    }

    public void doSomething() {
        //
    }
}
