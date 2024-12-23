package com.louis.javadesignpatterns.creational.simple_factory;


import com.louis.javadesignpatterns.creational.simple_factory.factory.OperationFactory;

/**
 * Created by louisgeek on 2024/10/28.
 */
public class Main {
    public static void main(String[] args) {
        IOperation operation = OperationFactory.create("+");
        operation.operate(1, 2);
    }
}
