package com.louis.javadesignpatterns.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 */
//3 Double-Checked Locking 双重检查锁 DCL，进行了两次判空
public class Singleton3 {
    //声明 volatile 关键字，会在编译时加 lock，禁止了指令重排序
    private static volatile Singleton3 sInstance;

    private Singleton3() {

    }

    public static Singleton3 getInstance() {
        //判断一次，避免不必要的同步锁
        if (sInstance == null) {
            synchronized (Singleton3.class) {
                if (sInstance == null) {
                    sInstance = new Singleton3();
                }
            }
        }
        return sInstance;
    }

    public void doSomething() {
        //
    }
}
