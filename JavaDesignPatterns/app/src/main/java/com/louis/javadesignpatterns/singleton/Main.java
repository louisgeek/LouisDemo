package com.louis.javadesignpatterns.singleton;


import com.louis.javadesignpatterns.simple_factory.IOperation;
import com.louis.javadesignpatterns.simple_factory.factory.OperationFactory;

/**
 * Created by louisgeek on 2024/10/28.
 */
public class Main {
    public static void main(String[] args) {
        //1饿汉式
        Singleton11.getInstance().doSomething();
        Singleton12.getInstance().doSomething();
        Singleton13.getInstance().doSomething();
        //2懒汉式
        Singleton21.getInstance().doSomething();
        Singleton22.getInstance().doSomething();
        Singleton23.getInstance().doSomething();
        Singleton24.getInstance().doSomething();
        //3双重检查锁
        Singleton3.getInstance().doSomething();
        //4静态内部类
        Singleton4.getInstance().doSomething();
        //5枚举式
        Singleton5.INSTANCE.doSomething();
    }
}
