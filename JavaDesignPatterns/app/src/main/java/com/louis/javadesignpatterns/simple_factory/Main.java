package com.louis.javadesignpatterns.simple_factory;


import com.louis.javadesignpatterns.simple_factory.factory.OperationFactory;

/**
 * Created by louisgeek on 2024/10/28.
 */
public class Main {
    public void main(String[] args) {
        IOperation operation = OperationFactory.create("+");
        operation.operate(1, 2);
    }
}
