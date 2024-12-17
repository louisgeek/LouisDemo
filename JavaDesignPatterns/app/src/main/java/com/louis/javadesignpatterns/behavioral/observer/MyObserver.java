package com.louis.javadesignpatterns.behavioral.observer;

/**
 * Created by louisgeek on 2024/12/15.
 */
public class MyObserver implements Observer {

    private final String name;

    public MyObserver(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println(name + " 收到消息: " + message);
    }
}
