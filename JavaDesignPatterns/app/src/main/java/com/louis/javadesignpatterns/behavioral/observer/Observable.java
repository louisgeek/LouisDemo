package com.louis.javadesignpatterns.behavioral.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by louisgeek on 2024/12/15.
 */
public interface Observable {
    void addObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers();
}
