package com.louis.javadesignpatterns.creational.factory_method;

/**
 * Created by louisgeek on 2024/10/28.
 */
public class OperationAdd implements IOperation {
    @Override
    public int operate(int a, int b) {
        return a + b;
    }
}
