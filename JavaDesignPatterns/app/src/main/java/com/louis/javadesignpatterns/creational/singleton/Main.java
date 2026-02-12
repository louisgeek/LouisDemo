package com.louis.javadesignpatterns.creational.singleton;


/**
 * Created by louisgeek on 2024/10/28.
 * 需要懒加载：首选 Singleton4（静态内部类），备选：Singleton3（DCL，复杂、易写错）
 * 不需要懒加载：需要防反射/序列化攻击选 Singleton5（枚举），不需要就选 Singleton11、Singleton12、Singleton13
 * Singleton21、Singleton23 不安全，Singleton22、Singleton24 性能较差，不推荐使用
 */
public class Main {
    public static void main(String[] args) {
        //1 饿汉式
        Singleton11.getInstance().doSomething();
        Singleton12.getInstance().doSomething();
        Singleton13.getInstance().doSomething();
        //2 懒汉式
        Singleton21.getInstance().doSomething();
        Singleton22.getInstance().doSomething();
        Singleton23.getInstance().doSomething();
        Singleton24.getInstance().doSomething();
        //3 双重检查锁
        Singleton3.getInstance().doSomething();
        //4 静态内部类
        Singleton4.getInstance().doSomething();
        //5 枚举
        Singleton5.INSTANCE.doSomething();
    }
}
