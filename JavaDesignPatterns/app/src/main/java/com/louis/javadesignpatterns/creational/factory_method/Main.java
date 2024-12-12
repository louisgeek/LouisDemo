package com.louis.javadesignpatterns.creational.factory_method;


import com.louis.javadesignpatterns.creational.factory_method.factory.OperationAddFactory;
import com.louis.javadesignpatterns.creational.factory_method.factory.OperationFactory;

/**
 * Created by louisgeek on 2024/10/28.
 */
public class Main {
    public static void main(String[] args) {
        OperationFactory operationFactory = new OperationAddFactory();
        IOperation operation = operationFactory.create();
        operation.operate(1, 2);
    }
}
