package com.louis.javadesignpatterns.creational.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 */
//2.1 懒汉式 线程不安全
@Deprecated
public class Singleton21 {
    private static Singleton21 sInstance;

    private Singleton21() {
    }

    //只适合在单线程情况下使用，如果是多线程情况下，一个线程进入 if (sInstance == null) 判断语句块，还没来得及往下执行，另一个线程也通过了这个 if 判断语句，此时就会产生多个实例
    public static Singleton21 getInstance() {
        if (sInstance == null) {
            sInstance = new Singleton21();
        }
        return sInstance;
    }

    public void doSomething() {
        //
    }
}
