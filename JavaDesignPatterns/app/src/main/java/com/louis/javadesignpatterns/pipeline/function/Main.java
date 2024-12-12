package com.louis.javadesignpatterns.pipeline.function;


import java.util.function.Function;

/**
 * Created by louisgeek on 2024/12/12.
 */
public class Main {

    public static void main(String[] args) {
        test1();
        test2();
        test3();
        test4();
    }

    //采用函数式接口
    public static void test1() {
        String input = " Hello World  ";
        Function<String, String> firstPipe = new TrimProcessor();
        Function<String, String> secondPipe = new UpperCaseProcessor();
        String output = secondPipe.apply(firstPipe.apply(input));
        System.out.println("Input: '" + input + "'");
        System.out.println("Output: '" + output + "'");
    }

    //引入 Lambda 表达式
    public static void test2() {
        String input = " Hello World  ";
        Function<String, String> firstPipe = it -> it.trim();
        Function<String, String> secondPipe = it -> it.toUpperCase();
        String output = secondPipe.apply(firstPipe.apply(input));
        System.out.println("Input: '" + input + "'");
        System.out.println("Output: '" + output + "'");
    }

    //方法引用继续简化
    public static void test3() {
        String input = " Hello World  ";
        Function<String, String> firstPipe = String::trim;//方法引用
        Function<String, String> secondPipe = String::toUpperCase;//方法引用
        String output = secondPipe.apply(firstPipe.apply(input));
        System.out.println("Input: '" + input + "'");
        System.out.println("Output: '" + output + "'");
    }

    //可以用 andThen 和 compose 连写
    public static void test4() {
        String input = " Hello World  ";
        Function<String, String> firstPipe = String::trim;//方法引用
        Function<String, String> secondPipe = String::toUpperCase;//方法引用
        Function<String, Integer> thirdPipe = String::length;//方法引用
        //...
        Function<String, Integer> pipeline = firstPipe.andThen(secondPipe).andThen(thirdPipe);
        //Function<String, String> pipeline = firstPipe.compose(secondPipe);
        Integer outputLen = pipeline.apply(input);
        System.out.println("InputLen: '" + input.length() + "'");
        System.out.println("OutputLen: '" + outputLen + "'");
    }
}
