package com.louis.javadesignpatterns.creational.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 */
//2.3 懒汉式 线程不安全
@Deprecated
public class Singleton23 {
    private static Singleton23 sInstance;

    private Singleton23() {
    }

    public static Singleton23 getInstance() {
        if (sInstance == null) {
            //多个线程依旧能够通过 if 判断，虽然同步了实例化的代码，但还是会多次实例化
            synchronized (Singleton23.class) {
                sInstance = new Singleton23();
            }
        }
        return sInstance;
    }

    public void doSomething() {
        //
    }
}
