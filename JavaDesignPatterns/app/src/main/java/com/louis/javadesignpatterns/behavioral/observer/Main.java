package com.louis.javadesignpatterns.behavioral.observer;


/**
 * Created by louisgeek on 2024/12/12.
 */
public class Main {
    public static void main(String[] args) {
        MyObservable myObservable = new MyObservable();

        Observer observer1 = new MyObserver("观察者1");
        Observer observer2 = new MyObserver("观察者2");
        Observer observer3 = new MyObserver("观察者3");

        myObservable.addObserver(observer1);
        myObservable.addObserver(observer2);
        myObservable.addObserver(observer3);

        myObservable.setMessage("测试消息1");

        myObservable.removeObserver(observer1);

        myObservable.setMessage("测试消息2");
    }
}
