package com.louis.javadesignpatterns.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 */
//1.1 饿汉式 静态常量
public class Singleton11 {
    //基于 Class Loader 类加载机制
    private final static Singleton11 INSTANCE = new Singleton11();

    //私有构造方法，防止被外部直接实例化
    private Singleton11() {
    }

    public static Singleton11 getInstance() {
        return INSTANCE;
    }

    public void doSomething() {
        //
    }
}
