package com.louis.javadesignpatterns.creational.abstract_factory;


import com.louis.javadesignpatterns.creational.abstract_factory.factory.ProductFactory1;
import com.louis.javadesignpatterns.creational.abstract_factory.factory.ProductFactory2;
import com.louis.javadesignpatterns.creational.abstract_factory.factory.ProductFactory;

/**
 * Created by louisgeek on 2024/10/28.
 */
public class Main {
    public static void main(String[] args) {
        ProductFactory productFactory1 = new ProductFactory1();
        IProductA productA1 = productFactory1.createProductA();
        IProductB productB1 = productFactory1.createProductB();

        ProductFactory productFactory2 = new ProductFactory2();
        IProductA productA2 = productFactory2.createProductA();
        IProductB productB2 = productFactory2.createProductB();
    }
}
