package com.louis.javadesignpatterns.behavioral.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by louisgeek on 2024/12/15.
 */
public class MyObservable implements Observable {
    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        // 当消息改变时，通知所有观察者
        this.notifyObservers();
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
