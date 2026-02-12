package com.louis.javadesignpatterns.creational.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 * 1.3 饿汉式 静态全局变量 静态代码块
 * JVM 保证类加载过程保证线程安全
 * 可以在静态代码块中添加更复杂的初始化逻辑（如异常处理）
 */
public class Singleton13 {
    private static Singleton13 sInstance;

    static {
        //在类加载时自动执行
        sInstance = new Singleton13();
    }

    private Singleton13() {
    }

    public static Singleton13 getInstance() {
        //直接返回已创建好的实例
        return sInstance;
    }

    public void doSomething() {
        //
    }
}
