package com.louis.javadesignpatterns.creational.abstract_factory.factory;

import com.louis.javadesignpatterns.creational.abstract_factory.IProductA;
import com.louis.javadesignpatterns.creational.abstract_factory.IProductB;
import com.louis.javadesignpatterns.creational.abstract_factory.ProductA1;
import com.louis.javadesignpatterns.creational.abstract_factory.ProductB1;

/**
 * Created by louisgeek on 2024/10/29.
 */
public class ProductFactory1 implements ProductFactory {

    @Override
    public IProductA createProductA() {
        return new ProductA1();
    }

    @Override
    public IProductB createProductB() {
        return new ProductB1();
    }
}
