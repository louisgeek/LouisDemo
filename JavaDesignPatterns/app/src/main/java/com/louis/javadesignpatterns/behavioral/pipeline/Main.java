package com.louis.javadesignpatterns.behavioral.pipeline;


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
