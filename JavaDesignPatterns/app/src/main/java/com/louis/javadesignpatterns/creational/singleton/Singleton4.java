package com.louis.javadesignpatterns.creational.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 */
//4 静态内部类
public class Singleton4 {
    private Singleton4() {
    }

    //静态内部类
    private static class SingletonInstanceHolder {
        //依托了内部类的加载机制，类的静态属性只会在第一次加载该类的时候进行初始化
        private static final Singleton4 INSTANCE = new Singleton4();
    }

    public static Singleton4 getInstance() {
        return SingletonInstanceHolder.INSTANCE;
    }

    public void doSomething() {
        //
    }
}
