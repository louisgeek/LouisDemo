package com.louis.javadesignpatterns.creational.singleton;

/**
 * Created by louisgeek on 2024/12/12.
 * 5 枚举
 * 枚举本身就是单例的，构造函数默认是 private 的
 * 不支持懒加载
 */
public enum Singleton5 {
    INSTANCE;

    public void doSomething() {
        //
    }
}
