package com.louis.javadesignpatterns.pipeline;


import com.louis.javadesignpatterns.factory_method.IOperation;
import com.louis.javadesignpatterns.factory_method.factory.OperationAddFactory;
import com.louis.javadesignpatterns.factory_method.factory.OperationFactory;

/**
 * Created by louisgeek on 2024/12/12.
 */
public class Main {
    public static void main(String[] args) {
        String input = " Hello World  ";
        Pipe<String, String> firstPipe = new TrimProcessor();
        Pipe<String, String> secondPipe = new UpperCaseProcessor();
        String output = secondPipe.process(firstPipe.process(input));
        System.out.println("Input: '" + input + "'");
        System.out.println("Output: '" + output + "'");
    }
}
