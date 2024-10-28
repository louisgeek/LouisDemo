package com.louis.javadesignpatterns.factory_method;

/**
 * Created by louisgeek on 2024/10/28.
 */
public class OperationMul implements IOperation {
    @Override
    public int operate(int a, int b) {
        return a * b;
    }
}
