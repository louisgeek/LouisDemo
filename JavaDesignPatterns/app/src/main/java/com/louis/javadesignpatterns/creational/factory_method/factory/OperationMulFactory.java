package com.louis.javadesignpatterns.creational.factory_method.factory;


import com.louis.javadesignpatterns.creational.factory_method.IOperation;
import com.louis.javadesignpatterns.creational.factory_method.OperationMul;

/**
 * Created by louisgeek on 2024/10/28.
 */
public class OperationMulFactory extends OperationFactory {
    @Override
    public IOperation create() {
        return new OperationMul();
    }
}
