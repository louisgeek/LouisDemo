package com.louis.javadesignpatterns.creational.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 * 3 Double-Checked Locking 双重检查锁 DCL，进行了两次判空
 * 懒加载
 */
public class Singleton3 {
    //声明 volatile 关键字，禁止了指令重排序，保证可见性和有序性
    //sInstance = new Singleton3(); 不是原子操作，分为 3 步：
    //1. 分配内存空间
    //2. 初始化对象
    //3. 将 sInstance 指向内存地址
    private static volatile Singleton3 sInstance;

    private Singleton3() {

    }

    public static Singleton3 getInstance() {
        //判断一次，避免不必要的同步
        if (sInstance == null) {
            synchronized (Singleton3.class) {
                //复合操作-start
                if (sInstance == null) {
                    sInstance = new Singleton3();
                }
                //复合操作-end
            }
        }
        return sInstance;
    }

    public void doSomething() {
        //
    }
}
