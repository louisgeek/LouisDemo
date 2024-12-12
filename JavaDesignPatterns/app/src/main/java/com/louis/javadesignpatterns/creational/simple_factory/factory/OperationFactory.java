package com.louis.javadesignpatterns.creational.simple_factory.factory;


import com.louis.javadesignpatterns.creational.simple_factory.IOperation;
import com.louis.javadesignpatterns.creational.simple_factory.OperationAdd;
import com.louis.javadesignpatterns.creational.simple_factory.OperationDiv;
import com.louis.javadesignpatterns.creational.simple_factory.OperationMinus;
import com.louis.javadesignpatterns.creational.simple_factory.OperationMul;

/**
 * Created by louisgeek on 2024/10/28.
 */
public class OperationFactory {
    public static IOperation create(String operation) {
        IOperation iOperation = null;
        switch (operation) {
            case "+":
                iOperation = new OperationAdd();
                break;
            case "-":
                iOperation = new OperationMinus();
                break;
            case "*":
                iOperation = new OperationMul();
                break;
            case "/":
                iOperation = new OperationDiv();
                break;
        }
        return iOperation;
    }
}