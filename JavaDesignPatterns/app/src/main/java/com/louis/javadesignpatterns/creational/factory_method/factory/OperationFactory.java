package com.louis.javadesignpatterns.creational.factory_method.factory;


import com.louis.javadesignpatterns.creational.factory_method.IOperation;

/**
 * Created by louisgeek on 2024/10/28.
 */
public abstract class OperationFactory {
    public abstract IOperation create();
}
