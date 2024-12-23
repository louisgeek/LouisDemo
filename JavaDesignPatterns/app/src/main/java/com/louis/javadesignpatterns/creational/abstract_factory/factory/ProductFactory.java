package com.louis.javadesignpatterns.creational.abstract_factory.factory;


import com.louis.javadesignpatterns.creational.abstract_factory.IProductA;
import com.louis.javadesignpatterns.creational.abstract_factory.IProductB;

/**
 * Created by louisgeek on 2024/10/28.
 */
public interface ProductFactory {
    IProductA createProductA();

    IProductB createProductB();
}