package com.louis.javadesignpatterns.factory_method.factory;


import com.louis.javadesignpatterns.factory_method.IOperation;
import com.louis.javadesignpatterns.factory_method.OperationMinus;

/**
 * Created by louisgeek on 2024/10/28.
 */
public class OperationMinusFactory extends OperationFactory {
    @Override
    public IOperation create() {
        return new OperationMinus();
    }
}
